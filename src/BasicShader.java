public class BasicShader extends Shader{

    private static final BasicShader instance = new BasicShader();

    public BasicShader() {
        super();

        addVertexShaderFromFile("basicVertex.vs");
        addFragmentShaderFromFile("basicFragment.fs");
        compileShaders();

        addUniform("transform");
        addUniform("colour");
    }

    @Override
    public void updateUniforms(Matrix4F worldMatrix, Matrix4F projectedMatrix, Material material) {

        if (material.getTexture() != null){
            material.getTexture().bindTexture();
        } else {
            RenderUtil.unbindTextures();
        }

        setUniformM("transform", projectedMatrix);
        setUniformV("colour", material.getColour());
    }

    public static BasicShader getInstance(){
        return instance;
    }
}
