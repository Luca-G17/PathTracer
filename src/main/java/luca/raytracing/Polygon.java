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
    public double length2(Point3D p) {
        return p.dotProduct(p);
    }

    public boolean rayIsInPolygon(Point3D loc) {
        Line cast = new Line(loc, lines.get(0).getU(), 100); // Change
        int intersections = 0;
        for (Line l : lines) {
            Point3D r = l.getP1().subtract(l.getP0()); //  l1 = p + tr
            Point3D s = cast.getP1().subtract(cast.getP0()); // l2 = q + us
            Point3D f = cast.getP0().subtract(l.getP0());
            boolean areParallel = r.dotProduct(s) == 0.0;
            boolean arePlanar = f.dotProduct(r.crossProduct(s)) != 0.0;
            if (arePlanar && !areParallel) {
                Point3D rXs = r.crossProduct(s);
                double t = (f.crossProduct(s)).dotProduct(rXs) / length2(rXs);
                double u = (f.crossProduct(r)).dotProduct(rXs) / length2(rXs);
                if (t >= 0.0 && t <= 1.0 && u >= 0.0 && u < 1.0)
                    intersections++;
            }
            else if (areParallel) {
                // Check if they are the same line in that case
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
