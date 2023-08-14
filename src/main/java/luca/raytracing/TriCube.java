package luca.raytracing;

import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.Arrays;

public class TriCube extends WorldObject {

    private final double height;
    TriCube(Material mat, double h, Point3D pos, Point3D rot) {
        super(mat, pos);
        this.height = h;
        Point3D x = new Point3D(h, 0, 0);
        Point3D y = new Point3D(0, -h, 0);
        Point3D z = new Point3D(0, 0, h);
        Rectangle front = new Rectangle(
                Point3D.ZERO,
                y,
                x,
                x.add(y),
                mat,
                "Front"
        );
        Rectangle left = new Rectangle(
                Point3D.ZERO,
                y,
                z,
                y.add(z),
                mat,
                "Left"
        );
        Rectangle top = new Rectangle(
                Point3D.ZERO,
                z,
                x,
                z.add(x),
                mat,
                "Top"
        );
        Rectangle back = front.Translate(new Point3D(0, 0, h));
        Rectangle right = left.Translate(new Point3D(h, 0, 0));
        Rectangle bottom = top.Translate(new Point3D(0, -h, 0));
        this.mesh = new ArrayList<>(Arrays.asList(
                front,
                left,
                top,
                back,
                right,
                bottom
        ));
        Rotate(rot);
        front.FlipNormal();
        right.FlipNormal();
        bottom.FlipNormal();
        Translate(pos);
    }
    private void Rotate(Point3D rot) {
        Matrix rPitch = Matrix.RotationVectorAxis(rot.getX(), new Point3D(1, 0, 0));
        Matrix rYaw = Matrix.RotationVectorAxis(rot.getY(), new Point3D(0, 1, 0));
        Matrix rRoll = Matrix.RotationVectorAxis(rot.getZ(), new Point3D(0, 0, 1));
        Matrix r = Matrix.Combine(Arrays.asList(rPitch, rYaw, rRoll));
        this.mesh = getMesh().stream().map(p -> p.Rotate(r)).toList();
    }
    private void Translate(Point3D t) {
        this.mesh = getMesh().stream().map(p -> p.Translate(t)).toList();
        this.setPos(t);
    }
}
