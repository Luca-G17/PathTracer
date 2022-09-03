package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.List;

public class WorldObject {
    protected List<Polygon> mesh;
    private final Material mat;
    private Point3D pos;
    WorldObject(Material mat, Point3D pos) {
        this.mat = mat;
        this.pos = pos;
    }
    public List<Polygon> getMesh() { return mesh; }
    public Material getMat() { return mat; }
    public Point3D getPos() { return pos; }
    public void setPos(Point3D pos) { this.pos = pos; }
}
