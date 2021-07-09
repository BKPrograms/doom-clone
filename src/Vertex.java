public class Vertex {

    public static final int VERTEX_SIZE = 8;

    private Vector3F position;
    private Vector2F textureCoordinate; // Where on the current polygon I put textures
    private Vector3F normal;

    public Vertex(Vector3F position) {
        // this.position = position;

        this(position, new Vector2F(0, 0)); // Uses the below constructor
    }

    public Vertex(Vector3F position, Vector2F texturepos){
        this(position, texturepos, new Vector3F(0, 0, 0));
    }

    public Vertex(Vector3F position, Vector2F texturepos, Vector3F normal){
        this.position = position;
        this.textureCoordinate = texturepos;
        this.normal = normal;
    }

    public Vector3F getPosition() {
        return position;
    }

    public void setPosition(Vector3F position) {
        this.position = position;
    }

    public Vector2F getTextureCoordinate() {
        return textureCoordinate;
    }

    public void setTextureCoordinate(Vector2F textureCoordinate) {
        this.textureCoordinate = textureCoordinate;
    }

    public Vector3F getNormal() {
        return normal;
    }

    public void setNormal(Vector3F normal) {
        this.normal = normal;
    }
}
