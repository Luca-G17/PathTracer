package luca.raytracing;

import javafx.geometry.Point3D;
import javafx.scene.effect.Light.Point;

import java.util.List;
import java.util.Random;
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

    private Point3D getRandomPointOnPolygon() {
        Line l1 = lines.get(0);
        Line l2 = lines.get(1);
        Random rand = new Random();
        double u = rand.nextDouble();
        double v = rand.nextDouble();
        return l1.getP0().add(l1.getU().multiply(u)).add(l2.getU().multiply(v));
    }
    private Point3D obliqueCast() {
        boolean castValid = false;
        Point3D v = new Point3D(1, 1, 1);
        while (!castValid) {
            castValid = true;
            // pick two points on the plane built by the polygon
            Point3D p1 = getRandomPointOnPolygon();
            Point3D p2 = getRandomPointOnPolygon();
            if (p1.equals(p2))
                castValid = false;
            // maybe add a check in case p1 == p2
            v = p2.subtract(p1);
            for (Line l : lines) {
                if (v.dotProduct(l.getU()) == 0) {
                    castValid = false;
                }
            }
        }
        return v;
    }

    private double perimeter() {
        double total = 0;
        for (Line l : lines) {
            total += l.getLength();
        }
        return total;
    }

    /*
    public boolean rayIsInPolygon(Point3D loc) {
        for (Line l : lines) {
            if (loc.equals(l.getP0())) { //TODO: Tolerances?
                return true;
            }
        }
        Point3D cDir = obliqueCast();
        Line cast = new Line(loc, cDir, perimeter());
        int intersections = 0;
        for (Line l : lines) {
            Point3D r = l.getP1().subtract(l.getP0()); //  l1 = p + tr
            Point3D s = cast.getP1().subtract(cast.getP0()); // l2 = q + us
            Point3D f = cast.getP0().subtract(l.getP0());
            boolean areParallel = r.dotProduct(s) == 0.0;
            boolean arePlanar = f.dotProduct(r.crossProduct(s)) != 0.0; //TODO: Tolerances?
            if (arePlanar && !areParallel) {
                Point3D rXs = r.crossProduct(s);
                double t = (f.crossProduct(s)).dotProduct(rXs) / length2(rXs);
                if (t >= 0.0 && t <= 1.0) // If the intersection is in the segment
                    intersections++;
                // Need to add a check for if the cast hits a corner, or if the 'loc' == corner
            }
            else if (areParallel) {
                // Something has gone horribly wrong
                assert(false);
            }
        }
        return intersections % 2 != 0; // Odd = inside polygon
    }

     */

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
