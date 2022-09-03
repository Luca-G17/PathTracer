package luca.raytracing;

import javafx.geometry.Point3D;

public class Direction {
    private final Point3D vector;
    private final double cosTheta;

    Direction(Point3D vector, Basis basis) {
        this.vector = vector;
        this.cosTheta = Math.max(0, basis.getNormal().dotProduct(vector));
    }

    public double getCosTheta() { return cosTheta; }
    public Point3D getVector() { return vector; }
}
