package com.company;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class Main {

    public static void main(String[] args) {
    String data = "";
        try {
            File myObj = new File(/*"/Users/user/Desktop/Geosearch/src/com/company/ukraine_poi.csv"*/);
            Scanner myReader = new Scanner(myObj);
            if (myReader.hasNextLine()) {
               data = myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        Point d = new Point (data);
        System.out.println(d.measureDistance(50.44075, 30.55263 ));
	// write your code here
    }



}
