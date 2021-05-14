package com.company;

import java.util.LinkedList;

 class Node {
    ///coordinates of the angle point of a rectangle and dimensions of its sides - characteristics of a MBR, which is inserted into the node
    //children - array of node entries (when node is not a leaf)
    //At the start, every node is created as a leaf node
    final float[] coordinates;
    final float[] dimensions;
    final LinkedList<Node> children;
    final boolean leaf;

   Node parent;

     Node(float[] coordinates, float[] dimensions, boolean leaf) {
        this.coordinates = new float [coordinates.length];
        this.dimensions = new float [dimensions.length];
        System.arraycopy(coordinates, 0, this.coordinates, 0, coordinates.length);
        System.arraycopy(dimensions, 0, this.dimensions, 0, dimensions.length);
        this.leaf = leaf;
        children = new LinkedList<Node>();
    }

}

class Item<T> extends Node{
    final T item;

    public Item(float[] coordinates, float[] dimensions, T item){
        super(coordinates, dimensions, true);
        this.item = item;
    }

}
