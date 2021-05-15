package rtree.node;

public class Rectangle {
    private Point topLeftPoint;
    private Point bottomRightPoint;
    private double width;
    private double height;

    public Rectangle(Point topLeftPoint, double width, double height) throws Exception {
        if (width < 0 || height < 0) {
            // You'd better create custom exceptions, because they're more to the point
            throw new Exception("Width or height of an rectangle can't be less than zero");
        }
        this.topLeftPoint = topLeftPoint;
        this.width = width;
        this.height = height;

        Point bottomRightPoint = new Point(topLeftPoint.X + width, topLeftPoint.Y + height);
        this.bottomRightPoint = bottomRightPoint;
    }

    public Rectangle(Point topLeftPoint, Point bottomRightPoint) throws Exception {
        if (topLeftPoint.GetX() > bottomRightPoint.GetX() ) {
            throw new Exception("Points aren't in the right order");
        }
        if (topLeftPoint.GetY() < bottomRightPoint.GetY()) {
            throw new Exception("Points aren't in the right order");
        }

        this.topLeftPoint = topLeftPoint;
        this.bottomRightPoint = bottomRightPoint;

        this.width = Math.abs(topLeftPoint.GetX() - bottomRightPoint.GetX());
        this.height = Math.abs(topLeftPoint.GetY() - bottomRightPoint.GetY());
    }

    public static Rectangle FromPoint(Point point) throws Exception {
        return new Rectangle(point, 0, 0);
    }

    public static Rectangle searchRectangle(Point fromPoint, double searchLength) throws Exception {
//        double lengthOfLatitudeInUkraine;
//        double lengthOfLongitudeInUkraine;
        double d = searchLength * Math.sqrt(2) / 2;
        double topLeftPointX = fromPoint.GetX() - (d * (1/111d));
        double topLeftPointY = fromPoint.GetY() +  (d/Math.cos(Math.toRadians(fromPoint.GetX())) * 111.32);
        Point topLeftPoint = new Point(topLeftPointX, topLeftPointY);

        double topRightPointX = fromPoint.GetX() + (d * (1/111d));
        double topRightPointY = fromPoint.GetY() - (d/Math.cos(Math.toRadians(fromPoint.GetX())) * 111.32);
        Point topRightPoint = new Point(topRightPointX, topRightPointY);

        return new Rectangle(topLeftPoint, topRightPoint);
    }
    /////https://www.auraq.com/distance-calculations-using-latitudes-and-longitudes

    public double CalculateArea() {
        return height * width;
    }

    /**
     * Returns new rectangle that is big enough to include both
     */
    public static Rectangle CalculateBoundingBox(Rectangle first, Rectangle second) throws Exception {
        double xMin = Double.min(first.topLeftPoint.GetX(), second.topLeftPoint.GetX());
        double yMin = Double.min(first.bottomRightPoint.GetY(), second.bottomRightPoint.GetY());
        double xMax = Double.max(first.bottomRightPoint.GetX(), second.bottomRightPoint.GetX());
        double yMax = Double.max(first.topLeftPoint.GetY(), second.topLeftPoint.GetY());


        return new Rectangle(new Point(xMin, yMax), new Point(xMax, yMin));
    }

    public static boolean IsIntersect(Rectangle first, Rectangle second) {
        return !IsEitherOfRectanglesOnTheTopOfOther(first, second) && !IsEitherOfRectanglesToTheLeftOfOther(first, second);
    }

    private static boolean IsEitherOfRectanglesToTheLeftOfOther(Rectangle first, Rectangle second) {
        return first.topLeftPoint.X > second.bottomRightPoint.X || second.topLeftPoint.X > first.bottomRightPoint.X;
    }

    private static boolean IsEitherOfRectanglesOnTheTopOfOther(Rectangle first, Rectangle second) {
        return first.topLeftPoint.Y < second.bottomRightPoint.Y || second.topLeftPoint.Y < first.bottomRightPoint.Y;
    }

}
