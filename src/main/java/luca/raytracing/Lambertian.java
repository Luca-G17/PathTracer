package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.Random;

public class Lambertian implements Material {
    private final Point3D albedo;
    private final Point3D emittance;
    private final Random random;
    private final double smoothness;
    Lambertian(Point3D albedo, Point3D emittance, double smoothness, Random rand) {
        this.albedo = albedo;
        this.emittance = emittance;
        this.random = rand;
        this.smoothness = smoothness;
    }
    Lambertian(Point3D albedo, double mutator, double smoothness, Random rand) {
        this.albedo = albedo.multiply(mutator);
        this.emittance = Point3D.ZERO;
        this.smoothness = smoothness;
        this.random = rand;
    }
    @Override public Point3D weightPDF(Direction outgoing, Basis basis) {
        return this.albedo;
    }

    enum SAMPLING_MODE {
        UNIFORM,
        COSINE_WEIGHTED,
    }

    @Override public PostCollision samplePDF(Direction outgoing, Basis basis, boolean isInsideMesh) {

        double r1 = random.nextDouble();
        double r2 = random.nextDouble();
        double phi = 2.0 * Math.PI * r1;
        double x = Math.cos(phi) * Math.sqrt(r2);
        double y = Math.sqrt(1 - r2);
        double z = Math.sin(phi) * Math.sqrt(r2);

        Point3D diffuseDir = new Point3D(x, y, z);
        diffuseDir = (basis.getTransform().Multiply(diffuseDir)).normalize();
        Point3D specularDir = SpecularReflect(outgoing, basis);

        diffuseDir = VectorMath.Lerp(diffuseDir, specularDir, smoothness);
        return new PostCollision(diffuseDir, false);
    }

    public static Point3D SpecularReflect(Direction outgoing, Basis basis) {
        Point3D normal = basis.getNormal().normalize();
        Point3D incoming = outgoing.getVector().multiply(-1).normalize();
        return incoming.subtract(normal.multiply(2.0 * normal.dotProduct(incoming)));
    }

    @Override public Point3D emittance(Direction outgoing, Basis basis){
        return emittance;
    }
}
