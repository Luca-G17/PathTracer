package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.Random;

public interface Material {
    Point3D weightPDF(Direction outgoing, Basis basis);
    Point3D samplePDF(Direction outgoing, Basis basis, Random random);
    Point3D BRDF();
    Point3D emittance(Direction outgoing, Basis basis);
}
