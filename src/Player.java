import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Random;

public class Player {

    public static final float GUN_OFFSET = -0.0875f;

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVE_SPEED = 10;
    private static final Vector3F zeroVec = new Vector3F(0, 0, 0);
    public static final float PLAYER_SIZE = 0.3f;

    public static float SHOOT_DISTANCE = 1000;

    public static final int MIN_DMG = 10;

    public static final int MAX_DMG = 50;

    private Random rand;

    private Vector3F movementVec = zeroVec;

    private Camera camera;

    private int health;
    private int ammoCount;

    public static final int MAX_HEALTH = 100;
    public static final int MAX_AMMO = 75;

    private MyMesh gunMesh;
    private Material gunMat;
    private Transform gunTransform;

    private static final float TEX_MAX_X = 0;
    private static final float TEX_MIN_X = -1;

    private static final float TEX_MAX_Y = 0;
    private static final float TEX_MIN_Y = -1;

    public static final float Y_SIZE = 0.0625f;
    public static float X_SIZE = Y_SIZE;
    private static final float START = 0;

    private double timeSinceLastShot;

    private float SHOTGUN_ANGLE = 10f;

    private int curr_weapon;

    private int shootBuffer;
    private AudioSource sb;

    private AudioSource shotgunsource;
    private int shotgunBuffer;

    private int soundBuff;
    private AudioSource soundSource;

    private ArrayList<Texture[]> weapons;

    SpotLight flashlight = new SpotLight(new PointLight(new BaseLight(new Vector3F(0, 0, 0), 0.2f), new Attenuation(0, 0, 0.1f), new Vector3F(-2, 0, 5f), 30), new Vector3F(1, 1, 1), 0.7f);

    public Player(Vector3F startingPos) {

        PhongShader.setSpotLights(new SpotLight[]{flashlight});

        camera = new Camera(startingPos, new Vector3F(0, 0, 1), new Vector3F(0, 1, 0));
        rand = new Random();

        curr_weapon = 0; // Default Pistol is 0 (1 is Shotgun)

        health = MAX_HEALTH;
        ammoCount = MAX_AMMO;

        weapons = new ArrayList<>();

        weapons.add(new Texture[]{new Texture("PISGB0.png"), new Texture("PISFA0.png")});
        weapons.add(new Texture[]{new Texture("TacticalShotgun.png")});


        gunMat = new Material(weapons.get(0)[0]); // TacticalShotgun.png

        float val1 = gunMat.getTexture().getHeight() / gunMat.getTexture().getWidth();
        float val2 = gunMat.getTexture().getWidth() / gunMat.getTexture().getHeight();

        X_SIZE = (float) (Y_SIZE / (Math.max(val1, val2) * 2.0));


        if (gunMesh == null) {

            Vertex[] vertices = new Vertex[]{new Vertex(new Vector3F(-X_SIZE, START, START), new Vector2F(TEX_MAX_X, TEX_MAX_Y)),
                    new Vertex(new Vector3F(-X_SIZE, Y_SIZE, START), new Vector2F(TEX_MAX_X, TEX_MIN_Y)),
                    new Vertex(new Vector3F(X_SIZE, Y_SIZE, START), new Vector2F(TEX_MIN_X, TEX_MIN_Y)),
                    new Vertex(new Vector3F(X_SIZE, START, START), new Vector2F(TEX_MIN_X, TEX_MAX_Y))};

            int[] indices = new int[]{0, 1, 2,
                    0, 2, 3,};

            gunMesh = new MyMesh(vertices, indices, true);

        }

        gunTransform = new Transform();
        gunTransform.setTranslation(7, 0, 7);

    }

    boolean mouseInViewMode = false;
    static Vector2F centerPosition = new Vector2F(GameWindow.getWidth() / 2, GameWindow.getHeight() / 2);

    private void alignGun() {
        // Moving Gun

        gunTransform.setTranslation(camera.getPosition().add(camera.getForward().normalized().mult(0.105f)).add(camera.getRight().normalized().mult(0.01f)));
        gunTransform.getTranslation().setY(gunTransform.getTranslation().getY() + GUN_OFFSET);

        Vector3F orientation = Transform.getCamera().getPosition().sub(gunTransform.getTranslation()).normalized();

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

        if (orientation.getX() < 0) {
            angle += 180;
        }
        // We require that extra 90 degree turn as it's the plane edge rather than center
        gunTransform.getRotation().setY(angle + 90);
    }

