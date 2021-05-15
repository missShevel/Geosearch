package rtree.node;

public class Building {
    double latitude;
    double longitude;
    String type;
    String subtype;
    String name;

    public Building(double latitude, double longitude, String type, String subtype, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.subtype = subtype;
        this.name = name;
    }
    public Building(){
        this.latitude = 0;
        this.longitude = 0;
        this.type = "";
        this.subtype = "";
        this.name = "";
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
    public double getLat(){return this.latitude; }
    public double getLon(){return this.longitude; }

}
