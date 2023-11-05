package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.List;
import java.util.Optional;

public class Sphere extends WorldObject implements Poly, Hittable {

    private final double radius;
    private Point3D centre;
    private AABB bbox;

    Sphere(Material mat, Point3D pos, double radius) {
        super(mat, pos);
        this.radius = radius;
        this.centre = pos;
        GenerateBoundingBox();
    }


    @Override
    public Point3D GetNormal() {
        return null;
    }

    @Override
    public Poly Rotate(MatrixNxM rotation) {
        return null;
    }

    @Override
    public Poly Translate(Point3D t) {
        return null;
    }

    @Override
    public boolean RayHit(Point3D col) {
        return false;
    }

    @Override
    public void FlipNormal() {

    }

    @Override
    public Optional<Point3D> HitLoc(Ray ray) {
        // Sphere: r^2 = (P - C)^2 = P^2 - 2PC + C^2
        // Line: P = p0 + tV
        // Determinant: b^2 - 4ac
        final Point3D v = ray.getDirection();
        final Point3D p0 = ray.getOrigin();
        final double a = v.dotProduct(v);
        final double b = 2.0 * (v.dotProduct(p0) - v.dotProduct(centre));
        final double c = -2.0 * (p0.dotProduct(centre)) + centre.dotProduct(centre) + p0.dotProduct(p0) - (radius * radius);
        final double determinant = (b * b) - (4.0 * a * c);
        if (determinant == 0) {
            double t = -b / (2.0 * a);
            Point3D c1 = p0.add(v.multiply(t));
            return Optional.of(c1);
        } else if (determinant > 0) {
            // Use closer collision point
            double t1 = (-b + Math.sqrt(determinant)) / (2.0 * a);
            double t2 = (-b - Math.sqrt(determinant)) / (2.0 * a);
            Point3D c1 = p0.add(v.multiply(t1));
            Point3D c2 = p0.add(v.multiply(t2));
            Point3D closer;
            Point3D further;
            if (VectorMath.Length2(c1.subtract(p0)) <= VectorMath.Length2(c2.subtract(p0))) {
                closer = c1;
                further = c2;
            } else {
                closer = c2;
                further = c1;
            }
            boolean closerBehind = (closer.subtract(p0)).dotProduct(v) < 0;
            boolean furtherBehind = (further.subtract(p0).dotProduct(v)) < 0;
            if (!closerBehind) {
                return Optional.of(closer);
            } else if (!furtherBehind) {
                return Optional.of(further);
            }
        }
        // No Roots
        return Optional.empty();
    }

    @Override
    public List<Point3D> GetPoints() {
        return null;
    }

    @Override
    public Sphere Scale(final double ScaleX, final double ScaleY, final double ScaleZ) {
        return this;
    }

    @Override
    public AABB GetBoundingBox() {
        return bbox;
    }

    @Override
    public void GenerateBoundingBox() {
        Point3D r = new Point3D(radius, radius, radius);
        Point3D min = centre.subtract(r);
        Point3D max = centre.add(r);
        this.bbox = new AABB(min, max);
    }

    @Override
    public Optional<Collision> Collision(Ray ray) {
        Optional<Point3D> optLoc = HitLoc(ray);
        if (optLoc.isPresent()) {
            Point3D loc = optLoc.get();
            Point3D normal = loc.subtract(centre);

            boolean rayTowardsNormal = ray.getDirection().dotProduct(normal) < 0.0;
            boolean collisionAfterOrigin = ray.getDirection().dotProduct(loc.subtract(ray.getOrigin())) > 0.0;
            double dist = VectorMath.Length2(loc.subtract(ray.getOrigin()));
            if (!ray.IsInsideMesh() && rayTowardsNormal && collisionAfterOrigin) {
                return Optional.of(new Collision(loc, getMat(), normal, dist));
            }
            else if (ray.IsInsideMesh() && !rayTowardsNormal && collisionAfterOrigin) {
                return Optional.of(new Collision(loc, getMat(), normal, dist));
            }
        }
        return Optional.empty();
    }

    @Override
    public Point3D GetCentre() {
        return centre;
    }
}
