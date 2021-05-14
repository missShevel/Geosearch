package rtree.node;

public class Building {
    String type;
    String subtype;
    String name;

    public Building(String type, String subtype, String name) {
        this.type = type;
        this.subtype = subtype;
        this.name = name;
    }
    public String getType(){
        return this.type;
    }
    public String getName() {
        return this.name;
    }

    public String getAddress(){
        return this.subtype;
    }
}
