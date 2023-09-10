package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.Random;

public class Dielectric implements Material {

    private final double refractiveIndex;
    private final Point3D emittance;
    private final Point3D albedo;
    private final Random random;
    public Dielectric(final double refractiveIndex) {
        this.refractiveIndex = refractiveIndex;
        this.emittance = new Point3D(0, 0, 0);
        this.albedo = new Point3D(1.0, 1.0, 1.0);
        this.random = new Random(System.nanoTime());
    }

    @Override
    public Point3D weightPDF(Direction outgoing, Basis basis) {
        return this.albedo;
    }

    @Override
    public PostCollision samplePDF(Direction outgoing, Basis basis, boolean isInsideMesh) {

        Point3D normal = basis.getNormal();
        if (isInsideMesh) {
            normal = normal.multiply(-1);
        }
        Point3D incoming = outgoing.getVector().multiply(-1);
        double cosTheta = Math.min(outgoing.getVector().dotProduct(normal), 1.0);
        double sinTheta = Math.sqrt(1 - (cosTheta * cosTheta));
        double refractionRatio = 1.0 / refractiveIndex;
        if (isInsideMesh) {
            refractionRatio =  refractiveIndex;
        }

        // boolean cannotRefract = refractionRatio * sinTheta > 1.0;
        if (SchlickReflectance(cosTheta) > random.nextDouble()) {
            return new PostCollision(Mirror.reflect(outgoing, basis), false);
        } else {
            Point3D outPerpendicular = (incoming.add(normal.multiply(cosTheta))).multiply(refractionRatio);
            Point3D outParallel = normal.multiply(-Math.sqrt(Math.abs(1.0 - outPerpendicular.dotProduct(outPerpendicular))));
            return new PostCollision(outPerpendicular.add(outParallel), true);
        }
    }

    private double SchlickReflectance(double cosTheta) {
        double r0 = (1 - refractiveIndex) / (1 + refractiveIndex);
        r0 = r0 * r0;

        double d =  r0 + (1 - r0) * Math.pow(1 - cosTheta, 5);
        return d;
    }

    @Override
    public Point3D emittance(Direction outgoing, Basis basis) {
        return emittance;
    }
}
