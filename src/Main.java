import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL;

public class Main {
    // Dimensions and title
    public static final int MY_WIDTH = 1000;
    public static final int MY_HEIGHT = 1000;
    public static final String MY_TITLE = "Doomfenstein";
    public static final double FRAME_CAP = 120;

    private boolean isEngineRunning;

    private Game game;

    public Main() {

        System.out.println("OpenGL version: " + RenderUtil.getOpenGLVersion());
        RenderUtil.initGraphics();
        isEngineRunning = false;
        game = new Game();

    }

    public void startGame() {

        if (isEngineRunning) {
            return;
        }
        runGame();

    }

    public void stopGame() {
        if (!isEngineRunning) {
            return;
        }

        isEngineRunning = false;

    }

    public void runGame() {

        isEngineRunning = true;

        int frames = 0;
        int fpsCounter = 0;

        final double frameTime = 1.0 / FRAME_CAP;

        long lastTime = Time.getTime();
        double unprocessedTime = 0;

        // Loop condition to see if we want to exit the application
        while (isEngineRunning) {
            boolean render = false;

            long startTime = Time.getTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) Time.SECOND;
            fpsCounter += passedTime;

            while (unprocessedTime > frameTime) { // Implies frame needs to be updated
                render = true;

                unprocessedTime -= frameTime;

                if (GameWindow.isCloseRequested() || Input.getKey(Keyboard.KEY_X)) {
                    stopGame();
                }

                Time.setDelta(frameTime);

                game.input();
                Input.update();
                game.update();


                if (fpsCounter >= Time.SECOND){
                    //System.out.println("FPS: " + frames);

                    frames = 0;
                    fpsCounter = 0;
                }

            }

            if (render){
                render();
                frames += 1;
            } else {

                try {
                    Thread.sleep(1); // If no need to render, this saves processor time
                } catch (InterruptedException e){
                    e.printStackTrace();
                }


            }

        }

        cleanUp();

    }

    private void render() {
        RenderUtil.clearScreen();
        game.render();
        GameWindow.render();
    }

    private void cleanUp() {

        GameWindow.dispose();
        AL.destroy();

    }


    public static void main(String[] args) {

        GameWindow.createWindow(MY_WIDTH, MY_HEIGHT, MY_TITLE);

        Main game = new Main();

        game.runGame();


    }
}
