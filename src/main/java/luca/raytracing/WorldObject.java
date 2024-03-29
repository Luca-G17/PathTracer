package luca.raytracing;

import javafx.geometry.Point3D;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public abstract class WorldObject {
    private Material mat;
    private Point3D pos;
    public String id; // TODO: Remove this testing id
    WorldObject(Material mat, Point3D pos) {
        this.mat = mat;
        this.pos = pos;
    }
    WorldObject() {
    }
    public Material getMat() {
        return mat;
    }
    public Point3D getPos() {
        return pos;
    }
    public void setPos(Point3D pos) {
        this.pos = pos;
    }
    protected void setMat(Material mat) {
        this.mat = mat;
    }
    public static Line PointsToLine(Point3D p1, Point3D p2) {
        Point3D u = p2.subtract(p1);
        return new Line(p1, u, u.magnitude());
    }
    public abstract Optional<WorldObject.Collision> Collision(Ray ray);

    public record Collision(Point3D point, Material mat, Point3D normal, double dist) implements Comparable {
        Collision() {
            this(Point3D.ZERO, Material.EMPTY, Point3D.ZERO, 0.0);
        }

        @Override
        public int compareTo(Object o) {
            if (!(o instanceof Collision)) throw new RuntimeException("Object is not of type collision");
            return Double.compare(this.dist, ((Collision) o).dist);
        }
    }
}
