package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.*;

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
    public HashMap<String, Line> Lines() {
        return lines;
    }
    @Override
    public Optional<Point3D> HitLoc(Ray ray) {
        double x = normal.dotProduct(ray.getOrigin()); // n.p0
        double y = normal.dotProduct(ray.getDirection()); // n.u
        double t = (d - x) / y; // t = (d - n.p0) / n.u
        Point3D col = ray.getOrigin().add(ray.getDirection().multiply(t)); // p = p0 + tu
        if (RayHit(col)) {
            return Optional.of(col);
        }
        return Optional.empty();
    }

    @Override
    public List<Point3D> GetPoints() {
        ArrayList<Point3D> points = new ArrayList<>();
        points.add(lines.get("p1p2").getP0());
        points.add(lines.get("p2p3").getP0());
        points.add(lines.get("p3p1").getP0());
        return points;
    }

    public Triangle Rotate(MatrixNxM rotation) {
        HashMap<String, Line> ls = new HashMap<>();
        for (Map.Entry<String, Line> l : lines.entrySet()) {
            ls.put(l.getKey(), l.getValue().Rotate(rotation));
        }
        return new Triangle(ls);
    }

    @Override
    public Point3D GetNormal() {
        return normal;
    }

    @Override
    public Triangle Translate(Point3D t) {
        HashMap<String, Line> ls = new HashMap<>();
        for (Map.Entry<String, Line> l : lines.entrySet()) {
            ls.put(l.getKey(), l.getValue().translate(t));
        }
        return new Triangle(ls);
    }

    @Override
    public boolean RayHit(Point3D col) {
        // https://math.stackexchange.com/questions/4322/check-whether-a-point-is-within-a-3d-triangle
        Point3D A = lines.get("p1p2").getP0();
        Point3D B = lines.get("p2p3").getP0();
        Point3D C = lines.get("p3p1").getP0();
        double area = ((B.subtract(A)).crossProduct(C.subtract(A))).magnitude();
        Point3D PC = C.subtract(col);

        Point3D PB = B.subtract(col);

        Point3D PA = A.subtract(col);
        double alpha = PB.crossProduct(PC).magnitude() / area;
        double beta = PC.crossProduct(PA).magnitude() / area;
        double gamma = PA.crossProduct(PB).magnitude() / area;
        return  (alpha >= 0 && alpha <= 1) &&
                (beta  >= 0 && beta  <= 1) &&
                (gamma >= 0 && gamma <= 1) &&
                (Math.abs(alpha + beta + gamma - 1) <= 0.01);
    }

    @Override
    public Triangle Scale(final double ScaleX, final double ScaleY, final double ScaleZ) {
        return this;
    }
}
