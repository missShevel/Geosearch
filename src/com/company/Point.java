package com.company;

import static java.lang.Long.valueOf;

public class Point {
    public double latitude;
    public double longitude;
    public String type;
    public String subType;
    public String name;
    public String address;

    public Point(String info){
       String [] infoArr = info.split(";");
       latitude = Double.parseDouble((infoArr[0]).replace(',', '.'));
       longitude = Double.parseDouble((infoArr[1]).replace(',', '.'));
       type = infoArr[2];
       subType = infoArr[3];
       name = infoArr[4];
       address = infoArr[5];
    }

    public double measureDistance(double lat2, double lon2){
        final int R = 6371;
        double lat1 = this.latitude;
        double lon1 = this.longitude;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a) , Math.sqrt(1 - a));
        return R * c;
    }
}
