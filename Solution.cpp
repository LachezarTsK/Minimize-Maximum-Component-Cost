
#include <span>
#include <limits>
#include <vector>
#include <ranges>
#include <algorithm>
using namespace std;

class UnionFind {

    vector<int> rank;
    vector<int> parent;
    int numberOfConnectedComponents;

public:
    UnionFind(int numberOfNodes) {
        rank.assign(numberOfNodes, 1);
        parent.resize(numberOfNodes);
        ranges::iota(parent, 0);
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
        }
        else {
            parent[first] = second;
            rank[second] += rank[first];
        }
    }

    int getNumberOfConnectedComponents() const {
        return numberOfConnectedComponents;
    }
};

class Solution {

    static const int NO_COST = 0;
    static const int INFINITE_COST = numeric_limits<int>::max();

public:
    int minCost(int numberOfNodes, vector<vector<int>>& edges, int maxNumberOfConnectedComponents) const {
            if (edges.size() == 0 || maxNumberOfConnectedComponents == numberOfNodes) {
                return NO_COST;
            }
            return findMinimizedMaxComponentCost(numberOfNodes, edges, maxNumberOfConnectedComponents);
    }

private:
    int findMinimizedMaxComponentCost(int numberOfNodes, span<const vector<int>> edges, int maxNumberOfConnectedComponents) const {
        int minCost = NO_COST;
        int maxCost = findMaxCost(edges);
        int minimizedMaxComponentCost = INFINITE_COST;

        while (minCost <= maxCost) {
            int targetCost = minCost + (maxCost - minCost) / 2;
            int numberOfConnectedComponents = connectNodesAtEdgesNotExceedingTargetCost(targetCost, numberOfNodes, edges);

            if (numberOfConnectedComponents <= maxNumberOfConnectedComponents) {
                minimizedMaxComponentCost = targetCost;
                maxCost = targetCost - 1;
            }
            else {
                minCost = targetCost + 1;
            }
        }

        return minimizedMaxComponentCost;
    }

    int findMaxCost(span<const vector<int>> edges) const {
        int maxCost = 0;
        for (const auto& edge : edges) {
            int cost = edge[2];
            maxCost = max(maxCost, cost);
        }
        return maxCost;
    }

    int connectNodesAtEdgesNotExceedingTargetCost(int targetCost, int numberOfNodes, span<const vector<int>> edges) const {
        UnionFind unionFind(numberOfNodes);
        for (const auto& edge : edges) {

            int nodeOne = edge[0];
            int nodeTwo = edge[1];
            int cost = edge[2];

            if (cost <= targetCost) {
                unionFind.joinByRank(nodeOne, nodeTwo);
            }
        }
        return unionFind.getNumberOfConnectedComponents();
    }
};
