package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.*;

public class RayTracer {
    // Collisions:
    // Compute intersection point between plane and ray
    // Compute if ray intersects inside polygon
    // the closest intersection returned
    private final int maxDepth = 10;
    private final Random random;
    private final List<WorldObject> world;
    private final List<Light> lights;
    RayTracer(List<WorldObject> world, List<Light> lights) {
        this.world = world;
        this.lights = lights;
        random = new Random();
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
                if (ray.getDirection().dotProduct(originToLoc) > 0){
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
    public Point3D traceRay(Ray ray) {
        Point3D weights = new Point3D(1, 1, 1);
        Point3D total = Point3D.ZERO;
        for (int bounce = 0; bounce < maxDepth; bounce++) {
            Optional<Collision> optCol = rayCollision(ray);
            if (optCol.isEmpty()) break;
            Collision col = optCol.get();

            Basis basis = new Basis(col.polygon.getNormal());
            Direction outgoing = new Direction(ray.getDirection().multiply(-1), basis);
            Material mat = col.hit.getMat();

            total = total.add(vectorMultiply(weights, mat.emittance(outgoing, basis)));

            for (Light light : lights) {
                Point3D colToLight = light.getPosition().subtract(col.point);
                double dist = colToLight.magnitude();
                if (occlude(new Ray(col.point, colToLight), light)) {
                    Direction incoming = new Direction(colToLight.normalize(), basis);
                    total = total.add(vectorMultiply(weights, mat.BRDF())).multiply(incoming.getCosTheta() * light.getIntensity() / (dist * dist));
                }
            }

            Point3D weight = mat.weightPDF(outgoing, basis);
            double p = Math.max(weight.getX(), Math.max(weight.getY(), weight.getZ()));
            if (bounce > 2) {
                if (random.nextDouble() <= p)
                    weight = weight.multiply(1 / p);
                else
                    break;
            }

            weights = vectorMultiply(weights, weight);
            Point3D newDir = mat.samplePDF(outgoing, basis, random);
            ray = new Ray(col.point(), newDir);
        }
        return total;
    }
}
