package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class BVH implements Hittable {
    private Hittable left;
    private Hittable right;
    private AABB bbox;
    private List<Hittable> primitives;
    private static final int SAH_BUCKETS = 12;
    private static final double MAX_TRIANGLE_AREA = 50;
    public BVH(List<Hittable> primitives) {
        this.primitives = primitives;
        GenerateBoundingBox();
        if (primitives.size() == 0) return;
        // Now split the AABB along its longest axis
        Point3D span = bbox.Span();
        int axis = 0;
        if (span.getY() > span.getX()) axis = 1;
        if (span.getZ() > VectorMath.P3At(span, axis)) axis = 2;
        double split = SAHSplit(axis);
        List<Hittable> leftT = new ArrayList<>();
        List<Hittable> rightT = new ArrayList<>();
        for (Hittable t : primitives) {
            if (VectorMath.P3At(t.GetCentre(), axis) < split)
                leftT.add(t);
            else
                rightT.add(t);
        }
        if (rightT.size() == 0) {
            left = new HittableList(leftT);
            right = new BVH(rightT);
            System.out.printf("Root: %d\n", leftT.size());
            return;
        }
        if (leftT.size() == 0) {
            right = new HittableList(rightT);
            left = new BVH(leftT);
            System.out.printf("Root: %d\n", rightT.size());
            return;
        }

        if (leftT.size() == 1)
            left = leftT.get(0);
        else
            left = new BVH(leftT);
        if (rightT.size() == 1)
            right = rightT.get(0);
        else
            right = new BVH(rightT);
    }

    public static List<Triangle> TriangleListSubdivision(List<Triangle> triangles) {
        List<Triangle> subdividedTriangles = new ArrayList<>();
        for (Triangle t : triangles) {
            subdividedTriangles.addAll(TriangleSubdivision(t));
        }
        return subdividedTriangles;
    }

    public static List<Triangle> TriangleSubdivision(Triangle t) {
        if (t.GetBoundingBox().LargestArea() > MAX_TRIANGLE_AREA) {
            // Find the longest edge
            int longestI = 0;
            List<Line> lines = t.Lines().values().stream().toList();
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).getLength() > lines.get(longestI).getLength()) {
                    longestI = i;
                }
            }
            Line longest = lines.get(longestI);
            Point3D opposing = Point3D.ZERO;
            // Find opposing point
            for (Point3D p : t.GetPoints()) {
                if (!VectorMath.P3Equal(p, longest.getP0()) && !VectorMath.P3Equal(p, longest.getP1())) {
                    opposing = p;
                    break;
                }
            }

            // Find midpoint along longest edge
            Point3D midpoint = longest.getP0().midpoint(longest.getP1());

            // May have to sort out winding order
            Point3D normal = t.GetNormal();
            Point3D t1Norm = (longest.getP0().subtract(midpoint)).crossProduct(opposing.subtract(longest.getP0()));
            Point3D t2Norm = (midpoint.subtract(opposing).crossProduct(longest.getP1().subtract(midpoint)));

            Triangle t1;
            Triangle t2;
            if (t1Norm.dotProduct(normal) > 0)
                t1 = new Triangle(t.getMat(), midpoint, longest.getP0(), opposing);
            else
                t1 = new Triangle(t.getMat(), longest.getP0(), midpoint, opposing);
            if (t2Norm.dotProduct(normal) > 0)
                t2 = new Triangle(t.getMat(), opposing, midpoint, longest.getP1());
            else
                t2 = new Triangle(t.getMat(), midpoint, opposing, longest.getP1());

            List<Triangle> ts1 = TriangleSubdivision(t1);
            List<Triangle> ts2 = TriangleSubdivision(t2);
            return Stream.concat(ts1.stream(), ts2.stream()).toList();
        }
        return List.of(t);
    }

    // Surface area heuristic splitting
    public double SAHSplit(int axis) {
        Point3D span = bbox.Span();
        int[] bucketCounts = new int[SAH_BUCKETS];
        AABB[] buckets = new AABB[SAH_BUCKETS];
        for (int i = 0; i < SAH_BUCKETS; i++) buckets[i] = new AABB();
        for (int i = 0; i < primitives.size(); i++) {
            int b = (int) Math.floor((VectorMath.P3At(primitives.get(i).GetCentre(), axis) - bbox.axis(axis).min) / VectorMath.P3At(span, axis) * SAH_BUCKETS) - 1;
            if (b == -1) b++;
            bucketCounts[b]++;
            buckets[b] = new AABB(buckets[b], primitives.get(i).GetBoundingBox());
        }
        double[] costs = new double[SAH_BUCKETS - 1];
        for (int i = 0; i < SAH_BUCKETS - 1; i++) {
            AABB b0 = new AABB(), b1 = new AABB();
            int count0 = 0, count1 = 0;
            for (int j = 0; j <= i; j++) {
                b0 = new AABB(b0, buckets[j]);
                count0 += bucketCounts[j];
            }
            for (int j = i + 1; j < SAH_BUCKETS; j++) {
                b1 = new AABB(b1, buckets[j]);
                count1 += bucketCounts[j];
            }
            costs[i] = 0.125f + (count0 * b0.AreaInAxis(axis) + count1 * b1.AreaInAxis(axis)) / bbox.AreaInAxis(axis);
        }
        int minCostI = 0;
        for (int i = 0; i < SAH_BUCKETS - 1; i++) {
            if (costs[i] < costs[minCostI]) {
                minCostI = i;
            }
        }
        return VectorMath.P3At(span, axis) * minCostI / SAH_BUCKETS + bbox.axis(axis).min;
    }
    @Override
    public AABB GetBoundingBox() {
        return null;
    }

    @Override
    public void GenerateBoundingBox() {
        AABB bbox = new AABB();
        for (Hittable p : primitives) {
            bbox = new AABB(bbox, p.GetBoundingBox());
        }
        this.bbox = bbox;
    }

    @Override
    public Optional<WorldObject.Collision> Collision(Ray ray) {
        if (bbox.Collision(ray).isEmpty()) return Optional.empty();
        Optional<WorldObject.Collision> leftC;
        Optional<WorldObject.Collision> rightC;
        if (left == null) leftC = Optional.empty();
        else leftC = left.Collision(ray);
        if (right == null) rightC = Optional.empty();
        else rightC = right.Collision(ray);

        if (leftC.isPresent() && rightC.isPresent()) {
            double d1 = VectorMath.Length2(leftC.get().point().subtract(ray.getOrigin()));
            double d2 = VectorMath.Length2(rightC.get().point().subtract(ray.getOrigin()));
            if (d1 < d2)
                return leftC;
            else
                return rightC;
        }
        else if (leftC.isPresent()) {
            return leftC;
        }
        else if (rightC.isPresent()) {
            return rightC;
        }
        return Optional.empty();
    }

    @Override
    public Point3D GetCentre() {
        return null;
    }

    private record HittableList(List<Hittable> hittables) implements Hittable {
        @Override
            public AABB GetBoundingBox() {
                return null;
            }

            @Override
            public void GenerateBoundingBox() {

            }

            @Override
            public Optional<WorldObject.Collision> Collision(Ray ray) {
                WorldObject.Collision min = new WorldObject.Collision(Point3D.ZERO, Material.EMPTY, Point3D.ZERO, Integer.MAX_VALUE);
                for (Hittable h : hittables) {
                    Optional<WorldObject.Collision> col = h.Collision(ray);
                    if (col.isPresent()) {
                        if (col.get().compareTo(min) < 0) {
                            min = col.get();
                        }
                    }
                }
                if (min.dist() == Integer.MAX_VALUE)
                    return Optional.empty();
                return Optional.of(min);
            }

            @Override
            public Point3D GetCentre() {
                return null;
            }
        }
}
