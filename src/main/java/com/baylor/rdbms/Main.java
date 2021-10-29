package com.baylor.rdbms;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Insert query");
        Scanner sc = new Scanner(System.in);
        String query = sc.nextLine();

        try {
            processQuery(query);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

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

                break;
            default:
                throw new Exception("Invalid operator: " + operator);
        }
    }
}
