package luca.raytracing;

public interface Hittable {
    public AABB GetBoundingBox();
    public  void GenerateBoundingBox();
}
