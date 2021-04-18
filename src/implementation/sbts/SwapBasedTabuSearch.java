package implementation.sbts;

import implementation.Graph;

import java.util.*;

public class SwapBasedTabuSearch {

    Graph graph;

    Set<Integer> bestSolution;
    Set<Integer> currentSolution;
    int bestSolutionSize;

    HashMap<Integer, Integer> tabuList;
    HashMap<Integer, Set<Integer>> neighbourhoods;
    HashMap<Integer, Integer> mappingDegrees;
    HashMap<Integer, Integer> expandingDegrees;
    HashMap<Integer, Integer> diversifyingDegrees;

    public SwapBasedTabuSearch(Graph graph) {
        this.graph = graph;
        this.neighbourhoods = new HashMap<Integer, Set<Integer>>() {{
            put(0, new HashSet<>());
            put(1, new HashSet<>());
            put(2, new HashSet<>());
            put(3, new HashSet<>());
        }};
        initializeDegrees();
    }

    private void initializeDegrees() {
        this.mappingDegrees = new HashMap<>();
        this.expandingDegrees = new HashMap<>();
        this.diversifyingDegrees = new HashMap<>();
    }

    public Set<Integer> search(int maxIter) {
        currentSolution = initializeSolution();
        bestSolution = new HashSet<>(currentSolution);
        bestSolutionSize = bestSolution.size();
        computeInitialDegrees(currentSolution);
        computeInitialNeighbourhoods();

        tabuList = new HashMap<>();
        for (int iter = 0; iter <= maxIter; iter++) {
            boolean isIntensified = intensification();
            if (isIntensified) {
                if (currentSolution.size() > bestSolutionSize) {
                    bestSolution = new HashSet<>(currentSolution);
                    bestSolutionSize = bestSolution.size();
                }
            } else {
                diversification();
            }

            updateTabuList();
        }
        return bestSolution;
    }

    private boolean intensification() {
        if (!neighbourhoods.get(0).isEmpty()) {
            swap(getRandomNode(neighbourhoods.get(0)), 0);
            return true;
        } else if (!neighbourhoods.get(1).isEmpty()) {
            List<Integer> nodes = new ArrayList<>(neighbourhoods.get(1));

            HashMap<Integer, ArrayList<Integer>> possibleMoves = new HashMap<>();
            boolean isBigger = neighbourhoods.get(1).size() > neighbourhoods.get(2).size() + neighbourhoods.get(3).size();
            if (isBigger) {
                List<Integer> nodesToRemove = new ArrayList<>(nodes);
                for (int node: nodes) {
                    List<Integer> neighbours = graph.getAdjacentNodes(node);
                    for (int neighbour: neighbours) {
                        if (currentSolution.contains(neighbour) && expandingDegrees.get(neighbour) == 1) {
                            nodesToRemove.add(neighbour);
                        }
                    }
                }
                nodes.removeAll(nodesToRemove);
            }

            for (int node : nodes) {
                List<Integer> neighbours = graph.getAdjacentNodes(node);
                for (int neighbour : neighbours) {
                    if (currentSolution.contains(neighbour)) {
                        int expandingDeg = expandingDegrees.get(neighbour);
                        if (!possibleMoves.containsKey(expandingDeg)) {
                            possibleMoves.put(expandingDeg, new ArrayList<>());
                        }
                        possibleMoves.get(expandingDeg).add(node);
                    }
                }
            }

            if (possibleMoves.keySet().isEmpty()) {
                return false;
            }

            List<Integer> moves = possibleMoves.get(Collections.max(possibleMoves.keySet()));
            if (moves.size() == 1 && !tabuList.containsKey(moves.get(0))) {
                swap(moves.get(0), 1);
            } else {
                int move = -1;
                int maxDiversifyingDeg = -1;
                for (int node : moves) {
                    if (diversifyingDegrees.get(node) > maxDiversifyingDeg && !tabuList.containsKey(node)) {
                        move = node;
                        maxDiversifyingDeg = diversifyingDegrees.get(node);
                    }
                }
                if (move == -1) {
                    return false;
                }
                swap(move, 1);
            }
            return true;
        }
        return false;
    }

    private void diversification() {
        boolean isBigger = neighbourhoods.get(1).size() > neighbourhoods.get(2).size() + neighbourhoods.get(3).size();

        int neighbourhood;
        if (isBigger) {
            neighbourhood = 3;
        } else {
            neighbourhood = new Random().nextInt(2) + 2;
        }

        int move = -1;
        int maxDiversifyingDeg = -1;
        for (int node : neighbourhoods.get(neighbourhood)) {
            if (diversifyingDegrees.get(node) > maxDiversifyingDeg && !tabuList.containsKey(node)) {
                move = node;
                maxDiversifyingDeg = diversifyingDegrees.get(node);
            }
        }
        if (move == -1) {
            return;
        }
        swap(move, neighbourhood);

    }

    private void updateTabuList() {
        HashMap<Integer, Integer> tabuEntries = new HashMap<Integer, Integer>(tabuList);

        for (Map.Entry<Integer, Integer> tabuItem : tabuEntries.entrySet()) {
            if (tabuItem.getValue() - 1 == 0) {
                tabuList.remove(tabuItem.getKey());
            } else {
                tabuList.put(tabuItem.getKey(), tabuItem.getValue() - 1);
            }
        }
    }

