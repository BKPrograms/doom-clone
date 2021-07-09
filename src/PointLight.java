public class PointLight {
    private BaseLight baseLight;
    private Attenuation att;
    private Vector3F position;
    private float range;

    public PointLight(BaseLight baseLight, Attenuation att, Vector3F position, float range) {
        this.baseLight = baseLight;
        this.att = att;
        this.position = position;
        this.range = range;
    }

    public BaseLight getBaseLight() {
        return baseLight;
    }

    public void setBaseLight(BaseLight baseLight) {
        this.baseLight = baseLight;
    }

    public Attenuation getAtt() {
        return att;
    }

    public void setAtt(Attenuation att) {
        this.att = att;
    }

    public Vector3F getPosition() {
        return position;
    }

    public void setPosition(Vector3F position) {
        this.position = position;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }
}
