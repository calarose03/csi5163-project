package implementation.approx;

import implementation.Graph;

import java.util.*;

public class MISApproximation {

    Graph originalGraph;

    public MISApproximation(Graph graph) {
        this.originalGraph = graph;
    }

    public Set<Integer> approximateMIS() {
        return approximateMIS(originalGraph);
    }

    private Set<Integer> approximateMIS(Graph graph) {

        if (graph.getNodes().isEmpty()) {
            return new HashSet<>();
        }

        int minDeg = 6;
        int mindDegNode = -1;
        for (int node : graph.getNodes()) {
            if (graph.getAdjacentNodes(node).size() < minDeg) {
                mindDegNode = node;
                minDeg = graph.getAdjacentNodes(node).size();
            }
        }

        if (minDeg < 3) {
            // remove minDegNode and its neighbours
            Graph newGraph = removeNode(mindDegNode, graph);
            Set<Integer> independentSet = approximateMIS(newGraph);
            independentSet.add(mindDegNode);

            return independentSet;
        } else if (minDeg == 3) {
            return degree3(mindDegNode, graph);
        } else if (minDeg == 4) {
            return degree4(mindDegNode, graph);
        } else {
            return degree5(mindDegNode, graph);
        }
    }

    private Set<Integer> degree3(int node, Graph graph) {
        List<Integer> neighbours = graph.getAdjacentNodes(node);

        boolean contains = false;
        for (int u : neighbours) {
            for (int v : neighbours) {
                if (graph.isEdge(u, v)) {
                    contains = true;
                }
            }
        }

        if (contains) {
            int firstNode = neighbours.get(0);

            neighbours.remove(0);
            Set<Integer> nodesToKeep = new HashSet<>(graph.getNodes());
            nodesToKeep.remove(node);
            nodesToKeep.removeAll(neighbours);

            Graph newGraph = createNewGraph(nodesToKeep, graph);

            updateNeighbours(newGraph, firstNode, graph.getAdjacentNodes(neighbours.get(0)));
            updateNeighbours(newGraph, firstNode, graph.getAdjacentNodes(neighbours.get(1)));

            Set<Integer> independentSet = approximateMIS(newGraph);
            if (independentSet.contains(firstNode)) {
                independentSet.addAll(neighbours);
            } else {
                independentSet.add(node);
            }
            return independentSet;
        } else {
            Graph newGraph = removeNode(node, graph);
            Set<Integer> independentSet = approximateMIS(newGraph);
            independentSet.add(node);
            return independentSet;
        }
    }

    private Set<Integer> degree4(int node, Graph graph) {
        ArrayList<Integer> neighbours = graph.getAdjacentNodes(node);

        int edges = 0;
        for (int u : neighbours) {
            for (int v : neighbours) {
                if (graph.isEdge(u, v)) {
                    edges++;
                }
            }
        }

        if (edges == 0) {
            int firstNode = neighbours.get(0);
            int thirdNode = neighbours.get(2);
            int secondNode = neighbours.get(1);
            int fourthNode = neighbours.get(3);
            neighbours.removeAll(Arrays.asList(firstNode, thirdNode));

            Set<Integer> nodesToKeep = new HashSet<>(graph.getNodes());
            nodesToKeep.remove(node);
            nodesToKeep.removeAll(neighbours);

            Graph newGraph = createNewGraph(nodesToKeep, graph);
            updateNeighbours(newGraph, firstNode, graph.getAdjacentNodes(secondNode));
            updateNeighbours(newGraph, thirdNode, graph.getAdjacentNodes(fourthNode));

            Set<Integer> independentSet = approximateMIS(newGraph);
            if (!independentSet.contains(firstNode) && !independentSet.contains(thirdNode)) {
                independentSet.add(node);
            }
            if (independentSet.contains(firstNode)) {
                independentSet.add(secondNode);
            }
            if (!independentSet.contains(firstNode) && independentSet.contains(thirdNode)) {
                independentSet.add(fourthNode);
            }

            return independentSet;
        } else {
            findOrderForDeg4(graph, neighbours);
            int firstNode = neighbours.get(0);
            int thirdNode = neighbours.get(2);
            neighbours.remove(0);

            Set<Integer> nodesToKeep = new HashSet<>(graph.getNodes());
            nodesToKeep.remove(node);
            nodesToKeep.removeAll(neighbours);

            Graph newGraph = createNewGraph(nodesToKeep, graph);

            updateNeighbours(newGraph, firstNode, graph.getAdjacentNodes(thirdNode));

            Set<Integer> independentSet = approximateMIS(newGraph);

            if (independentSet.contains(firstNode)) {
                independentSet.add(thirdNode);
            } else {
                independentSet.add(node);
            }
            return independentSet;
        }
    }

