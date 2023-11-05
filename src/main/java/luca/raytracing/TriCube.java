package luca.raytracing;

import javafx.geometry.Point3D;
import javafx.scene.shape.Mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;

public class TriCube extends MeshObject {

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
        back.id = "Back";
        right.id = "Right";
        bottom.id = "Bottom";
        // Instead rotate them around their centres
        front = front.RotateAroundCentre(MatrixNxM.RotationMatrix(Math.PI, 0, 0));
        right = right.RotateAroundCentre(MatrixNxM.RotationMatrix(0, Math.PI, 0));
        bottom = bottom.RotateAroundCentre(MatrixNxM.RotationMatrix(0, 0, Math.PI));

        this.mesh = new ArrayList<>(Arrays.asList(
                front,
                left,
                top,
                back,
                right,
                bottom
        )).stream().flatMap(r -> r.Triangles().stream()).toList();
        Translate(new Point3D(-height / 2, height / 2, -height / 2));
        Rotate(rot);
        Translate(pos);

        this.mesh.forEach(t -> t.id = "CUBE");
    }

    private void Rotate(Point3D rot) {
        MatrixNxM r = MatrixNxM.RotationMatrix(rot.getX(), rot.getY(), rot.getZ());
        this.mesh = this.mesh.stream().map(p -> p.Rotate(r)).toList();
    }

    private void Translate(Point3D t) {
        this.mesh = this.mesh.stream().map(p -> p.Translate(t)).toList();
        this.setPos(t);
    }

    public Point3D Centre() {
        // Get all points into hashset
        HashSet<Point3D> points = new HashSet<>();
        for (Poly rect : mesh) {
            points.addAll(rect.GetPoints());
        }

        Point3D total = Point3D.ZERO;
        for (Point3D p : points) {
            total = total.add(p);
        }
        return total.multiply(1.0 / points.size());
    }
    private void Scale(final double ScaleX, final double ScaleY, final double ScaleZ) {
        // Translate to centre of the cube
        Point3D centre = this.Centre();
        this.Translate(centre.multiply(-1));
        this.mesh = mesh.stream().map(p -> p.Scale(ScaleX, ScaleY, ScaleZ)).toList();
        this.Translate(centre);
    }
}
