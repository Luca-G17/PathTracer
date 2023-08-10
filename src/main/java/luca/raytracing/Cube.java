package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.Arrays;

public class Cube extends WorldObject {
    private final double height;
    Cube(Material mat, double h, Point3D pos, Point3D rot) {
        super(mat, pos);
        this.height = h;
        Line x = new Line(Point3D.ZERO, new Point3D(1, 0, 0), h);
        Line y = new Line(Point3D.ZERO, new Point3D(0, -1, 0), h);
        Line z = new Line(Point3D.ZERO, new Point3D(0, 0, 1), h);
        Polygon front = new Polygon(Arrays.asList( // Front
                x,
                y,
                x.translate(new Point3D(0, -height, 0)),
                y.translate(new Point3D(height, 0, 0))
        ), "Front");
        Polygon left = new Polygon(Arrays.asList(
                y,
                z,
                y.translate(new Point3D(0, 0, height)),
                z.translate(new Point3D(0, -height, 0))
        ), "Left");
        Polygon top = new Polygon(Arrays.asList(
                x,
                z,
                x.translate(new Point3D(0, 0, height)),
                z.translate(new Point3D(height, 0, 0))
        ), "Top");
        Polygon back = front.Translate(new Point3D(0, 0, height));
        Polygon right = left.Translate(new Point3D(height, 0, 0));
        Polygon bottom = top.Translate(new Point3D(0, -height, 0));
        back.setId("Back"); right.setId("Right"); bottom.setId("Bottom");
        this.mesh = new ArrayList<>(Arrays.asList(
                front,
                left,
                top,
                back,
                right,
                bottom
        ));
        rotate(rot);
        translate(pos);
        flipEdgeNormal("Back");
        flipEdgeNormal("Right");
        flipEdgeNormal("Top");
    }
    private void flipEdgeNormal(String id) {
        for (Poly p : mesh) {
            if (p.getId().equals(id)) {
                p.FlipNormal();
            }
        }
    }
    private void translate(Point3D t) {
        this.mesh = getMesh().stream().map(p -> p.Translate(t)).toList();
        this.setPos(t);
    }
    private void rotate(Point3D rot) {
        Matrix rPitch = Matrix.RotationVectorAxis(rot.getX(), new Point3D(1, 0, 0));
        Matrix rYaw = Matrix.RotationVectorAxis(rot.getY(), new Point3D(0, 1, 0));
        Matrix rRoll = Matrix.RotationVectorAxis(rot.getZ(), new Point3D(0, 0, 1));
        Matrix r = Matrix.Combine(Arrays.asList(rPitch, rYaw, rRoll));
        this.mesh = getMesh().stream().map(p -> p.Rotate(r)).toList();
    }
}
