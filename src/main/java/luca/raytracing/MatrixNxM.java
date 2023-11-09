package luca.raytracing;

import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatrixNxM {

    private final int n;
    private final List<List<Double>> cols;
    private List<List<Double>> rows;
    public MatrixNxM(List<List<Double>> cols) {
        this.cols = cols;
        this.n = cols.size();
        PopulateRows();
    }
    public MatrixNxM(Point3D c0, Point3D c1, Point3D c2) {
        this.n = 3;
        this.cols = new ArrayList<>();
        cols.add(Point3DtoList(c0));
        cols.add(Point3DtoList(c1));
        cols.add(Point3DtoList(c2));
        PopulateRows();
    }
    private void PopulateRows() {
        rows = new ArrayList<>();
        for (int i = 0; i < cols.get(0).size(); i++) {
            List<Double> row = new ArrayList<>();
            for (List<Double> col : cols) {
                row.add(col.get(i));
            }
            rows.add(row);
        }
    }
    public MatrixNxM Multiply(MatrixNxM m) {
        List<List<Double>> newMatrixCols = new ArrayList<>();
        for (List<Double> col : m.cols) {
            List<Double> newCol = new ArrayList<>();
            for (List<Double> row : this.rows) {
                newCol.add(DotProduct(col, row));
            }
            newMatrixCols.add(newCol);
        }
        return new MatrixNxM(newMatrixCols);
    }

    public Point3D Multiply(Point3D p) {
        assert(this.cols.size() == 3);
        MatrixNxM point = this.Multiply(new MatrixNxM(List.of(Point3DtoList(p))));
        return ListToPoint3D(point.cols.get(0));
    }

    public MatrixNxM Multiply(double m) {
        List<List<Double>> newMatrixCols = new ArrayList<>();
        for (List<Double> col : this.cols) {
            List<Double> newCol = new ArrayList<>();
            for (int f = 0; f < this.rows.size(); f++) {
                newCol.add(col.get(f) * m);
            }
            newMatrixCols.add(newCol);
        }
        return new MatrixNxM(newMatrixCols);
    }

    public MatrixNxM Add(MatrixNxM m) {
        List<List<Double>> newMatrixCols = new ArrayList<>();
        for (int i = 0; i < this.cols.size(); i++) {
            List<Double> newCol = new ArrayList<>();
            for (int f = 0; f < this.rows.size(); f++) {
                newCol.add(this.cols.get(i).get(f) + m.cols.get(i).get(f));
            }
            newMatrixCols.add(newCol);
        }
        return new MatrixNxM(newMatrixCols);
    }

    private double DotProduct(List<Double> l1, List<Double> l2) {
        double total = 0;
        for (int i = 0; i < l1.size(); i++) {
            total += l1.get(i) * l2.get(i);
        }
        return total;
    }

    public List<Double> GetCol(int i) {
        return cols.get(i);
    }
    public static List<Double> Point3DtoList(Point3D point) {
        return Arrays.asList(point.getX(), point.getY(), point.getZ());
    }

    public static Point3D ListToPoint3D(List<Double> doubleList) {
        assert(doubleList.size() == 3);
        return new Point3D(doubleList.get(0), doubleList.get(1), doubleList.get(2));
    }

    public static MatrixNxM RotationMatrix(double x, double y, double z) {
        //  Yaw
        //  | cos(x) | -sin(x) | 0 |
        //  | sin(x) |  cos(x) | 0 |    = Rx(x)
        //  |   0    |    0    | 1 |
        //
        //  Pitch
        //  |  cos(y) | 0 | sin(y) |
        //  |    0    | 1 |   0    |    = Ry(y)
        //  | -sin(y) | 0 | cos(y) |
        //
        //  Roll
        //  | 1 |   0    |    0    |
        //  | 0 | cos(z) | -sin(z) |    = Rz(z)
        //  | 0 | sin(z) |  cos(z) |
        //
        // R = Rx(x) * Ry(y) * Rz(z)
        List<List<Double>> yawCols = new ArrayList<>();
        yawCols.add(Arrays.asList(Math.cos(z), Math.sin(z), 0.0));
        yawCols.add(Arrays.asList(-Math.sin(z), Math.cos(z), 0.0));
        yawCols.add(Arrays.asList(0.0, 0.0, 1.0));

        List<List<Double>> pitchCols = new ArrayList<>();
        pitchCols.add(Arrays.asList(Math.cos(y), 0.0, -Math.sin(y)));
        pitchCols.add(Arrays.asList(0.0, 1.0, 0.0));
        pitchCols.add(Arrays.asList(Math.sin(y), 0.0, Math.cos(y)));

        List<List<Double>> rollCols = new ArrayList<>();
        rollCols.add(Arrays.asList(1.0, 0.0, 0.0));
        rollCols.add(Arrays.asList(0.0, Math.cos(x), Math.sin(x)));
        rollCols.add(Arrays.asList(0.0, -Math.sin(x), Math.cos(x)));

        MatrixNxM yaw = new MatrixNxM(yawCols);
        MatrixNxM pitch = new MatrixNxM(pitchCols);
        MatrixNxM roll = new MatrixNxM(rollCols);
        return yaw.Multiply(pitch).Multiply(roll);
    }
}
