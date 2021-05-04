package com.company;

import java.util.LinkedList;

public class RTree<Point> {

    private final int maxEntries;
    private final int minEntries;
    private final int numDims;

    private Node root;
    private int size;

    public RTree(int M, int m, int D){
        assert (m <= (M/2));
        this.numDims = D;
        this.maxEntries = M;
        this.minEntries = m;
        root = buildRoot(true);

    }

    public RTree(){
        this(50, 2,2);
    }

    private Node buildRoot(boolean isLeaf){

    }

    private class Node{
        ///coordinates of the angle point of a rectangle and dimensions of its sides - characteristics of a MBR, which is inserted into the node
        //children - array of node entries (when node is not a leaf)
        //At the start, every node is created as a leaf node
        final float[] coordinates;
        final float[] dimensions;
        final LinkedList<Node> children;
        final boolean leaf;

        Node parent;

        private Node(float[] coordinates, float[] dimensions, boolean leaf) {
            this.coordinates = new float [coordinates.length];
            this.dimensions = new float [dimensions.length];
            System.arraycopy(coordinates, 0, this.coordinates, 0, coordinates.length);
            System.arraycopy(dimensions, 0, this.dimensions, 0, dimensions.length);
            this.leaf = leaf;
            children = new LinkedList<Node>();
        }
    }


}
