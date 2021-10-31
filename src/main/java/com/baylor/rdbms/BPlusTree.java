package com.baylor.rdbms;

import java.io.Serializable;
import java.util.*;

// implements Serializable to store and load object from file
class BPlusTree implements Serializable {
    static final int M = 4;

    Node root;
    int height;

    BPlusTree() {
        root = new Node(0);
        height = 0;
    }

    class Node implements Serializable {
        int numEntries;
        Entry[] entries = new Entry[M];

        Node(int numEntries) {
            this.numEntries = numEntries;
        }

        @Override
        public String toString() {
            StringBuilder nodeStr = new StringBuilder("node = | ");
            for (int i = 0; i < numEntries; i++) {
                nodeStr.append(entries[i]).append(" | ");
            }
            return nodeStr.toString();
        }
    }

    class Entry implements Serializable {
        String key;
        Node next;
        List<Integer> values;

        // constructor for internal node entry
        Entry(String key, Node next) {
            this.key = key;
            this.next = next;
            this.values = null;
        }

        // constructor for leaf node entry
        Entry(String key, Integer value) {
            this.key = key;
            this.next = null;
            values = new ArrayList<>(Collections.singleton(value));
        }

        @Override
        public String toString() {
            if (next == null) { // leaf node
                return "key = " + key + ", values = " + values;
            } else { // internal node
                return "key = " + key;
            }
        }
    }

    List<Integer> search(String key) {
        return searchRec(root, key, height);
    }

    List<Integer> searchRec(Node node, String key, int ht) {
        Entry[] entries = node.entries;

        if (ht == 0) { // leaf node
            for (int i = 0; i < node.numEntries; i++) {
                if (isEqual(key, entries[i].key)) return entries[i].values;
            }
        } else { // internal node
            for (int i = 0; i < node.numEntries; i++) {
                if (i + 1 == node.numEntries || isLess(key, entries[i + 1].key))
                    return searchRec(entries[i].next, key, ht - 1);
            }
        }
        return null;
    }

    void insert(String key, Integer value) {
        Node node = insertRec(root, key, value, height);

        if (node != null) { // spilt root
            Node newRoot = new Node(2);
            newRoot.entries[0] = new Entry(root.entries[0].key, root);
            newRoot.entries[1] = new Entry(node.entries[0].key, node);
            root = newRoot;
            height++;
        }
    }

    Node insertRec(Node node, String key, Integer value, int ht) {
        Entry entry = new Entry(key, value);
        int pos = node.numEntries;

        if (ht == 0) { // leaf node
            for (int i = 0; i < node.numEntries; i++) {
                if (isEqual(key, node.entries[i].key)) { // duplicate key
                    node.entries[i].values.add(value);
                    return null;
                } else if (isLess(key, node.entries[i].key)) {
                    pos = i;
                    break;
                }
            }
        } else { // internal node
            for (int i = 0; i < node.numEntries; i++) {
                if ((i + 1 == node.numEntries) || isLess(key, node.entries[i + 1].key)) {
                    Node newNode = insertRec(node.entries[i].next, key, value, ht - 1);

                    if (newNode == null) {
                        return null;
                    } else {
                        entry.key = newNode.entries[0].key;
                        entry.next = newNode;
                        pos = i + 1;
                        break;
                    }
                }
            }
        }

        if (node.numEntries - pos >= 0) {
            System.arraycopy(node.entries, pos, node.entries, pos + 1, node.numEntries - pos);
        }

        node.entries[pos] = entry;
        node.numEntries++;

        if (node.numEntries < M) return null;
        else return split(node);
    }

    Node split(Node node) {
        Node newNode = new Node(M / 2);
        node.numEntries = M / 2;
        System.arraycopy(node.entries, 2, newNode.entries, 0, M / 2);
        return newNode;
    }

    // print the tree level-by-level using BFS
    @Override
    public String toString() {
        StringBuilder treeStr = new StringBuilder();

        class Item {
            final Node node;
            final String parent; // parent entry key
            final int level;

            public Item(Node node, String parent, int level) {
                this.node = node;
                this.parent = parent;
                this.level = level;
            }
        }

        Queue<Item> queue = new ArrayDeque<>();
        queue.add(new Item(root, "null", 1));

        int curLevel = 0;

        while (!queue.isEmpty()) {
            Item item = queue.remove();

            if (item.level > curLevel) {
                if (item.level <= height) {
                    treeStr.append("level = ").append(item.level).append("\n");
                } else {
                    treeStr.append("level = ").append(item.level).append(" (leaf)").append("\n");
                }
                curLevel = item.level;
            }

            treeStr.append(item.node).append(" parent-key = ").append(item.parent).append("\n");

            for (int i = 0; i < item.node.numEntries; i++) {
                if (item.node.entries[i].next != null) {
                    queue.add(new Item(item.node.entries[i].next, item.node.entries[i].key, item.level + 1));
                }
            }
        }

        return treeStr.toString();
    }

    boolean isLess(String k1, String k2) {
        return k1.compareTo(k2) < 0;
    }

    boolean isEqual(String k1, String k2) {
        return k1.compareTo(k2) == 0;
    }

    /*public static void main(String[] args) {
        BPlusTree bpt = new BPlusTree();

        char key = 'a';

        for (int i = 1; i <= 10; i++, key++) {
            bpt.insert(key + "", i);
        }

        bpt.insert("a", 20); // duplicate

        System.out.println("a: " + bpt.search("a"));
        System.out.println("d: " + bpt.search("j"));
        System.out.println("k: " + bpt.search("k"));
        System.out.println();

        System.out.println("height: " + bpt.height);
        System.out.println();

        System.out.println(bpt);

        try {
            Helper.storeBPlusTree(bpt, "tree.txt");
            BPlusTree bpt2 = Helper.loadBPlusTree("tree.txt");
            System.out.println("serialization check = " + bpt.toString().equals(bpt2.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}