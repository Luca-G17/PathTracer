package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.*;

public class Rectangle {

    private final Triangle t1;
    private final Triangle t2;
    public String id;
    private Point3D centre;
    private final Material mat;
    public Rectangle(double w, double h, Point3D topLeft, double roll, double pitch, Material mat, String id) {
        this.id = id;
        Point3D bottomLeft = new Point3D(topLeft.getX(), topLeft.getY() - h, topLeft.getZ());
        Point3D topRight = new Point3D(topLeft.getX() + w, topLeft.getY(), topLeft.getZ());
        Point3D bottomRight = new Point3D(topLeft.getX() + w, topLeft.getY() - h, topLeft.getZ());
        this.t1 = new Triangle(mat, topLeft, bottomLeft, bottomRight);
        this.t2 = new Triangle(mat, topLeft, topRight, bottomRight);
        CalculateCentreCoords();
        Rotate(MatrixNxM.RotationMatrix(pitch, 0.0, roll));
        this.mat = mat;
    }
    public Rectangle(Point3D p1, Point3D p2, Point3D p3, Point3D p4, Material mat, String id) {
        this.id = id;
        this.t1 = new Triangle(mat, p1, p2, p3);
        this.t2 = new Triangle(mat, p2, p4, p3);
        CalculateCentreCoords();
        this.mat = mat;
    }
    public Rectangle(Triangle t1, Triangle t2, String id, Material mat) {
        this.t1 = t1;
        this.t2 = t2;
        this.id = id;
        CalculateCentreCoords();
        this.mat = mat;
    }
    private void CalculateCentreCoords() {
        // Take average of all points
        List<Point3D> ps = GetPoints();
        Point3D total = Point3D.ZERO;
        for (Point3D p : ps) {
            total = total.add(p);
        }
        assert(ps.size() == 4);
        centre = total.multiply(1.0 / 4.0);
    }

    public List<Point3D> GetPoints() {
        List<Point3D> coords = new ArrayList<>();
        coords.add(t1.Lines().get("p1p2").getP0());
        coords.add(t1.Lines().get("p2p3").getP0());
        coords.add(t1.Lines().get("p3p1").getP0());
        coords.add(t2.Lines().get("p2p3").getP0());
        return coords;
    }
    public Rectangle Scale(final double XScale, final double YScale, final double ZScale) {
        List<Point3D> coords = GetPoints();
        MatrixNxM coordsMatrix = new MatrixNxM(coords.stream().map(MatrixNxM::Point3DtoList).toList());
        MatrixNxM scale = new MatrixNxM(Arrays.asList(
                Arrays.asList(XScale, 0.0, 0.0),
                Arrays.asList(0.0, YScale, 0.0),
                Arrays.asList(0.0, 0.0, ZScale)
        ));
        coordsMatrix = scale.Multiply(coordsMatrix);
        return new Rectangle(
                MatrixNxM.ListToPoint3D(coordsMatrix.GetCol(0)),
                MatrixNxM.ListToPoint3D(coordsMatrix.GetCol(1)),
                MatrixNxM.ListToPoint3D(coordsMatrix.GetCol(2)),
                MatrixNxM.ListToPoint3D(coordsMatrix.GetCol(3)),
                this.mat,
                this.id
        );
    }
    public void FlipNormal() {
        t1.FlipNormal();
        t2.FlipNormal();
    }

    public Optional<Point3D> HitLoc(Ray ray) {
        Optional<Point3D> loc = t1.HitLoc(ray);
        if (loc.isEmpty()) {
            loc = t2.HitLoc(ray);
        }
        return loc;
    }

    public Point3D GetNormal() {
        return t1.GetNormal();
    }

    public Rectangle Rotate(MatrixNxM r) {
        return new Rectangle(this.t1.Rotate(r), this.t2.Rotate(r), this.id, this.mat);
    }
    public Rectangle Translate(Point3D t) {
        return new Rectangle(t1.Translate(t), t2.Translate(t), this.id, this.mat);
    }

    public boolean RayHit(Point3D col) {
        return t1.RayHit(col) || t2.RayHit(col);
    }

    public String getId() {
        return id;
    }

    public Rectangle RotateAroundCentre(MatrixNxM r) {
        Rectangle tmpRect = Translate(centre.multiply(-1));
        tmpRect = new Rectangle(tmpRect.t1.Rotate(r), tmpRect.t2.Rotate(r), this.id, this.mat);
        tmpRect = tmpRect.Translate(centre);
        return tmpRect;
    }
    public void SetId(String id) {
        this.id = id;
    }

    public List<Triangle> Triangles() {
        return Arrays.asList(t1, t2);
    }
}
