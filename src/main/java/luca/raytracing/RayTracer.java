package luca.raytracing;

import javafx.geometry.Point3D;
import javafx.geometry.Pos;

import java.util.*;

public class RayTracer {
    // Collisions:
    // Compute intersection point between plane and ray
    // Compute if ray intersects inside polygon
    // the closest intersection returned
    private final int maxDepth = 50;
    private final List<WorldObject> world;
    private final BVH BVHWorld;
    RayTracer(List<MeshObject> meshes, List<Sphere> spheres) {
        this.world = new ArrayList<>();
        world.addAll(meshes);
        world.addAll(spheres);

        List<Triangle> triangles = meshes.stream().flatMap(m -> m.HittableMesh().stream()).toList();
        // triangles = BVH.TriangleListSubdivision(triangles);

        List<Hittable> hittables = new ArrayList<>();
        hittables.addAll(triangles);
        hittables.addAll(spheres);
        BVHWorld = new BVH(hittables);
    }
    private Optional<WorldObject.Collision> rayCollision(Ray ray) {
        List<WorldObject.Collision> collisions = new ArrayList<>();
        for (WorldObject obj : world) {
            obj.Collision(ray).ifPresent(collisions::add);
        }
        return collisions.stream().min(Comparator.comparingDouble(c -> (c.point().subtract(ray.getOrigin())).magnitude()));
    }
    private Optional<WorldObject.Collision> rayCollisionBVH(Ray ray) {
        return BVHWorld.Collision(ray);
    }
    private Point3D vectorMultiply(Point3D v1, Point3D v2) {
        return new Point3D(v1.getX() * v2.getX(), v1.getY() * v2.getY(), v1.getZ() * v2.getZ());
    }

    public Point3D traceRayRecursive(Ray ray, int depth) {
        Optional<WorldObject.Collision> optCol = rayCollisionBVH(ray);
        if (depth > maxDepth) {
            return Point3D.ZERO;
        }
        if (optCol.isEmpty()) {
            return new Point3D(0.1f, 0.1f, 0.1f);
        }
        WorldObject.Collision col = optCol.get();
        Basis basis = new Basis(col.normal());
        Direction outgoing = new Direction(ray.getDirection().multiply(-1), basis);

        Material mat = col.mat();
        Point3D throughput = mat.weightPDF(outgoing, basis);

        if (mat.emittance(outgoing, basis).magnitude() != 0) { // i.e its a light
            return mat.emittance(outgoing, basis);
        }

        Material.PostCollision postCol = mat.samplePDF(outgoing, basis, ray.IsInsideMesh());
        boolean rayIsInsideMesh = ray.IsInsideMesh() ^ postCol.isRefracted();
        double offset = !rayIsInsideMesh ? 0.001 : -0.001;
        Ray newRay = new Ray(col.point().add(col.normal().multiply(offset)), postCol.outVector(), rayIsInsideMesh);
        return vectorMultiply(throughput, traceRayRecursive(newRay, depth + 1));
    }
}
