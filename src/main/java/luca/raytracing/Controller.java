package luca.raytracing;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class Controller {
    private Camera camera;
    private final RayTracer tracer;
    private final int HEIGHT = 600;
    private final int WIDTH = 1000;
    private final int SAMPLES = 10000;
    private final boolean gui;
    @FXML public Canvas canvas;
    @FXML public Text mousePos;
    @FXML public VBox box;

    private static Map<String, Material> materials = Map.of(
            "WHITE", new Lambertian(new Point3D(1.0f, 1.0f, 1.0f), 0.75f),
            "RED", new Lambertian(new Point3D(1.0f, 0.0f, 0.0f), 0.75f),
            "GREEN", new Lambertian(new Point3D(0.0f, 1.0f, 0.0f), 0.75f),
            "BLUE", new Lambertian(new Point3D(0.0f, 0.0f, 1.0f), 0.75f),
            "MIRROR", new Mirror(new Point3D(1f, 1f, 1f), Point3D.ZERO),
            "WHITE-EMITTER", new Lambertian(new Point3D(1.0f, 1.0f, 1.0f), new Point3D(8.0f, 8.0f, 8.0f)),
            "DIELECTRIC", new Dielectric(1.2)
    );
    enum Scene {
        POLYGONS,
        TRIANGLES
    }
    public Controller(boolean gui) {
        List<WorldObject> world = new ArrayList<>();
        this.gui = gui;

        Scene scene = Scene.TRIANGLES;
        switch (scene) {
            case POLYGONS -> {
                world.add(new Plane(materials.get("WHITE"), 100, 0, 0, new Point3D(-20, -2, -20)));
                world.add(new Plane(materials.get("RED"), 100, -Math.PI / 2, 0, new Point3D(-20, -3, 14)));
                world.add(new Plane(materials.get("BLUE"), 100, 0, -Math.PI / 2, new Point3D(-4, 97, -20)));
                world.add(new Plane(materials.get("WHITE"), 100, 0, Math.PI / 2, new Point3D(5, -3, -20)));
                world.add(new Plane(materials.get("WHITE"), 100, Math.PI / 2, 0, new Point3D(-20, 40, -3)));
                world.add(new Plane(materials.get("WHITE"), 100, Math.PI, 0, new Point3D(-20, 10, 20)));

                world.add(new Cube(materials.get("WHITE-EMITTER"), 3, new Point3D(0, 2, 10), new Point3D(0, Math.PI / 8, 0))); // Mirror Cube
                world.add(new Cube(materials.get("WHITE"), 1, new Point3D(-3, -1, 5), new Point3D(0, 0, 0)));
                // world.add(new TriPlane(materials.get("GREEN"), new Point3D(0, 0, 0), new Point3D(1, 4, 6), new Point3D(1, 1, 6), new Point3D(2, 1, 6)));
                //Cube light = new Cube(materials.get("WHITE-EMITTER"), 1, new Point3D(-2, 3, 4), new Point3D(0, 0, 0));
                // lights.add(new Light(new Point3D(0, 6, 10), 50.0));
                // lights.add(new Light(new Point3D(0, 6, 4), 110.0));
                this.camera = new Camera(new Point3D(1, 3, 1), -10 * Math.PI / 180, 10 * Math.PI / 180, 80 * Math.PI / 180);

            }
            case TRIANGLES -> {
                double boxHeight = 10;
                double boxWidth = 15;
                double boxDepth = 15;
                Point3D floorBackLeft = new Point3D(-boxWidth / 2, -2, boxDepth);
                Point3D floorBackRight = new Point3D(boxWidth / 2, -2, boxDepth);
                Point3D floorFrontLeft = new Point3D(-boxWidth / 2, -2, 0);
                Point3D floorFrontRight = new Point3D(boxWidth / 2, -2, 0);
                Point3D roofBackLeft = floorBackLeft.add(new Point3D(0, boxHeight, 0));
                Point3D roofBackRight = floorBackRight.add(new Point3D(0, boxHeight, 0));
                Point3D roofFrontLeft = floorFrontLeft.add(new Point3D(0, boxHeight, 0));
                Point3D roofFrontRight = floorFrontRight.add(new Point3D(0, boxHeight, 0));

                // Box
                TriPlane top = new TriPlane(materials.get("WHITE"), new Point3D(0, 0, 0), roofBackLeft, roofFrontLeft, roofBackRight, roofFrontRight);
                world.add(new TriPlane(materials.get("WHITE"), new Point3D(0, 0, 0), floorFrontLeft, floorBackLeft, floorFrontRight, floorBackRight)); // Floor
                world.add(new TriPlane(materials.get("RED"), new Point3D(0, 0, 0), floorFrontLeft, roofFrontLeft, floorBackLeft, roofBackLeft)); // Left
                world.add(new TriPlane(materials.get("WHITE"), new Point3D(0, 0, 0), floorBackLeft, roofBackLeft, floorBackRight, roofBackRight)); // Back
                world.add(top); // Top
                world.add(new TriPlane(materials.get("GREEN"), new Point3D(0, 0, 0), floorBackRight, roofBackRight, floorFrontRight, roofFrontRight)); // Right
                world.add(new TriPlane(materials.get("WHITE"), new Point3D(0, 0, 0), floorFrontRight, roofFrontRight, floorFrontLeft, roofFrontLeft)); // Front
                //world.add(new TriPlane(materials.get("WHITE-EMITTER"), new Point3D(0, 0, 0), new Point3D(-boxWidth / 6, boxHeight,  2 * boxDepth / 3)));

                // World Objects
                TriPlane topLight = top.Scale(0.3, 0, 0.3).Translate(0.0, -0.001, 0.0);
                topLight.id = "light";
                topLight.SetMaterial(materials.get("WHITE-EMITTER"));
                world.add(topLight);
                //world.add(new TriCube(materials.get("MIRROR"), 2, new Point3D(-2, 1, 10), new Point3D(0, 0, 0)));
                world.add(new TriCube(materials.get("WHITE"), 2, new Point3D(-1, 1, 10), new Point3D(0, Math.PI / 8, 0)));
                world.add(new Sphere(materials.get("DIELECTRIC"), new Point3D(1.3, 0.7, 6), 0.5));
                world.add(new TriCube(materials.get("MIRROR"), 2, new Point3D(3, 1, 11), new Point3D(0, 0, 0)));
                // Camera
                this.camera = new Camera(new Point3D(0, 3, 1), 0 * Math.PI / 180, 0 * Math.PI / 180, 80 * Math.PI / 180);
            }
        }
        tracer = new RayTracer(world);
    }

    @FXML
    public void initialize() {
        EventHandler<MouseEvent> mouseEvent = e -> {
            String coordsStr = String.format("Mouse Coords: (%d, %d)", Math.round(e.getX()), Math.round(e.getY()));
            mousePos.setText(coordsStr);
        };
        box.setOnMouseMoved(mouseEvent);
    }
    public void updateCanvas(int width, int height, Color[][] bitmap) {
        Platform.runLater(() -> {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color c = new Color(bitmap[y][x].getRed(), bitmap[y][x].getGreen(), bitmap[y][x].getBlue(), 1);
                    canvas.getGraphicsContext2D().getPixelWriter().setColor(x, y, c);
                }
            }
        });
    }

    private void OutputBitmap(int width, int height, int samples, Color[][] bitmap) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bufferedImage.setRGB(x, y, ColorToIntRGB(bitmap[y][x]));
            }
        }
        try {
            String filename = "Render.png";
            if (gui) {
                filename = String.format("Renders/Render-(%dx%dx%d).png", width, height, samples);
            }
            File outputFile = new File(filename);
            ImageIO.write(bufferedImage, "png", outputFile);
            System.out.printf("Image written to %s\n", filename);
        } catch (IOException e) {
            System.out.printf("ERROR Writing to File: %s\n", e.getMessage());
        }
    }
    private int ColorToIntRGB(Color c) {
        int rgba = c.hashCode();
        int a = rgba & 0x000000FF;
        return ((rgba >> 8) & 0x00FFFFFF) + (a << 24);
    }
    private Color rollingColorAverage(Point3D newCol, Color average, int n) {
        if (n == 0) {
            average = new Color(0, 0, 0, 0);
        }
        Point3D pointAverage;
        n += 1;
        Point3D prevAverage = new Point3D(average.getRed(), average.getGreen(), average.getBlue());
        pointAverage = prevAverage.add((newCol.subtract(prevAverage)).multiply(1.0 / n));
        return new Color(Math.min(pointAverage.getX(), 1) , Math.min(pointAverage.getY(), 1), Math.min(pointAverage.getZ(), 1), 1);
    }
    public Color[][] initBitmap(int height, int width) {
        Color[][] b = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int f = 0; f < width; f++) {
                b[i][f] = new Color(0, 0, 0, 1);
            }
        }
        return b;
    }
    public void startTracing() throws InterruptedException {
        double uScale = 1;
        double vScale = 1;


        if (WIDTH > HEIGHT) uScale = (double)WIDTH / HEIGHT;
        else if (HEIGHT > WIDTH) vScale = (double)HEIGHT / WIDTH;

        Color[][] bitmap = initBitmap(HEIGHT, WIDTH);
        // Startup Threads:
        final int threads = 4;
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
            long end = System.currentTimeMillis();
            long averageSampleDuration = ((delta * s) + (end - start)) / (s + 1);
            delta = end - start;
            System.out.printf("Samples Rendered: %d/%d -- Average Duration: %ds\r", s + 1, SAMPLES, averageSampleDuration / 1000);
            if (s % 1 == 0 && gui) {
                updateCanvas(WIDTH, HEIGHT, bitmap);
            }
            if (s % 100 == 0) {
                OutputBitmap(WIDTH, HEIGHT, s, bitmap);
            }
        }
    }
    private class Worker implements Runnable {

        private final Semaphore full;
        private final Semaphore empty;
        private final CountDownLatch finished;
        private final Deque<int[]> workQueue;
        private final Object queueMutex;
        private final Object bitmapMutex;
        private final double UScale;
        private final double VScale;
        private final Color[][] bitmap;
        Worker(final Semaphore full,
               final Semaphore empty,
               final CountDownLatch finished,
               final Deque<int[]> workQueue,
               final Object queueMutex,
               final Object bitmapMutex,
               final double UScale,
               final double VScale,
               final Color[][] bitmap) {
            this.full = full;
            this.empty = empty;
            this.finished = finished;
            this.workQueue = workQueue;
            this.queueMutex = queueMutex;
            this.bitmapMutex = bitmapMutex;
            this.UScale = UScale;
            this.VScale = VScale;
            this.bitmap = bitmap;
        }
        @Override
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
    }
}