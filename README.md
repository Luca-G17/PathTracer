# PathTracer

This project is a backward path tracer written in Java using JavaFX for displaying the resultant renders. 

## Collisions
### Triangles
1. Check if the ray intersects the bounding box using the [slab-test](https://tavianator.com/2011/ray_box.html)
   ```Java
   public Optional<Collision> Collision(Ray ray) {
        // Slab test:
        double tx1 = (x.min - ray.getOrigin().getX()) * ray.getDirectionInv().getX();
        double tx2 = (x.max - ray.getOrigin().getX()) * ray.getDirectionInv().getX();
        double tmin = Math.min(tx1, tx2);
        double tmax = Math.max(tx1, tx2);
        double ty1 = (y.min - ray.getOrigin().getY()) * ray.getDirectionInv().getY();
        double ty2 = (y.max - ray.getOrigin().getY()) * ray.getDirectionInv().getY();
        tmin = Math.max(tmin, Math.min(ty1, ty2));
        tmax = Math.min(tmax, Math.max(ty1, ty2));

        if (tmax >= tmin) return Optional.of(new Collision());
        else return Optional.empty();
    }```
2. Find the intersection point $I$ of the ray and the plane that the triangle lies within
   ```Java
   public Optional<Point3D> HitLoc(Ray ray) {
        double x = normal.dotProduct(ray.getOrigin()); // n.p0
        double y = normal.dotProduct(ray.getDirection()); // n.u
        double t = (d - x) / y; // t = (d - n.p0) / n.u
        Point3D col = ray.getOrigin().add(ray.getDirection().multiply(t)); // p = p0 + tu
        if (RayHit(col)) {
            return Optional.of(col);
        }
        return Optional.empty();
   }
   ```
3. Check if $I$ point lies within the area of the triangle using [barycentric-coordinates](https://en.wikipedia.org/wiki/Barycentric_coordinate_system)
   ```Java
   public boolean RayHit(Point3D col) {
        // https://math.stackexchange.com/questions/4322/check-whether-a-point-is-within-a-3d-triangle
        Point3D A = lines.get("p1p2").getP0();
        Point3D B = lines.get("p2p3").getP0();
        Point3D C = lines.get("p3p1").getP0();
        double area = ((B.subtract(A)).crossProduct(C.subtract(A))).magnitude();
        Point3D PC = C.subtract(col);

        Point3D PB = B.subtract(col);

        Point3D PA = A.subtract(col);
        double alpha = PB.crossProduct(PC).magnitude() / area;
        double beta = PC.crossProduct(PA).magnitude() / area;
        double gamma = PA.crossProduct(PB).magnitude() / area;
        return  (alpha >= 0 && alpha <= 1) &&
                (beta  >= 0 && beta  <= 1) &&
                (gamma >= 0 && gamma <= 1) &&
                (Math.abs(alpha + beta + gamma - 1) <= 0.01);
   }
   ```
### Spheres
1. Check if the ray intersects with the bounding box
2. Find the intersection points between the ray and the sphere
   $$
     I= \begin{cases} \text{No Intersection} & d < 0 \\ \text{One Intersection Point} & d = 0 \\ \text{Two Intersection Points} & d > 0 \end{cases}
   $$
