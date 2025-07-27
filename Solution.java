
import java.util.Arrays;
import java.util.stream.IntStream;

public class Solution {

    private static final int NO_COST = 0;
    private static final int INFINITE_COST = Integer.MAX_VALUE;

    public int minCost(int numberOfNodes, int[][] edges, int maxNumberOfConnectedComponents) {
        if (edges.length == 0 || maxNumberOfConnectedComponents == numberOfNodes) {
            return NO_COST;
        }
        return findMinimizedMaxComponentCost(numberOfNodes, edges, maxNumberOfConnectedComponents);
    }

    private int findMinimizedMaxComponentCost(int numberOfNodes, int[][] edges, int maxNumberOfConnectedComponents) {
        int minCost = NO_COST;
        int maxCost = findMaxCost(edges);
        int minimizedMaxComponentCost = INFINITE_COST;

        while (minCost <= maxCost) {
            int targetCost = minCost + (maxCost - minCost) / 2;
            int numberOfConnectedComponents = connectNodesAtEdgesNotExceedingTargetCost(targetCost, numberOfNodes, edges);

            if (numberOfConnectedComponents <= maxNumberOfConnectedComponents) {
                minimizedMaxComponentCost = targetCost;
                maxCost = targetCost - 1;
            } else {
                minCost = targetCost + 1;
            }
        }

        return minimizedMaxComponentCost;
    }

    private int findMaxCost(int[][] edges) {
        int maxCost = 0;
        for (int[] edge : edges) {
            int cost = edge[2];
            maxCost = Math.max(maxCost, cost);
        }
        return maxCost;
    }

    private int connectNodesAtEdgesNotExceedingTargetCost(int targetCost, int numberOfNodes, int[][] edges) {
        UnionFind unionFind = new UnionFind(numberOfNodes);
        for (int[] edge : edges) {

            int nodeOne = edge[0];
            int nodeTwo = edge[1];
            int cost = edge[2];

            if (cost <= targetCost) {
                unionFind.joinByRank(nodeOne, nodeTwo);
            }
        }
        return unionFind.getNumberOfConnectedComponents();
    }
}

class UnionFind {

    private final int[] rank;
    private final int[] parent;
    private int numberOfConnectedComponents;

    UnionFind(int numberOfNodes) {
        rank = new int[numberOfNodes];
        Arrays.fill(rank, 1);
        parent = IntStream.range(0, numberOfNodes).toArray();
        numberOfConnectedComponents = numberOfNodes;
    }

    int findParent(int index) {
        if (parent[index] != index) {
            parent[index] = findParent(parent[index]);
        }
        return parent[index];
    }

    void joinByRank(int indexOne, int indexTwo) {
        int first = findParent(indexOne);
        int second = findParent(indexTwo);
        if (first == second) {
            return;
        }
        --numberOfConnectedComponents;

        if (rank[first] >= rank[second]) {
            parent[second] = first;
            rank[first] += rank[second];
        } else {
            parent[first] = second;
            rank[second] += rank[first];
        }
    }

    int getNumberOfConnectedComponents() {
        return numberOfConnectedComponents;
    }
}
