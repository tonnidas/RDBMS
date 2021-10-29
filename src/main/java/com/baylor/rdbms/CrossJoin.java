package com.baylor.rdbms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrossJoin {

    public static void process(String[] params) throws Exception {

        if (params.length != 3) {
            throw new Exception("Cross join should have exactly 3 params");
        }

        // read first input file where first element of the data is the header row
        List<String[]> data1 = Helper.parseCSVFile(params[0]);

        // read second input file where first element of the data is the header row
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

        // write output, outputFile = params[2]
        Helper.writeCSVFile(params[2], outputData);
    }
}
