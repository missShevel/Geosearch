package com.company;

import java.util.Arrays;
import java.util.LinkedList;

public class RTree<T> {

    private final int maxEntries;
    private final int minEntries;
    private final int numDims;

    private final float[] pointDims;

    private Node root;
    private int size;

    public RTree(int M, int m, int D) {
        assert (m <= (M / 2));
        this.numDims = D;
        this.maxEntries = M;
        this.minEntries = m;
        pointDims = new float[D];
        root = buildRoot(true);

    }

    public RTree() {
        this(50, 2, 2);
    }

    private Node buildRoot(boolean isLeaf) {
        float[] initCoords = new float[2];
        float[] initDimensions = new float[2];
        for (int i = 0; i < 2; i++) {
            initCoords[i] = (float) Math.sqrt(Float.MAX_VALUE);
            initDimensions[i] = -2.0f * (float) Math.sqrt(Float.MAX_VALUE);
        }
        return new Node(initCoords, initDimensions, isLeaf);
    }


    public void insert(float[] coordinates, float[] dimensions, T entry) {
        Item position = new Item(coordinates, dimensions, entry);
        Node L = chooseLeaf(root, position);
        L.children.add(position);
        size++;
        position.parent = L;
        if (L.children.size() > maxEntries) {
            Node[] splits = splitNode(L);
            adjustTree(splits[0], splits[1]);
        } else {
            adjustTree(L, null);
        }
    }

    public void insert(float[] coordinates, T item) {
        insert(coordinates, pointDims, item);
    }

    //Algorithm ChooseLeaf
// Select a leaf node in which to place a new index entry E
    private Node chooseLeaf(Node n, Item position) {
        if (n.leaf) {
            return n;
        }
        float minimalEnlargement = Float.MAX_VALUE;
        Node next = null;
        for (Node c : n.children) {
            float enlargement = getRequiredEnlargement(c.coordinates, c.dimensions, position);
            if (enlargement < minimalEnlargement) {
                minimalEnlargement = enlargement;
                next = c;
            } else if (enlargement == minimalEnlargement) {
                float currentArea = 1.0f;
                float newArea = 1.0f;
                for (int i = 0; i < c.dimensions.length; i++) {
                    currentArea *= next.dimensions[i];
                    newArea *= c.dimensions[i];
                }
                if (newArea < currentArea) {
                    next = c;
                }
            }
        }
        return chooseLeaf(next, position);
    }

    private float getRequiredEnlargement(float[] coordinates, float[] dimensions, Node n) {
        float area = getArea(dimensions);
        float[] deltas = new float[dimensions.length];
        for (int i = 0; i < dimensions.length; i++) {
            if (coordinates[i] + dimensions[i] < n.coordinates[i] + n.dimensions[i]) {
                deltas[i] = n.coordinates[i] + n.dimensions[i] - coordinates[i] - dimensions[i];
            } else if (coordinates[i] + dimensions[i] > n.coordinates[i] + n.dimensions[i]) {
                deltas[i] = coordinates[i] - n.coordinates[i];
            }
        }
        float expanded = 1.0f;
        for (int i = 0; i < dimensions.length; i++) {
            expanded *= dimensions[i] + deltas[i];
        }
        return (expanded - area);
    }

    private float getArea(float[] dimensions) {
        return dimensions[0] * dimensions[1];
    }

    private Node[] splitNode(Node l) {
        //What is it??
        Node[] resultNodes = new Node[]{l, new Node(l.coordinates, l.dimensions, l.leaf)};
        resultNodes[1].parent = l.parent;
        //Is it a black magic?
        if (resultNodes[1].parent != null) {
            resultNodes[1].parent.children.add(resultNodes[1]);
        }
        LinkedList<Node> cc = new LinkedList<Node>(l.children);
        l.children.clear();
        Node[] elements = pickSeeds(cc);
        resultNodes[0].children.add(elements[0]);
        resultNodes[1].children.add(elements[1]);
        tighten(resultNodes);
        while (!cc.isEmpty()) {
            //If all entries have
            //been assigned, stop If one group has
            //so few entries that all the rest must
            //be assigned to it in order for it to
            //have the minimum number m, assign
            //them and stop
            if ((resultNodes[0].children.size() >= minEntries)
                    && (resultNodes[1].children.size() + cc.size() == minEntries)) {
                resultNodes[1].children.addAll(cc);
                cc.clear();
                //check if required
                tighten(resultNodes);
                return resultNodes;
            }
            Node nextEntry = pickNext(cc, resultNodes);
            Node optimal;
            float e0 = getRequiredEnlargement(resultNodes[0].coordinates, resultNodes[0].dimensions, nextEntry);
            float e1 = getRequiredEnlargement(resultNodes[1].coordinates, resultNodes[1].dimensions, nextEntry);
            //adding the entry to
            //the group with smaller area,
            if (e0 < e1) {
                optimal = resultNodes[0];
            }
            //adding the entry to
            //the group with fewer entries,
            else if (e0 > e1) {
                optimal = resultNodes[1];
            }
            //adding the entry to the either group [??????]
            else {
                float a0 = getArea(resultNodes[0].dimensions);
                float a1 = getArea(resultNodes[1].dimensions);
                if (a0 < a1) {
                    optimal = resultNodes[0];
                    //why e0? is it a typo?
                } else if (/*a0*/ e0 > a1) {
                    optimal = resultNodes[1];
                } else {
                    if (resultNodes[0].children.size() < resultNodes[1].children.size()) {
                        optimal = resultNodes[0];
                    } else if (resultNodes[0].children.size() > resultNodes[1].children.size()) {
                        optimal = resultNodes[1];
                    } else {
                        optimal = resultNodes[(int) Math.round(Math.random())];
                    }
                }
            }
            optimal.children.add(nextEntry);
            tighten(optimal);

        }
        return resultNodes;
    }

