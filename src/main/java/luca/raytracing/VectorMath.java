package luca.raytracing;

import javafx.geometry.Point3D;

public class VectorMath {
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
}
