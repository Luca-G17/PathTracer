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

    private record Collision(Point3D point, WorldObject hit, Polygon polygon) {}

    // TODO: If this has problems potentially the line cast parallel to the plane is on top of a boundary line
    private Optional<Collision> rayCollision(Ray ray) {
        List<Collision> collisions = new ArrayList<>();
        for (WorldObject obj : world) {
            for (Polygon p : obj.getMesh()) {
                Point3D loc = p.hitLoc(ray);

                // double d = ray.getDirection().dotProduct(ray.getOrigin());
                Point3D originToLoc = loc.subtract(ray.getOrigin());
                if (ray.getDirection().dotProduct(originToLoc) > 0) {
                    if (p.rayIsInPolygon(loc))
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

        Basis basis = new Basis(col.polygon.getNormal());
        Direction outgoing = new Direction(ray.getDirection().multiply(-1), basis);

        Material mat = col.hit.getMat();
        Point3D throughput = mat.weightPDF(outgoing, basis);

        if (mat.emittance(outgoing, basis).magnitude() != 0) { // i.e its a light
            return mat.emittance(outgoing, basis);
        }

        Point3D newDir = mat.samplePDF(outgoing, basis);
        Ray newRay = new Ray(col.point().add(col.polygon.getNormal().multiply(0.001)), newDir);
        return vectorMultiply(throughput, traceRayRecursive(newRay, depth + 1));
    }
    public Point3D traceRay(Ray ray) {
        Point3D background = new Point3D(0.7, 0.8, 1.0);
        Point3D throughput = new Point3D(1, 1, 1);
        Point3D total = Point3D.ZERO;
        for (int bounce = 0; bounce < maxDepth; bounce++) {
            Optional<Collision> optCol = rayCollision(ray);
            if (optCol.isEmpty()) {
                total = background;
                break;
            }
            Collision col = optCol.get();

            // Compute outgoing ray direction
            Basis basis = new Basis(col.polygon.getNormal());
            Direction outgoing = new Direction(ray.getDirection().multiply(-1), basis);

            // Apply emittance
            Material mat = col.hit.getMat();
            total = total.add(vectorMultiply(throughput, mat.emittance(outgoing, basis)));

            Point3D weight = mat.weightPDF(outgoing, basis);
            throughput = vectorMultiply(throughput, weight);
            for (Light light : lights) {
                Point3D colToLight = light.getPosition().subtract(col.point);
                double dist = colToLight.magnitude();
                if (occlude(new Ray(col.point, colToLight), light)) {
                    Direction incoming = new Direction(colToLight.normalize(), basis);
                    // total += weights * BRDF * incomingCosTheta * lightIntensity / distance^2
                    total = total.add(vectorMultiply(throughput, mat.BRDF())).multiply(incoming.getCosTheta() * light.getIntensity() / (dist * dist));
                }
            }

            Random rand = new Random();
            double p = Math.max(weight.getX(), Math.max(weight.getY(), weight.getZ()));
            if (bounce > 2) {
                if (rand.nextDouble() <= p)
                    throughput = weight.multiply(1 / p);
                else
                    break;
            }
            Point3D newDir = mat.samplePDF(outgoing, basis);
            ray = new Ray(col.point(), newDir);
        }
        return total;
    }
}
