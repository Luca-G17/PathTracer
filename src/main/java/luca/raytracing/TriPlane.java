package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.ArrayList;

public class TriPlane extends WorldObject {

    private final Point3D p1;
    private final Point3D p2;
    private final Point3D p3;
    private final Point3D p4;
    public TriPlane(Material mat, Point3D pos, Point3D p1, Point3D p2, Point3D p3, Point3D p4) {
        super(mat, pos);
        Triangle t1 = new Triangle(mat, p1, p2, p3);
        Triangle t2 = new Triangle(mat, p2, p3, p4);
        t2.FlipNormal();
        this.mesh = new ArrayList<>();
        this.mesh.add(t1);
        this.mesh.add(t2);
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
    }
}
