package luca.raytracing;

import javafx.geometry.Point3D;

public class Rectangle {

    private final Triangle t1;
    private final Triangle t2;
    public final String id;
    public Rectangle(double w, double h, Point3D topLeft, double roll, double pitch, Material mat, String id) {
        this.id = id;
        Point3D bottomLeft = new Point3D(topLeft.getX(), topLeft.getY() - h, topLeft.getZ());
        Point3D topRight = new Point3D(topLeft.getX() + w, topLeft.getY(), topLeft.getZ());
        Point3D bottomRight = new Point3D(topLeft.getX() + w, topLeft.getY() - h, topLeft.getZ());
        this.t1 = new Triangle(mat, topLeft, bottomLeft, bottomRight);
        this.t2 = new Triangle(mat, topLeft, topRight, bottomRight);
        Rotate(Matrix.Rotation(pitch, 0, roll), topLeft);
    }
    public Rectangle(Point3D p1, Point3D p2, Point3D p3, Point3D p4, Material mat, String id) {
        this(p1.subtract(p2).magnitude(), p2.subtract(p3).magnitude(), p1, 0, 0, mat, id);
    }
    public Rectangle(Triangle t1, Triangle t2, String id) {
        this.t1 = t1;
        this.t2 = t2;
        this.id = id;
    }
    public void FlipNormal() {
        t1.FlipNormal();
        t2.FlipNormal();
    }
    public Rectangle Rotate(Matrix r, Point3D origin) {
        return new Rectangle(t1.Rotate(r, origin), t2.Rotate(r, origin), this.id);
    }
    public Rectangle Translate(Point3D t) {
        return new Rectangle(t1.Translate(t), t2.Translate(t), this.id);
    }
}
