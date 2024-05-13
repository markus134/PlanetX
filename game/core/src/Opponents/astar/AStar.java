package Opponents.astar;


import java.util.*;

/**
 * A Star Algorithm
 *
 * @author Marcelo Surriabre
 * @version 2.1, 2017-02-23
 */
public class AStar {
    private static final int DEFAULT_HV_COST = 2; // Horizontal - Vertical Cost
    private static final int DEFAULT_DIAGONAL_COST = 2;
    private int hvCost;
    private int diagonalCost;
    private Node[][] searchArea;
    private PriorityQueue<Node> openList;
    private Set<Node> closedSet;
    private Node initialNode;
    private Node finalNode;

    /**
     * Constructor for A*. You can specify hvCost and diagonalCost here.
     * @param rows
     * @param cols
     * @param initialNode
     * @param finalNode
     * @param hvCost
     * @param diagonalCost
     */
    public AStar(int rows, int cols, Node initialNode, Node finalNode, int hvCost, int diagonalCost) {
        this.hvCost = hvCost;
        this.diagonalCost = diagonalCost;
        setInitialNode(initialNode);
        setFinalNode(finalNode);
        this.searchArea = new Node[rows][cols];
        this.openList = new PriorityQueue<>(Comparator.comparingInt(Node::getF));
        setNodes();
        this.closedSet = new HashSet<>();
    }

    /**
     * Constructor for A*. Default costs are used.
     * @param rows
     * @param cols
     * @param initialNode
     * @param finalNode
     */
    public AStar(int rows, int cols, Node initialNode, Node finalNode) {
        this(rows, cols, initialNode, finalNode, DEFAULT_HV_COST, DEFAULT_DIAGONAL_COST);
    }

    /**
     * Set nodes.
     */
    private void setNodes() {
        for (int i = 0; i < searchArea.length; i++) {
            for (int j = 0; j < searchArea[0].length; j++) {
                Node node = new Node(i, j);
                node.calculateHeuristic(getFinalNode());
                this.searchArea[i][j] = node;
            }
        }
    }

