package com.baylor.rdbms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NaturalJoin {
    public static void process(String[] params) throws Exception {

        if (params.length != 3) {
            throw new Exception("Natural join should have exactly 3 params");
        }

        // read first input file where first element of the data is the header row
        List<String[]> data1 = Helper.parseCSVFile(params[0]);

        // read second input file where first element of the data is the header row
        List<String[]> data2 = Helper.parseCSVFile(params[1]);

        // determine common columns among data1 and data2 using their header rows
        List<String> commonCols = Helper.findCommonColumns(data1.get(0), data2.get(0));

        // now call the cross join operator and write output in a intermediate file
        // input files will remain the same, use temp file for output file
        String tempCrossJoinFile = "temp-cross-join.csv";
        CrossJoin.process(new String[]{params[0], params[1], tempCrossJoinFile});

        // use select to match common columns
        // chain output of one Select operation to input of next Select operation
        String data2Prefix = Helper.getTableName(params[1]) + ".";
        String tempSelectFile = tempCrossJoinFile;
        for (String commonCol : commonCols) {
            String tempSelectFileOutput = "temp-select-" + commonCol + ".csv";
            Select.process(new String[]{tempSelectFile, tempSelectFileOutput, commonCol, data2Prefix + commonCol});
            tempSelectFile = tempSelectFileOutput;
        }

        // find final output column names removing duplicates columns
        // keep the first table's columns same and remove common columns from second table
        List<String> finalOutputCols = new ArrayList<>(Arrays.asList(data1.get(0)));
        for (String col : data2.get(0)) {
            if (!commonCols.contains(col)) {
                finalOutputCols.add(col);
            }
        }

        // use project to prepare final output
        ArrayList<String> projectParams = new ArrayList<>();
        projectParams.add(tempSelectFile); // input to Project operator
        projectParams.add(params[2]); // final output file
        projectParams.addAll(finalOutputCols); // final col names without duplicates
        Project.process(projectParams.toArray(new String[0])); // convert list to String[] when calling Project
    }
}
