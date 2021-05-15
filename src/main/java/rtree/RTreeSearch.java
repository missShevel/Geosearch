package rtree;
import rtree.node.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RTreeSearch {
    public static void main(String[] args) throws Exception {

        String db = args[0].substring(args[0].indexOf("=") + 1);
        double lat = Double.parseDouble(args[1].substring(args[1].indexOf("=") + 1) );
        double loong = Double.parseDouble(args[2].substring(args[2].indexOf("=") + 1)) ;
        double size = Double.parseDouble(args[3].substring(args[3].indexOf("=") + 1)) ;
        String type = args[4].substring(args[4].indexOf("=") + 1);
        // String command = args[0];
        // String path = args[1];
        BuildTree(db);
        SearchTree(lat, loong, size, type);
        System.out.println();

    }

    private static RTree tree = null;

    public static void BuildTree(String db) throws Exception {
        // TODO replace with arg from command line, won't work with jar file
        // https://mkyong.com/java/java-read-a-file-from-resources-folder/
        NodeBuilder builder = new NodeBuilder();

        List<DataNode> nodes = FromFileBuilder.BuildObjectsFromFileLines(/*"./ukraine_min.csv"*/db, builder);
        tree = new RTree();

        for (DataNode node : nodes) {
            tree.Insert(node);
        }

    }

    public static void SearchTree(double x, double y, double size, String type) throws Exception {
        // var found = tree.SearchForBuildingsInArea(new Rectangle(new Point(50.60659, 30.45436), 0.1, 0.1));
          var found = tree.SearchForBuildingsInArea(Rectangle.searchRectangle(new Point(x, y), size),type);
        findTheClosestBuilding(found, x, y);

         printFoundObjects(found);
    }

    public static void printFoundObjects(List<Building> foundObjects){
        int listNumber = 1;
        for (Building b : foundObjects){
            if(!b.getName().isEmpty()) {
                System.out.println(listNumber + ". " + b.getName() + "[" + b.getAddress()+"]");
                listNumber++;
            }
        }
    }

    public static void findTheClosestBuilding(List<Building> all, double lat, double lon){
        double minDistance = Double.MAX_VALUE;
        Building closest = new Building();
        for (Building b : all){
            double  d =  Math.sqrt(Math.pow(((lat - b.getLat())*111.321),2) + Math.pow(((lon - b.getLon())*Math.cos(lon )),2));
          if (d < minDistance){
              minDistance = d;
              closest = b;
          }
        }
        System.out.println("The closest building of this type is: " + closest.getName());
        System.out.println("Distance is: " + minDistance + "km");
    }

}

