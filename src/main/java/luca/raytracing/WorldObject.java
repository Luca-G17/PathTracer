package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.List;

public class WorldObject {
    protected List<Poly> mesh;
    private final Material mat;
    private Point3D pos;
    WorldObject(Material mat, Point3D pos) {
        this.mat = mat;
        this.pos = pos;
    }
    public List<Poly> getMesh() { return mesh; }
    public Material getMat() { return mat; }
    public Point3D getPos() { return pos; }
    public void setPos(Point3D pos) { this.pos = pos; }
    public static Line PointsToLine(Point3D p1, Point3D p2) {
        Point3D u = p2.subtract(p1);
        return new Line(p1, u, u.magnitude());
    }
}
