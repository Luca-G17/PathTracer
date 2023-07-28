package luca.raytracing;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static javafx.scene.input.MouseEvent.MOUSE_MOVED;

public class Controller {
    private final Camera camera;
    private final RayTracer tracer;
    private final int HEIGHT = 800;
    private final int WIDTH = 1000;
    private final int SAMPLES = 2000;
    @FXML public Canvas canvas;
    @FXML public Text mousePos;
    @FXML public VBox box;

    private static Map<String, Material> materials = Map.of(
            "WHITE", new Lambertian(new Point3D(0.75f, 0.75f, 0.75f)),
            "GREEN", new Lambertian(new Point3D(0.0f, 0.75f, 0.0f)),
            "RED", new Lambertian(new Point3D(0.75f, 0.25f, 0.25f)),
            "BLUE", new Lambertian(new Point3D(0.25f, 0.25f, 0.75f)),
            "MIRROR", new Mirror(new Point3D(1f, 1f, 1f), Point3D.ZERO),
            "WHITE-EMITTER", new Lambertian(new Point3D(0.75f, 0.75f, 0.75f), new Point3D(2f, 2f, 2f))
    );

    public Controller() {
        // Create world here
        List<WorldObject> world = new ArrayList<>();
        world.add(new Plane(materials.get("WHITE"), 100, 0, 0, new Point3D(-20, -2, -20)));
        world.add(new Plane(materials.get("RED"), 100, -Math.PI / 2, 0, new Point3D(-20, -3, 14)));
        world.add(new Plane(materials.get("BLUE"), 100, 0, -Math.PI / 2, new Point3D(-4, 97, -20)));
        world.add(new Plane(materials.get("WHITE-EMITTER"), 100, 0, Math.PI / 2, new Point3D(5, -3, -20)));
        world.add(new Plane(materials.get("WHITE"), 100, Math.PI / 2, 0, new Point3D(-20, 40, -3)));
        world.add(new Plane(materials.get("WHITE"), 100, Math.PI, 0, new Point3D(-20, 10, 20)));

        world.add(new Cube(materials.get("GREEN"), 3, new Point3D(0, 2, 10), new Point3D(0, Math.PI / 8, 0))); // Mirror Cube
        world.add(new Cube(materials.get("WHITE"), 1, new Point3D(-1, -1, 5), new Point3D(0, 0, 0)));
        //Cube light = new Cube(materials.get("WHITE-EMITTER"), 1, new Point3D(-2, 3, 4), new Point3D(0, 0, 0));
        List<Light> lights = new ArrayList<>();
        // lights.add(new Light(new Point3D(0, 6, 10), 50.0));
        // lights.add(new Light(new Point3D(0, 6, 4), 110.0));


        this.camera = new Camera(new Point3D(0, 3, 1), -10 * Math.PI / 180, 0 * Math.PI / 180, 80 * Math.PI / 180);
        tracer = new RayTracer(world, lights);
    }

    @FXML
    public void initialize() throws InterruptedException {
        EventHandler<MouseEvent> mouseEvent = e -> {
            String coordsStr = String.format("Mouse Coords: (%d, %d)", Math.round(e.getX()), Math.round(e.getY()));
            mousePos.setText(coordsStr);
        };
        box.setOnMouseMoved(mouseEvent);
    }
    public void updateCanvas(int width, int height, Color[][] bitmap) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(bitmap[y][x].getRed(), bitmap[y][x].getGreen(), bitmap[y][x].getBlue(), 1);
                canvas.getGraphicsContext2D().getPixelWriter().setColor(x, y, c);
            }
        }
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

        Object mutex = new Object();

        if (WIDTH > HEIGHT) uScale = (double)WIDTH / HEIGHT;
        else if (HEIGHT > WIDTH) vScale = (double)HEIGHT / WIDTH;
        double finalUScale = uScale;
        double finalVScale = vScale;

        // Spin up n threads, wait all threads compute a single round of the bitmap
        Color[][] bitmap = initBitmap(HEIGHT, WIDTH);
        for (int s = 0; s < SAMPLES; s++) {
            // Each thread computes a row
            int finalS = s;
            IntStream.range(0, HEIGHT).parallel().forEach(y -> {
                for (int x = 0; x < WIDTH; x++) {
                    double u = 2 * (((double)x + 0.5) / (WIDTH - 1)) - 1;
                    double v = 1 - (2 * (((double)y + 0.5) / (HEIGHT - 1)));
                    u *= finalUScale;
                    v *= finalVScale;
                    Ray ray = camera.transformRay(u, v);
                    //Point3D color = tracer.traceRay(ray);
                    Point3D color = tracer.traceRayRecursive(ray, 0);
                    synchronized (mutex) {
                        Color average = rollingColorAverage(color, bitmap[y][x], finalS);
                        bitmap[y][x] = average;
                    }
                }
            });
            // Display bitmap
            if (s % 1 == 0) {
                updateCanvas(WIDTH, HEIGHT, bitmap);
            }
        }
    }
}