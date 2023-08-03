package luca.raytracing;

import javafx.geometry.Point3D;

public interface Material {

    Point3D weightPDF(Direction outgoing, Basis basis);
    Point3D samplePDF(Direction outgoing, Basis basis);
    Point3D BRDF();
    Point3D emittance(Direction outgoing, Basis basis);
}
