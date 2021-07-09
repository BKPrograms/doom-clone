import java.util.ArrayList;
import java.util.Random;

public class HellKnight extends Enemy {

    private static MyMesh enemyMesh;

    public static final float scale = 0.5f * Level.SQUARE_HEIGHT;
    public static final float Y_SIZE = scale;
    public static float X_SIZE = Y_SIZE;
    private static final float SOLDIER_START = 0;

    public static final float MOVE_SPEED = 9f;
    public static final float DIST_FROM_PLAYER = 1f;

    public static final float HIT_DISTANCE = 10;

    private int soundBuff2;
    private AudioSource soundSource2;

    private int soundBuff3;
    private AudioSource soundSource3;

    private int soundBuff4;
    private AudioSource soundSource4;

    private boolean canLook;
    private boolean canShoot;

    public static final int MIN_DMG = 1;

    public static final int MAX_DMG = 2;

    private double deathTime;

    private boolean containsDrop;

    private PointLight enemyLight = new PointLight(new BaseLight(new Vector3F(0.2f, 0.8f, 0.2f), 0.2f), new Attenuation(0, 0, 0.1f), new Vector3F(-2, 0, 5f), 30);

    public HellKnight(Transform transformer) {

        super(transformer);

        PhongShader.setPointLights(new PointLight[]{enemyLight});

        currentState = IDLE;

        canShoot = false;
        canLook = false;

        soundBuff2 = AudioMaster.loadSound("hksight.wav");
        soundSource2 = new AudioSource(transform.getTranslation());

        soundBuff3 = AudioMaster.loadSound("hkattack.wav");
        soundSource3 = new AudioSource(transform.getTranslation());

        soundBuff4 = AudioMaster.loadSound("hkdeath.wav");
        soundSource4 = new AudioSource(transform.getTranslation());


        rand = new Random();

        containsDrop = true;

        health = 800;

        if (animations == null) {
            animations = new ArrayList<Texture>();

            // Walking animations (Consider separate ArrayLists)
            animations.add(new Texture("hkw1.png"));
            animations.add(new Texture("hkw1.png"));
            animations.add(new Texture("hkw1.png"));

            // Hitting Animations
            animations.add(new Texture("hka1.png"));
            animations.add(new Texture("hka2.png"));
            animations.add(new Texture("hka3.png"));
        }


        enemyMat = new Material(animations.get(0));

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

            enemyMat.setTexture(animations.get(0));

        } else {

            enemyMat.setTexture(animations.get(1));

            if (canLook) {

                Vector2F lineStart = new Vector2F(transform.getTranslation().getX(), transform.getTranslation().getZ());

                Vector2F lookDirection = new Vector2F(orientation.getX(), orientation.getZ());

                Vector2F shootPathEnd = lineStart.add(lookDirection.mult(HIT_DISTANCE));

                Vector2F collVec = Game.getLevel().checkIntersections(lineStart, shootPathEnd, false, false);

                Vector2F swizzledPlayerPos = Transform.getCamera().getPosition().getXZ(); //new Vector2F(Transform.getCamera().getPosition().getX(), Transform.getCamera().getPosition().getZ());

                if ((collVec == null || swizzledPlayerPos.sub(lineStart).length() < collVec.sub(lineStart).length()) && distance < 15) {
                    soundSource2.play(soundBuff2);
                    currentState = CHASING;

                }

                canLook = false;
            }

        }


    }


    private void chaseUpdate(Vector3F orientation, float distance, double timeDecimals) {


        if (timeDecimals < 0.25) {
            enemyMat.setTexture(animations.get(0));
        } else if (timeDecimals < 0.5) {
            enemyMat.setTexture(animations.get(1));
        } else if (timeDecimals < 0.75) {
            enemyMat.setTexture(animations.get(2));
        } else {
            enemyMat.setTexture(animations.get(3));
        }

        if (distance > DIST_FROM_PLAYER) { // It's time to move closer ot player

            float move_amt = MOVE_SPEED * (float) Time.getDelta();

            Vector3F oldPos = transform.getTranslation();

            Vector3F newPos = transform.getTranslation().add(orientation.mult(move_amt));

            Vector3F collVec = Game.getLevel().checkCollisions(oldPos, newPos, WIDTH * 2, LENGTH * 2, true, true, false);

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
            enemyMat.setTexture(animations.get(3));
        } else if (timeDecimals < 0.2) {
            enemyMat.setTexture(animations.get(4));
        } else if (timeDecimals < 0.5) {
            enemyMat.setTexture(animations.get(5));
            if (canShoot) {

                soundSource3.play(soundBuff3);
                if (distance < DIST_FROM_PLAYER) {
                    Game.getLevel().damagePlayer(rand.nextInt(MAX_DMG - MIN_DMG) + MIN_DMG);
                } else {
                    currentState = CHASING;
                }


            }

        } else {
            enemyMat.setTexture(animations.get(4));
            canShoot = true;
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

        if (containsDrop) {

            soundSource4.play(soundBuff4);

            Vector3F dropPos = transform.getTranslation().add(orientation.mult(0.1f));

            Vector3F dropPos2 = transform.getTranslation().add(orientation.mult(0.2f));

            Game.getLevel().addGameItem(new Medkit(dropPos));
            Game.getLevel().addGameItem(new Ammo(dropPos2));

            containsDrop = false;
        }

        enemyMat.setTexture(animations.get(1));
        transform.setScaling(1, 0.5f, 1);
    }

    private void alignWithCamera(Vector3F orientation) {

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));


        if (orientation.getX() < 0) {
            angle += 180;
        }
        // We require that extra 90 degree turn as it's the plane edge rather than center
        transform.getRotation().setY(angle + 90);

        enemyLight.setPosition(transform.getTranslation().add(orientation.mult(0.1f).add(new Vector3F(0, 1, 0))));

    }

    float timer = 0;

    @Override
    public void update() {

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
        Shader shader = BasicShader.getInstance(); //Game.getLevel().getShader();
        shader.bind();
        shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), enemyMat);
        enemyMesh.draw();
    }

}