    private void swap(int node, int neighbourhood) {
        List<Integer> neighbours = graph.getAdjacentNodes(node);
        currentSolution.add(node);
        expandingDegrees.put(node, 0);
        mappingDegrees.remove(node);
        diversifyingDegrees.remove(node);
        neighbourhoods.get(neighbourhood).remove(node);

        List<Integer> removed = new ArrayList<>();
        int tabuTenure = 0;
        if (neighbourhood == 1) {
            boolean isBigger = neighbourhoods.get(1).size() > neighbourhoods.get(2).size() + neighbourhoods.get(3).size();
            tabuTenure = isBigger ? 10 + new Random().nextInt(neighbourhoods.get(1).size()) : neighbourhoods.get(1).size();
        } else if (neighbourhood > 1) {
            tabuTenure = 7;
        }

        for (int neighbour : neighbours) {
            if (currentSolution.contains(neighbour)) {
                currentSolution.remove(neighbour);
                expandingDegrees.remove(neighbour);
                mappingDegrees.put(neighbour, 1);
                diversifyingDegrees.put(neighbour, 0);
                removed.add(neighbour);
                tabuList.put(neighbour, tabuTenure + 1);
            } else {
                int mappingDeg = mappingDegrees.get(neighbour) + 1;

                if (mappingDeg == 1) {
                    expandingDegrees.put(node, expandingDegrees.get(node) + 1);
                } else if (mappingDeg == 2) {
                    expandingDegrees.put(node, expandingDegrees.get(node) - 1);
                }

                if (mappingDeg < 4) {
                    neighbourhoods.get(mappingDeg - 1).remove(neighbour);
                    neighbourhoods.get(mappingDeg).add(neighbour);
                }
                mappingDegrees.put(neighbour, mappingDeg);
                diversifyingDegrees.put(neighbour, diversifyingDegrees.get(neighbour) - 1);
            }
        }
        updateRemovedNodes(removed);
    }

    private void updateRemovedNodes(List<Integer> nodes) {
        for (int node : nodes) {
            for (int neighbour : graph.getAdjacentNodes(node)) {

                if (!mappingDegrees.containsKey(neighbour) || mappingDegrees.get(neighbour) == null) {
                    continue;
                }

                int mappingDeg = mappingDegrees.get(neighbour) - 1;
                diversifyingDegrees.put(neighbour, diversifyingDegrees.get(neighbour) + 1);
                diversifyingDegrees.put(node, diversifyingDegrees.get(node) + 1);

                mappingDegrees.put(neighbour, mappingDeg);
                if (mappingDeg == 1) {
                    for (int nb : graph.getAdjacentNodes(neighbour)) {
                        if (currentSolution.contains(nb)) {
                            expandingDegrees.put(nb, expandingDegrees.get(nb) + 1);
                        }
                    }
                }
                if (mappingDeg < 3) {
                    neighbourhoods.get(mappingDeg).add(neighbour);
                    neighbourhoods.get(mappingDeg + 1).remove(neighbour);
                }
            }
        }
    }

    private void computeInitialDegrees(Set<Integer> solution) {
        Set<Integer> nodes = new HashSet<>(graph.getNodes());
        nodes.removeAll(solution);

        for (int node : nodes) {
            List<Integer> neighbours = graph.getAdjacentNodes(node);
            int mappingDegree = 0;
            int diversifyingDegree = 0;

            for (int neighbour : neighbours) {
                if (solution.contains(neighbour)) {
                    mappingDegree++;
                } else {
                    diversifyingDegree++;
                }
            }

            mappingDegrees.put(node, mappingDegree);
            diversifyingDegrees.put(node, diversifyingDegree);
        }

        for (int node : solution) {
            int expandingDegree = 0;

            for (int neighbour : graph.getAdjacentNodes(node)) {
                if (nodes.contains(neighbour) && mappingDegrees.get(neighbour) == 1) {
                    expandingDegree++;
                }
            }
            expandingDegrees.put(node, expandingDegree);
        }
    }

    private void computeInitialNeighbourhoods() {
        for (Map.Entry<Integer, Integer> node : mappingDegrees.entrySet()) {
            if (node.getValue() < 3) {
                neighbourhoods.get(node.getValue()).add(node.getKey());
            } else {
                neighbourhoods.get(3).add(node.getKey());
            }
        }
    }

    private Set<Integer> initializeSolution() {
        HashSet<Integer> s = new HashSet<>();

        Set<Integer> keyset = new HashSet<>(graph.getNodes());

        while (!keyset.isEmpty()) {
            int u = getRandomNode(keyset);

            s.add(u);
            keyset.remove(u);
            keyset.removeAll(graph.getAdjacentNodes(u));
        }

        return s;
    }

    private int getRandomNode(Set<Integer> nodeSet) {
        int item = new Random().nextInt(nodeSet.size());
        int i = 0;

        for (int node : nodeSet) {
            if (i == item) {
                return node;
            }
            i++;
        }
        return 1;
    }
}
