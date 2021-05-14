package rtree.node;

import org.javatuples.Pair;

import java.util.*;

public class Node {
    List<Node> children;
    private Node parent;
    Rectangle boundingBox;
    private boolean isLeaf;
    private boolean isRoot;
    private int maxChildrenCount;

    protected Node(boolean isLeaf, boolean isRoot, int maxChildrenCount) {
        this.isRoot = isRoot;
        this.isLeaf = isLeaf;
        this.maxChildrenCount = maxChildrenCount;
        this.children = new LinkedList<>();
    }

    protected Node(Rectangle boundingBox, boolean isLeaf, int maxChildrenCount) {
        this.isLeaf = isLeaf;
        this.boundingBox = boundingBox;
        this.maxChildrenCount = maxChildrenCount;
        this.children = new LinkedList<>();
    }

    public static Node CreateRootNode(int maxChildrenCount) {
        return new Node(true, true, maxChildrenCount);
    }

    private static Node CreateLeafNode(Node parent) {
        return new Node(true, false, parent.maxChildrenCount);
    }

    public static Node ChooseNodeWithMinimalAreaIncrease(Node targetNode, List<Node> pickFromNodes) throws Exception {
        double minimalAreaIncrease = Double.MAX_VALUE;
        Node minimalAreaIncreaseNode = null;

        for (Node childNode : pickFromNodes) {
            double areaIncrease = Node.CalculateAreaIncrease(targetNode, childNode);
            if (areaIncrease < minimalAreaIncrease) {
                minimalAreaIncrease = areaIncrease;
                minimalAreaIncreaseNode = childNode;
            } else if (areaIncrease == minimalAreaIncrease) {
                double minimalAreaIncreaseNodeArea = minimalAreaIncreaseNode.GetArea();
                double childNodeArea = childNode.GetArea();

                if (childNodeArea < minimalAreaIncreaseNodeArea) {
                    minimalAreaIncrease = areaIncrease;
                    minimalAreaIncreaseNode = childNode;
                }
            }
        }

        return minimalAreaIncreaseNode;
    }

    public boolean IsLeaf() {
        return isLeaf;
    }

    public void AddChild(Node child) throws Exception {
        children.add(child);
        child.SetParent(this);

        if (children.size() > maxChildrenCount) {
            SplitNode();
        }
        RecalculateBoundingBox();
    }

    public boolean IsIntersects(Rectangle area) {
        return Rectangle.IsIntersect(this.boundingBox, area);
    }

    private void ReplaceChildren(List<Node> newChildren) throws Exception {
        this.children = newChildren;
        for (Node childNode : newChildren) {
            childNode.SetParent(this);
        }
    }

    private void SetParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Returns read-only list
     * <p>
     * As list is an object, we can modify returned list outside (add new children),
     * which is undesirable. To prevent this, we call Collections.unmodifiableList
     * that'll make the list unmodifiable
     */
    public List<Node> GetChildren() {
        return Collections.unmodifiableList(this.children);
    }

    public static double CalculateAreaIncrease(Node first, Node second) throws Exception {
        Rectangle newBoundingBox = Rectangle.CalculateBoundingBox(first.boundingBox, second.boundingBox);
        return newBoundingBox.CalculateArea() - first.boundingBox.CalculateArea();
    }

    public double GetArea() {
        return this.boundingBox.CalculateArea();
    }

    /**
     * Recalculates bounding box of the current node and propagates recalculates to
     * the parent node
     */
    private void RecalculateBoundingBox() throws Exception {
        boolean seen = false;
        Rectangle acc = null;
        for (Node child : this.children) {
            Rectangle box = child.boundingBox;
            if (!seen) {
                seen = true;
                acc = box;
            } else {
                acc = Rectangle.CalculateBoundingBox(acc, box);
            }
        }
        this.boundingBox = (seen ? Optional.of(acc) : Optional.<Rectangle>empty()).get();

        if (!this.isRoot && this.parent != null) {
            this.parent.RecalculateBoundingBox();
        }
    }

