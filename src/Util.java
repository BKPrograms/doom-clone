import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Util {

    public static FloatBuffer createFloatBuffer(int s){

        return BufferUtils.createFloatBuffer(s);

    }

    public static IntBuffer createIntBuffer(int size){
        return BufferUtils.createIntBuffer(size);
    }

    public static FloatBuffer createFlippedBuffer(Vertex[] vertices){

        FloatBuffer buffer = createFloatBuffer(vertices.length * Vertex.VERTEX_SIZE);

        for (Vertex v: vertices) {
            buffer.put(v.getPosition().getX());
            buffer.put(v.getPosition().getY());
            buffer.put(v.getPosition().getZ());

            // Adding texture positions
            buffer.put(v.getTextureCoordinate().getX());
            buffer.put(v.getTextureCoordinate().getY());

            // Adding normal positions
            buffer.put(v.getNormal().getX());
            buffer.put(v.getNormal().getY());
            buffer.put(v.getNormal().getZ());
        }

        buffer.flip();

        return buffer;

    }


    public static FloatBuffer createFlippedBuffer(Matrix4F value){
        FloatBuffer buffer = createFloatBuffer(16);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                buffer.put(value.get(i, j));
            }
        }
        buffer.flip();

        return buffer;
    }


    public static IntBuffer createFlippedBuffer(int[] ints){
        IntBuffer buffer = createIntBuffer(ints.length);

        for (int v: ints) {

            buffer.put(v);

        }

        buffer.flip();
        return buffer;

    }


    public static String[] removeEmptyStrings(String[] s){


        ArrayList<String> results = new ArrayList<>();

        for (String value : s) {
            if (!value.equals("")) {
                results.add(value);
            }

        }

        String[] res = new String[results.size()];

        results.toArray(res);

        return res;

    }

    public static int[] integerToIntArr(Integer[] arr){

        int[] res = new int[arr.length];


        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i];
        }

        return res;

    }

    public static ByteBuffer createByteBuffer(int size)
    {
        return BufferUtils.createByteBuffer(size);
    }
}