    //Algorithm PickSeeds
// Select two entries to be the first elements of the groups
//PSl [Calculate inefficiency of grouping entries together]
// For each pair of entries E1 and E2, compose a rectangle J including E1 I and E2 I. Calculate d = area(J) - area(E1 I) - area(E2 I)
//PS2 [Choose the most wasteful pair ]
//Choose the pair with the largest d
    private Node[] pickSeeds(LinkedList<Node> fromNodes) {
        Node[] elementsToTheGroup = new Node[2];
        float maxWaste = -1.0f * Float.MAX_VALUE;
        for (Node n1 : fromNodes) {
            for (Node n2 : fromNodes) {
                if (n1 == n2) continue;
                float n1area = getArea(n1.dimensions);
                float n2area = getArea(n2.dimensions);
                float Jarea = 1.0f;
                for (int i = 0; i < numDims; i++) {
                    float jc0 = Math.min(n1.coordinates[i], n2.coordinates[i]);
                    float jc1 = Math.max(n1.coordinates[i] + n1.dimensions[i], n2.coordinates[i] + n2.dimensions[i]);
                    Jarea *= (jc1 - jc0);

                }
                float waste = Jarea - n1area - n2area;
                if (waste > maxWaste) {
                    maxWaste = waste;
                    elementsToTheGroup[0] = n1;
                    elementsToTheGroup[1] = n2;

                }
            }
        }
        fromNodes.remove(elementsToTheGroup[0]);
        fromNodes.remove(elementsToTheGroup[1]);
        return elementsToTheGroup;
    }

    // Select one remaining
    //entry for classification in a group.
    private Node pickNext(LinkedList<Node> childsDivided, Node[] possibleCont) {
        //[Determine cost of putting each entry in each group
        float maxd = -1.0f * Float.MAX_VALUE;
        Node nextEntry = null;
        for (Node c : childsDivided) {
            float d1 = getRequiredEnlargement(possibleCont[0].coordinates, possibleCont[0].dimensions, c);
            float d2 = getRequiredEnlargement(possibleCont[1].coordinates, possibleCont[0].dimensions, c);
            float difference = Math.abs(d2 - d1);
            //Find entry with greatest preference for one group
            if (difference > maxd) {
                maxd = difference;
                nextEntry = c;
            }
        }
        assert (nextEntry != null) : "No node was selected";
        childsDivided.remove(nextEntry);
        return nextEntry;

    }

    //підігнати розміри прямокутника до крйніх координат
    private void tighten(Node... n) {
        assert (n.length >= 1) : "Pass some entries to tighten them";
        for (Node entry : n) {
            assert (entry.children.size() > 0) : "tighten() cannot be called for the empty node";
            float[] minCoords = new float[2];
            float[] maxCoords = new float[2];
            for (int i = 0; i < 2; i++) {
                minCoords[i] = Float.MAX_VALUE;
                maxCoords[i] = -1.0f * Float.MAX_VALUE;

                for (Node c : entry.children) {
                    c.parent = entry;
                    if (c.coordinates[i] < minCoords[i]) {
                        minCoords[i] = c.coordinates[i];
                    }
                    if ((c.coordinates[i] + c.dimensions[i]) > maxCoords[i]) {
                        maxCoords[i] = (c.coordinates[i] + c.dimensions[i]);
                    }
                }
            }
            for (int i = 0; i < 2; i++) {
                maxCoords[i] -= minCoords[i];
            }
            System.arraycopy(minCoords, 0, entry.coordinates, 0, numDims);
            System.arraycopy(maxCoords, 0, entry.dimensions, 0, numDims);
        }

    }

    private void adjustTree(Node N, Node NN) {
        if (N == root) {
            if (NN != null) {
                root = buildRoot(false);
                root.children.add(N);
                N.parent = root;
                root.children.add(NN);
                NN.parent = root;
            }
            tighten(root);
            return;
        }
        tighten(N);
        if (NN != null) {
            tighten(NN);
            if (N.parent.children.size() > maxEntries) {
                Node[] splitted = splitNode(N.parent);
                adjustTree(splitted[0], splitted[1]);
            }
        }
        if (N.parent != null) {
            adjustTree(N.parent, null);
        }
    }
}


//tighten & adjustTree = similar things???
