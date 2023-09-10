package luca.raytracing;

import javafx.geometry.Point3D;

public class Mirror implements Material {
    private final Point3D reflection;
    private final Point3D emittance;
    public Mirror(Point3D reflection, Point3D emittance) {
        this.reflection = reflection;
        this.emittance = emittance;
    }

    @Override public Point3D weightPDF(Direction outgoing, Basis basis) {
        return this.reflection;
    }

    @Override public PostCollision samplePDF(Direction outgoing, Basis basis, boolean isInsideMesh) {
        return new PostCollision(reflect(outgoing, basis), false);
    }

    @Override public Point3D emittance(Direction outgoing, Basis basis) {
        return emittance;
    }

    public static Point3D reflect(Direction outgoing, Basis basis) {
        Point3D normal = basis.getNormal().normalize();
        Point3D incoming = outgoing.getVector().multiply(-1).normalize();
        return incoming.subtract(normal.multiply(2.0 * normal.dotProduct(incoming)));
    }
}
