import java.util.ArrayList;

public class LostSoul extends Enemy {

    private static MyMesh enemyMesh;

    public static final float scale = 0.5f;
    public static final float Y_SIZE = scale;
    public static float X_SIZE = Y_SIZE;
    private static final float SOLDIER_START = 0;

    public static final float MOVE_SPEED = 15f;
    public static final float DIST_FROM_PLAYER = 0.2f;

    public static final float HIT_DISTANCE = 10;

    private boolean canLook;

    public static final int MIN_DMG = 40;

    public static final int MAX_DMG = 50;

    private double deathTime;

    private int soundBuff;
    private AudioSource soundSource;

    private int soundBuff2;
    private AudioSource soundSource2;

    public LostSoul(Transform transform) {

        super(transform);

        health = 80;

        soundBuff = AudioMaster.loadSound("lsexplosion.wav");

        soundSource = new AudioSource(transform.getTranslation());

        soundBuff2 = AudioMaster.loadSound("lshurt.wav");

        soundSource2 = new AudioSource(transform.getTranslation());

        transform.getTranslation().setY(rand.nextFloat() * Level.SQUARE_HEIGHT/2);

        if (animations == null) {
            animations = new ArrayList<Texture>();

            // Walking animations (Consider separate ArrayLists)
            animations.add(new Texture("lsw1.png"));
            animations.add(new Texture("lsw2.png"));
            animations.add(new Texture("lsw3.png"));
            animations.add(new Texture("lsw4.png"));

            // Chasing Animations
            animations.add(new Texture("lsc1.png"));
            animations.add(new Texture("lsc2.png"));

            // Blowing up
            for (int i = 1; i < 8; i++) {
                animations.add(new Texture("explos" + i + ".png"));
            }
        }


        enemyMat = new Material(animations.get(rand.nextInt(3 - 1) + 1));

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

            enemyMesh = new MyMesh(vertices, indices, true);

        }

        deathTime = 0;


    }

    private void idleUpdate(Vector3F orientation, float distance, double timeDecimals) {


        if (timeDecimals < 0.5) {

            canLook = true;

            enemyMat.setTexture(animations.get(1));

        } else {

            enemyMat.setTexture(animations.get(3));

            if (canLook) {

                Vector2F lineStart = new Vector2F(transform.getTranslation().getX(), transform.getTranslation().getZ());

                Vector2F lookDirection = new Vector2F(orientation.getX(), orientation.getZ());

                Vector2F shootPathEnd = lineStart.add(lookDirection.mult(HIT_DISTANCE));

                Vector2F collVec = Game.getLevel().checkIntersections(lineStart, shootPathEnd, false, false);

                Vector2F swizzledPlayerPos = Transform.getCamera().getPosition().getXZ(); // new Vector2F(Transform.getCamera().getPosition().getX(), Transform.getCamera().getPosition().getZ());

                if ((collVec == null || swizzledPlayerPos.sub(lineStart).length() < collVec.sub(lineStart).length()) && distance < 5) {
                    enemyMat.setTexture(animations.get(0));
                    currentState = CHASING;

                }

                canLook = false;
            }

        }


    }


    private void chaseUpdate(Vector3F orientation, float distance, double timeDecimals) {


        if (timeDecimals < 0.25) {
            enemyMat.setTexture(animations.get(4));
        } else if (timeDecimals < 0.5) {
            enemyMat.setTexture(animations.get(5));
        } else if (timeDecimals < 0.75){
            enemyMat.setTexture(animations.get(4));
        } else {
            enemyMat.setTexture(animations.get(5));
        }

        if (distance > DIST_FROM_PLAYER) { // It's time to move closer ot player

            float move_amt = MOVE_SPEED * (float) Time.getDelta();

            Vector3F oldPos = transform.getTranslation();

            Vector3F newPos = transform.getTranslation().add(orientation.mult(move_amt)).rotate(rand.nextInt(20 + 20) - 20, new Vector3F(0, 1, 0));

            Vector3F collVec = Game.getLevel().checkCollisions(oldPos, newPos, WIDTH, LENGTH, true, true, false);

            collVec.setY(1);

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

        if (timeDecimals < 0.1) {
            enemyMat.setTexture(animations.get(4));
        } else if (timeDecimals < 0.2) {
            enemyMat.setTexture(animations.get(5));
        } else if (timeDecimals < 0.3) {
            enemyMat.setTexture(animations.get(6));
        } else if (timeDecimals < 0.4) {
            enemyMat.setTexture(animations.get(7));
        } else if (timeDecimals < 0.5) {
            enemyMat.setTexture(animations.get(8));
        } else if (timeDecimals < 0.6) {
            enemyMat.setTexture(animations.get(9));
        } else if (timeDecimals < 0.7) {
            enemyMat.setTexture(animations.get(10));
        } else if (timeDecimals < 0.8) {
            enemyMat.setTexture(animations.get(11));
        } else if (timeDecimals < 0.9) {
            enemyMat.setTexture(animations.get(12));
            soundSource.play(soundBuff);
            if (distance < DIST_FROM_PLAYER) {
                Game.getLevel().damagePlayer(rand.nextInt(MAX_DMG - MIN_DMG) + MIN_DMG);
            }
            currentState = DED;
        } else {
            enemyMat.setTexture(animations.get(4));
        }

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


    private void dyingUpdate(Vector3F orientation, float distance, double timeDecimals) {

        double currTime = Time.getTime() / (double) Time.SECOND;

        if (deathTime == 0) {
            deathTime = currTime; // Starting to keep track of when soldier died
        }

        if (currTime < 0.1f + deathTime) {
            enemyMat.setTexture(animations.get(0));
        } else if (currTime < 0.3f + deathTime) {
            soundSource2.play(soundBuff2);
            enemyMat.setTexture(animations.get(1));
        } else if (currTime < 0.45f + deathTime) {
            enemyMat.setTexture(animations.get(2));
        } else if (currTime < 0.6f + deathTime) {
            enemyMat.setTexture(animations.get(3));
        } else {
            currentState = DED;
        }


    }

    // Literally a dead state in our little DFA
    private void deadUpdate(Vector3F orientation, float distance, double timeDecimals) {

        enemyMat.setTexture(animations.get(1));
        transform.setScaling(1, 0.5f, 1);
        transform.getTranslation().setY(0.1f);
    }

    private void alignWithCamera(Vector3F orientation) {

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));


        if (orientation.getX() < 0) {
            angle += 180;
        }
        // We require that extra 90 degree turn as it's the plane edge rather than center
        transform.getRotation().setY(angle + 90);

    }


    @Override
    public void update() {

        Vector3F dir = Transform.getCamera().getPosition().sub(transform.getTranslation());
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
