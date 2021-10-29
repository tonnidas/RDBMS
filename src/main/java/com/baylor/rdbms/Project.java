package com.baylor.rdbms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Project {
    // filename A B C
    public static void process(String[] params) throws Exception {
        if (params.length < 2) {
            throw new Exception("Project should have at least 2 params (input and output file name)");
        }

        // read input file where first element of the data is the header row
        List<String[]> data = Helper.parseCSVFile(params[0]);

        // if no columns name is given then project all columns i.e. input = output
        if (params.length == 2) {
            // write output
            Helper.writeCSVFile(params[1], data);
            return;
        }

        // get indexes of all project columns
        List<Integer> colIndexes = new ArrayList<>();
        for (int i = 2; i < params.length; i++) {
            // data.get(0) is the header row
            int index = Helper.getColumnIndex(data.get(0), params[i]);
            if (index == -1) {
                throw new Exception("Column not found: " + params[i]);
            } else {
                colIndexes.add(index);
            }
        }

        List<String[]> outputData = new ArrayList<>();

        // output has the projected columns only
        // i.e. all the params except the first two (input and output filename)
        outputData.add(Arrays.copyOfRange(params, 2, params.length));

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

        // write output, outputFile = params[1]
        Helper.writeCSVFile(params[1], outputData);
    }
}
