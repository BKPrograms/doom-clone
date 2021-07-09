public class DirectionalLight {

    private BaseLight baseLight;
    private Vector3F direction;

    public DirectionalLight(BaseLight baseLight, Vector3F direction) {
        this.baseLight = baseLight;
        this.direction = direction.normalized();
    }

    public BaseLight getBaseLight() {
        return baseLight;
    }

    public void setBaseLight(BaseLight baseLight) {
        this.baseLight = baseLight;
    }

    public Vector3F getDirection() {
        return direction;
    }

    public void setDirection(Vector3F direction) {
        this.direction = direction;
    }
}
