public class PhongShader extends Shader{

    private static final PhongShader instance = new PhongShader();
    private static Vector3F ambientLight = new Vector3F(0.1f, 0.1f, 0.1f);
    private static DirectionalLight directionalLight = new DirectionalLight(new BaseLight(new Vector3F(1, 1, 1), 0), new Vector3F(0, 0, 0));
    private static final int MAX_POINT_LIGHTS = 4;
    private static final int MAX_SPOT_LIGHTS = 4;

    private static PointLight[] pointLights = new PointLight[] {};

    private static SpotLight[] spotLights = new SpotLight[] {};

    private PhongShader() {
        super();

        addVertexShaderFromFile("phongVertex.vs");
        addFragmentShaderFromFile("phongFragment.fs");
//        addVertexShader(ResourceLoader.loadShaders("phongVertex.vs"));
//        addFragmentShader(ResourceLoader.loadShaders("phongFragment.fs"));
        compileShaders();

        addUniform("transform");
        addUniform("transformProjected");
        addUniform("baseColour");
        addUniform("ambientLight");

        addUniform("directional.base.colour");
        addUniform("directional.base.intensity");
        addUniform("directional.direction");


        addUniform("specularIntensity");
        addUniform("specularPower");
        addUniform("eyePos");


        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            addUniform("pointLights[" + i + "].base.colour");
            addUniform("pointLights[" + i + "].base.intensity");
            addUniform("pointLights[" + i + "].att.constant");
            addUniform("pointLights[" + i + "].att.linear");
            addUniform("pointLights[" + i + "].att.exponent");
            addUniform("pointLights[" + i + "].position");
            addUniform("pointLights[" + i + "].range");
        }


        for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
            addUniform("spotLights[" + i + "].point.base.colour");
            addUniform("spotLights[" + i + "].point.base.intensity");
            addUniform("spotLights[" + i + "].point.att.constant");
            addUniform("spotLights[" + i + "].point.att.linear");
            addUniform("spotLights[" + i + "].point.att.exponent");
            addUniform("spotLights[" + i + "].point.position");
            addUniform("spotLights[" + i + "].point.range");

            addUniform("spotLights[" + i + "].direction");
            addUniform("spotLights[" + i + "].coneCutOff");
        }

    }

    @Override
    public void updateUniforms(Matrix4F worldMatrix, Matrix4F projectedMatrix, Material material) {

        if (material.getTexture() != null){
            material.getTexture().bindTexture();
        } else {
            RenderUtil.unbindTextures();
        }

        setUniformM("transform", worldMatrix);
        setUniformM("transformProjected", projectedMatrix);
        setUniformV("baseColour", material.getColour());
        setUniformV("ambientLight", ambientLight);
        setUniformDL("directional", directionalLight);
        setUniformf("specularIntensity", material.getSpecularIntensity());
        setUniformf("specularPower", material.getSpecularPower());
        setUniformV("eyePos", Transform.getCamera().getPosition());

        for (int i = 0; i < pointLights.length; i++) {
            setUniformPL("pointLights[" + i + "]", pointLights[i]);
        }

        for (int i = 0; i < spotLights.length; i++) {
            setUniformSL("spotLights[" + i + "]", spotLights[i]);
        }

    }

    public static PhongShader getInstance(){
        return instance;
    }

    public static Vector3F getAmbientLight() {
        return ambientLight;
    }

    public static void setAmbientLight(Vector3F ambientLight) {
        PhongShader.ambientLight = ambientLight;
    }

    public void setUniformBase(String name, BaseLight base){
        setUniformV(name + ".colour", base.getColour());
        setUniformf(name + ".intensity", base.getIntensity());
    }

    public void setUniformDL(String name, DirectionalLight directionalLight){

        setUniformBase(name + ".base", directionalLight.getBaseLight());
        setUniformV(name + ".direction", directionalLight.getDirection());

    }

    public static DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public static void setDirectionalLight(DirectionalLight directionalLight) {
        PhongShader.directionalLight = directionalLight;
    }

    public static void setPointLights(PointLight[] pointLights){
        if (pointLights.length > MAX_POINT_LIGHTS){
            System.err.println("Error: Too many point lights, max allowed is " + MAX_POINT_LIGHTS);
            new Exception().printStackTrace();
            System.exit(1);
        }

        PhongShader.pointLights = pointLights;
    }

    public void setUniformPL(String name, PointLight pointLight){ // Pointlight

        setUniformBase(name + ".base", pointLight.getBaseLight());
        setUniformf(name + ".att.constant", pointLight.getAtt().getConstant());
        setUniformf(name + ".att.linear", pointLight.getAtt().getLinear());
        setUniformf(name + ".att.exponent", pointLight.getAtt().getExponent());
        setUniformV(name + ".position", pointLight.getPosition());
        setUniformf(name + ".range", pointLight.getRange());

    }

    public static void setSpotLights(SpotLight[] spotLights){

        if (spotLights.length > MAX_SPOT_LIGHTS){
            System.err.println("Error: Too many spot lights, max allowed is " + MAX_SPOT_LIGHTS);
            new Exception().printStackTrace();
            System.exit(1);
        }

        PhongShader.spotLights = spotLights;

    }


    public void setUniformSL(String name, SpotLight sl){ // Spotlight
        setUniformPL(name + ".point", sl.getPointLight());
        setUniformV(name + ".direction", sl.getDirection());
        setUniformf(name + ".coneCutOff", sl.getConeCutOff());

    }
}
