
import kotlin.math.max

class Solution {

    private companion object {
        const val NO_COST = 0
        const val INFINITE_COST = Int.MAX_VALUE
    }

    fun minCost(numberOfNodes: Int, edges: Array<IntArray>, maxNumberOfConnectedComponents: Int): Int {
        if (edges.isEmpty() || maxNumberOfConnectedComponents == numberOfNodes) {
            return NO_COST
        }
        return findMinimizedMaxComponentCost(numberOfNodes, edges, maxNumberOfConnectedComponents)
    }

    private fun findMinimizedMaxComponentCost(numberOfNodes: Int, edges: Array<IntArray>, maxNumberOfConnectedComponents: Int): Int {
        var minCost = NO_COST
        var maxCost = findMaxCost(edges)
        var minimizedMaxComponentCost = INFINITE_COST

        while (minCost <= maxCost) {
            val targetCost = minCost + (maxCost - minCost) / 2
            val numberOfConnectedComponents =
                connectNodesAtEdgesNotExceedingTargetCost(targetCost, numberOfNodes, edges)

            if (numberOfConnectedComponents <= maxNumberOfConnectedComponents) {
                minimizedMaxComponentCost = targetCost
                maxCost = targetCost - 1
            } else {
                minCost = targetCost + 1
            }
        }

        return minimizedMaxComponentCost
    }

    private fun findMaxCost(edges: Array<IntArray>): Int {
        var maxCost = 0
        for ((_, _, cost) in edges) {
            maxCost = max(maxCost, cost)
        }
        return maxCost
    }

    private fun connectNodesAtEdgesNotExceedingTargetCost(targetCost: Int, numberOfNodes: Int, edges: Array<IntArray>): Int {
        val unionFind = UnionFind(numberOfNodes)
        for ((nodeOne, nodeTwo, cost) in edges) {
            if (cost <= targetCost) {
                unionFind.joinByRank(nodeOne, nodeTwo)
            }
        }
        return unionFind.getNumberOfConnectedComponents()
    }
}

class UnionFind(private val numberOfNodes: Int) {

    private val rank = IntArray(numberOfNodes) { 1 }
    private val parent = IntArray(numberOfNodes) { i -> i }
    private var numberOfConnectedComponents = numberOfNodes

    fun findParent(index: Int): Int {
        if (parent[index] != index) {
            parent[index] = findParent(parent[index])
        }
        return parent[index]
    }

    fun joinByRank(indexOne: Int, indexTwo: Int) {
        val first = findParent(indexOne)
        val second = findParent(indexTwo)
        if (first == second) {
            return
        }
        --numberOfConnectedComponents

        if (rank[first] >= rank[second]) {
            parent[second] = first
            rank[first] += rank[second]
        } else {
            parent[first] = second
            rank[second] += rank[first]
        }
    }

    fun getNumberOfConnectedComponents(): Int {
        return numberOfConnectedComponents
    }
}
