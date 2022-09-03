package luca.raytracing;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Controller {
    private final Camera camera;
    private final RayTracer tracer;
    private final int HEIGHT = 800;
    private final int WIDTH = 1000;
    private final int SAMPLES = 2000;
    @FXML public Canvas canvas;
    @FXML private Label welcomeText;
    private static List<Material> materials = new ArrayList<>(Arrays.asList(
            new Lambertian(new Point3D(0.75f, 0.75f, 0.75f)),
            new Lambertian(new Point3D(0.75f, 0.75f, 0.25f)),
            new Lambertian(new Point3D(0.75f, 0.25f, 0.25f)),
            new Lambertian(new Point3D(0.25f, 0.25f, 0.75f)),
            new Mirror(new Point3D(0.8f, 0.8f, 0.8f), Point3D.ZERO)
    ));
    public Controller() {
        // Create world here
        List<WorldObject> world = new ArrayList<>();
        world.add(new Plane(materials.get(1), 100, 0, 0, new Point3D(-20, -2, -20)));
        world.add(new Plane(materials.get(2), 100, -Math.PI / 2, 0, new Point3D(-20, -3, 14)));
        world.add(new Plane(materials.get(3), 100, 0, -Math.PI / 2, new Point3D(-4, 97, -20)));
        world.add(new Plane(materials.get(2), 100, 0, Math.PI / 2, new Point3D(5, -3, -20)));
        world.add(new Plane(materials.get(0), 100, Math.PI / 2, 0, new Point3D(-20, 97, -20)));

        world.add(new Cube(materials.get(4), 3, new Point3D(0, 2, 10), new Point3D(0, Math.PI / 8, 0))); // Mirror Cube
        world.add(new Cube(materials.get(0), 1, new Point3D(-1, -1, 5), new Point3D(0, 0, 0)));

        List<Light> lights = new ArrayList<>();
        // lights.add(new Light(new Point3D(0, 6, 10), 50.0));
        lights.add(new Light(new Point3D(0, 6, 3), 100.0));


        this.camera = new Camera(new Point3D(0, 3, 1), -25 * Math.PI / 180, 0, 75 * Math.PI / 180);
        tracer = new RayTracer(world, lights);
    }
    public void updateCanvas(int width, int height, Color[][] bitmap) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        canvas.getGraphicsContext2D().getPixelWriter().setColor(x, y, bitmap[y][x]);
                    }
                }
            }});
    }
    @FXML protected void onTraceButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        double uScale = 1;
        double vScale = 1;

        if (WIDTH > HEIGHT) uScale = (double)WIDTH / HEIGHT;
        else if (HEIGHT > WIDTH) vScale = (double)HEIGHT / WIDTH;
        double finalUScale = uScale;
        double finalVScale = vScale;
        Color bitmap[][] = new Color[HEIGHT][WIDTH];
        long startTime = System.nanoTime();
        AtomicInteger threadsFinished = new AtomicInteger();
        IntStream.range(0, HEIGHT).parallel().forEach(y -> {
            for (int x = 0; x < WIDTH; x++) {
                double u = 2 * ((double)x / (WIDTH - 1)) - 1;
                double v = 1 - 2 * ((double)y / (HEIGHT - 1));
                u *= finalUScale;
                v *= finalVScale;
                Ray ray = camera.transformRay(u, v);
                Point3D color = Point3D.ZERO;
                for (int s = 0; s < SAMPLES; s++) {
                    color = color.add(tracer.traceRay(ray));
                }
                color = color.multiply(1.0 / SAMPLES);
                bitmap[y][x] = new Color(Math.min(color.getX(), 1) , Math.min(color.getY(), 1), Math.min(color.getZ(), 1), 1);
            }
            while(true) {
                int eThreadsFinished = threadsFinished.get();
                int newValue = eThreadsFinished + 1;
                if (threadsFinished.compareAndSet(eThreadsFinished, newValue))
                    break;
            }
            System.out.printf("Rows Finished: %d/%d\r", threadsFinished.get(), HEIGHT);
        });
        long endTime = System.nanoTime();
        System.out.println("Elapsed Time: " + (endTime - startTime));
        updateCanvas(WIDTH, HEIGHT, bitmap);
    }

}