package rtree.node;

public class Point {
    // Don't want user to be able to change coordinates - point is immutable, it'll
    // prevent tricky bugs
    double X;
    double Y;

    public Point(double x, double y) {
        X = x;
        Y = y;
    }

    public double GetX() {
        return X;
    }

    public double GetY() {
        return Y;
    }
}