    private void pistolShoot() {

        sb.play(shootBuffer);

        gunMat.setTexture(weapons.get(0)[1]);

        Vector2F lineStart = camera.getPosition().getXZ(); // new Vector2F(camera.getPosition().getX(), camera.getPosition().getZ());

        Vector2F castDirection = camera.getForward().getXZ().normalize(); //new Vector2F(camera.getForward().getX(), camera.getForward().getZ()).normalize();

        Vector2F lineEnd = lineStart.add(castDirection.mult(SHOOT_DISTANCE));

        Game.getLevel().checkIntersections(lineStart, lineEnd, true, false);
    }

    private void shootAtAngle(float angle) { // Shotgun spread type thing

        shotgunsource.play(shotgunBuffer);

        Vector2F lineStart = camera.getPosition().getXZ(); // new Vector2F(camera.getPosition().getX(), camera.getPosition().getZ());

        Vector2F castDirection = camera.getForward().getXZ().normalize(); //new Vector2F(camera.getForward().getX(), camera.getForward().getZ()).normalize();

        Vector2F lineEnd = lineStart.add(castDirection.mult(SHOOT_DISTANCE));

        Game.getLevel().checkIntersections(lineStart, lineEnd, true, true);
    }


    private void switchWeapon(int weaponToSwitch) {

        if (curr_weapon != weaponToSwitch) {
            gunMat.setTexture(weapons.get(weaponToSwitch)[0]);
            curr_weapon = weaponToSwitch;
        }

    }

    public void initAudio() {
        shootBuffer = AudioMaster.loadSound("dspistol.wav");
        sb = new AudioSource(camera.getPosition());

        soundBuff = AudioMaster.loadSound("dsplpain.wav");
        soundSource = new AudioSource(camera.getPosition());

        soundSource.setVolume(0.6f);

        shotgunsource = new AudioSource(camera.getPosition());
        shotgunBuffer = AudioMaster.loadSound("dsshotgun.wav");
    }

    boolean flashOn = true;

    public void input() {

        double curr_time = Time.getTime() / (double) Time.SECOND;

        boolean old = Input.getCursorVisible();

        if (Input.getKey(Keyboard.KEY_ESCAPE)) {
            Input.setCursorVisible(true);
            mouseInViewMode = false;

        }

        if (Input.getMouse(0)) {
            Input.setCursorVisible(false);
            Input.setMousePosition(centerPosition);
            mouseInViewMode = true;
        }

        if (Input.getKeyDown(Keyboard.KEY_H)) {
            System.out.println("Health: " + health);
            System.out.println("Ammo: " + ammoCount);

        }

        if (mouseInViewMode) {


            if (Input.getKeyDown(Keyboard.KEY_E)) {

                Game.getLevel().openDoors(camera.getPosition());

            }

            if (Input.getKeyDown(Keyboard.KEY_F)) {
                int myInt = flashOn ? 1 : 0;
                flashlight.getPointLight().getBaseLight().setColour(new Vector3F(1, 1, 1).mult(myInt));
                flashOn = !flashOn;

            }
            int dwheel = Input.getMouseScrollAmt();

            if (dwheel > 0) {

                SHOOT_DISTANCE = 5;
                switchWeapon(1); // Shotgun

            } else if (dwheel < 0) {
                SHOOT_DISTANCE = 1000;
                switchWeapon(0);
            } else if (Input.getMouseDown(0) && (Input.getCursorVisible() == old)) {


                if ((curr_weapon == 0 && ammoCount > 0) || (curr_weapon == 1 && ammoCount > 4)) { // Shoot time


                    if (curr_weapon == 0 && (curr_time - timeSinceLastShot >= 0.4)) {
                        timeSinceLastShot = Time.getTime() / (double) Time.SECOND;

                        ammoCount -= 1;

                        pistolShoot();
                    } else if (curr_weapon == 1 && (curr_time - timeSinceLastShot >= 0.7)) {

                        timeSinceLastShot = Time.getTime() / (double) Time.SECOND;

                        ammoCount -= 5;

                        for (float i : new float[]{-0.5f, -0.25f, 0, 0.25f, 0.5f}) {
                            shootAtAngle(i * SHOTGUN_ANGLE);
                        }
                    }

                } else {
                    System.out.println("click click");
                }


            }


            movementVec = zeroVec; // How much we're gonna move by (initially zero vector in case we don't)

            if (Input.getKey(Keyboard.KEY_W)) {

                movementVec = movementVec.add(camera.getForward());

            }
            if (Input.getKey(Keyboard.KEY_S)) {
                movementVec = movementVec.sub(camera.getForward());

            }
            if (Input.getKey(Keyboard.KEY_A)) {
                movementVec = movementVec.add(camera.getLeft());

            }
            if (Input.getKey(Keyboard.KEY_D)) {
                movementVec = movementVec.add(camera.getRight());

            }

            movementVec.setY(0); // Stops us from going up or down

            Vector2F deltaPos = Input.getMousePosition().sub(centerPosition);

            boolean rotY = deltaPos.getX() != 0;
            boolean rotX = deltaPos.getY() != 0;

            if (rotY) {
                camera.rotateY(deltaPos.getX() * MOUSE_SENSITIVITY);
            }

            if (rotX) {
                camera.rotateX(-deltaPos.getY() * MOUSE_SENSITIVITY);
            }

            if (rotY || rotX) {
                Input.setMousePosition(centerPosition);
            }

        }

        alignGun();
    }

