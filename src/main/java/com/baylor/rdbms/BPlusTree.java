package com.baylor.rdbms;

import java.io.Serializable;
import java.util.*;

/**
 * This BPlusTree class implement a BPlusTree indexing.
 * It implements Java Serializable to enable storing a object into a file or loading a object from a file.
 * It supports three operations: insert, search, and toString.
 * The insert operation inserts a key-value pair into the tree.
 * It can store duplicates i.e. multiple values under a single key.
 * The search operation takes a key as input and returns a list of values that belongs to the key.
 * The toString operation prints the BPlusTree into a human readable string format.
 * It prints the tree level-by-level using BFS traversal.
 * For internal nodes, it prints the keys and pointers, where each pointer represent a child nodeID.
 * For leaf node, it prints the keys and associated values.
 */
class BPlusTree implements Serializable {
    static final int M = 100;

    Node root;
    int height, numNodes;

    BPlusTree() {
        root = new Node(0);
        height = 0;
        numNodes = 1;
    }

    class Node implements Serializable {
        int numEntries, nodeID;
        Entry[] entries = new Entry[M];

        Node(int numEntries) {
            this.numEntries = numEntries;
            this.nodeID = numNodes++;
        }

        public String toString(boolean isLeaf) {
            List<String> keys = new ArrayList<>();
            List<Integer> pointers = new ArrayList<>();

            if (isLeaf) {
                for (int i = 0; i < numEntries; i++) {
                    keys.add("\n    (key = " + entries[i].key + ", values = " + entries[i].values + ")");
                }
                return "nodeID = " + nodeID + ", keys = " + keys;
            } else {
                pointers.add(entries[0].next.nodeID);
                for (int i = 1; i < numEntries; i++) {
                    keys.add(entries[i].key);
                    pointers.add(entries[i].next.nodeID);
                }
                return "nodeID = " + nodeID + ", keys = " + keys + ", nodeIDPointers = " + pointers;
            }
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

        // shift right after pos
        for (int i = node.numEntries; i > pos; i--) {
            node.entries[i] = node.entries[i - 1];
        }

        node.entries[pos] = entry;
        node.numEntries++;

        if (node.numEntries < M) return null;
        else return split(node);
    }

    Node split(Node node) {
        Node newNode = new Node(M / 2);
        node.numEntries = M / 2;

        // copy last half to new node
        for (int i = 0; i < M; i++) {
            if (i < M / 2) {
                newNode.entries[i] = node.entries[M / 2 + i];
            } else {
                node.entries[i] = null;
            }
        }

        return newNode;
    }

    public String toString() {
        StringBuilder treeStr = new StringBuilder();

        Queue<Node> nodeQueue = new ArrayDeque<>();
        Queue<Integer> levelQueue = new ArrayDeque<>();

        nodeQueue.add(root);
        levelQueue.add(1);

        int curLevel = 0;

        while (!nodeQueue.isEmpty()) {
            Node node = nodeQueue.remove();
            int level = levelQueue.remove();

            boolean isLeaf = level > height;

            if (level > curLevel) { // new level
                if (isLeaf) {
                    treeStr.append("\n").append("level = ").append(level).append(" (leaf)").append("\n");
                } else {
                    treeStr.append("\n").append("level = ").append(level).append("\n");
                }
                curLevel = level;
            }

            treeStr.append(node.toString(isLeaf)).append("\n");

            for (int i = 0; i < node.numEntries; i++) {
                if (node.entries[i].next != null) {
                    nodeQueue.add(node.entries[i].next);
                    levelQueue.add(level + 1);
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

//    public static void main(String[] args) {
//        BPlusTree bpt = new BPlusTree();
//
//        char key = 'a';
//
//        for (int i = 1; i <= 10; i++, key++) {
//            bpt.insert(key + "", i);
//        }
//
//        bpt.insert("a", 20); // duplicate
//
//        System.out.println("a: " + bpt.search("a"));
//        System.out.println("d: " + bpt.search("j"));
//        System.out.println("k: " + bpt.search("k"));
//        System.out.println();
//
//        System.out.println("height: " + bpt.height);
//        System.out.println();
//
//        System.out.println(bpt);
//
//        try {
//            Helper.storeBPlusTree(bpt, "tree.txt");
//            BPlusTree bpt2 = Helper.loadBPlusTree("tree.txt");
//            System.out.println("serialization check = " + bpt.toString().equals(bpt2.toString()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}