package luca.raytracing;

import javafx.geometry.Point3D;

public class VectorMath {

    public static double Length2(Point3D p) {
        return p.dotProduct(p);
    }
}
