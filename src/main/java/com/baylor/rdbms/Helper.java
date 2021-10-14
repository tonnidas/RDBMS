package com.baylor.rdbms;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
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
}