    private Set<Integer> degree5(int node, Graph graph) {
        ArrayList<Integer> neighbours = new ArrayList<>(graph.getAdjacentNodes(node));

        findOrderForDeg5(graph, neighbours);

        int firstNode = neighbours.get(0);
        int thirdNode = neighbours.get(2);
        neighbours.remove(0);

        Set<Integer> nodesToKeep = new HashSet<>(graph.getNodes());
        nodesToKeep.remove(node);
        nodesToKeep.removeAll(neighbours);

        Graph newGraph = createNewGraph(nodesToKeep, graph);

        updateNeighbours(newGraph, firstNode, graph.getAdjacentNodes(thirdNode));

        Set<Integer> independentSet = approximateMIS(newGraph);
        if (independentSet.contains(firstNode)) {
            independentSet.add(thirdNode);
        } else {
            independentSet.add(node);
        }

        return independentSet;
    }

    private Graph createNewGraph(Set<Integer> nodes, Graph graph) {
        Graph newGraph = new Graph(nodes);

        for (int u : newGraph.getNodes()) {
            for (int v : newGraph.getNodes()) {
                if (graph.isEdge(u, v) && !newGraph.isEdge(u, v)) {
                    newGraph.addEdge(u, v);
                }
            }
        }
        return newGraph;
    }

    private Graph removeNode(int node, Graph graph) {
        Set<Integer> nodesToKeep = new HashSet<>(graph.getNodes());
        nodesToKeep.remove(node);
        nodesToKeep.removeAll(graph.getAdjacentNodes(node));

        Graph newGraph = new Graph(nodesToKeep);
        for (int u : newGraph.getNodes()) {
            for (int v : newGraph.getNodes()) {
                if (graph.isEdge(u, v) && !newGraph.isEdge(u, v)) {
                    newGraph.addEdge(u, v);
                }
            }
        }
        return newGraph;
    }

    private void updateNeighbours(Graph graph, int node, ArrayList<Integer> nodes) {
        for (int u : nodes) {
            if (graph.getNodes().contains(u) && !graph.isEdge(node, u)) {
                graph.addEdge(node, u);
            }
        }
    }

    private void findOrderForDeg4(Graph graph, ArrayList<Integer> nodes) {
        boolean inOrder = false;
        while (!inOrder) {
            int first = nodes.get(0);
            int second = nodes.get(1);
            int third = nodes.get(2);
            int fourth = nodes.get(3);

            if (graph.isEdge(first, third)) {
                if (graph.isEdge(second, fourth)) {
                    inOrder = true;
                } else if ((graph.isEdge(second, first) && graph.isEdge(third, fourth)) ||
                        (graph.isEdge(first, fourth) && graph.isEdge(second, third))) {
                    inOrder = true;
                }
            }
            if (!inOrder) {
                nodes.add(first);
                nodes.remove(0);
            }
        }
    }

    private void findOrderForDeg5(Graph graph, ArrayList<Integer> nodes) {
        boolean inOrder = false;
        while (!inOrder) {

            if (nodes == null) {
                System.out.println("node list is null????");
            }

            if (!graph.isEdge(nodes.get(0), nodes.get(2))) {
                inOrder = true;
            } else {
                nodes.add(nodes.get(0));
                nodes.remove(0);
            }
        }
    }
}
