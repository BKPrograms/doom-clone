import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class Level {
    private MyMesh levelMesh;
    private Bitmap level;
    private Shader shader;
    private Material material;
    private Transform transformer;
    private ArrayList<Door> doors;
    private Player player;

    public static final float SQUARE_WIDTH = 1;
    public static final float SQUARE_LENGTH = 1;
    public static final float SQUARE_HEIGHT = 1;

    private static final float OPEN_DOOR_DIS = 2;
    private static final float DOOR_OPEN_MOVEMENT_AMT = 0.9f;

    private static final int NUM_TEX_EXP = 4;
    private static final int NUM_TEXTURES = (int) Math.pow(2, NUM_TEX_EXP);

//    private int musicBuffer;
//    private AudioSource musicSource;

    private ArrayList<Enemy> enemies;


//    private ArrayList<Projectile> projectiles;
//    private ArrayList<Projectile> projectilesToRemove;

    // All possible lines we can intersect with
    private ArrayList<Vector2F> collisionPosStart;
    private ArrayList<Vector2F> collisionPosEnd;

    private ArrayList<Medkit> medkits;
    private ArrayList<Medkit> medkitsToRemove;

    private ArrayList<Ammo> ammoClips;
    private ArrayList<Ammo> ammoClipsToRemove;

    public Level(String filenameForLevel, String filenameForTexture, Player p) {
        player = p;
        level = new Bitmap(filenameForLevel).flipY();
        shader = PhongShader.getInstance();
        material = new Material(new Texture(filenameForTexture), new Vector3F(1, 1, 1), 0.5f, 8);
        transformer = new Transform();

        PhongShader.setAmbientLight(new Vector3F(0.1f, 0.1f, 0.1f).mult(3));

        AudioMaster.init();
        AudioMaster.setListenerPos(player.getCamera().getPosition());

        generateLevel();

        player.initAudio();


//        musicBuffer = AudioMaster.loadSound("ripandtear.wav");
//        musicSource = new AudioSource(player.getCamera().getPosition());
//        musicSource.setVolume(0.01f);
//        musicSource.setLooping(true);
//        musicSource.play(musicBuffer);


    }

    public void input() {


        player.input();

    }


    float timer = 0.0f;

    public void update() {
        player.update();

        timer += Time.getDelta();

        float indra = (float) Math.abs(Math.sin(timer));
        float indraDos = (float) Math.abs(Math.cos(timer));


        for (Door d : doors) {
            d.update();
        }

        for (Enemy e : enemies) {
            e.update();
        }

        for (Medkit m : medkits) {
            m.update();
        }

        for (Ammo a : ammoClips) {
            a.update();
        }

//        for (Projectile p: projectiles ) {
//            p.update();
//        }

        for (Medkit rm : medkitsToRemove) {
            medkits.remove(rm);
        }

        for (Ammo a : ammoClipsToRemove) {
            ammoClips.remove(a);
        }

//        for (Projectile p: projectilesToRemove) {
//            projectiles.remove(p);
//        }

    }

    public void render() {

        shader.bind();
        shader.updateUniforms(transformer.getTransformation(), transformer.getProjectedTransformation(), material);
        levelMesh.draw();
        for (Door d : doors) {

            d.render();

        }
        for (Enemy e : enemies) {
            e.render();
        }

        for (Medkit m : medkits) {
            m.render();
        }


        for (Ammo a : ammoClips) {
            a.render();
        }

//        for (Projectile p: projectiles ) {
//            p.render();
//        }

        player.render();
    }


    public void openDoors(Vector3F doorOpenerPos) {

        for (Door d : doors) {

            if (d.getTransform().getTranslation().sub(doorOpenerPos).length() < OPEN_DOOR_DIS) {
                d.openDoor();

            }

        }
    }

    public Vector3F checkCollisions(Vector3F oldPos, Vector3F newPos, float objectWidth, float objectLength, boolean checkWalls, boolean checkDoors, boolean checkEnemies) {

        Vector2F collisionVec = new Vector2F(1, 1); // Change this to 3F if you ever want to add jump
        Vector3F movement = newPos.sub(oldPos);

        if (movement.length() > 0) { // Potential collision check

            Vector2F blockSize = new Vector2F(SQUARE_WIDTH, SQUARE_LENGTH);
            Vector2F objSize = new Vector2F(objectWidth, objectLength);

            Vector2F oldPos2 = new Vector2F(oldPos.getX() * SQUARE_WIDTH, oldPos.getZ() * SQUARE_LENGTH);
            Vector2F newPos2 = new Vector2F(newPos.getX() * SQUARE_WIDTH, newPos.getZ() * SQUARE_LENGTH);


            if (checkWalls) {
                for (int i = 0; i < level.getWidth(); i++) {
                    for (int j = 0; j < level.getHeight(); j++) {

                        if ((level.getPixelAt(i, j) & 0xFFFFFF) == 0) {
                            collisionVec = collisionVec.mult(rectCollide(oldPos2, newPos2, objSize, blockSize, blockSize.mult(new Vector2F(i * SQUARE_WIDTH, j * SQUARE_LENGTH))));
                        }


                    }
                }
            }


            if (checkDoors) {
                for (Door d : doors) {

                    Vector2F currentDoorSize = d.getDoorSize();

                    Vector2F tempDoorVec2 = new Vector2F(d.getTransform().getTranslation().getX() * SQUARE_WIDTH, d.getTransform().getTranslation().getZ() * SQUARE_LENGTH);

                    collisionVec = collisionVec.mult(rectCollide(oldPos2, newPos2, objSize, currentDoorSize, tempDoorVec2));

                }
            }

            if (checkEnemies) {
                for (Enemy e : enemies) {
                    Vector2F currentESize = e.getSize();

                    Vector2F tempEVec2 = new Vector2F(e.getTransform().getTranslation().getX() * SQUARE_WIDTH, e.getTransform().getTranslation().getZ() * SQUARE_LENGTH); // Enemy Pos

                    collisionVec = collisionVec.mult(rectCollide(oldPos2, newPos2, objSize, currentESize, tempEVec2));
                }
            }

        }

        return new Vector3F(collisionVec.getX(), 0, collisionVec.getY());

    }


    private Vector2F rectCollide(Vector2F oldPos, Vector2F newPos, Vector2F size1, Vector2F size2, Vector2F pos2) {
        Vector2F res = new Vector2F(0, 0); // Default we assume it's out of bounds/colliding

        // Left and right edge checking to see if we're in bound
        if (((newPos.getX() + size1.getX() < pos2.getX()) ||
                (newPos.getX() - size1.getX() > pos2.getX() + size2.getX() * size2.getX()) ||
                (oldPos.getY() + size1.getY() < pos2.getY()) ||
                (oldPos.getY() - size1.getY() > pos2.getY() + size2.getY() * size2.getY()))) {

            res.setX(1);

        }


        if ((oldPos.getX() + size1.getX() < pos2.getX() ||
                oldPos.getX() - size1.getX() > pos2.getX() + size2.getX() * size2.getX() ||
                newPos.getY() + size1.getY() < pos2.getY() ||
                newPos.getY() - size1.getY() > pos2.getY() + size2.getY() * size2.getY())) {

            res.setY(1);
        }


        return res;

    }


    private void addFaceToMesh(ArrayList<Integer> indices, int startLoc, boolean direction) {

        if (direction) {

            indices.add(startLoc + 2);
            indices.add(startLoc + 1);
            indices.add(startLoc);
            indices.add(startLoc + 3);
            indices.add(startLoc + 2);
            indices.add(startLoc);
        } else {
            indices.add(startLoc);
            indices.add(startLoc + 1);
            indices.add(startLoc + 2);
            indices.add(startLoc);
            indices.add(startLoc + 2);
            indices.add(startLoc + 3);
        }
    }

    private float[] calcTexCoords(int value) {

        int texX = value / NUM_TEXTURES;
        int texY = texX % NUM_TEX_EXP;

        texX /= NUM_TEX_EXP;

        float[] result = new float[4];

        result[0] = 1f - (float) texX / (float) NUM_TEX_EXP; // XHigher
        result[1] = result[0] - 1f / (float) NUM_TEX_EXP; // Xlower
        result[3] = 1f - (float) texY / (float) NUM_TEX_EXP; // Ylower
        result[2] = result[3] - 1f / (float) NUM_TEX_EXP; // YHigher


        return result;

    }

    private void addVertices(ArrayList<Vertex> vertices, int i, int j, boolean x, boolean y, boolean z, float offset, float[] texCoords) {

        if (x && z) { // Floor
            vertices.add(new Vertex(new Vector3F(i * SQUARE_WIDTH, offset * SQUARE_HEIGHT, j * SQUARE_LENGTH), new Vector2F(texCoords[1], texCoords[3])));
            vertices.add(new Vertex(new Vector3F((i + 1) * SQUARE_WIDTH, offset * SQUARE_HEIGHT, j * SQUARE_LENGTH), new Vector2F(texCoords[0], texCoords[3])));
            vertices.add(new Vertex(new Vector3F((i + 1) * SQUARE_WIDTH, offset * SQUARE_HEIGHT, (j + 1) * SQUARE_LENGTH), new Vector2F(texCoords[0], texCoords[2])));
            vertices.add(new Vertex(new Vector3F(i * SQUARE_WIDTH, offset * SQUARE_HEIGHT, (j + 1) * SQUARE_LENGTH), new Vector2F(texCoords[1], texCoords[2])));
        } else if (x && y) { // Ceiling
            vertices.add(new Vertex(new Vector3F(i * SQUARE_WIDTH, j * SQUARE_HEIGHT, offset * SQUARE_LENGTH), new Vector2F(texCoords[1], texCoords[3])));
            vertices.add(new Vertex(new Vector3F((i + 1) * SQUARE_WIDTH, j * SQUARE_HEIGHT, offset * SQUARE_LENGTH), new Vector2F(texCoords[0], texCoords[3])));
            vertices.add(new Vertex(new Vector3F((i + 1) * SQUARE_WIDTH, (j + 1) * SQUARE_HEIGHT, offset * SQUARE_LENGTH), new Vector2F(texCoords[0], texCoords[2])));
            vertices.add(new Vertex(new Vector3F(i * SQUARE_WIDTH, (j + 1) * SQUARE_HEIGHT, offset * SQUARE_LENGTH), new Vector2F(texCoords[1], texCoords[2])));

        } else if (y && z) { // Wall
            vertices.add(new Vertex(new Vector3F(offset * SQUARE_WIDTH, i * SQUARE_HEIGHT, j * SQUARE_LENGTH), new Vector2F(texCoords[1], texCoords[3])));
            vertices.add(new Vertex(new Vector3F(offset * SQUARE_WIDTH, i * SQUARE_HEIGHT, (j + 1) * SQUARE_LENGTH), new Vector2F(texCoords[0], texCoords[3])));
            vertices.add(new Vertex(new Vector3F(offset * SQUARE_WIDTH, (i + 1) * SQUARE_HEIGHT, (j + 1) * SQUARE_LENGTH), new Vector2F(texCoords[0], texCoords[2])));
            vertices.add(new Vertex(new Vector3F(offset * SQUARE_WIDTH, (i + 1) * SQUARE_HEIGHT, j * SQUARE_LENGTH), new Vector2F(texCoords[1], texCoords[2])));
        } else {
            System.err.println("Invalid plane used in level generator");
            new Exception().printStackTrace();
            System.exit(1);
        }


    }

    private void addDoor(int iVal, int jVal, float[] tcoords) {

        Transform doorTransform = new Transform();


        // If up and down are nothing (0xFFFFFF removes alpha)
        boolean xDoor = ((level.getPixelAt(iVal, jVal - 1) & 0xFFFFFF) == 0) && ((level.getPixelAt(iVal, jVal + 1) & 0xFFFFFF) == 0);

        boolean yDoor = ((level.getPixelAt(iVal - 1, jVal) & 0xFFFFFF) == 0) && ((level.getPixelAt(iVal + 1, jVal) & 0xFFFFFF) == 0);

        // XOR used here because we can't have a door in the middle of room
        if (xDoor == yDoor) {

            System.err.println("Door level design issue at " + iVal + " " + jVal);
            new Exception().printStackTrace();
            System.exit(1);

        }

        Vector3F openPos = null;

        if (yDoor) {
            doorTransform.setTranslation(new Vector3F(iVal * SQUARE_WIDTH, 0, (jVal + (SQUARE_LENGTH / 2)) * SQUARE_LENGTH));
            openPos = doorTransform.getTranslation().sub(new Vector3F(DOOR_OPEN_MOVEMENT_AMT * SQUARE_WIDTH, 0, 0));
        }

        if (xDoor) {
            doorTransform.setTranslation(new Vector3F((iVal + (SQUARE_WIDTH / 2)) * SQUARE_WIDTH, 0, jVal * SQUARE_LENGTH));
            doorTransform.setRotation(0, 90, 0);
            openPos = doorTransform.getTranslation().sub(new Vector3F(0, 0, DOOR_OPEN_MOVEMENT_AMT * SQUARE_LENGTH));
        }

        // openPos = doorTransform.getTranslation().sub(new Vector3F(0,  -DOOR_OPEN_MOVEMENT_AMT * SQUARE_HEIGHT, 0));
        doors.add(new Door(doorTransform, openPos, tcoords));


    }

    private void addSoldier(int iVal, int jVal) {

        Transform temp = new Transform();

        temp.setTranslation(iVal * SQUARE_WIDTH, 0, jVal * SQUARE_LENGTH);

        enemies.add(new Soldier(temp));


    }

    private void addHellKnight(int iVal, int jVal) {

        Transform temp = new Transform();

        temp.setTranslation(iVal * SQUARE_WIDTH, 0, jVal * SQUARE_LENGTH);

        enemies.add(new HellKnight(temp));


    }


    private void addSpecialItem(int blueLevel, int iVal, int jVal, float[] tcoords) {


        if ((blueLevel % 16) == 0) {
            addDoor(iVal, jVal, tcoords);
        } else if ((blueLevel % 15) == 0) {
            addSoldier(iVal, jVal);
        } else if (blueLevel == 129) {
            player = new Player(new Vector3F((iVal + 0.5f) * SQUARE_WIDTH, 0.4375f, (jVal + 0.5f) * SQUARE_LENGTH));
        } else if (blueLevel % 14 == 0) {
            addHellKnight(iVal, jVal);
        }

    }

    private void generateLevel() {

        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        ArrayList<Integer> indices = new ArrayList<Integer>();
        doors = new ArrayList<>();
        medkits = new ArrayList<>();
        enemies = new ArrayList<>();
        collisionPosStart = new ArrayList<>();
        collisionPosEnd = new ArrayList<>();
        medkitsToRemove = new ArrayList<>();
//        projectiles = new ArrayList<>();
//        projectilesToRemove = new ArrayList<>();

        medkits.add(new Medkit(new Vector3F(7, 0, 7)));

        ammoClips = new ArrayList<>();

        ammoClips.add(new Ammo(new Vector3F(12, 0, 12)));

        ammoClipsToRemove = new ArrayList<>();


        for (int i = 0; i < level.getWidth(); i++) {
            for (int j = 0; j < level.getHeight(); j++) {
                if ((level.getPixelAt(i, j) & 0xFFFFFF) == 0) { // Bitwise operation to check if it's a wall or not
                    continue;
                }

                // Pulling out the green part of current pixel and shifting it a full byte.
                // Texture Coords for every square


                float[] texCoords = calcTexCoords((level.getPixelAt(i, j) & 0x00FF00) >> 8);


                // Generate Floor:

                // Clockwise order

                addFaceToMesh(indices, vertices.size(), true);

                addVertices(vertices, i, j, true, false, true, 0, texCoords);

                // Texture Coords for every square
                // Using blue part for ceiling
                texCoords = calcTexCoords((level.getPixelAt(i, j) & 0xFF));

                // Generate Ceiling:

                addSpecialItem((level.getPixelAt(i, j) & 0xFF), i, j, texCoords);

                addFaceToMesh(indices, vertices.size(), false);

                addVertices(vertices, i, j, true, false, true, 1, texCoords);

                //Generate Walls

                // Pulling out the red part of current pixel and shifting it a full 2 bytes to get integer value of R

                // Texture Coords for every square

                texCoords = calcTexCoords((level.getPixelAt(i, j) & 0xFF0000) >> 16);

                if ((level.getPixelAt(i, j - 1) & 0xFFFFFF) == 0) {

                    collisionPosStart.add(new Vector2F(i * SQUARE_WIDTH, j * SQUARE_LENGTH));

                    collisionPosEnd.add(new Vector2F((i + 1) * SQUARE_WIDTH, j * SQUARE_LENGTH));

                    addFaceToMesh(indices, vertices.size(), false);

                    addVertices(vertices, i, 0, true, true, false, j, texCoords);
                }
                if ((level.getPixelAt(i, j + 1) & 0xFFFFFF) == 0) {

                    collisionPosStart.add(new Vector2F(i * SQUARE_WIDTH, (j + 1) * SQUARE_LENGTH));

                    collisionPosEnd.add(new Vector2F((i + 1) * SQUARE_WIDTH, (j + 1) * SQUARE_LENGTH));

                    addFaceToMesh(indices, vertices.size(), true);

                    addVertices(vertices, i, 0, true, true, false, (j + 1), texCoords);
                }
                if ((level.getPixelAt(i - 1, j) & 0xFFFFFF) == 0) {

                    collisionPosStart.add(new Vector2F(i * SQUARE_WIDTH, j * SQUARE_LENGTH));

                    collisionPosEnd.add(new Vector2F(i * SQUARE_WIDTH, (j + 1) * SQUARE_LENGTH));


                    addFaceToMesh(indices, vertices.size(), true);

                    addVertices(vertices, 0, j, false, true, true, i, texCoords);
                }
                if ((level.getPixelAt(i + 1, j) & 0xFFFFFF) == 0) {

                    collisionPosStart.add(new Vector2F((i + 1) * SQUARE_WIDTH, j * SQUARE_LENGTH));

                    collisionPosEnd.add(new Vector2F((i + 1) * SQUARE_WIDTH, (j + 1) * SQUARE_LENGTH));


                    addFaceToMesh(indices, vertices.size(), false);

                    addVertices(vertices, 0, j, false, true, true, (i + 1), texCoords);
                }
            }
        }

        Vertex[] vertices1 = new Vertex[vertices.size()];
        Integer[] indices1 = new Integer[indices.size()];

        vertices.toArray(vertices1);
        indices.toArray(indices1);

        levelMesh = new MyMesh(vertices1, Util.integerToIntArr(indices1), true);

        Transform temp = new Transform();

        temp.setTranslation(player.getCamera().getPosition().sub(new Vector3F(10, 0, 10)));

        enemies.add(new LostSoul(temp));

    }

    public Shader getShader() {

        return shader;
    }

    private float crossProd2F(Vector2F a, Vector2F b) {

        return a.getX() * b.getY() - a.getY() * b.getX();

    }

    // Checks if an intersection is happening between two lines
    private Vector2F lineIntersectCheck(Vector2F line1Start, Vector2F line1End, Vector2F line2Start, Vector2F line2End) {

        Vector2F line1 = line1End.sub(line1Start);

        Vector2F line2 = line2End.sub(line2Start);

        float cross = crossProd2F(line1, line2);

        // linestart1 + line1 * a == linestart2 + line2 * b

        if (cross == 0) { // Parallel
            return null;
        }

        Vector2F distanceBetweenStarts = line2Start.sub(line1Start);

        float a = crossProd2F(distanceBetweenStarts, line2) / cross;
        float b = crossProd2F(distanceBetweenStarts, line1) / cross;


        if (0 < a && a < 1 && 0 < b && b < 1) {
            return line1Start.add(line1.mult(a));
        }

        return null;

    }

    public Vector2F checkIntersections(Vector2F lineStart, Vector2F lineEnd, boolean wantToHurtEnemies, boolean dmgDropOff) {

        Vector2F nearestIntersection = null;

        for (int i = 0; i < collisionPosStart.size(); i++) {

            Vector2F intersectionVec = lineIntersectCheck(lineStart, lineEnd, collisionPosStart.get(i), collisionPosEnd.get(i));
            nearestIntersection = findNearestVec2(intersectionVec, nearestIntersection, lineStart);

        }


        for (Door d : doors) {

            Vector2F doorpos = new Vector2F(d.getTransform().getTranslation().getX(), d.getTransform().getTranslation().getZ());

            Vector2F collVec = lineIntersectRect(lineStart, lineEnd, doorpos, d.getDoorSize());

            nearestIntersection = findNearestVec2(nearestIntersection, collVec, lineStart);


        }


        if (wantToHurtEnemies) {

            Vector2F nearestMonsterIntersection = null;
            Enemy nearestEnemy = null;

//            projectiles.add(new Projectile(new Vector3F(lineStart.getX(), player.getCamera().getPosition().getY(), lineStart.getY()), new Vector3F(lineEnd.getX(), player.getCamera().getPosition().getY(), lineEnd.getY())));

            for (Enemy e : enemies) {

                Vector2F enemyPos = new Vector2F(e.getTransform().getTranslation().getX(), e.getTransform().getTranslation().getZ());

                Vector2F collVec = lineIntersectRect(lineStart, lineEnd, enemyPos, e.getSize());

                nearestMonsterIntersection = findNearestVec2(nearestMonsterIntersection, collVec, lineStart);

                if (nearestMonsterIntersection == collVec) {
                    nearestEnemy = e;
                }

            }


            if (nearestMonsterIntersection != null && (nearestIntersection == null ||
                    nearestMonsterIntersection.sub(lineStart).length() < nearestIntersection.sub(lineStart).length())) {
                int cmdg = player.getDmgAmt();

                if (dmgDropOff) {
                    cmdg *= 1 / nearestMonsterIntersection.sub(lineStart).length();
                }

                System.out.println("enemy hit for " + cmdg);

                nearestEnemy.damageEnemy(cmdg);
            }

        }


        return nearestIntersection;

    }

    private Vector2F findNearestVec2(Vector2F a, Vector2F b, Vector2F relativePos) {
        if (b != null && (a == null ||
                a.sub(relativePos).length() > b.sub(relativePos).length()))
            return b;

        return a;
    }

    // Checks if we intersect with a specific rectangle.
    public Vector2F lineIntersectRect(Vector2F lineStart, Vector2F lineEnd, Vector2F rectPos, Vector2F rectSize) {

        Vector2F result = null;

        Vector2F collisionVector = lineIntersectCheck(lineStart, lineEnd, rectPos, new Vector2F(rectPos.getX() + rectSize.getX(), rectPos.getY()));
        result = findNearestVec2(result, collisionVector, lineStart);

        collisionVector = lineIntersectCheck(lineStart, lineEnd, rectPos, new Vector2F(rectPos.getX(), rectPos.getY() + rectSize.getY()));
        result = findNearestVec2(result, collisionVector, lineStart);

        collisionVector = lineIntersectCheck(lineStart, lineEnd, new Vector2F(rectPos.getX(), rectPos.getY() + rectSize.getY()), rectPos.add(rectSize));
        result = findNearestVec2(result, collisionVector, lineStart);

        collisionVector = lineIntersectCheck(lineStart, lineEnd, new Vector2F(rectPos.getX() + rectSize.getX(), rectPos.getY()), rectPos.add(rectSize));
        result = findNearestVec2(result, collisionVector, lineStart);

        return result;


    }

    public void damagePlayer(int dmgAmt) {

        player.damagePlayer(dmgAmt);

    }

    public Player getPlayer() {
        return player;
    }


    public void removeGameItem(GameItem item) {

        if (item instanceof Medkit) {
            medkitsToRemove.add((Medkit) item);
        } else if (item instanceof Ammo) {
            ammoClipsToRemove.add((Ammo) item);
        }

//        else if (item instanceof Projectile){
//            projectilesToRemove.add((Projectile) item);
//        }


    }


    public void addGameItem(GameItem item) {

        if (item instanceof Medkit) {
            medkits.add((Medkit) item);
        } else if (item instanceof Ammo) {
            ammoClips.add((Ammo) item);
        }

//        else if (item instanceof Projectile){
//            projectiles.add((Projectile) item);
//        }
    }


    public void givePlayerAmmo(int ammo) {

        player.addAmmo(ammo);

    }


}
