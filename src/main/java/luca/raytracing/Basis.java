package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.List;

public class Basis {
    private MatrixNxM transform;

    Basis(Point3D normal) {
        Point3D tangent;
        if (normal.getX() == 0)
            tangent = new Point3D(1, 0, 0);
        else {
            // (z, 0, -x) = cross((0, 1, 0), (x, y , z))
            tangent = new Point3D(normal.getZ(), 0, -normal.getX());
        }
        Point3D bitangent = tangent.crossProduct(normal);
        transform = new MatrixNxM(tangent.normalize(), normal.normalize(), bitangent.normalize());
    }
    Basis(Point3D normal, Point3D tangent, Point3D bitangent) {
        transform = new MatrixNxM(tangent, normal, bitangent);
    }
    public MatrixNxM getTransform() {
        return transform;
    }
    public Point3D getTangent() {
        return MatrixNxM.ListToPoint3D(transform.GetCol(0));
    }
    public Point3D getNormal() {
        return MatrixNxM.ListToPoint3D(transform.GetCol(1));
    }
    public Point3D getBitangent() {
        return MatrixNxM.ListToPoint3D(transform.GetCol(2));
    }
}
