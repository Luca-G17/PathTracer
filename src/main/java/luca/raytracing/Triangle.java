package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.HashMap;
import java.util.Map;

public class Triangle implements Poly {
    private Point3D normal;
    private double d;
    private final HashMap<String, Line> lines = new HashMap<>();
    Triangle(Material mat, Point3D p1, Point3D p2, Point3D p3) {
        lines.put("p1p2", WorldObject.PointsToLine(p1, p2));
        lines.put("p2p3", WorldObject.PointsToLine(p2, p3));
        lines.put("p3p1", WorldObject.PointsToLine(p3, p1));
        ComputeNormal();
    }
    Triangle(HashMap<String, Line> lines) {
        this.lines.putAll(lines);
        ComputeNormal();
    }
    public void ComputeNormal() {
        this.normal = lines.get("p1p2").crossProduct(lines.get("p2p3"));
        this.d = lines.get("p1p2").getP0().dotProduct(normal);
    }
    public void FlipNormal() {
        normal = normal.multiply(-1);
        d = lines.get("p1p2").getP0().dotProduct(normal);
    }
    public Point3D HitLoc(Ray ray) {
        double x = normal.dotProduct(ray.getOrigin()); // n.p0
        double y = normal.dotProduct(ray.getDirection()); // n.u
        double t = (d - x) / y; // t = (d - n.p0) / n.u
        return ray.getOrigin().add(ray.getDirection().multiply(t)); // p = p0 + tu
    }
    public Triangle Rotate(Matrix r, Point3D origin) {
        HashMap<String, Line> ls = new HashMap<>();
        for (Map.Entry<String, Line> l : lines.entrySet()) {
            ls.put(l.getKey(), l.getValue().rotate(r, origin));
        }
        return new Triangle(ls);
    }
    public Triangle Rotate(Matrix r) {
        return Rotate(r, Point3D.ZERO);
    }
    public Triangle Translate(Point3D t) {
        HashMap<String, Line> ls = new HashMap<>();
        for (Map.Entry<String, Line> l : lines.entrySet()) {
            ls.put(l.getKey(), l.getValue().translate(t));
        }
        return new Triangle(ls);
    }
    public boolean RayHit(Point3D col) {
        // https://math.stackexchange.com/questions/4322/check-whether-a-point-is-within-a-3d-triangle
        double area = lines.get("p1p2").crossProduct(lines.get("p3p1")).magnitude();
        Point3D PC = lines.get("p3p1").getP0().subtract(col);
        Point3D PB = lines.get("p2p1").getP0().subtract(col);
        Point3D PA = lines.get("p1p2").getP0().subtract(col);
        double alpha = PB.crossProduct(PC).magnitude() / area;
        double beta = PC.crossProduct(PA).magnitude() / area;
        double gamma = 1 - alpha - beta;
        return  (alpha >= 0 && alpha <= 1) &&
                (beta  >= 0 && beta  <= 1) &&
                (gamma >= 0 && gamma <= 1) &&
                (alpha + beta + gamma == 1);
    }
}
