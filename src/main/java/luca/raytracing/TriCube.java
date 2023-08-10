package luca.raytracing;

import javafx.geometry.Point3D;

public class TriCube extends WorldObject {

    private final double height;
    TriCube(Material mat, double h, Point3D pos, Point3D rot, int p) {
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
    }
}
