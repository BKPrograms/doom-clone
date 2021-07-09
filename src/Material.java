public class Material {
    private Texture texture;
    private Vector3F colour;
    private float specularIntensity; // Reflection factor, aka how intense the reflection will be. So "metal" surfaces have higher specularIntensity.
    private float specularPower; // The "width" of reflection beam. A large metal sheet has a higher exponent than a small metal sphere.

    public Material(Texture texture){

        this(texture, new Vector3F(1,1,1));
    }


    public Material(Texture texture, Vector3F colour) {

        this(texture, colour, 2, 32);
    }

    public Material(Texture texture, Vector3F colour, float specularIntensity, float specularPower) {
        this.texture = texture;
        this.colour = colour;
        this.specularPower = specularPower;
        this.specularIntensity = specularIntensity;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Vector3F getColour() {
        return colour;
    }

    public void setColour(Vector3F colour) {
        this.colour = colour;
    }

    public float getSpecularIntensity() {
        return specularIntensity;
    }

    public void setSpecularIntensity(float specularIntensity) {
        this.specularIntensity = specularIntensity;
    }

    public float getSpecularPower() {
        return specularPower;
    }

    public void setSpecularPower(float specularPower) {
        this.specularPower = specularPower;
    }
}
