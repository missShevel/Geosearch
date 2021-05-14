package rtree.node;

import rtree.FromFileBuilder;

public class NodeBuilder extends FromFileBuilder.FromStringBuilder<DataNode> {
    @Override
    public DataNode<Building> fromString(String line) throws Exception {
        String[] parameters = line.split(";");

        double latitude = Double.parseDouble((parameters[0]).replace(',', '.'));
        double longitude = Double.parseDouble((parameters[1]).replace(',', '.'));

        String type = parameters.length >= 3 ? parameters[2] : "";
        String subtype = parameters.length >= 4 ? parameters[3] : "";
        String name = parameters.length >= 5 ? parameters[4] : "";

        Building building = new Building(type, subtype, name);
        Rectangle buildingPosition = Rectangle.FromPoint(new Point(latitude, longitude));
        return new DataNode<Building>(building, buildingPosition);
    }
}
