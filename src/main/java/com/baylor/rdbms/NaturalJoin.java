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
        List<String> commonCols = new ArrayList<>();
        for (String s1 : data1.get(0)) {
            for (String s2 : data2.get(0)) {
                if (s1.equals(s2)) {
                    commonCols.add(s1);
                    break;
                }
            }
        }

        // modify data2 header row to add a suffix to the common column names
        // this is required since we will use the select operator on cross join output
        String suffix = "-2";
        String[] data2Header = data2.get(0);
        for (int i = 0; i < data2Header.length; i++) {
            if (commonCols.contains(data2Header[i])) {
                data2Header[i] = data2Header[i] + suffix;
            }
        }
        data2.set(0, data2Header);

        // write modified data2 into a temp file
        String tempInput2File = "temp-" + params[1]; // add a "temp-" prefix to original file name
        Helper.writeCSVFile(tempInput2File, data2);

        // now call the cross join operator and write output in a intermediate file
        // first input will remain the same, use temp files for second input and output file
        String tempCrossJoinFile = "temp-cross-join.csv";
        CrossJoin.process(new String[]{params[0], tempInput2File, tempCrossJoinFile});

        // use select to match common columns
        // chain output of one Select operation to input of next Select operation
        String tempSelectFile = tempCrossJoinFile;
        for (String commonCol : commonCols) {
            String tempSelectFileOutput = "temp-select-" + commonCol + ".csv";
            Select.process(new String[]{tempSelectFile, tempSelectFileOutput, commonCol, commonCol + suffix});
            tempSelectFile = tempSelectFileOutput;
        }

        // find final output column names removing duplicates columns
        // keep the first table's columns same and remove common columns from second table
        List<String> finalOutputCols = new ArrayList<>(Arrays.asList(data1.get(0)));
        for (String col : data2Header) {
            // don't include columns with the predefined suffix
            if (!col.endsWith(suffix)) {
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
