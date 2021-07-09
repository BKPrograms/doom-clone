// Class to handle Shaders and their respective operations.
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

public class Shader {

    private int program;
    private HashMap<String, Integer> uniforms;

    public Shader() {
        program = glCreateProgram();
        uniforms = new HashMap<String, Integer>(); // Stores uniform GLSL variables such as colour of certain points and transformations

        if (program == 0){
            System.err.println("Shader creation failed: could not find valid memory location.");
            System.exit(1);
        }
    }

    private void addProgram(String text, int type){

        int shader = glCreateShader(type);

        if (shader == 0){
            System.err.println("Shader creation failed: could not find valid memory location when adding shader.");
            System.exit(1);
        }

        glShaderSource(shader, text);
        glCompileShader(shader);


        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0){
            System.err.println(glGetShaderInfoLog(shader, 1024));
            System.exit(1);
        }

        glAttachShader(program, shader);

    }



    public void addVertexShader(String text){
        addProgram(text, GL_VERTEX_SHADER);
    }

    public void addGeometryShader(String text){
        addProgram(text, GL_GEOMETRY_SHADER);
    }

    public void addFragmentShader(String text){
        addProgram(text, GL_FRAGMENT_SHADER);
    }

    public void compileShaders(){

        glLinkProgram(program);

        if (glGetProgrami(program, GL_LINK_STATUS) == 0){
            System.err.println(glGetProgramInfoLog(program, 1024));
            System.exit(1);
        }


        glValidateProgram(program);

        if (glGetProgrami(program, GL_VALIDATE_STATUS) == 0){
            System.err.println(glGetProgramInfoLog(program, 1024));
            System.exit(1);
        }

    }


    public void bind(){

        glUseProgram(program);

    }

    public void updateUniforms(Matrix4F worldMatrix, Matrix4F projectedMatrix, Material material){

    }

    // Uniform variables are GLSL variables we can directly manipulate from this java program
    public void addUniform(String uniformTitle){

        int uniformLocation = glGetUniformLocation(program, uniformTitle);

        if (uniformLocation == 0xFFFFFFFF) { // Error check
            System.err.println("Error, could not find uniform variable: " + uniformTitle);
            new Exception().printStackTrace();
            System.exit(1);
        }

        uniforms.put(uniformTitle, uniformLocation);

    }

    public void setUniformi(String name, int value){
        glUniform1i(uniforms.get(name), value);
    }

    public void setUniformf(String name, float value){
        glUniform1f(uniforms.get(name), value);
    }
    public void setUniformV(String name, Vector3F value){
        glUniform3f(uniforms.get(name), value.getX(), value.getY(), value.getZ());
    }

    public void setUniformM(String name, Matrix4F value){
        glUniformMatrix4(uniforms.get(name), true,Util.createFlippedBuffer(value));
    }


    public void addVertexShaderFromFile(String text){
        addProgram(loadShaders(text), GL_VERTEX_SHADER);
    }

    public void addGeometryShaderFromFile(String text){
        addProgram(loadShaders(text), GL_GEOMETRY_SHADER);
    }

    public void addFragmentShaderFromFile(String text){
        addProgram(loadShaders(text), GL_FRAGMENT_SHADER);
    }

    private static String loadShaders(String filename) {

        StringBuilder shaderSource = new StringBuilder();

        BufferedReader shaderReader = null;

        try {

            shaderReader = new BufferedReader(new FileReader("./res/shaders/" + filename));

            String line;

            while ((line = shaderReader.readLine()) != null) {

                shaderSource.append(line).append("\n");

            }

            shaderReader.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return shaderSource.toString();

    }

}
