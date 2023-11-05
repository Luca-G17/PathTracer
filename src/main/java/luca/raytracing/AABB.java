package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.Optional;

// Axis Aligned Bounding Box in 3D
public class AABB extends WorldObject {
    Interval x, y, z;

    public AABB() {
        this.x = new Interval(Integer.MAX_VALUE, Integer.MIN_VALUE);
        this.y = new Interval(Integer.MAX_VALUE, Integer.MIN_VALUE);
        this.z = new Interval(Integer.MAX_VALUE, Integer.MIN_VALUE);
    }

    public AABB(Point3D min, Point3D max) {
        super();
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

    public Point3D Span() {
        return new Point3D(x.size(), y.size(), z.size());
    }

    public Interval axis(int a) {
        if (a == 0) return x;
        if (a == 1) return y;
        return z;
    }

    @Override
    public Optional<Collision> Collision(Ray ray) {
        // Slab test:
        double tx1 = (x.min - ray.getOrigin().getX()) * ray.getDirectionInv().getX();
        double tx2 = (x.max - ray.getOrigin().getX()) * ray.getDirectionInv().getX();
        double tmin = Math.min(tx1, tx2);
        double tmax = Math.max(tx1, tx2);
        double ty1 = (y.min - ray.getOrigin().getY()) * ray.getDirectionInv().getY();
        double ty2 = (y.max - ray.getOrigin().getY()) * ray.getDirectionInv().getY();
        tmin = Math.max(tmin, Math.min(ty1, ty2));
        tmax = Math.min(tmax, Math.max(ty1, ty2));

        if (tmax >= tmin) return Optional.of(new Collision());
        else return Optional.empty();
    }

    public double AreaInAxis(int axis) {
        int a1 = 0;
        int a2 = 1;
        if (axis == 2)
            a1 = 2;
        return axis(a1).size() * axis(a2).size();
        // if axis = x = 0
        // we want the area in the x-y plane 0,1
        // if axis = y = 1
        // we want the area in the x-y plane 0,1
        // if axis = z = 2
        // we want the area in the y-z plane 1,2
    }

    public double LargestArea() {
        return Math.max(Math.max(AreaInAxis(0), AreaInAxis(1)), AreaInAxis(2));
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
        public double size() {
            return max - min;
        }
    }
}
