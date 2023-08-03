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

    @Override public Point3D samplePDF(Direction outgoing, Basis basis) {
        return (basis.getNormal().multiply(2).multiply(outgoing.getCosTheta())).subtract(outgoing.getVector());
    }

    @Override public Point3D BRDF() {
        return Point3D.ZERO;
    }

    @Override public Point3D emittance(Direction outgoing, Basis basis) {
        return emittance;
    }
}
