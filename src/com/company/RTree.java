package com.company;

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
        Node[] elements = quadraticElementCollect(cc);
        resultNodes[0].children.add(elements[0]);
        resultNodes[1].children.add(elements[1]);
        tighten(resultNodes);
        while (!cc.isEmpty()) {
            if ((resultNodes[0].children.size() >= minEntries)
                    && (resultNodes[1].children.size() + cc.size() == minEntries)) {
                resultNodes[1].children.addAll(cc);
                cc.clear();
                //check if required
                tighten(resultNodes);
                return resultNodes;
            }
            Node nextContainer = quadraticContainerPicker(cc);
            Node optimal;
            float e0 = getRequiredEnlargement(resultNodes[0].coordinates, resultNodes[0].dimensions, nextContainer);
            float e1 = getRequiredEnlargement(resultNodes[1].coordinates, resultNodes[1].dimensions, nextContainer);
            if (e0 < e1) {
                optimal = resultNodes[0];
            } else if (e0 > e1) {
                optimal = resultNodes[1];
            } else {
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
            optimal.children.add(nextContainer);
            tighten(optimal);

        }
        return resultNodes;
    }

    private Node[] quadraticElementCollect(LinkedList<Node> fromNodes) {
    }

    private Node quadraticContainerPicker(LinkedList<Node> containers) {
    }

    private void tighten(Node... n) {


    }

    private void adjustTree(Node split, Node split1) {

    }


}
