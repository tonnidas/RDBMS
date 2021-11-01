package com.baylor.rdbms;

import java.util.Arrays;
import java.util.Scanner;

/**
 * The Main class takes input of a query, parses operator name, and execute appropriate operator.
 * It supports select, project, cross, join, btree operators.
 * This query is case sensitive i.e. it should be "select", not "Select" or "SELECT".
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Insert query");
        Scanner sc = new Scanner(System.in);
        String query = sc.nextLine();

        try {
            processQuery(query);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Reason:");
            e.printStackTrace();
        }
    }

    /**
     * processQuery(String query) method might throw exception for any invalid operator word, such as "SELECT", "joiiin" etc.
     * It splits the query and finds out the key operation (select, project, cross, join, btree)
     * All the operations are done in separate classes. There are total 5 classes for these 5 operations, select, project, cross, join, btree.
     * This method calls another method, className.process(Rest of the query except the key operation word)
     */
    public static void processQuery(String query) throws Exception {
        // separate operator and params
        String[] parts = query.split("\\s", -1);
        String operator = parts[0];
        String[] params = Arrays.copyOfRange(parts, 1, parts.length);

        switch (operator) {
            case "select":
                Select.process(params);
                break;
            case "project":
                Project.process(params);
                break;
            case "cross":
                CrossJoin.process(params);
                break;
            case "join":
                NaturalJoin.process(params);
                break;
            case "btree":
                Helper.printBPlusTree(params);
                break;
            default:
                throw new Exception("Invalid operator: " + operator);
        }
    }
}
