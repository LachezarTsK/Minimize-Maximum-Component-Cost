
function minCost(numberOfNodes: number, edges: number[][], maxNumberOfConnectedComponents: number): number {
    if (edges.length === 0 || maxNumberOfConnectedComponents === numberOfNodes) {
        return Util.NO_COST;
    }
    return findMinimizedMaxComponentCost(numberOfNodes, edges, maxNumberOfConnectedComponents);
};

function findMinimizedMaxComponentCost(numberOfNodes: number, edges: number[][], maxNumberOfConnectedComponents: number): number {
    let minCost = Util.NO_COST;
    let maxCost = findMaxCost(edges);
    let minimizedMaxComponentCost = Util.INFINITE_COST;

    while (minCost <= maxCost) {
        const targetCost = minCost + Math.floor((maxCost - minCost) / 2);
        const numberOfConnectedComponents = connectNodesAtEdgesNotExceedingTargetCost(targetCost, numberOfNodes, edges);

        if (numberOfConnectedComponents <= maxNumberOfConnectedComponents) {
            minimizedMaxComponentCost = targetCost;
            maxCost = targetCost - 1;
        } else {
            minCost = targetCost + 1;
        }
    }

    return minimizedMaxComponentCost;
}


function findMaxCost(edges: number[][]): number {
    let maxCost = 0;
    for (let edge of edges) {
        const cost = edge[2];
        maxCost = Math.max(maxCost, cost);
    }
    return maxCost;
}

function connectNodesAtEdgesNotExceedingTargetCost(targetCost: number, numberOfNodes: number, edges: number[][]): number {
    const unionFind = new UnionFind(numberOfNodes);
    for (let [nodeOne, nodeTwo, cost] of edges) {
        if (cost <= targetCost) {
            unionFind.joinByRank(nodeOne, nodeTwo);
        }
    }
    return unionFind.getNumberOfConnectedComponents();
}

class Util {
    static NO_COST = 0;
    static INFINITE_COST = Number.MAX_SAFE_INTEGER;
}

class UnionFind {

    private rank: number[];
    private parent: number[];
    private numberOfConnectedComponents: number;

    constructor(numberOfNodes: number) {
        this.rank = new Array(numberOfNodes).fill(1);
        this.parent = Array.from(new Array(numberOfNodes).keys());
        this.numberOfConnectedComponents = numberOfNodes;
    }

    findParent(index: number): number {
        if (this.parent[index] !== index) {
            this.parent[index] = this.findParent(this.parent[index]);
        }
        return this.parent[index];
    }

    joinByRank(indexOne: number, indexTwo: number): void {
        const first = this.findParent(indexOne);
        const second = this.findParent(indexTwo);
        if (first === second) {
            return;
        }
        --this.numberOfConnectedComponents;

        if (this.rank[first] >= this.rank[second]) {
            this.parent[second] = first;
            this.rank[first] += this.rank[second];
        } else {
            this.parent[first] = second;
            this.rank[second] += this.rank[first];
        }
    }

    getNumberOfConnectedComponents(): number {
        return this.numberOfConnectedComponents;
    }
}
