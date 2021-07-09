public class Medkit extends GameItem {


    public static float PICKUP_DIST = 0.5f;

    private static  MyMesh mesh;
    private static Material medKitMat;

    private Transform transform;

    private static final float TEX_MAX_X = 0;
    private static final float TEX_MIN_X = -1;

    private static final float TEX_MAX_Y = 0;
    private static final float TEX_MIN_Y = -1;

    public static final float scale = 0.2f;
    public static final float Y_SIZE = scale;
    public static float X_SIZE = Y_SIZE;
    private static final float START = 0;

    private int soundBuff1;
    private AudioSource soundSource1;

    public Medkit(Vector3F pos){

        soundBuff1 = AudioMaster.loadSound("smallmedkit1.wav");
        soundSource1 = new AudioSource(pos);

        if (medKitMat == null){
            medKitMat = new Material(new Texture("MEDIA0.png"));
        }


        float val1 = medKitMat.getTexture().getHeight() / medKitMat.getTexture().getWidth();
        float val2 = medKitMat.getTexture().getWidth() / medKitMat.getTexture().getHeight();

        X_SIZE = (float) (Y_SIZE / (Math.max(val1, val2) * 2.0));

        if (mesh == null) {

            Vertex[] vertices = new Vertex[]{new Vertex(new Vector3F(-X_SIZE, START, START), new Vector2F(TEX_MAX_X, TEX_MAX_Y)),
                    new Vertex(new Vector3F(-X_SIZE, Y_SIZE, START), new Vector2F(TEX_MAX_X, TEX_MIN_Y)),
                    new Vertex(new Vector3F(X_SIZE, Y_SIZE, START), new Vector2F(TEX_MIN_X, TEX_MIN_Y)),
                    new Vertex(new Vector3F(X_SIZE, START, START), new Vector2F(TEX_MIN_X, TEX_MAX_Y))};

            int[] indices = new int[]{0, 1, 2,
                    0, 2, 3,};

            mesh = new MyMesh(vertices, indices, true);

        }

        transform = new Transform();
        transform.setTranslation(pos);
    }

    public void update(){

        Vector3F orientation = Transform.getCamera().getPosition().sub(transform.getTranslation());

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));


        if (orientation.getX() < 0) {
            angle += 180;
        }

        transform.getRotation().setY(angle + 90);

        if (orientation.length() < PICKUP_DIST){

            if (Game.getLevel().getPlayer().getHealth() < Player.MAX_HEALTH){
                soundSource1.play(soundBuff1);
                Game.getLevel().damagePlayer(-10);
                Game.getLevel().removeGameItem(this);
            }

        }

    }

    public void render(){
        Shader shader = Game.getLevel().getShader();
        shader.bind();
        shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), medKitMat);
        mesh.draw();
    }
}
