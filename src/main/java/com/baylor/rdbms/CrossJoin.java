package com.baylor.rdbms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrossJoin {

    public static void process(String[] params) throws Exception {

        // params: f1 f2 oname
        if (params.length != 3) {
            throw new Exception("Cross join should have exactly 3 params");
        }

        String inputFile1 = params[0];
        String inputFile2 = params[1];
        String outputFile = params[2];

        // read first input file where first element of the data is the header row
        List<String[]> data1 = Helper.parseCSVFile(inputFile1);

        // read second input file where first element of the data is the header row
        List<String[]> data2 = Helper.parseCSVFile(inputFile2);

        // determine common columns among data1 and data2 using their header rows
        List<String> commonCols = Helper.findCommonColumns(data1.get(0), data2.get(0));

        // modify common column names in data2 row to add a table name prefix
        String data2Prefix = Helper.getTableName(inputFile2) + ".";
        String[] data2Header = data2.get(0);
        for (int i = 0; i < data2Header.length; i++) {
            if (commonCols.contains(data2Header[i])) {
                data2Header[i] = data2Prefix + data2Header[i];
            }
        }
        data2.set(0, data2Header);

        List<String[]> outputData = new ArrayList<>();
        List<String> temp = new ArrayList<>(Arrays.asList(data1.get(0)));
        temp.addAll(Arrays.asList(data2.get(0)));
        outputData.add(temp.toArray(new String[0]));

        for (int i = 1; i < data1.size(); i++) {
            for (int j = 1; j < data2.size(); j++) {
                List<String> combined = new ArrayList<>(Arrays.asList(data1.get(i)));
                combined.addAll(Arrays.asList(data2.get(j)));
                outputData.add(combined.toArray(new String[0]));
            }
        }

        // write output
        Helper.writeCSVFile(outputFile, outputData);
    }
}
