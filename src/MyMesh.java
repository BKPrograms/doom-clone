import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class MyMesh {

    private int vbo; // Acts as a sort of pointer/handle
    private int ibo;
    private int size;

    private void initMeshData(){
        vbo = glGenBuffers();
        ibo = glGenBuffers();
        size = 0;
    }

    public MyMesh(String filename){

        initMeshData();
        loadMesh(filename);
    }

    public MyMesh(Vertex[] vertices, int[] indices){
        this(vertices, indices, false);
    }

    public MyMesh(Vertex[] vertices, int[] indices, boolean calcNormals){
        initMeshData();
        addVertices(vertices, indices, calcNormals);
    }

//    private void addVertices(Vertex[] vertices, int[] indices){
//
//        addVertices(vertices, indices, false);
//
//    }

    private void addVertices(Vertex[] vertices, int[] indices, boolean calcNormals){


        if (calcNormals){
            calcNormals(vertices, indices);
        }

        size = indices.length; // * Vertex.VERTEX_SIZE;

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glBufferData(GL_ARRAY_BUFFER, Util.createFlippedBuffer(vertices), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.createFlippedBuffer(indices), GL_STATIC_DRAW);
    }

    public void draw(){

        glEnableVertexAttribArray(0); // Actual shape data

        glEnableVertexAttribArray(1); // Texture data

        glEnableVertexAttribArray(2); // Normals data

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        // Position Data pointer:
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.VERTEX_SIZE * 4, 0);

        // Texture position pointer:
        glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.VERTEX_SIZE * 4, 12);

        // Normals pointer:
        glVertexAttribPointer(2, 3, GL_FLOAT, false, Vertex.VERTEX_SIZE * 4, 20);

        // glDrawArrays(GL_TRIANGLES, 0, size); // causes strange issues when loading levels

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

        glDrawElements(GL_TRIANGLES, size, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
    }


    private void calcNormals(Vertex[] vertices, int[] indices){

        for (int i = 0; i < indices.length; i += 3) {

            int i0 = indices[i];
            int i1 = indices[i + 1];
            int i2 = indices[i + 2];

            Vector3F v1 = vertices[i1].getPosition().sub(vertices[i0].getPosition());
            Vector3F v2 = vertices[i2].getPosition().sub(vertices[i0].getPosition());

            Vector3F normal = v1.crossProd(v2).normalized();

            vertices[i0].setNormal(vertices[i0].getNormal().add(normal));
            vertices[i1].setNormal(vertices[i1].getNormal().add(normal));
            vertices[i2].setNormal(vertices[i2].getNormal().add(normal));

        }

        for (int i = 0; i < vertices.length; i++) {
            vertices[i].setNormal(vertices[i].getNormal().normalized());
        }


    }

    private MyMesh loadMesh(String filename) {
        String[] splitArray = filename.split("\\.");

        String fileExt = splitArray[splitArray.length - 1];

        if (!fileExt.equals("obj")) {
            System.err.println("Invalid file type, only .obj files are accepted.");
            new Exception().printStackTrace();
            System.exit(1);
        }

        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        ArrayList<Integer> indices = new ArrayList<Integer>();

        BufferedReader objReader;

        try {

            objReader = new BufferedReader(new FileReader("./res/models/" + filename));

            String line;

            while ((line = objReader.readLine()) != null) {

                String[] tokens = line.split(" ");

                tokens = Util.removeEmptyStrings(tokens);

//                if (tokens.length == 0 || tokens[0].equals("#")) {
//                    continue;
//                } else

                if (tokens.length > 0){ // In case we hit a blank line
                    if (tokens[0].equals("v")) {
                        vertices.add(new Vertex(new Vector3F(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]))));
                    } else if (tokens[0].equals("f")){
                        indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
                        indices.add(Integer.parseInt(tokens[2].split("/")[0]) - 1);
                        indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);


                        if (tokens.length > 4){
                            indices.add(Integer.parseInt(tokens[1].split("/")[0]) - 1);
                            indices.add(Integer.parseInt(tokens[3].split("/")[0]) - 1);
                            indices.add(Integer.parseInt(tokens[4].split("/")[0]) - 1);
                        }

                    }
                }

            }

            objReader.close();

            Vertex[] vertices1 = new Vertex[vertices.size()];
            Integer[] indices1 = new Integer[indices.size()];

            vertices.toArray(vertices1);
            indices.toArray(indices1);

            addVertices(vertices1, Util.integerToIntArr(indices1), true);


        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }

}