    public void update() {

        double currTime = Time.getTime() / (double) Time.SECOND;


        if (0.3 < currTime - timeSinceLastShot && currTime - timeSinceLastShot < 0.5 && curr_weapon == 0) {

            gunMat.setTexture(new Texture("PISGB0.png"));
        }


        float translationAmount = (float) (MOVE_SPEED * Time.getDelta());


        if (movementVec.length() > 0) { // If player decides to move check for potential collision
            movementVec = movementVec.normalized();// Gets direction we need to move in

            Vector3F oldPos = camera.getPosition();

            Vector3F newPos = oldPos.add(movementVec.mult(translationAmount));

            Vector3F collisionVec = Game.getLevel().checkCollisions(oldPos, newPos, PLAYER_SIZE, PLAYER_SIZE, true, true, false);

            movementVec = movementVec.mult(collisionVec); // The 0 and 1's in the corresponding place cancel

            if (movementVec.length() > 0) {
                camera.move(movementVec, translationAmount);
            }


        }


        flashlight.getPointLight().setPosition(Transform.getCamera().getPosition().sub(new Vector3F(0, 0.15f, 0)).add(Transform.getCamera().getForward().normalized().mult(0.1f)));
        flashlight.setDirection(Transform.getCamera().getForward());


    }

    public void render() {
        Shader shader = Game.getLevel().getShader();
        shader.bind();
        shader.updateUniforms(gunTransform.getTransformation(), gunTransform.getProjectedTransformation(), gunMat);
        gunMesh.draw();

    }

    public Camera getCamera() {
        return camera;
    }

    public void setPos(Vector3F pos) {
        camera.setPosition(pos);
    }

    public int getDmgAmt() {

        return rand.nextInt(MAX_DMG - MIN_DMG) + MIN_DMG;

    }

    public void damagePlayer(int dmgAmt) {

        if (dmgAmt > 0) {
            soundSource.play(soundBuff);
        }

        health -= dmgAmt;

        if (health > MAX_HEALTH)
            health = MAX_HEALTH;

        if (health <= 0 && -20 <= health) {
            System.out.println("gg");
            camera.getPosition().setY(0.2f);
            gunMat = null;
            Input.setCursorVisible(true);
            mouseInViewMode = false;
        } else if (health < -20){
            Game.setIsRunning(false);
        }
    }

    public void addAmmo(int amt) {

        ammoCount += amt;

    }

    public int getHealth() {
        return health;
    }

    public int getAmmoCount() {
        return ammoCount;
    }

}
