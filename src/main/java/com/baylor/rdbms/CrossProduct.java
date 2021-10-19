package com.baylor.rdbms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrossProduct {

    public static void process(String[] params) throws Exception {

        if (params.length != 2) {
            throw new Exception("Cross product should have exactly 2 params");
        }

        // first element of the data is the header row
        List<String[]> data1 = Helper.parseCSVFile(params[0]);

        // first element of the data is the header row
        List<String[]> data2 = Helper.parseCSVFile(params[1]);

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
        Helper.writeCSVFile("output.csv", outputData);
    }
}
