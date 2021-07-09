public class BaseLight {


    private Vector3F colour;
    private float intensity;

    public BaseLight(Vector3F colour, float intensity) {
        this.colour = colour;
        this.intensity = intensity;
    }

    public Vector3F getColour() {
        return colour;
    }

    public void setColour(Vector3F colour) {
        this.colour = colour;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
