package luca.raytracing;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class App extends Application {

    @Override public void start(Stage stage) throws IOException {
        Controller controller = new Controller(true);
        EventHandler<WindowEvent> windowEventHandler = windowEvent -> new Thread(() -> StartPathTracer(controller)).start();

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("MainWindow.fxml"));
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
        stage.setTitle("Path Tracer");
        stage.setScene(scene);
        stage.setOnShowing(windowEventHandler);
        stage.show();
    }
    public static void StartPathTracer(final Controller controller) {
        try {
            final long startTime = System.currentTimeMillis();
            System.out.println("Path Tracing");
            controller.startTracing();
            final long endTime = System.currentTimeMillis();
            System.out.printf("Execution Time: %ds\n", (endTime - startTime) / 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        launch();
    }
}