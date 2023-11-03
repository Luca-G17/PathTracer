package luca.raytracing;

import javafx.geometry.Point3D;

// Axis Aligned Bounding Box in 3D
public class AABB {
    Interval x, y, z;

    public AABB(Point3D min, Point3D max) {
        this.x = new Interval(min.getX(), max.getX());
        this.y = new Interval(min.getY(), max.getY());
        this.z = new Interval(min.getZ(), max.getZ());
    }
    public AABB(Interval x, Interval y, Interval z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Combine two AABBs by combining their intervals
    public AABB(AABB b0, AABB b1) {
        this.x = new Interval(b0.x, b1.x);
        this.y = new Interval(b0.y, b1.y);
        this.z = new Interval(b0.z, b1.z);
    }

    public static class Interval {
        double min, max;

        Interval(double min, double max) {
            this.min = min;
            this.max = max;
        }
        // Take the Union of two intervals
        Interval(Interval i0, Interval i1) {
            this.min = Math.min(i0.min, i1.min);
            this.max = Math.max(i0.max, i1.max);
        }
    }
}
