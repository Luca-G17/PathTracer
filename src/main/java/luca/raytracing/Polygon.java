package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Polygon implements Poly {
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

    @Override
    public Point3D HitLoc(Ray ray) {
        double x = normal.dotProduct(ray.getOrigin()); // n.p0
        double y = normal.dotProduct(ray.getDirection()); // n.u
        double t = (d - x) / y; // t = (d - n.p0) / n.u
        return ray.getOrigin().add(ray.getDirection().multiply(t)); // p = p0 + tu
    }

    public void setId(String id) { this.id = id; }

    // TODO: Make this immutable later
    public void FlipNormal() {
        normal = normal.multiply(-1);
        d = lines.get(0).getP0().dotProduct(normal);
    }

    @Override
    public Point3D GetNormal() { return normal; }
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

    public boolean rayIsInPolygon(Point3D loc, boolean overload) {
        // Convert loc to u, v coords
        Basis basis = new Basis(lines.get(0).getU(), this.GetNormal(), lines.get(1).getU());
        Point3D locInBasis = basis.getTransform().MultiplyPoint3D(loc);
        if (locInBasis.getX() <= 1 && locInBasis.getX() >= 0 && locInBasis.getZ() <= 1 && locInBasis.getZ() >= 0)
            return true;
        return false;
    }
    public boolean RayHit(Point3D loc) {
        // Line cast = new Line(loc, lines.get(0).getU(), 0);
        Line cast = new Line(loc, obliqueCast(), 300);
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
    public Polygon Translate(Point3D p) {
        return new Polygon(lines.stream().map(x -> x.translate(p)).collect(Collectors.toList()), this.id);
    }
    public Polygon Rotate(Matrix r) {
        return new Polygon(lines.stream().map(x -> x.rotate(r)).collect(Collectors.toList()), this.id);
    }
    public Polygon Rotate(Matrix r, Point3D origin) {
        return Translate(origin).Rotate(r).Translate(origin.multiply(-1));
    }
}
