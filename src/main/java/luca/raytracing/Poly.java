package luca.raytracing;

import javafx.geometry.Point3D;

public interface Poly {
    String id = "";

    public Point3D GetNormal();
    public Poly Rotate(Matrix rot);
    public Poly Rotate(Matrix rot, Point3D origin);
    public Poly Translate(Point3D t);
    public boolean RayHit(Point3D col);
    public void FlipNormal();
    public default String getId() {
        return id;
    }
    public Point3D HitLoc(Ray ray);
}
