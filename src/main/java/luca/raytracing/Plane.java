package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Plane extends WorldObject {
    private final double length;
    Plane(Material mat, double l, double pitch, double roll, Point3D pos) {
        super(mat, pos);
        this.length = l;
        Line x = new Line(Point3D.ZERO, new Point3D(1, 0, 0), l);
        Line y = new Line(Point3D.ZERO, new Point3D(0, -1, 0), l);
        Line z = new Line(Point3D.ZERO, new Point3D(0, 0, 1), l);
        Polygon p = new Polygon(Arrays.asList(
                x,
                z,
                x.translate(new Point3D(0, 0, l)),
                z.translate(new Point3D(l,0, 0))
        ));
        Matrix rPitch = Matrix.RotationVectorAxis(pitch, new Point3D(1, 0, 0));
        Matrix rRoll = Matrix.RotationVectorAxis(roll, new Point3D(0, 0, 1));
        Matrix r = Matrix.Combine(Arrays.asList(rPitch, rRoll));
        p = p.rotate(r);
        p = p.translate(pos);
        p.flipNormal();
        mesh = new ArrayList<>(List.of(p));
    }
}
