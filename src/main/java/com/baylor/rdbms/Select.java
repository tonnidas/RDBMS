package com.baylor.rdbms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Select {
    // case 1 = filename A B
    // case 2 = filename A 10
    public static void process(String[] params) throws Exception {
        if (params.length != 4) {
            throw new Exception("Select should have exactly 4 params");
        }

        // read input file where first element of the data is the header row
        List<String[]> data = Helper.parseCSVFile(params[0]);

        // get the index of the first column
        int colIndex1 = Helper.getColumnIndex(data.get(0), params[2]);
        if (colIndex1 == -1) {
            throw new Exception("Column not found: " + params[2]);
        }

        // get the index of the second column
        // if the second column is not found then case 2
        int colIndex2 = Helper.getColumnIndex(data.get(0), params[3]);
        if (colIndex2 == -1) {
            selectCase2(colIndex1, params[3], params[1]);
        } else {
            selectCase1(data, colIndex1, colIndex2, params[1]);
        }
    }

    public static void selectCase1(List<String[]> data, int colIndex1, int colIndex2, String outputFile) throws Exception {
        List<String[]> outputData = new ArrayList<>();

        // output has same columns as the input
        outputData.add(data.get(0));

        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            if (row[colIndex1].equals(row[colIndex2])) {
                outputData.add(row);
            }
        }

        // write output
        Helper.writeCSVFile(outputFile, outputData);
    }

    public static void selectCase2(int colIndex, String value, String outputFile) {

    }
}