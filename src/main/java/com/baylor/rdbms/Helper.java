package com.baylor.rdbms;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Helper {
    public static List<String[]> parseCSVFile(String fileName) throws Exception {
        CSVReader csvReader = new CSVReader(new FileReader(fileName));
        List<String[]> data = csvReader.readAll();
        csvReader.close();
        return data;
    }

    // return -1 if index not found
    public static int getColumnIndex(String[] headers, String colName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(colName)) {
                return i;
            }
        }
        return -1;
    }

    public static void writeCSVFile(String fileName, List<String[]> data) throws Exception {
        CSVWriter csvWriter = new CSVWriter(new FileWriter(fileName));
        csvWriter.writeAll(data);
        csvWriter.close();
        System.out.println("Output written to: " + fileName);
    }

    public static List<String> findCommonColumns(String[] header1, String[] header2) {
        List<String> commonCols = new ArrayList<>();
        for (String s1 : header1) {
            for (String s2 : header2) {
                if (s1.equals(s2)) {
                    commonCols.add(s1);
                    break;
                }
            }
        }
        return commonCols;
    }

    // remove ".csv" extension from file name to get table name
    public static String getTableName(String fileName) {
        String fileNameExtension = ".csv";
        if (fileName.endsWith(fileNameExtension)) {
            return fileName.substring(0, fileName.length() - fileNameExtension.length());
        } else {
            return fileName;
        }
    }

    public static void storeBPlusTree(BPlusTree tree, String fileName) throws Exception {
        FileOutputStream f = new FileOutputStream(fileName);
        ObjectOutputStream o = new ObjectOutputStream(f);

        o.writeObject(tree);

        o.close();
        f.close();
    }

    public static BPlusTree loadBPlusTree(String fileName) throws Exception {
        FileInputStream fi = new FileInputStream(fileName);
        ObjectInputStream oi = new ObjectInputStream(fi);

        BPlusTree tree = (BPlusTree) oi.readObject();

        oi.close();
        fi.close();

        return tree;
    }
}
