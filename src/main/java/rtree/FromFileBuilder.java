package rtree;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class FromFileBuilder {
    public static <TBuilt> List<TBuilt> BuildObjectsFromFileLines(String filePath, FromStringBuilder<TBuilt> builder) {
        LinkedList<TBuilt> list = new LinkedList<>();

        Scanner scanner = null;
        try {
            File file = new File(filePath);
            scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                try {
                    list.add(builder.fromString(line));
                } catch(Exception e) {}
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File wasn't found");
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        return list;
    }

    public abstract static class FromStringBuilder<T> {
        abstract public T fromString(String line) throws Exception;
    }
}
