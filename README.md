# PathTracer

This project is a backward path tracer written in Java using JavaFX for displaying the resultant renders. 

## Contents ##
- [Collisions](#collisions)
- [Tracing Function](#tracing-function)
- [Parallelization](#parallelization)
## Tracing Function ##
1. Find the closest collision to the ray origin between the ray and the scene.
2. Get the colour of the object collided with.
3. If the material has emittance return.
4. Get the reflection direction. This is a linear interpolation for Lambertian materials between specular reflection and diffuse reflection, scaled by the material's smoothness. For Dielectrics Snell's law is used to compute the refracted ray.
5. Recurse with the new ray returning the product of the throughput colours, if the ray hits an emissive material this product is multiplied by the emissivity.
```Java
 public Point3D traceRayRecursive(Ray ray, int depth) {
     Optional<WorldObject.Collision> optCol = rayCollisionBVH(ray);
     if (depth > maxDepth) {
         return Point3D.ZERO;
     }
     if (optCol.isEmpty()) {
         return new Point3D(0.1f, 0.1f, 0.1f);
     }
     WorldObject.Collision col = optCol.get();
     Basis basis = new Basis(col.normal());
     Direction outgoing = new Direction(ray.getDirection().multiply(-1), basis);

     Material mat = col.mat();
     Point3D throughput = mat.weightPDF(outgoing, basis);

     if (mat.emittance(outgoing, basis).magnitude() != 0) { // i.e its a light
         return mat.emittance(outgoing, basis);
     }

     Material.PostCollision postCol = mat.samplePDF(outgoing, basis, ray.IsInsideMesh());
     boolean rayIsInsideMesh = ray.IsInsideMesh() ^ postCol.isRefracted();
     double offset = !rayIsInsideMesh ? 0.001 : -0.001; // Prevent surface acne
     Ray newRay = new Ray(col.point().add(col.normal().multiply(offset)), postCol.outVector(), rayIsInsideMesh);
     return vectorMultiply(throughput, traceRayRecursive(newRay, depth + 1));
}
```
## Collisions
### Triangles
1. Check if the ray intersects the bounding box using the [slab test](https://tavianator.com/2011/ray_box.html)  
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
 }
```
   
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
3. Check if $I$ point lies within the area of the triangle using [barycentric coordinates](https://en.wikipedia.org/wiki/Barycentric_coordinate_system)  
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
   - If discriminant == 0: 1 Intersection Point
   - If discriminant >  0: 2 Intersection Points
   - If discriminant <  0: No Intersection
```Java
public Optional<Point3D> HitLoc(Ray ray) {
     // Sphere: r^2 = (P - C)^2 = P^2 - 2PC + C^2
     // Line: P = p0 + tV
     // Discriminant: b^2 - 4ac
     final Point3D v = ray.getDirection();
     final Point3D p0 = ray.getOrigin();
     final double a = v.dotProduct(v);
     final double b = 2.0 * (v.dotProduct(p0) - v.dotProduct(centre));
     final double c = -2.0 * (p0.dotProduct(centre)) + centre.dotProduct(centre) + p0.dotProduct(p0) - (radius * radius);
     final double discriminant = (b * b) - (4.0 * a * c);
     if (discriminant == 0) {
         double t = -b / (2.0 * a);
         Point3D c1 = p0.add(v.multiply(t));
         return Optional.of(c1);
     } else if (discriminant > 0) {
         // Use closer collision point
         double t1 = (-b + Math.sqrt(discriminant)) / (2.0 * a);
         double t2 = (-b - Math.sqrt(discriminant)) / (2.0 * a);
         Point3D c1 = p0.add(v.multiply(t1));
         Point3D c2 = p0.add(v.multiply(t2));
         Point3D closer;
         Point3D further;
         if (VectorMath.Length2(c1.subtract(p0)) <= VectorMath.Length2(c2.subtract(p0))) {
             closer = c1;
             further = c2;
         } else {
             closer = c2;
             further = c1;
         }
         boolean closerBehind = (closer.subtract(p0)).dotProduct(v) < 0;
         boolean furtherBehind = (further.subtract(p0).dotProduct(v)) < 0;
         if (!closerBehind) {
             return Optional.of(closer);
         } else if (!furtherBehind) {
             return Optional.of(further);
         }
     }
     // No Roots
     return Optional.empty();
}
```
## Parallelization
### Producer
1. The producer's job is for N samples, add each pixel to the work queue for the workers to trace a ray from.
2. The producer waits on the queue mutex, once acquired it adds a pixel location to the work queue, it then decrements the full semaphore if > 0.
3. Once the entire image is in the queue the producer waits until the 'finished' countdown latch reaches zero, at which point the worker threads have finished processing the current image. This means we can output a complete image for every sample.
```Java
public void startTracing() throws InterruptedException {
     Color[][] bitmap = initBitmap(HEIGHT, WIDTH);
     // Startup Threads:
     final int threads = 12;
     final Semaphore full = new Semaphore(0);
     final Semaphore empty = new Semaphore(WIDTH);
     final CountDownLatch finished = new CountDownLatch(WIDTH * HEIGHT);
     Object queueMutex = new Object();
     final Object bitmapMutex = new Object();
     final Deque<int[]> workQueue = new ArrayDeque<>();
     for (int i = 0; i < threads; i++) {
         Thread t = new Thread(new Worker(full, empty, finished, workQueue, queueMutex, bitmapMutex, uScale, vScale, bitmap));
         t.start();
     }
     long delta = 0;
     // Producer Loop:
     for (int s = 0; s < SAMPLES; s++) {
         long start = System.currentTimeMillis();
         for (int y = 0; y < HEIGHT; y++) {
             for (int x = 0; x < WIDTH; x++) {
                 try {
                     empty.acquire();
                     synchronized (queueMutex) {
                         workQueue.add(new int[]{x, y, s});
                     }
                     full.release();
                 } catch (InterruptedException e) {
                     System.out.println("Producer Interrupted, EXCEPTION: " + e);
                 }
             }
         }
         finished.await();
     }
}
```
### Worker
1. The worker's job is to take pixels from the work queue, trace a ray from the pixel, and then compute the rolling average of the pixel's existing colour and colour generated from tracing the ray given the current sample count.
2. The workers will first wait on the full semaphore until it is > 0, once acquired they will wait on the queueMutex preventing race conditions between other workers and the producer. They 
```Java
public void run() {
   while (true) {
       try {
           full.acquire();
           int[] coords;
           synchronized (queueMutex) {
               coords = workQueue.pop();
           }
           int x = coords[0];
           int y = coords[1];
           int s = coords[2];
           double u = 2 * (((double)x + 0.5) / (WIDTH - 1)) - 1;
           double v = 1 - (2 * (((double)y + 0.5) / (HEIGHT - 1)));
           u *= UScale;
           v *= VScale;
           Ray ray = camera.transformRay(u, v);
           Point3D color = tracer.traceRayRecursive(ray, 0);

           synchronized (bitmapMutex) {
               Color average = rollingColorAverage(color, bitmap[y][x], s);
               bitmap[y][x] = average;
           }
           empty.release();
           finished.countDown();
       } catch (InterruptedException e) {
           System.out.println("Worker interrupted, EXCEPTION: " + e.getMessage());
       }
   }
}
```
