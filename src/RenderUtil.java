import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;

public class RenderUtil {


    public static void clearScreen(){

        // TODO: Stencil buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    }


    public static void initGraphics(){
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Sets world colour to black background

        // By default two instances are drawn, one facing away and one towards camera. We want to get rid of the
        // one that will never be seen by the camera
        glFrontFace(GL_CW);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);


        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_DEPTH_CLAMP);
        glEnable(GL_TEXTURE_2D); // Start off with textures "on"
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        // glEnable(GL_FRAMEBUFFER_SRGB); // Helps with gamma correction

    }


    public static String getOpenGLVersion(){

        return glGetString(GL_VERSION);

    }

    public static void setTextures(boolean enabled){
        if (enabled){
            glEnable(GL_TEXTURE_2D);
        } else {
            glDisable(GL_TEXTURE_2D);
        }
    }


    public static void setClearColour(Vector3F colour){
        glClearColor(colour.getX(), colour.getY(), colour.getZ(), 1.0f);
    }

    public static void unbindTextures(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