    /**
     * Set blocks from map.
     * @param map
     */
    public void setBlocks(int[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 1) {
                    setBlock(i, j);
                }
            }
        }
    }

    /**
     * Find an optimal path.
     * @return optimal path if found, otherwise empty list
     */
    public List<Node> findPath() {
        openList.add(initialNode);
        while (!isEmpty(openList)) {
            Node currentNode = openList.poll();
            closedSet.add(currentNode);
            if (isFinalNode(currentNode)) {
                return getPath(currentNode);
            } else {
                addAdjacentNodes(currentNode);
            }
        }
        return new ArrayList<>();
    }

    /**
     * Get path
     * @param currentNode
     * @return path
     */
    private List<Node> getPath(Node currentNode) {
        List<Node> path = new ArrayList<>();
        path.add(currentNode);
        Node parent;
        while ((parent = currentNode.getParent()) != null) {
            path.add(0, parent);
            currentNode = parent;
        }
        return path;
    }

    /**
     * Add adjacent nodes.
     * @param currentNode
     */
    private void addAdjacentNodes(Node currentNode) {
        addAdjacentUpperRow(currentNode);
        addAdjacentMiddleRow(currentNode);
        addAdjacentLowerRow(currentNode);
    }

    /**
     * Add adjacent lower row.
     * @param currentNode
     */
    private void addAdjacentLowerRow(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int lowerRow = row + 1;
        if (lowerRow < getSearchArea().length) {
//            if (col - 1 >= 0) {
//                checkNode(currentNode, col - 1, lowerRow, getDiagonalCost()); // Comment this line if diagonal movements are not allowed
//            }
//            if (col + 1 < getSearchArea()[0].length) {
//                checkNode(currentNode, col + 1, lowerRow, getDiagonalCost()); // Comment this line if diagonal movements are not allowed
//            }
            checkNode(currentNode, col, lowerRow, getHvCost());
        }
    }

    /**
     * Add adjacent middle row.
     * @param currentNode
     */
    private void addAdjacentMiddleRow(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int middleRow = row;
        if (col - 1 >= 0) {
            checkNode(currentNode, col - 1, middleRow, getHvCost());
        }
        if (col + 1 < getSearchArea()[0].length) {
            checkNode(currentNode, col + 1, middleRow, getHvCost());
        }
    }

    /**
     * Add adjacent upper row.
     * @param currentNode
     */
    private void addAdjacentUpperRow(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int upperRow = row - 1;
        if (upperRow >= 0) {
//            if (col - 1 >= 0) {
//                checkNode(currentNode, col - 1, upperRow, getDiagonalCost()); // Comment this if diagonal movements are not allowed
//            }
//            if (col + 1 < getSearchArea()[0].length) {
//                checkNode(currentNode, col + 1, upperRow, getDiagonalCost()); // Comment this if diagonal movements are not allowed
//            }
            checkNode(currentNode, col, upperRow, getHvCost());
        }
    }

    /**
     * Check if node is not a block and we haven't already viewed this node.
     * @param currentNode
     * @param col
     * @param row
     * @param cost
     */
    private void checkNode(Node currentNode, int col, int row, int cost) {
        Node adjacentNode = getSearchArea()[row][col];
        //System.out.println("checking node " + currentNode + ", blocked: " + adjacentNode.isBlock());
        if (!adjacentNode.isBlock() && !getClosedSet().contains(adjacentNode)) {
            if (!getOpenList().contains(adjacentNode)) {
                adjacentNode.setNodeData(currentNode, cost);
                getOpenList().add(adjacentNode);
            } else {
                boolean changed = adjacentNode.checkBetterPath(currentNode, cost);
                if (changed) {
                    // Remove and Add the changed node, so that the PriorityQueue can sort again its
                    // contents with the modified "finalCost" value of the modified node
                    getOpenList().remove(adjacentNode);
                    getOpenList().add(adjacentNode);
                }
            }
        }
    }

    /**
     * Check if currentNode is at the end
     * @param currentNode
     * @return true if current node is at the end, otherwise false
     */
    private boolean isFinalNode(Node currentNode) {
        return currentNode.equals(finalNode);
    }

    /**
     * Check if open list is empty,
     * @param openList
     * @return whether open list is empty
     */
    private boolean isEmpty(PriorityQueue<Node> openList) {
        return openList.isEmpty();
    }

    /**
     * Set node as a block.
     * @param row
     * @param col
     */
    private void setBlock(int row, int col) {
        this.searchArea[row][col].setBlock(true);
    }

    /**
     * Get initial node.
     * @return initial node
     */
    public Node getInitialNode() {
        return initialNode;
    }

    /**
     * Set initial node.
     * @param initialNode
     */
    public void setInitialNode(Node initialNode) {
        this.initialNode = initialNode;
    }

    /**
     * Get final node.
     * @return final node
     */
    public Node getFinalNode() {
        return finalNode;
    }

    /**
     * Set final node.
     * @param finalNode
     */
    public void setFinalNode(Node finalNode) {
        this.finalNode = finalNode;
    }

    /**
     * Get search area.
     * @return search area
     */
    public Node[][] getSearchArea() {
        return searchArea;
    }

    /**
     * Set search area.
     * @param searchArea
     */
    public void setSearchArea(Node[][] searchArea) {
        this.searchArea = searchArea;
    }

    /**
     * Get open list.
     * @return open list
     */
    public PriorityQueue<Node> getOpenList() {
        return openList;
    }

    /**
     * Set open list.
     * @param openList
     */
    public void setOpenList(PriorityQueue<Node> openList) {
        this.openList = openList;
    }

    /**
     * Get closed set.
     * @return closed set
     */
    public Set<Node> getClosedSet() {
        return closedSet;
    }

    /**
     * Set closed set
     * @param closedSet
     */
    public void setClosedSet(Set<Node> closedSet) {
        this.closedSet = closedSet;
    }

    /**
     * Get horizontal and vertical cost.
     * @return horizontal and vertical cost
     */
    public int getHvCost() {
        return hvCost;
    }

    /**
     * Set horizontal and vertical cost.
     * @param hvCost
     */
    public void setHvCost(int hvCost) {
        this.hvCost = hvCost;
    }

    /**
     * Get diagonal cost.
     * @return diagonal cost
     */
    private int getDiagonalCost() {
        return diagonalCost;
    }

    /**
     * Set diagonal cost.
     * @param diagonalCost
     */
    private void setDiagonalCost(int diagonalCost) {
        this.diagonalCost = diagonalCost;
    }
}
