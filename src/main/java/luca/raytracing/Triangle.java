package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.*;

public class Triangle implements Poly, Hittable {
    private Point3D normal;
    private double d;
    private final HashMap<String, Line> lines = new HashMap<>();
    private AABB bbox;
    private Material mat;
    private final Point3D centre;
    public String id;
    Triangle(Material mat, Point3D p1, Point3D p2, Point3D p3) {
        lines.put("p1p2", WorldObject.PointsToLine(p1, p2));
        lines.put("p2p3", WorldObject.PointsToLine(p2, p3));
        lines.put("p3p1", WorldObject.PointsToLine(p3, p1));
        this.centre = ComputeCentre();
        ComputeNormal();
        this.mat = mat;
        GenerateBoundingBox();
    }
    Triangle(HashMap<String, Line> lines, Material mat) {
        this.lines.putAll(lines);
        this.centre = ComputeCentre();
        ComputeNormal();
        GenerateBoundingBox();
        this.mat = mat;
    }
    private Point3D ComputeCentre() {
        return lines.get("p1p2").getP0()
                .add(lines.get("p2p3").getP0())
                .add(lines.get("p3p1").getP0())
                .multiply(1.0 / 3);
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
        return new Triangle(ls, this.mat);
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
        return new Triangle(ls, this.mat);
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
    public void SetMaterial(Material mat) {
        this.mat = mat;
    }

    @Override
    public Triangle Scale(final double ScaleX, final double ScaleY, final double ScaleZ) {
        return this;
    }

    @Override
    public AABB GetBoundingBox() {
        return bbox;
    }

    @Override
    public void GenerateBoundingBox() {
        Point3D min = new Point3D(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        Point3D max = new Point3D(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Point3D p : this.GetPoints()) {
            for (int a = 0; a < 3; a++) {
                double d = VectorMath.P3At(p, a);
                if (d < VectorMath.P3At(min, a))
                    min = VectorMath.P3SetAt(p, a, d);
                else if (d > VectorMath.P3At(max, a))
                    max = VectorMath.P3SetAt(p, a, d);
            }
        }
        this.bbox = new AABB(min, max);
    }

    @Override
    public Optional<WorldObject.Collision> Collision(Ray ray) {
        double x = normal.dotProduct(ray.getOrigin()); // n.p0
        double y = normal.dotProduct(ray.getDirection()); // n.u
        if (y == 0) return Optional.empty();
        double t = (d - x) / y; // t = (d - n.p0) / n.u
        Point3D col = ray.getOrigin().add(ray.getDirection().multiply(t)); // p = p0 + tu
        if (RayHit(col)) {
            boolean rayTowardsNormal = ray.getDirection().dotProduct(normal) < 0.0;
            boolean collisionAfterOrigin = ray.getDirection().dotProduct(col.subtract(ray.getOrigin())) > 0.0;
            double dist = VectorMath.Length2(col.subtract(ray.getOrigin()));
            if (!ray.IsInsideMesh() && rayTowardsNormal && collisionAfterOrigin) {
                return Optional.of(new WorldObject.Collision(col, mat, normal, dist));
            }
            else if (ray.IsInsideMesh() && !rayTowardsNormal && collisionAfterOrigin) {
                return Optional.of(new WorldObject.Collision(col, mat, normal, dist));
            }
        }
        return Optional.empty();
    }
    @Override
    public Point3D GetCentre() {
        return centre;
    }

    public Material getMat() {
        return mat;
    }
}
