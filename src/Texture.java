import org.newdawn.slick.opengl.TextureLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture {

    // Note, loading textures with 2^n x 2^m dimensions is optimal

    private int id;
    private float width;
    private float height;

    public Texture(String filename){

        this(loadTexture2(filename)); // Uses below constructor

        float[] dims = getDims(filename);

        this.width = dims[0];
        this.height = dims[1];
    }

    public Texture(int id){

        this.id = id;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void bindTexture(){
        glBindTexture(GL_TEXTURE_2D, this.id);
    }

    private static int loadTexture(String filename){

        String[] splitArray = filename.split("\\.");

        String fileExt = splitArray[splitArray.length - 1];

        try {

            int id = TextureLoader.getTexture(fileExt, new FileInputStream(new File(".\\res\\textures\\" + filename)), GL_NEAREST).getTextureID();

            return id;

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return 0;
    }

    // Alternate texture loading method that doesn't use SlickUtil as there is a texture clipping issue with it.
    private static int loadTexture2(String fileName)
    {
        try
        {
            BufferedImage image = ImageIO.read(new File("./res/textures/" + fileName));

            boolean hasAlpha = image.getColorModel().hasAlpha();

            int[] pixels = image.getRGB(0, 0, image.getWidth(),
                    image.getHeight(), null, 0, image.getWidth());

            ByteBuffer buffer = Util.createByteBuffer(image.getWidth() * image.getHeight() * 4);

            for (int y = 0; y < image.getHeight(); y++)
            {
                for (int x = 0; x < image.getWidth(); x++)
                {
                    int pixel = pixels[y * image.getWidth() + x];

                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    buffer.put((byte) ((pixel) & 0xFF));
                    if (hasAlpha)
                        buffer.put((byte) ((pixel >> 24) & 0xFF));
                    else
                        buffer.put((byte) (0xFF));
                }
            }

            buffer.flip();

            int texture = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texture);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

            return texture;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        return 0;
    }

    private static float[] getDims(String filename){

        float[] res = new float[2];

        String[] splitArray = filename.split("\\.");

        String fileExt = splitArray[splitArray.length - 1];


        try {
            float width = TextureLoader.getTexture(fileExt, new FileInputStream(new File(".\\res\\textures\\" + filename)), GL_NEAREST).getImageWidth();
            float height = TextureLoader.getTexture(fileExt, new FileInputStream(new File(".\\res\\textures\\" + filename)), GL_NEAREST).getImageHeight();

            res[0] = width;
            res[1] = height;

            return res;

        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }


        return res;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
