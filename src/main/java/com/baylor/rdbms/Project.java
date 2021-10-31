package com.baylor.rdbms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Project class takes an array of string parameters as input.
 * It validates the length of the input and throws exception if parameters length is less than 2.
 * The parameters are in the form, "fname oname A B C", where A, B, C are column names.
 * It throws exception if it cannot find the input csv file or specified column names.
 * It writes the output in the outputFile in csv format.
 * If the input relation is R(A, B, C, D) and we are projecting A, B, C, the output will have only A, B, C columns.
 * If the input relation is R(A, B, C, D) and we were not given any attribute to project, we project the whole relation
 * and the output will have A, B, C, D columns.
 */
public class Project {
    public static void process(String[] params) throws Exception {

        // params: fname oname A B C
        if (params.length < 2) {
            throw new Exception("Project should have at least 2 params (input and output file name)");
        }

        String inputFile = params[0];
        String outputFile = params[1];
        String[] columns = Arrays.copyOfRange(params, 2, params.length);

        // read input file where first element of the data is the header row
        List<String[]> data = Helper.parseCSVFile(inputFile);

        // if no columns name is given then project all columns i.e. input = output
        if (columns.length == 0) {
            // write output
            Helper.writeCSVFile(outputFile, data);
            return;
        }

        // get indexes of all project columns
        List<Integer> colIndexes = new ArrayList<>();
        for (String c : columns) {
            // data.get(0) is the header row
            int index = Helper.getColumnIndex(data.get(0), c);
            if (index == -1) {
                throw new Exception("Column not found: " + c);
            } else {
                colIndexes.add(index);
            }
        }

        List<String[]> outputData = new ArrayList<>();

        // output has the projected columns only
        // i.e. all the params except the first two (input and output filename)
        outputData.add(columns);

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
        Helper.writeCSVFile(outputFile, outputData);
    }
}
