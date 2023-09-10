package luca.raytracing;

public class CommandLine {
    public static void main (String[] arg) {
        Controller controller = new Controller(false);
        App.StartPathTracer(controller);
    }
}
