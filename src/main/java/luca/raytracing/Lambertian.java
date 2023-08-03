package luca.raytracing;

import javafx.geometry.Point3D;

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
    @Override public Point3D samplePDF(Direction outgoing, Basis basis) {
        Random rand = new Random(System.nanoTime());
        double u1 = rand.nextDouble();
        double u2 = rand.nextDouble();

        double theta = Math.acos(2 * u1 - 1) - (Math.PI / 2);
        double phi = 2 * Math.PI * u2;

        double x = Math.cos(theta) * Math.cos(phi);
        double y = Math.cos(theta) * Math.sin(phi);
        double z = Math.sin(theta);
        Point3D dir = new Point3D(
                x,
                Math.abs(y),
                z
        );
        /*
        double sinTheta = Math.sqrt(u1);
        double cosTheta = Math.sqrt(1 - u1);

        double phi = 2 * Math.PI * u2;

        double x = sinTheta * Math.cos(phi);
        double z = sinTheta * Math.sin(phi);
        Point3D dir = new Point3D(
                sinTheta * Math.cos(phi),
                Math.sqrt(1.0 - x * x - z * z),
                sinTheta * Math.sin(phi));
        */
        /*
        double u = random.nextDouble();
        double v = random.nextDouble();
        double theta = u * 2.0 * Math.PI;
        double phi = Math.acos(2.0 * v - 1.0);
        double r = Math.cbrt(random.nextDouble());
        double sinTheta = Math.sin(theta);
        double cosTheta = Math.cos(theta);
        double sinPhi = Math.sin(phi);
        double cosPhi = Math.cos(phi);
        Point3D dir = new Point3D(
                r * sinPhi * cosTheta,
                r * sinPhi * sinTheta,
                Math.abs(r * cosPhi)
        );
        */
        return (basis.getTransform().MultiplyPoint3D(dir)).normalize();
    }

    @Override public Point3D BRDF() {
        return albedo.multiply(1 / Math.PI);
    }
    @Override public Point3D emittance(Direction outgoing, Basis basis){
        return emittance;
    }
}
