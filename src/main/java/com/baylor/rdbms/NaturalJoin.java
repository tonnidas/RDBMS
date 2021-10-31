package com.baylor.rdbms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The NaturalJoin class takes an array of string parameters as input.
 * It validates the length of the input and throws exception if parameters length is not 3.
 * The parameters are in the form, "f1 f2 oname", where f1 and f2 are input files.
 * It throws exception if it cannot find the input csv files.
 * NaturalJoin first call CrossJoin and write output to a temp file "temp-cross-join.csv".
 * Then it calls Select (case 1) to n times, where n is the number of common columns among f1 and f2.
 * The first Select operation takes the output of the cross join as input.
 * The subsequent Select operation takes output of the previous Selection operation as input.
 * All Select operations write output in temporary files "temp-select-{columnName}.csv".
 * Finally duplicate columns are eliminated using the Project operation.
 * Project operation takes the output of the last Select operation as input.
 * Project writes output in the final outputFile in csv format.
 * If the input relations are R(A, B) and S(B, C), the output will have (A, B, C) columns.
 * Here we keep only one copy of the common columns.
 */
public class NaturalJoin {
    public static void process(String[] params) throws Exception {

        // params: f1 f2 oname
        if (params.length != 3) {
            throw new Exception("Natural join should have exactly 3 params");
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

        // now call the cross join operator and write output in a intermediate file
        // input files will remain the same, use temp file for output file
        String tempCrossJoinFile = "temp-cross-join.csv";
        CrossJoin.process(new String[]{inputFile1, inputFile2, tempCrossJoinFile});

        // use select to match common columns
        // chain output of one Select operation to input of next Select operation
        String data2Prefix = Helper.getTableName(inputFile2) + ".";
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
        projectParams.add(outputFile); // final output file
        projectParams.addAll(finalOutputCols); // final col names without duplicates
        Project.process(projectParams.toArray(new String[0])); // convert list to String[] when calling Project
    }
}
