package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public abstract class MeshObject extends WorldObject {
    protected List<Triangle> mesh;
    MeshObject(Material mat, Point3D pos) {
        super(mat, pos);
    }

    @Override
    public Optional<WorldObject.Collision> Collision(Ray ray) {
        List<Collision> cols = new ArrayList<>();
        for (Poly p : mesh) {
            Optional<Point3D> optLoc = p.HitLoc(ray);
            if (optLoc.isPresent()) {
                Point3D loc = optLoc.get();
                boolean rayTowardsNormal = ray.getDirection().dotProduct(p.GetNormal()) < 0.0;
                boolean collisionAfterOrigin = ray.getDirection().dotProduct(loc.subtract(ray.getOrigin())) > 0.0;
                double dist = VectorMath.Length2(loc.subtract(ray.getOrigin()));
                if (!ray.IsInsideMesh() && rayTowardsNormal && collisionAfterOrigin) {
                    cols.add(new WorldObject.Collision(loc, getMat(), p.GetNormal(), dist));
                }
                else if (ray.IsInsideMesh() && !rayTowardsNormal && collisionAfterOrigin) {
                    cols.add(new WorldObject.Collision(loc, getMat(), p.GetNormal(), dist));
                }
            }
        }
        return cols.stream().min(Collision::compareTo);
    }

    public List<Triangle> HittableMesh() {
        return mesh;
    }
}
