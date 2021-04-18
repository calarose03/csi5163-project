package implementation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class CSVPrinter {

    private static final String FILENAME_SBTS = "sbts-results.txt";
    private static final String FILENAME_APPROX = "approx-results.txt";

    public static void printToCSVSBTS(String filename, double avgMISsize, double avgTime) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME_SBTS, true));
        writer.append(filename).append(", ").append(String.valueOf(avgMISsize)).append(", ").append(String.valueOf(avgTime)).append(" ms").append("\n");
        writer.close();
    }

    public static void printToCSVApprox(String filename,  double avgMISsize, double avgTime) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME_APPROX, true));
        writer.append(filename).append(", ").append(String.valueOf(avgMISsize)).append(", ").append(String.valueOf(avgTime)).append(" ms").append("\n");
        writer.close();
    }
}
