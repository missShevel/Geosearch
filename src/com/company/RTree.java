package com.company;

import java.util.LinkedList;

public class RTree<T> {

    private final int maxEntries;
    private final int minEntries;
    private final int numDims;

    private final float[] pointDims;

    private Node root;
    private int size;

    public RTree(int M, int m, int D){
        assert (m <= (M/2));
        this.numDims = D;
        this.maxEntries = M;
        this.minEntries = m;
        pointDims = new float[D];
        root = buildRoot(true);

    }

    public RTree(){
        this(50, 2,2);
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


    public void insert(float[] coordinates, float[] dimensions, T entry){
        Item position = new Item(coordinates, dimensions, entry);
        Node L = chooseLeaf(root, position);
        L.children.add(position);
        size++;
        position.parent = L;
        if (L.children.size() > maxEntries){
            Node[] splits = splitNode(L);
            adjustTree(splits[0], splits[1]);
        } else {
            adjustTree(L, null);
        }
    }

    public void insert(float[] coordinates, T item){
        insert(coordinates, pointDims, item);
    }
//Algorithm ChooseLeaf
// Select a leaf node in which to place a new index entry E
    private Node chooseLeaf(Node n, Item position) {
        if(n.leaf){
            return n;
        }
        float minimalEnlargement = Float.MAX_VALUE;
        Node next = null;
        for(Node c : n.children) {
            float enlargement = getRequiredSpace(c.coordinates, c.dimensions, position);
            if(enlargement < minimalEnlargement) {
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

    private float getRequiredSpace(float[] coordinates, float[] dimensions, Node n){

    }

    private Node[] splitNode(Node l) {
return
    }

    private void adjustTree(Node split, Node split1) {

    }




}
