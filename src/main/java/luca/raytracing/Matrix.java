package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.List;

public class Matrix {
    private final Point3D u;
    private final Point3D v;
    private final Point3D w;
    private final Point3D cu;
    private final Point3D cv;
    private final Point3D cw;

    Matrix(Point3D u, Point3D v, Point3D w) {
        this.u = u;
        this.v = v;
        this.w = w;
        this.cu = new Point3D(u.getX(), v.getX(), w.getX());
        this.cv = new Point3D(u.getY(), v.getY(), w.getY());
        this.cw = new Point3D(u.getZ(), v.getZ(), w.getZ());
    }
    public Matrix add(Matrix m){
        Point3D u = new Point3D(this.u.getX() + m.getU().getX(), this.u.getY() + m.getU().getY(), this.u.getZ() + m.getU().getZ());
        Point3D v = new Point3D(this.v.getX() + m.getV().getX(), this.v.getY() + m.getV().getY(), this.v.getZ() + m.getV().getZ());
        Point3D w = new Point3D(this.w.getX() + m.getW().getX(), this.w.getY() + m.getW().getY(), this.w.getZ() + m.getW().getZ());
        return new Matrix(u, v, w);
    }
    public static Matrix Combine(List<Matrix> transformations)
    {
        var mat = Matrix.identity();
        for (Matrix transform : transformations)
            mat = transform.multiplyMatrix(mat);
        return mat;
    }
    public static Matrix RotationVectorAxis(double theta, Point3D axis) {
        double c = Math.cos(theta);
        double s = Math.sin(theta);
        Point3D u = new Point3D(
                c + (axis.getX() * axis.getX()) * (1 - c),
                axis.getY() * axis.getX() * (1 - c) + (axis.getZ() * s),
                axis.getZ() * axis.getX() * (1 - c) - (axis.getY() * s)
        );
        Point3D v = new Point3D(
                axis.getX() * axis.getY() * (1 - c) - (axis.getZ() * s),
                c + (axis.getY() * axis.getY()) * (1 - c),
                axis.getZ() * axis.getY() * (1 - c) + (axis.getX() * s)
        );
        Point3D w = new Point3D(
                axis.getX() * axis.getZ() * (1 - c) + (axis.getY() * s),
                axis.getY() * axis.getZ() * (1 - c) - (axis.getX() * s),
                c + (axis.getZ() * axis.getZ()) * (1 - c)
        );
        return new Matrix(u, v, w);
    }
    public static Matrix RotationX(double pitch)
    {
        double c = Math.cos(pitch);
        double s = Math.sin(pitch);

        return new Matrix(new Point3D(+1, +0, +0),
                new Point3D(+0, +c, -s),
                new Point3D(+0, +s, +c));
    }
    public static Matrix RotationY(double yaw)
    {
        double c = Math.cos(yaw);
        double s = Math.sin(yaw);

        return new Matrix(new Point3D(+c, +0, +s),
                new Point3D(+0, +1, +0),
                new Point3D(-s, +0, +c));
    }
    public static Matrix RotationZ(double roll)
    {
        double c = Math.cos(roll);
        double s = Math.sin(roll);

        return new Matrix(new Point3D(+c, -s, +0),
                new Point3D(+s, +c, +0),
                new Point3D(+0, +0, +1));
    }
    public static Matrix Rotation(double pitch, double yaw, double roll)
    {
        return RotationX(pitch).multiplyMatrix(RotationY(yaw)).multiplyMatrix(RotationZ(roll));
    }
    public static Matrix identity() {
        Point3D u = new Point3D(1, 0, 0);
        Point3D v = new Point3D(0, 1, 0);
        Point3D w = new Point3D(0, 0, 1);
        return new Matrix(u, v, w);
    }
    public Matrix multiplyMatrix(Matrix m) {
        Point3D u2 = new Point3D(cu.dotProduct(m.getU()), cv.dotProduct(m.getU()), cw.dotProduct(m.getU()));
        Point3D v2 = new Point3D(cu.dotProduct(m.getV()), cv.dotProduct(m.getV()), cw.dotProduct(m.getV()));
        Point3D w2 = new Point3D(cu.dotProduct(m.getW()), cv.dotProduct(m.getW()), cw.dotProduct(m.getW()));
        return new Matrix(u2, v2, w2);
    }
    public Point3D MultiplyPoint3D(Point3D point) {
        double p1 = u.getX() * point.getX() + v.getX() * point.getY() + w.getX() * point.getZ();
        double p2 = u.getY() * point.getX() + v.getY() * point.getY() + w.getY() * point.getZ();
        double p3 = u.getZ() * point.getX() + v.getZ() * point.getY() + w.getZ() * point.getZ();
        return new Point3D(p1, p2, p3);
    }
    public Point3D getU() { return u; }
    public Point3D getV() { return v; }
    public Point3D getW() { return w; }
}
