package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TriPlane extends MeshObject {

    private final Point3D p1;
    private final Point3D p2;
    private final Point3D p3;
    private final Point3D p4;
    public TriPlane(Material mat, Point3D pos, Point3D p1, Point3D p2, Point3D p3, Point3D p4) {
        super(mat, pos);
        Triangle t1 = new Triangle(mat, p1, p2, p3);
        Triangle t2 = new Triangle(mat, p2, p3, p4);
        t2.FlipNormal();
        this.mesh = new ArrayList<>();
        this.mesh.add(t1);
        this.mesh.add(t2);
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
    }

    private TriPlane(Material mat, Point3D pos, MatrixNxM coords) {
        this(mat, pos,
                MatrixNxM.ListToPoint3D(coords.GetCol(0)),
                MatrixNxM.ListToPoint3D(coords.GetCol(1)),
                MatrixNxM.ListToPoint3D(coords.GetCol(2)),
                MatrixNxM.ListToPoint3D(coords.GetCol(3))
        );
    }

    public TriPlane Scale(double XScale, double YScale, double ZScale) {
        // Convert to MatrixNxM
        // Transform coords to origin, subtract the centre of the triplane from all coords
        // Multiply coords by scale matrix

        // Centre of the plane = average of all coords
        List<Double> centreOfPlane = MatrixNxM.Point3DtoList(CentreOfPlane());
        MatrixNxM translation = new MatrixNxM(Arrays.asList(
                centreOfPlane,
                centreOfPlane,
                centreOfPlane,
                centreOfPlane
        ));
        MatrixNxM scale = new MatrixNxM(Arrays.asList(
                Arrays.asList(XScale, 0.0, 0.0),
                Arrays.asList(0.0, YScale, 0.0),
                Arrays.asList(0.0, 0.0, ZScale)
        ));
        MatrixNxM coordsMatrix = GetCoordMatrix();
        coordsMatrix = coordsMatrix.Add(translation.Multiply(-1.0));
        coordsMatrix = scale.Multiply(coordsMatrix);
        coordsMatrix = coordsMatrix.Add(translation);
        return new TriPlane(this.getMat(), this.getPos(), coordsMatrix);
    }
    public TriPlane Translate(double XTranslate, double YTranslate, double ZTranslate) {
        MatrixNxM coordsMatrix = GetCoordMatrix();
        MatrixNxM translation = new MatrixNxM(Arrays.asList(
                Arrays.asList(XTranslate, YTranslate, ZTranslate),
                Arrays.asList(XTranslate, YTranslate, ZTranslate),
                Arrays.asList(XTranslate, YTranslate, ZTranslate),
                Arrays.asList(XTranslate, YTranslate, ZTranslate)
        ));
        coordsMatrix = coordsMatrix.Add(translation);
        return new TriPlane(this.getMat(), this.getPos(), coordsMatrix);
    }

    public void SetMaterial(Material mat) {
        this.setMat(mat);
    }

    private Point3D CentreOfPlane() {
        Point3D total = p1.add(p2).add(p3).add(p4);
        return total.multiply(1.0 / 4.0);
    }
    private MatrixNxM GetCoordMatrix() {
        List<List<Double>> coords = new ArrayList<>();
        coords.add(MatrixNxM.Point3DtoList(p1));
        coords.add(MatrixNxM.Point3DtoList(p2));
        coords.add(MatrixNxM.Point3DtoList(p3));
        coords.add(MatrixNxM.Point3DtoList(p4));
        return new MatrixNxM(coords);
    }

}
