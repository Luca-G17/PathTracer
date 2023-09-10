package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.Random;

public class Lambertian implements Material {
    private final Point3D albedo;
    private final Point3D emittance;
    private final Random random;

    Lambertian(Point3D albedo, Point3D emittance) {
        this.albedo = albedo;
        this.emittance = emittance;
        this.random = new Random(System.nanoTime());
    }
    Lambertian(Point3D albedo, double mutator) {
        this.albedo = albedo.multiply(mutator);
        this.emittance = Point3D.ZERO;
        this.random = new Random(System.nanoTime());
    }
    @Override public Point3D weightPDF(Direction outgoing, Basis basis) {
        return this.albedo;
    }
    @Override public PostCollision samplePDF(Direction outgoing, Basis basis, boolean isInsideMesh) {
        double r1 = random.nextDouble();
        double r2 = random.nextDouble();

        double phi = 2.0 * Math.PI * r1;
        double x = Math.cos(phi) * Math.sqrt(r2);
        double y = Math.sqrt(1 - r2);
        double z = Math.sin(phi) * Math.sqrt(r2);
        Point3D dir = new Point3D(x, y, z);
        return new PostCollision((basis.getTransform().MultiplyPoint3D(dir)).normalize(), false);
    }

    @Override public Point3D emittance(Direction outgoing, Basis basis){
        return emittance;
    }
}
