package luca.raytracing;

import javafx.geometry.Point3D;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface Poly {
    String id = "";

    Point3D GetNormal();
    Poly Rotate(MatrixNxM rotation);
    Poly Translate(Point3D t);
    Poly Scale(double ScaleX, double ScaleY, double ScaleZ);
    boolean RayHit(Point3D col);
    void FlipNormal();
    default String getId() {
        return id;
    }
    Optional<Point3D> HitLoc(Ray ray);
    List<Point3D> GetPoints();
}
