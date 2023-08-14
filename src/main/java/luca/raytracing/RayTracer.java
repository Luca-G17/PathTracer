package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.*;

public class RayTracer {
    // Collisions:
    // Compute intersection point between plane and ray
    // Compute if ray intersects inside polygon
    // the closest intersection returned
    private final int maxDepth = 50;
    private final List<WorldObject> world;
    private final List<Light> lights;
    RayTracer(List<WorldObject> world, List<Light> lights) {
        this.world = world;
        this.lights = lights;
    }

    private record Collision(Point3D point, WorldObject hit, Poly polygon) {}

    // TODO: If this has problems potentially the line cast parallel to the plane is on top of a boundary line
    private Optional<Collision> rayCollision(Ray ray) {
        List<Collision> collisions = new ArrayList<>();
        for (WorldObject obj : world) {
            for (Poly p : obj.getMesh()) {
                Point3D loc = p.HitLoc(ray);

                // double d = ray.getDirection().dotProduct(ray.getOrigin());
                Point3D originToLoc = loc.subtract(ray.getOrigin());
                if (p.RayHit(loc)) {
                    if (p instanceof TriCube) {
                        int x = 0;
                    }
                }
                if (ray.getDirection().dotProduct(originToLoc) > 0) {
                    if (p.RayHit(loc))
                        collisions.add(new Collision(loc, obj, p));
                }
            }
        }
        return collisions.stream().min(Comparator.comparingDouble(c -> (c.point.subtract(ray.getOrigin())).magnitude()));
    }
    private Point3D vectorMultiply(Point3D v1, Point3D v2) {
        return new Point3D(v1.getX() * v2.getX(), v1.getY() * v2.getY(), v1.getZ() * v2.getZ());
    }
    public boolean occlude(Ray ray, Light light) {
        Optional<Collision> col = rayCollision(ray);
        if (col.isPresent()) {
            Point3D colToLight = light.getPosition().subtract(col.get().point());
            return ray.getDirection().dotProduct(colToLight) < 0;
        }
        return true;
    }

    public Point3D traceRayRecursive(Ray ray, int depth) {
        Optional<Collision> optCol = rayCollision(ray);
        if (depth > maxDepth) {
            return Point3D.ZERO;
        }
        if (optCol.isEmpty()) {
            return new Point3D(0.1f, 0.1f, 0.1f);
        }
        Collision col = optCol.get();

        Basis basis = new Basis(col.polygon.GetNormal());
        Direction outgoing = new Direction(ray.getDirection().multiply(-1), basis);

        Material mat = col.hit.getMat();
        Point3D throughput = mat.weightPDF(outgoing, basis);

        if (mat.emittance(outgoing, basis).magnitude() != 0) { // i.e its a light
            return mat.emittance(outgoing, basis);
        }

        Point3D newDir = mat.samplePDF(outgoing, basis);
        Ray newRay = new Ray(col.point().add(col.polygon.GetNormal().multiply(0.001)), newDir);
        return vectorMultiply(throughput, traceRayRecursive(newRay, depth + 1));
    }
}
