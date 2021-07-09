import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Input {

    public static final int NUM_KEYCODES = 256;
    public static final int NUM_MOUSE_BUTTONS = 5;

    private static boolean mousevis = true;

    private static boolean[] m_lastKeys = new boolean[NUM_KEYCODES];
    private static boolean[] m_lastMouse = new boolean[NUM_MOUSE_BUTTONS];

    public static void update(){

        Mouse.getDWheel();

        for (int i = 0; i < NUM_KEYCODES; i++) {
            m_lastKeys[i] = getKey(i);
        }
        for (int i = 0; i < NUM_MOUSE_BUTTONS; i++) {
            m_lastMouse[i] = getMouse(i);
        }
    }

    public static boolean getKey(int keyCode){
        return Keyboard.isKeyDown(keyCode);
    }

    public static boolean getKeyDown(int keyCode){
        return getKey(keyCode) && !m_lastKeys[keyCode];
    }

    public static boolean getKeyUp(int keyCode){
        return !getKey(keyCode) && m_lastKeys[keyCode];
    }

    public static boolean getMouse(int mouseButton){
        return Mouse.isButtonDown(mouseButton);
    }

    public static boolean getMouseDown(int mouseButton){
        return getMouse(mouseButton) && !m_lastMouse[mouseButton];
    }

    public static boolean getMouseUp(int mouseButton){
        return !getMouse(mouseButton) && m_lastMouse[mouseButton];
    }

    public static Vector2F getMousePosition(){ return new Vector2F(Mouse.getX(), Mouse.getY()); }

    public static void setMousePosition(Vector2F newpos){Mouse.setCursorPosition((int) newpos.getX(), (int) newpos.getY());}

    public static void setCursorVisible(boolean enabled){Mouse.setGrabbed(!enabled); mousevis = enabled;}

    public static boolean getCursorVisible(){return mousevis;}

    public static int getMouseScrollAmt(){ return Mouse.getDWheel();}
}
