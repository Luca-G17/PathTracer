package luca.raytracing;

import javafx.geometry.Point3D;

public class VectorMath {
    public static double EPSILON = 1E-6;
    public static Point3D Inverse(Point3D p) {
        return new Point3D(1.0 / p.getX(), 1.0 / p.getY(), 1.0 / p.getZ());
    }
    public static double Length2(Point3D p) {
        return p.dotProduct(p);
    }
    public static double P3At(Point3D p, int i) {
        if (i == 0) return p.getX();
        if (i == 1) return p.getY();
        if (i == 2) return p.getZ();
        throw new ArrayIndexOutOfBoundsException(String.format("%d is out of range of a Point3D with 3 axis", i));
    }

    public static Point3D P3SetAt(Point3D p, int i, double d) {
        if (i == 0) return new Point3D(d, p.getY(), p.getZ());
        if (i == 1) return new Point3D(p.getX(), d, p.getZ());
        if (i == 2) return new Point3D(p.getX(), p.getY(), d);
        throw new ArrayIndexOutOfBoundsException(String.format("%d is out of range of a Point3D with 3 axis", i));
    }

    public static boolean P3Equal(Point3D p0, Point3D p1) {
        Point3D delta = p0.subtract(p1);
        return
                (Math.abs(delta.getX()) < EPSILON) &&
                (Math.abs(delta.getY()) < EPSILON) &&
                (Math.abs(delta.getZ()) < EPSILON);

    }
}
