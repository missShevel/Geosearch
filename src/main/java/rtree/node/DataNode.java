package rtree.node;

public class DataNode<TData> extends Node {
    public TData data;

    public DataNode(TData data, Rectangle boundingBox) {
        super(boundingBox, false, 0);
        this.data = data;
    }

    @Override
    public void AddChild(Node child) throws Exception {
        throw new Exception("Can't add children to DataNode");
    }
}
