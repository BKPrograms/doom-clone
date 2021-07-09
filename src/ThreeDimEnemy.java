public class ThreeDimEnemy extends Enemy{

    private static MyMesh enemyMesh;

    public static final float Y_SIZE = 0.6f;
    public static float X_SIZE = Y_SIZE;
    private static final float SOLDIER_START = 0;

    public static final float MOVE_SPEED = 2f;
    public static final float DIST_FROM_PLAYER = 1.25f;

    public static final float SHOOT_DISTANCE = 1000;

    public static final float SHOT_ANGLE = 10;

    private boolean canLook;
    private boolean canShoot;

    public static final float ATTACK_PROBABILITY = 0.5f;

    public static final int MIN_DMG = 5;

    public static final int MAX_DMG = 30;

    private double deathTime;

    private boolean containsDrop;
    private int dropType;

    public ThreeDimEnemy(Transform transformer) {
        super(transformer);

        canShoot = false;
        canLook = false;

        containsDrop = (rand.nextInt(2) == 1);

        health = 100;

        enemyMat = new Material(new Texture("arenaSpriteSheet.png"));

        float val1 = enemyMat.getTexture().getHeight() / enemyMat.getTexture().getWidth();
        float val2 = enemyMat.getTexture().getWidth() / enemyMat.getTexture().getHeight();

        X_SIZE = (float) (Y_SIZE / (Math.max(val1, val2) * 2.0));

        if (enemyMesh == null) {

            Vertex[] vertices = new Vertex[]{new Vertex(new Vector3F(-X_SIZE, SOLDIER_START, SOLDIER_START), new Vector2F(TEX_MAX_X, TEX_MAX_Y)),
                    new Vertex(new Vector3F(-X_SIZE, Y_SIZE, SOLDIER_START), new Vector2F(TEX_MAX_X, TEX_MIN_Y)),
                    new Vertex(new Vector3F(X_SIZE, Y_SIZE, SOLDIER_START), new Vector2F(TEX_MIN_X, TEX_MIN_Y)),
                    new Vertex(new Vector3F(X_SIZE, SOLDIER_START, SOLDIER_START), new Vector2F(TEX_MIN_X, TEX_MAX_Y))};

            int[] indices = new int[]{0, 1, 2,
                    0, 2, 3,};

            enemyMesh = new MyMesh("monkey3.obj");

        }

        deathTime = 0;


    }

    public void damageEnemy(int dmgAmt) {

        if (currentState != DED) {

            if (currentState == IDLE) {
                currentState = CHASING;
            }


            health -= dmgAmt;

            if (health <= 0) {
                currentState = DYING;
            }
        }

    }


    private void idleUpdate(Vector3F orientation, float distance, double timeDecimals) {


        if (timeDecimals < 0.5) {

            canLook = true;

        } else {

            if (canLook) {

                Vector2F lineStart = new Vector2F(transform.getTranslation().getX(), transform.getTranslation().getZ());

                Vector2F lookDirection = new Vector2F(orientation.getX(), orientation.getZ()); //.rotate((rand.nextFloat() - 0.5f) * SHOT_ANGLE);

                Vector2F shootPathEnd = lineStart.add(lookDirection.mult(SHOOT_DISTANCE));

                Vector2F collVec = Game.getLevel().checkIntersections(lineStart, shootPathEnd, false, false);

                Vector2F swizzledPlayerPos = new Vector2F(Transform.getCamera().getPosition().getX(), Transform.getCamera().getPosition().getZ());

                if (collVec == null || swizzledPlayerPos.sub(lineStart).length() < collVec.sub(lineStart).length()) {

                    currentState = CHASING;

                }

                canLook = false;
            }

        }


    }


    private void chaseUpdate(Vector3F orientation, float distance, double timeDecimals) {

        if (rand.nextDouble() < ATTACK_PROBABILITY * Time.getDelta()) { // Randomized attack behaviour
            currentState = ATTACKING;
        }


        if (distance > DIST_FROM_PLAYER) { // It's time to move closer to player

            float move_amt = MOVE_SPEED * (float) Time.getDelta();

            Vector3F oldPos = transform.getTranslation();

            Vector3F newPos = transform.getTranslation().add(orientation.mult(move_amt));

            Vector3F collVec = Game.getLevel().checkCollisions(oldPos, newPos, WIDTH, LENGTH, true, true, false);

            Vector3F finalVec = collVec.mult(orientation);

            if (collVec.length() > 0) {
                transform.setTranslation(transform.getTranslation().add(finalVec.mult(move_amt)));
            }

            if (finalVec.sub(orientation).length() != 0) {

                Game.getLevel().openDoors(transform.getTranslation());

            }


        } else {
            currentState = ATTACKING;
        }


    }

    private void attackUpdate(Vector3F orientation, float distance, double timeDecimals) {
        currentState = CHASING;
    }

    private void dyingUpdate(Vector3F orientation, float distance, double timeDecimals) {

        double currTime = Time.getTime() / (double) Time.SECOND;

        if (deathTime == 0) {
            deathTime = currTime; // Starting to keep track of when soldier died
        }

        currentState = DED;


    }

    // Literally a dead state in our little DFA
    private void deadUpdate(Vector3F orientation, float distance, double timeDecimals) {



        if (containsDrop) {

            Vector3F dropPos = transform.getTranslation().add(orientation.mult(0.1f));

            if (dropType == 0){
                Game.getLevel().addGameItem(new Medkit(dropPos));
            } else if (dropType == 1){
                Game.getLevel().addGameItem(new Ammo(dropPos));
            }

            containsDrop = false;
        }

        transform.setScaling(1, 0.5f, 1);
    }

    private void alignWithCamera(Vector3F orientation) {

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

        if (orientation.getX() < 0) {
            angle += 180;
        }
        // We require that extra 90 degree turn as it's the plane edge rather than center
        transform.getRotation().setY(angle + 270);
//        if (orientation.getX() < 0) {
//            transform.getRotation().setY(angle - 90);
//        } else {
//            transform.getRotation().setY(angle + 90);
//        }


    }

    public Vector3F getPos() {
        return transform.getTranslation();
    }

    float timer = 0;

    @Override
    public void update() {

        transform.getTranslation().setY(1f);

        timer += Time.getDelta();

        Vector3F dir = Transform.getCamera().getPosition().sub(transform.getTranslation()); //transform.getTranslation().sub(Transform.getCamera().getPosition());
        float distance = dir.length();
        Vector3F orientation = dir.div(distance);

        alignWithCamera(orientation);

        double currentTime = Time.getTime() / (double) Time.SECOND;

        double timeDecimals = currentTime - ((double) (int) currentTime);

        switch (currentState) {
            case IDLE:
                idleUpdate(orientation, distance, timeDecimals);
                break;
            case CHASING:
                chaseUpdate(orientation, distance, timeDecimals);
                break;
            case ATTACKING:
                attackUpdate(orientation, distance, timeDecimals);
                break;
            case DYING:
                dyingUpdate(orientation, distance, timeDecimals);
                break;
            case DED:
                deadUpdate(orientation, distance, timeDecimals);
                break;

        }

    }

    @Override
    public void render() {
        Shader shader = Game.getLevel().getShader();
        shader.bind();
        shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), enemyMat);
        enemyMesh.draw();
    }
}
