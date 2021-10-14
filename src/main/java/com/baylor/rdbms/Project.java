package com.baylor.rdbms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Project {
    // filename A B C
    public static void process(String[] params) throws Exception {
        if (params.length < 1) {
            throw new Exception("Project should have at least 1 param (filename)");
        }

        // first element of the data is the header row
        List<String[]> data = Helper.parseCSVFile(params[0]);

        // if no columns name is given then project all columns i.e. input = output
        if (params.length == 1) {
            // write output
            Helper.writeCSVFile("output.csv", data);
            return;
        }

        // get indexes of all project columns
        List<Integer> colIndexes = new ArrayList<>();
        for (int i = 1; i < params.length; i++) {
            int index = Helper.getColumnIndex(data.get(0), params[i]);
            if (index == -1) {
                throw new Exception("Column not found: " + params[i]);
            } else {
                colIndexes.add(index);
            }
        }

        List<String[]> outputData = new ArrayList<>();

        // output has the projected columns only
        // i.e. all the params except the first one (filename)
        outputData.add(Arrays.copyOfRange(params, 1, params.length));

        // for each row output projected columns
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            List<String> newRow = new ArrayList<>();

            for (int index : colIndexes) {
                newRow.add(row[index]);
            }

            // convert list to array and add to outputData
            outputData.add(newRow.toArray(new String[0]));
        }

        // write output
        Helper.writeCSVFile("output.csv", outputData);
    }
}
