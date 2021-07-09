import java.util.ArrayList;

public class Projectile extends GameItem{
    
    private static MyMesh mesh;
    private Material projectMat;

    private Transform transform;
    private Vector3F endPos;

    private static final float TEX_MAX_X = 0;
    private static final float TEX_MIN_X = -1;

    private static final float TEX_MAX_Y = 0;
    private static final float TEX_MIN_Y = -1;

    public static final float scale = 0.1f;
    public static final float Y_SIZE = scale;
    public static float X_SIZE = Y_SIZE;
    private static final float START = 0;

    private Vector3F start;
    private boolean hitSomethingYet;

    private ArrayList<Texture> animations;

    public Projectile(Vector3F startpos, Vector3F end) {

        this.endPos = end;
        this.start = startpos;

        hitSomethingYet = false;

        projectMat = new Material(new Texture("orb_red.png"));

        float val1 = projectMat.getTexture().getHeight() / projectMat.getTexture().getWidth();
        float val2 = projectMat.getTexture().getWidth() / projectMat.getTexture().getHeight();

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

        if (animations == null){
            animations = new ArrayList<Texture>();

            for (int i = 1; i < 8; i++) {
                animations.add(new Texture("explos" + i + ".png"));
            }

        }

        transform = new Transform();
        transform.setTranslation(startpos);
    }

    public void update() {

        Vector3F orientation = Transform.getCamera().getPosition().sub(transform.getTranslation());


        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));


        if (orientation.getX() < 0) {
            angle += 180;
        }

        transform.getRotation().setY(angle + 90);

        if (!hitSomethingYet){

            float move_amt = 15 * (float) Time.getDelta();

            orientation = endPos.sub(start).normalized();

            Vector3F oldPos = transform.getTranslation();

            Vector3F newPos = transform.getTranslation().add(orientation.mult(move_amt));

            Vector3F collVecWithWalls = Game.getLevel().checkCollisions(oldPos, newPos, 0.1f, 0.1f, true, true, true);

            Vector3F finalVec = collVecWithWalls.mult(orientation);

            if (collVecWithWalls.length() > 1) {
                transform.setTranslation(transform.getTranslation().add(finalVec.mult(move_amt)));
            } else{
                hitSomethingYet = true;
            }
        } else {
            explode();

        }

    }

    private void explode(){
        double currentTime = Time.getTime() / (double) Time.SECOND;

        double timeDecimals = currentTime - ((double) (int) currentTime);

        if (timeDecimals < 0.1){
            projectMat.setTexture(animations.get(0));
        } else if (timeDecimals < 0.2){
            projectMat.setTexture(animations.get(1));
        }else if (timeDecimals < 0.3){
            projectMat.setTexture(animations.get(2));
        }else if (timeDecimals < 0.4){
            projectMat.setTexture(animations.get(3));
        }else if (timeDecimals < 0.5){
            projectMat.setTexture(animations.get(4));
        } else if (timeDecimals < 0.6){
            projectMat.setTexture(animations.get(5));
        } else if (timeDecimals < 0.7){
            projectMat.setTexture(animations.get(6));
        } else {
            Game.getLevel().removeGameItem(this);
        }



    }

    public void render() {
        Shader shader = Game.getLevel().getShader();
        shader.bind();
        shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), projectMat);
        mesh.draw();
    }
}
