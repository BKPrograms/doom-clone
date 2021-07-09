public class SpotLight {
    private PointLight pointLight;
    private Vector3F direction;
    private float coneCutOff;

    public SpotLight(PointLight pointLight, Vector3F direction, float coneCutOff) {
        this.pointLight = pointLight;
        this.direction = direction.normalized();
        this.coneCutOff = coneCutOff;
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3F getDirection() {
        return direction;
    }

    public void setDirection(Vector3F direction) {
        this.direction = direction.normalized();
    }

    public float getConeCutOff() {
        return coneCutOff;
    }

    public void setConeCutOff(float coneCutOff) {
        this.coneCutOff = coneCutOff;
    }
}
