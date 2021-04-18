package implementation;

import implementation.approx.MISApproximation;
import implementation.sbts.SwapBasedTabuSearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class RunMIS {
    public static void main(String[] args) throws IOException {
        String filepath = new File("").getAbsolutePath();
        filepath = filepath.concat("\\data\\" + args[0]);

        switch (args[1]) {
            case "1":
                Graph graph = new Graph(filepath);
                byApproximation(graph);
                break;
            case "2":
                graph = new Graph(filepath);
                byTabuSearch(graph);
                break;
            case "3":
                automateRuns(filepath);
                break;
        }
    }

    private static void automateRuns(String filepath) throws IOException {
        FileReader fileReader = new FileReader(filepath);
        BufferedReader stdin = new BufferedReader(fileReader);

        String fileLine;
        while ((fileLine = stdin.readLine()) !=null && fileLine.trim().length()>0) {
            String graphfilepath = new File("").getAbsolutePath();
            graphfilepath = graphfilepath.concat("\\data\\" + fileLine);

            Graph graph = new Graph(graphfilepath);

            List<Long> approxTime = new ArrayList<>();
            List<Integer> approxSize = new ArrayList<>();

            List<Long> SBTSTime = new ArrayList<>();
            List<Integer> SBTSSize = new ArrayList<>();
            System.out.println(fileLine);
            for(int j = 0 ;j < 10; j++) {

                long approxStartTime = System.currentTimeMillis();
                MISApproximation misApproximation = new MISApproximation(graph);
                Set<Integer> approxMis = misApproximation.approximateMIS();
                long approxEndTime = System.currentTimeMillis();

                approxSize.add(approxMis.size());
                approxTime.add(approxEndTime-approxStartTime);

                long SBTSStartTime = System.currentTimeMillis();
                double maxIter = Math.pow(10,3);
                Set<Integer> SBTSMis = null;
                for (int i =0; i < 1; i++) {
                    SwapBasedTabuSearch misTS = new SwapBasedTabuSearch(graph);
                    Set<Integer> mis = misTS.search((int) maxIter);
                    if (SBTSMis == null || mis.size() > SBTSMis.size()) {
                        SBTSMis = new HashSet<>(mis);
                    }
                }
                long SBTSSEndTime = System.currentTimeMillis();

                SBTSSize.add(SBTSMis.size());
                SBTSTime.add(SBTSSEndTime-SBTSStartTime);
            }

            int approxSum = 0;
            for (int i : approxSize) {
                approxSum+=i;
            }
            double approxAverageSize = approxSum/(float)approxSize.size();

            int SBTSSum = 0;
            for (int i : SBTSSize) {
                SBTSSum+=i;
            }
            double SBTSAverageSize = SBTSSum/(float)SBTSSize.size();

            long approxSumTime = 0;
            for (long i : approxTime) {
                approxSumTime+=i;
            }
            double approxAverageTime = approxSumTime/(float)approxTime.size();

            long SBTSSumTime = 0;
            for (long i : SBTSTime) {
                SBTSSumTime+=i;
            }
            double SBTSAverageTime = SBTSSumTime/(float)SBTSTime.size();

            CSVPrinter.printToCSVApprox(fileLine, approxAverageSize, approxAverageTime);
            CSVPrinter.printToCSVSBTS(fileLine, SBTSAverageSize, SBTSAverageTime);
        }
    }



    private static void byApproximation(Graph graph) {
        MISApproximation misApproximation = new MISApproximation(graph);
        long startTime = System.currentTimeMillis();
        Set<Integer> mis = misApproximation.approximateMIS();
        long endTime = System.currentTimeMillis();

        System.out.println("APPROXIMATION");
        isIndependentSet(mis, graph);
        printMIS(mis, startTime, endTime, new HashMap<>());
    }

    private static void byTabuSearch(Graph graph) {
        long startTime = System.currentTimeMillis();
        double maxIter = Math.pow(10,4);
        Set<Integer> bestMIS = null;
        for (int i =0; i < 100; i++) {
            SwapBasedTabuSearch misTS = new SwapBasedTabuSearch(graph);
            Set<Integer> mis = misTS.search((int) maxIter);
            if (bestMIS == null || mis.size() > bestMIS.size()) {
                bestMIS = new HashSet<>(mis);
            }
        }
        long endTime = System.currentTimeMillis();

        System.out.println("SWAP-BASED TABU SEARCH");
        isIndependentSet(bestMIS, graph);
        printMIS(bestMIS, startTime, endTime, new HashMap<>());
    }

    private static void isIndependentSet(Set<Integer> mis, Graph graph) {
        for (int u: mis) {
            for (int v: mis) {
                if (graph.isEdge(u,v)) {
                    System.out.println("Not an actual indepedent set.");
                    break;
                }
            }
        }
    }

    private static void printMIS(Set<Integer> mis, long startTime, long endTime, Map<String, String> parameters) {
        if(!parameters.isEmpty()) {
            System.out.println("\nParameters");
            printParams(parameters);
        }

        System.out.println("\nMaximum Independent Set: " + mis.size());
        printMIS(mis);

        System.out.println("\nComputing time: " + String.valueOf(endTime-startTime) + "ms");
    }

    private static void printMIS(Set<Integer> mis) {
        String str = "";

        for(int node : mis) {
            str = str.concat(node + " ");
        }

        System.out.println(str);
    }

    private static void printParams(Map<String, String> params) {
        String str = "";

        for(Map.Entry<String, String> param : params.entrySet()) {
            str = str.concat( param.getKey() + ": " + param.getValue() + "\n");
        }

        System.out.print(str);
    }
}
