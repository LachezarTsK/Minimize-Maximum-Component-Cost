
package main
import "math"

const NO_COST = 0
const INFINITE_COST = math.MaxInt

func minCost(numberOfNodes int, edges [][]int, maxNumberOfConnectedComponents int) int {
    if len(edges) == 0 || maxNumberOfConnectedComponents == numberOfNodes {
        return NO_COST
    }
    return findMinimizedMaxComponentCost(numberOfNodes, edges, maxNumberOfConnectedComponents)
}

func findMinimizedMaxComponentCost(numberOfNodes int, edges [][]int, maxNumberOfConnectedComponents int) int {
    minCost := NO_COST
    maxCost := findMaxCost(edges)
    minimizedMaxComponentCost := INFINITE_COST

    for minCost <= maxCost {
        targetCost := minCost + (maxCost - minCost)/2
        numberOfConnectedComponents := connectNodesAtEdgesNotExceedingTargetCost(targetCost, numberOfNodes, edges)

        if numberOfConnectedComponents <= maxNumberOfConnectedComponents {
            minimizedMaxComponentCost = targetCost
            maxCost = targetCost - 1
        } else {
            minCost = targetCost + 1
        }
    }

    return minimizedMaxComponentCost
}

func findMaxCost(edges [][]int) int {
    maxCost := 0
    for _, edge := range edges {
        cost := edge[2]
        maxCost = max(maxCost, cost)
    }
    return maxCost
}

func connectNodesAtEdgesNotExceedingTargetCost(targetCost int, numberOfNodes int, edges [][]int) int {
    unionFind := NewUnionFind(numberOfNodes)
    for _, edge := range edges {
        nodeOne := edge[0]
        nodeTwo := edge[1]
        cost := edge[2]
        if cost <= targetCost {
            unionFind.joinByRank(nodeOne, nodeTwo)
        }
    }
    return unionFind.getNumberOfConnectedComponents()
}

type UnionFind struct {
    rank                        []int
    parent                      []int
    numberOfConnectedComponents int
}

func NewUnionFind(numberOfNodes int) UnionFind {
    unionFind := UnionFind{
        rank:                        make([]int, numberOfNodes),
        parent:                      make([]int, numberOfNodes),
        numberOfConnectedComponents: numberOfNodes,
    }

    for i := range numberOfNodes {
        unionFind.rank[i] = 1
        unionFind.parent[i] = i
    }

    return unionFind
}

func (this *UnionFind) findParent(index int) int {
    if this.parent[index] != index {
        this.parent[index] = this.findParent(this.parent[index])
    }
    return this.parent[index]
}

func (this *UnionFind) joinByRank(indexOne int, indexTwo int) {
    first := this.findParent(indexOne)
    second := this.findParent(indexTwo)
    if first == second {
        return
    }
    this.numberOfConnectedComponents--

    if this.rank[first] >= this.rank[second] {
        this.parent[second] = first
        this.rank[first] += this.rank[second]
    } else {
        this.parent[first] = second
        this.rank[second] += this.rank[first]
    }
}

func (this *UnionFind) getNumberOfConnectedComponents() int {
    return this.numberOfConnectedComponents
}
