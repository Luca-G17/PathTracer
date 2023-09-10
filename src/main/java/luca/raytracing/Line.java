package luca.raytracing;

import javafx.geometry.Point3D;

public class Line {
    private final Point3D p0;
    private final Point3D u;
    private final Point3D p1;
    private final double length;
    Line(Point3D p0, Point3D u, double length) {
        this.p0 = p0;
        this.u = u.normalize();
        this.p1 = this.u.multiply(length).add(p0);
        this.length = length;
    }
    public Point3D crossProduct(Line l) {
        return u.crossProduct(l.getU());
    }
    public double dotProduct(Line l) {
        return u.dotProduct(l.getU());
    }

    public Point3D getP1() {
        return p1;
    }
    public Point3D getP0() {
        return p0;
    }
    public double getLength() {
        return length;
    }
    public Point3D getU() {
        return u;
    }
    public Line translate(Point3D p) {
        return new Line(p0.add(p), u, length);
    }
    public Line Rotate(MatrixNxM rotation) {
        Point3D newP0 = rotation.Multiply(p0);
        Point3D newP1 = rotation.Multiply(p1);
        Point3D newU = newP1.subtract(newP0);
        return new Line(newP0, newU, length);
    }
}
