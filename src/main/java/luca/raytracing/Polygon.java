package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.List;
import java.util.stream.Collectors;

public class Polygon {
    private final List<Line> lines;
    private Point3D normal;
    private double d;
    private String id;
    Polygon(List<Line> lines, String id) {
        this(lines);
        this.id = id;
    }
    Polygon(List<Line> lines) {
        this.lines = lines;
        Line l1 = lines.get(0);
        Line l2 = lines.get(1);
        if (l1.dotProduct(l2) == 1) l2 = lines.get(2);
        normal = l1.crossProduct(l2);
        d = l1.getP0().dotProduct(normal); // n.p = d
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // TODO: Make this immutable later
    public void flipNormal() {
        normal = normal.multiply(-1);
        d = lines.get(0).getP0().dotProduct(normal);
    }
    public Point3D getNormal() { return normal; }

    public Point3D hitLoc(Ray ray) {
        double x = normal.dotProduct(ray.getOrigin()); // n.p0
        double y = normal.dotProduct(ray.getDirection()); // n.u
        double t = (d - x) / y; // t = (d - n.p0) / n.u
        return ray.getOrigin().add(ray.getDirection().multiply(t)); // p = p0 + tu
    }
    public double vectorRatio(Point3D v1, Point3D v2) {
        return v1.magnitude() / v2.magnitude();
    }
    public boolean rayIsInPolygon(Point3D loc) {
        Line cast = new Line(loc, lines.get(0).getU(), 0);
        int intersections = 0;
        for (Line l : lines) {
            Point3D g = l.getP0().subtract(cast.getP0());
            Point3D h = l.getU().crossProduct(g);
            Point3D k = l.getU().crossProduct(cast.getU());
            if (!h.equals(Point3D.ZERO) && !k.equals(Point3D.ZERO)) {
                double scalar = vectorRatio(h, k);
                if (scalar != 0){
                    Point3D scaledVector = cast.getU().multiply(scalar);
                    Point3D intersection;
                    // Checks if vectors are parallel or antiparallel
                    // TODO: May have to change this
                    if (h.getX() * k.getX() > 0 || h.getY() * k.getY() > 0 || h.getZ() * k.getZ() > 0){
                        intersection = cast.getP0().add(scaledVector);
                    }
                    else {
                        intersection = cast.getP0().subtract(scaledVector);
                    }
                    // Plane normal = castU
                    // d = castU.CastP0
                    // if castU.intersection - d > 0
                    double d = cast.getU().dotProduct(cast.getP0());
                    if (cast.getU().dotProduct(intersection) - d > 0){
                        double distToP0 = (l.getP0().subtract(intersection)).magnitude();
                        double distToP1 = (l.getP1().subtract(intersection)).magnitude();
                        if (distToP0 + distToP1 <= l.getLength())
                            intersections++;
                    }
                }
            }
        }
        return intersections % 2 != 0; // Odd = inside polygon
    }
    public Polygon translate(Point3D p) {
        return new Polygon(lines.stream().map(x -> x.translate(p)).collect(Collectors.toList()), this.id);
    }
    public Polygon rotate(Matrix r) {
        return new Polygon(lines.stream().map(x -> x.rotate(r)).collect(Collectors.toList()), this.id);
    }
}
