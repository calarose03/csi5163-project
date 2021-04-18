package implementation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

public class Graph {

    private final LinkedHashMap<Integer, ArrayList<Integer>> adjacencyList;

    public Graph(String filename) throws IOException {
        this.adjacencyList = new LinkedHashMap<>();
        buildAdjacencyList(filename);
    }

    public Graph(Set<Integer> nodes) {
        adjacencyList = new LinkedHashMap<>();

        for (int node : nodes) {
            adjacencyList.put(node, new ArrayList<>());
        }
    }

    public void addEdge(int u, int v) {
        adjacencyList.get(u).add(v);
        adjacencyList.get(v).add(u);
    }

    public void removeEdge(int u, int v) {
        adjacencyList.get(u).remove(v);
        adjacencyList.get(v).remove(u);
    }

    public Set<Integer> getNodes() {
        return adjacencyList.keySet();
    }

    public boolean isEdge(int u, int v) {
        return adjacencyList.get(u).contains(v);
    }

    public ArrayList<Integer> getAdjacentNodes(int u) {
        return adjacencyList.get(u);
    }

    private void buildAdjacencyList(String filename) throws IOException {

        FileReader fileReader = new FileReader(filename);
        BufferedReader stdin = new BufferedReader(fileReader);

        String fileLine = stdin.readLine();
        String[] lineValues = fileLine.split(" ");

        int numOfEdges = Integer.parseInt(lineValues[lineValues.length - 1]);
        for (int i = 0; i < numOfEdges; i++) {
            fileLine = stdin.readLine();
            lineValues = fileLine.split(" ");

            Set<Integer> keyset = adjacencyList.keySet();

            int u = Integer.parseInt(lineValues[lineValues.length-2]);
            int v = Integer.parseInt(lineValues[lineValues.length-1]);

            if (!keyset.contains(u)) {
                adjacencyList.put(u, new ArrayList<>());
            }
            if (!adjacencyList.get(u).contains(v)) {
                adjacencyList.get(u).add(v);
            }

            if (!keyset.contains(v)) {
                adjacencyList.put(v, new ArrayList<>());
            }
            if (!adjacencyList.get(v).contains(v)) {
                adjacencyList.get(v).add(u);
            }
        }
    }
}
