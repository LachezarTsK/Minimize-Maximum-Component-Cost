
using System;
using System.Linq;

public class Solution
{
    private static readonly int NO_COST = 0;
    private static readonly int INFINITE_COST = int.MaxValue;

    public int MinCost(int numberOfNodes, int[][] edges, int maxNumberOfConnectedComponents)
    {
        if (edges.Length == 0 || maxNumberOfConnectedComponents == numberOfNodes)
        {
            return NO_COST;
        }
        return FindMinimizedMaxComponentCost(numberOfNodes, edges, maxNumberOfConnectedComponents);
    }

    private int FindMinimizedMaxComponentCost(int numberOfNodes, int[][] edges, int maxNumberOfConnectedComponents)
    {
        int minCost = NO_COST;
        int maxCost = FindMaxCost(edges);
        int minimizedMaxComponentCost = INFINITE_COST;

        while (minCost <= maxCost)
        {
            int targetCost = minCost + (maxCost - minCost) / 2;
            int numberOfConnectedComponents = ConnectNodesAtEdgesNotExceedingTargetCost(targetCost, numberOfNodes, edges);

            if (numberOfConnectedComponents <= maxNumberOfConnectedComponents)
            {
                minimizedMaxComponentCost = targetCost;
                maxCost = targetCost - 1;
            }
            else
            {
                minCost = targetCost + 1;
            }
        }

        return minimizedMaxComponentCost;
    }

    private int FindMaxCost(int[][] edges)
    {
        int maxCost = 0;
        foreach (int[] edge in edges)
        {
            int cost = edge[2];
            maxCost = Math.Max(maxCost, cost);
        }
        return maxCost;
    }

    private int ConnectNodesAtEdgesNotExceedingTargetCost(int targetCost, int numberOfNodes, int[][] edges)
    {
        UnionFind unionFind = new UnionFind(numberOfNodes);
        foreach (int[] edge in edges)
        {

            int nodeOne = edge[0];
            int nodeTwo = edge[1];
            int cost = edge[2];

            if (cost <= targetCost)
            {
                unionFind.JoinByRank(nodeOne, nodeTwo);
            }
        }
        return unionFind.GetNumberOfConnectedComponents();
    }
}

class UnionFind
{
    private readonly int[] rank;
    private readonly int[] parent;
    private int numberOfConnectedComponents;

    public UnionFind(int numberOfNodes)
    {
        rank = new int[numberOfNodes];
        Array.Fill(rank, 1);
        parent = Enumerable.Range(0, numberOfNodes).ToArray();
        numberOfConnectedComponents = numberOfNodes;
    }

    public int FindParent(int index)
    {
        if (parent[index] != index)
        {
            parent[index] = FindParent(parent[index]);
        }
        return parent[index];
    }

    public void JoinByRank(int indexOne, int indexTwo)
    {
        int first = FindParent(indexOne);
        int second = FindParent(indexTwo);
        if (first == second)
        {
            return;
        }
        --numberOfConnectedComponents;

        if (rank[first] >= rank[second])
        {
            parent[second] = first;
            rank[first] += rank[second];
        }
        else
        {
            parent[first] = second;
            rank[second] += rank[first];
        }
    }

    public int GetNumberOfConnectedComponents()
    {
        return numberOfConnectedComponents;
    }
}
