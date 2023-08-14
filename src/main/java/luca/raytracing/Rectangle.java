package luca.raytracing;

import javafx.geometry.Point3D;

public class Rectangle implements Poly {

    private final Triangle t1;
    private final Triangle t2;
    public String id;
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
        // this(p1.subtract(p2).magnitude(), p1.subtract(p3).magnitude(), p1, 0, 0, mat, id);
        this.id = id;
        this.t1 = new Triangle(mat, p1, p2, p3);
        this.t2 = new Triangle(mat, p2, p3, p4);
        CheckNormals();
    }
    public Rectangle(Triangle t1, Triangle t2, String id) {
        this.t1 = t1;
        this.t2 = t2;
        this.id = id;
        CheckNormals();
    }
    private void CheckNormals() {
        if (t1.GetNormal().dotProduct(t2.GetNormal()) < 0) { // Ensure both normals are in the same direction
            t2.FlipNormal();
        }
    }
    public void FlipNormal() {
        t1.FlipNormal();
        t2.FlipNormal();
    }

    @Override
    public Point3D HitLoc(Ray ray) {
        return t1.HitLoc(ray);
    }

    @Override
    public Point3D GetNormal() {
        return t1.GetNormal();
    }

    @Override
    public Poly Rotate(Matrix rot) {
        return new Rectangle(t1.Rotate(rot), t2.Rotate(rot), this.id);
    }

    public Rectangle Rotate(Matrix r, Point3D origin) {
        return new Rectangle(t1.Rotate(r, origin), t2.Rotate(r, origin), this.id);
    }
    public Rectangle Translate(Point3D t) {
        return new Rectangle(t1.Translate(t), t2.Translate(t), this.id);
    }

    @Override
    public boolean RayHit(Point3D col) {
        return t1.RayHit(col) || t2.RayHit(col);
    }

    @Override
    public String getId() {
        return id;
    }

    public void SetId(String id) {
        this.id = id;
    }
}