    /**
     * Splits data nodes in a leaf node into two them
     */
    private void SplitNode() throws Exception {
        ArrayList<Node> nodesToSeparate = new ArrayList<Node>(children);

        Pair<Integer, Integer> nodesIndexesWithTheGreatestAreaIncrease = ChooseTwoNodesWithGreatestAreaIncreaseWhenCombined(
                nodesToSeparate);

        // create two nodes and add previous two data nodes to them
        Node firstNodeSplit = Node.CreateLeafNode(this);
        Node secondNodeSplit = Node.CreateLeafNode(this);
        ArrayList<Node> splits = new ArrayList<>(2);
        splits.add(firstNodeSplit);
        splits.add(secondNodeSplit);

        firstNodeSplit.AddChild(nodesToSeparate.get(nodesIndexesWithTheGreatestAreaIncrease.getValue0()));
        secondNodeSplit.AddChild(nodesToSeparate.get(nodesIndexesWithTheGreatestAreaIncrease.getValue1()));
        var minIndex = Math.min(nodesIndexesWithTheGreatestAreaIncrease.getValue0(), nodesIndexesWithTheGreatestAreaIncrease.getValue1());
        var maxIndex = Math.max(nodesIndexesWithTheGreatestAreaIncrease.getValue0(), nodesIndexesWithTheGreatestAreaIncrease.getValue1());
        nodesToSeparate.remove(maxIndex);
        nodesToSeparate.remove(minIndex);

        // put remaining children into two nodes
        while (!nodesToSeparate.isEmpty()) {
            int maximalSplitsAreaDifferenceNodeIndex = ChooseNodeWithTheMaximalAreaDifference(nodesToSeparate,
                    splits.get(0), splits.get(1));
            Node nodeToInsert = nodesToSeparate.get(maximalSplitsAreaDifferenceNodeIndex);

            Node splitToInsertInto = Node.ChooseNodeWithMinimalAreaIncrease(nodeToInsert, splits);
            splitToInsertInto.AddChild(nodeToInsert);
            nodesToSeparate.remove(maximalSplitsAreaDifferenceNodeIndex);
        }

        // Delete all children from the node, add two splits
        ReplaceChildren(splits);
        this.isLeaf = false;
    }

    /**
     * @param nodesToChooseFrom
     * @return pair of chosen node's indexes
     */
    private Pair<Integer, Integer> ChooseTwoNodesWithGreatestAreaIncreaseWhenCombined(List<Node> nodesToChooseFrom) throws Exception {
        double maximalAreaIncrease = Double.MIN_VALUE;
        int firstMaximalAreaIncreaseNodeIndex = 0;
        int secondMaximalAreaIncreaseNodeIndex = 0;

        for (int firstChildIndex = 0; firstChildIndex < nodesToChooseFrom.size(); firstChildIndex++) {
            for (int secondChildIndex = 0; secondChildIndex < nodesToChooseFrom.size(); secondChildIndex++) {
                if (firstChildIndex == secondChildIndex) {
                    continue;
                }

                double areaIncrease = Node.CalculateAreaIncrease(nodesToChooseFrom.get(firstChildIndex),
                        nodesToChooseFrom.get(secondChildIndex));

                if (areaIncrease > maximalAreaIncrease) {
                    maximalAreaIncrease = areaIncrease;
                    firstMaximalAreaIncreaseNodeIndex = firstChildIndex;
                    secondMaximalAreaIncreaseNodeIndex = secondChildIndex;
                }
            }
        }
        return new Pair<Integer, Integer>(firstMaximalAreaIncreaseNodeIndex, secondMaximalAreaIncreaseNodeIndex);
    }

    /**
     * Given two nodes and a list of nodes to choose from, it'll return a node from
     * `nodesToChooseFrom` that has greatest difference between AREA INCREASES when
     * combined with the first and then second node
     *
     * @param nodesToChooseFrom
     * @return
     */
    private int ChooseNodeWithTheMaximalAreaDifference(List<Node> nodesToChooseFrom, Node firstNode, Node secondNode) throws Exception {
        // pick next nodes to add
        double maximalSplitsAreaDifference = Double.MIN_VALUE;
        int maximalSplitsAreaDifferenceNodeIndex = 0;

        for (int nodeIndex = 0; nodeIndex < nodesToChooseFrom.size(); nodeIndex++) {
            Node node = nodesToChooseFrom.get(nodeIndex);
            double firstSplitAreaDifference = Node.CalculateAreaIncrease(firstNode, node);
            double secondSplitAreaDifference = Node.CalculateAreaIncrease(secondNode, node);

            double splitsAreaDifference = Math.abs(firstSplitAreaDifference - secondSplitAreaDifference);
            if (maximalSplitsAreaDifference < splitsAreaDifference) {
                maximalSplitsAreaDifference = splitsAreaDifference;
                maximalSplitsAreaDifferenceNodeIndex = nodeIndex;
            }
        }

        return maximalSplitsAreaDifferenceNodeIndex;
    }
}