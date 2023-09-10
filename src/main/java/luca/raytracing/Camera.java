package luca.raytracing;

import javafx.geometry.Point3D;

public class Camera {
    private Point3D pos;
    private double pitch, yaw;
    private double fov;
    private double fovFactor;
    private Matrix view;
    public Camera(Point3D pos, double pitch, double yaw, double fov) {
        this.pos = pos;
        this.pitch = pitch;
        this.yaw = yaw;
        this.fov = fov;
        updateViewMatrix();
    }
    public void setPos(Point3D pos) {
        this.pos = pos;
        updateViewMatrix();
    }
    public void setPitch(double pitch) {
        this.pitch = pitch;
        updateViewMatrix();
    }
    public void setYaw(double yaw) {
        this.yaw = yaw;
        updateViewMatrix();
    }
    public void setFov(double fov) {
        this.fov = fov;
        updateViewMatrix();
    }
    public Point3D getPos() { return pos; }
    public double getPitch() { return pitch; }
    public double getYaw() { return yaw; }
    public double getFov() { return fov; }

    private void updateViewMatrix() {
        view = Matrix.Rotation(pitch, yaw, 0);
        this.fovFactor = 1.0 / Math.tan(fov / 2);
    }
    public Ray transformRay(double u, double v) {
        Ray ray = new Ray(new Point3D(0, 0, 0), new Point3D(u, v, fovFactor), false);
        return ray.transform(view, pos);
    }
}
