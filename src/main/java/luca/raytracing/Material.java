package luca.raytracing;

import javafx.geometry.Point3D;

public interface Material {

    Point3D weightPDF(Direction outgoing, Basis basis);
    PostCollision samplePDF(Direction outgoing, Basis basis, boolean isInsideMesh);
    Point3D emittance(Direction outgoing, Basis basis);
    record PostCollision(Point3D outVector, boolean isRefracted) {
    }
}
