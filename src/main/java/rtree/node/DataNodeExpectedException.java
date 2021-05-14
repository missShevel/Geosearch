package rtree.node;

public class DataNodeExpectedException extends Exception {
    public DataNodeExpectedException() {
        super("Expected to find data node");
    }
}