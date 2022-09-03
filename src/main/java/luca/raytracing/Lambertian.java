package luca.raytracing;

import javafx.geometry.Point3D;
import javafx.scene.effect.Light;

import java.util.Random;

public class Lambertian implements Material {
    private final Point3D albedo;
    private final Point3D emittance;

    Lambertian(Point3D albedo, Point3D emittance) {
        this.albedo = albedo;
        this.emittance = emittance;
    }
    Lambertian(Point3D albedo) {
        this.albedo = albedo;
        this.emittance = Point3D.ZERO;
    }
    @Override public Point3D weightPDF(Direction outgoing, Basis basis) {
        return this.albedo;
    }
    @Override public Point3D samplePDF(Direction outgoing, Basis basis, Random random) {
        double u1 = random.nextDouble();
        double u2 = random.nextDouble();

        double sinTheta = Math.sqrt(u1);
        double cosTheta = Math.sqrt(1 - u1);

        double phi = 2 * Math.PI * u2;

        Point3D dir = new Point3D(
                sinTheta * Math.cos(phi),
                cosTheta,
                sinTheta * Math.sin(phi));
        return basis.getTransform().MultiplyPoint3D(dir).normalize();
    }
    @Override public Point3D BRDF() {
        return albedo.multiply(1 / Math.PI);
    }
    @Override public Point3D emittance(Direction outgoing, Basis basis){
        return emittance;
    }
}
