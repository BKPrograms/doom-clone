// The main display of the game and it's operations


import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class GameWindow {


    public static void createWindow(int width, int height, String winTitle) {

        try {

            Display.setTitle(winTitle);
            Display.setDisplayMode(new DisplayMode(width, height));
            // Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
            Display.create();
            Keyboard.create();
            Mouse.create();

        } catch (LWJGLException e) {
            e.printStackTrace();
        }

    }

    public static void render() {
        Display.update();
    }

    public static boolean isCloseRequested() {
        return Display.isCloseRequested();
    }

    public static int getWidth() {
        return Display.getDisplayMode().getWidth();

    }

    public static int getHeight() {

        return Display.getDisplayMode().getHeight();
    }

    public static void dispose(){
        Display.destroy();
        Keyboard.destroy();
        Mouse.destroy();
    }

    public static String getTitle() {
        return Display.getTitle();
    }

    public static void setTitle(String title){Display.setTitle(title);}
}
