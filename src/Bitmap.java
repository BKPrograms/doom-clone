import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Bitmap {

    private int width;
    private int height;
    private int[] pixels;

    public Bitmap(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new int[width * height]; // Imagine a "decompressed" 2D array rather than an actual 2D array
    }

    public Bitmap(String filename) {

        try {

            BufferedImage img = ImageIO.read(new File("./res/levels/" + filename));

            width = img.getWidth();
            height = img.getHeight();
            pixels = new int[width * height];
            img.getRGB(0, 0, width, height, pixels, 0, width);
        } catch (Exception e) {
            System.err.println("Something went wrong with loading the map!");
            e.printStackTrace();
        }

    }

    public int getPixelAt(int x, int y){

        return pixels[x + (y * width)];

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getPixels(){
        return pixels;
    }

    public void setPixel(int x, int y, int value){

        pixels[x + (y * width)] = value;
    }

    public Bitmap flipX(){
        int[] temp = new int[pixels.length];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                temp[i + j * width] = pixels[(width - i - 1) + j * width];
            }
        }

        pixels = temp;

        return this;
    }

    public Bitmap flipY(){
        int[] temp = new int[pixels.length];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                temp[i + j * width] = pixels[i + (height - j - 1) * width];
            }
        }

        pixels = temp;

        return this;
    }
}
