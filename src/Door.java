
public class Door {

    public static final float DOOR_LENGTH = 1;
    public static final float DOOR_WIDTH = 0.125f;
    public static final float DOOR_HEIGHT = 1;
    private static final float DOOR_START = 0;

    private static MyMesh doorMesh; // All doors use the same mesh which is why static
    private Transform transform;
    private Material doorMat;

    private boolean isOpening;
    private double openingStartTime; // Time when door starts opening
    private double openTime; // Time when door finish's opening
    private double closingStartTime; // Time when door starts closing
    private double closeTime; // Time when door finish's closing
    public static final double TIME_TO_OPEN = 2.0;
    public static final double CLOSE_DELAY = 0.8;

    private Vector3F openPos;
    private Vector3F closePos;


    private int soundBuff;
    private AudioSource soundSource;

    public Door(Transform transformer, Vector3F openPosition, float[] texCoords) {

        transform = transformer;
        doorMat = new Material(new Texture("doors.png"), new Vector3F(1,1,1), 4, 16);
        isOpening = false;
        closePos = transform.getTranslation().mult(1); // Copy of vector
        openPos = openPosition;

        soundBuff = AudioMaster.loadSound("door.wav");
        soundSource = new AudioSource(openPos);

        if (doorMesh == null){


            Vertex[] vertices =  new Vertex[]{

                    new Vertex(new Vector3F(DOOR_START,DOOR_START,DOOR_START), new Vector2F(texCoords[1], texCoords[3])),
                    new Vertex(new Vector3F(DOOR_START,DOOR_HEIGHT,DOOR_START),  new Vector2F(texCoords[1], texCoords[2])),
                    new Vertex(new Vector3F(DOOR_LENGTH,DOOR_HEIGHT,DOOR_START), new Vector2F(texCoords[0], texCoords[2])),
                    new Vertex(new Vector3F(DOOR_LENGTH,DOOR_START,DOOR_START),  new Vector2F(texCoords[0], texCoords[3])),

                    new Vertex(new Vector3F(DOOR_START,DOOR_START,DOOR_START), new Vector2F(texCoords[1] - 0.02f, texCoords[3])),
                    new Vertex(new Vector3F(DOOR_START,DOOR_HEIGHT,DOOR_START), new Vector2F(texCoords[0] - 0.02f, texCoords[3])),
                    new Vertex(new Vector3F(DOOR_START,DOOR_HEIGHT,DOOR_WIDTH), new Vector2F(texCoords[0], texCoords[2])),
                    new Vertex(new Vector3F(DOOR_START,DOOR_START,DOOR_WIDTH), new Vector2F(texCoords[1], texCoords[2])),

                    new Vertex(new Vector3F(DOOR_START,DOOR_START,DOOR_WIDTH),new Vector2F(texCoords[0], texCoords[3])),
                    new Vertex(new Vector3F(DOOR_START,DOOR_HEIGHT,DOOR_WIDTH), new Vector2F(texCoords[0], texCoords[2])),
                    new Vertex(new Vector3F(DOOR_LENGTH,DOOR_HEIGHT,DOOR_WIDTH),new Vector2F(texCoords[1], texCoords[2])),
                    new Vertex(new Vector3F(DOOR_LENGTH,DOOR_START,DOOR_WIDTH), new Vector2F(texCoords[1], texCoords[3])),

                    new Vertex(new Vector3F(DOOR_LENGTH,DOOR_START,DOOR_START), new Vector2F(texCoords[1]- 0.02f, texCoords[3])),
                    new Vertex(new Vector3F(DOOR_LENGTH,DOOR_HEIGHT,DOOR_START), new Vector2F(texCoords[0] - 0.02f, texCoords[3])),
                    new Vertex(new Vector3F(DOOR_LENGTH,DOOR_HEIGHT,DOOR_WIDTH), new Vector2F(texCoords[0], texCoords[2])),
                    new Vertex(new Vector3F(DOOR_LENGTH,DOOR_START,DOOR_WIDTH), new Vector2F(texCoords[1], texCoords[2]))};

            int[] indices =  new int[]{0,1,2,
                    0,2,3,

                    6,5,4,
                    7,6,4,

                    10,9,8,
                    11,10,8,

                    12,13,14,
                    12,14,15};

            doorMesh = new MyMesh(vertices, indices, true);
        }

    }

    private Vector3F VectorLerp(Vector3F startPos, Vector3F endPos, float lerpFactor){ // Interpolation

        return startPos.add(endPos.sub(startPos).mult(lerpFactor));

    }

    public void openDoor(){
        if (isOpening){
            return;
        }

        soundSource.play(soundBuff);

        openingStartTime = (double) Time.getTime()/ (double) Time.SECOND;

        openTime = openingStartTime + TIME_TO_OPEN;

        closingStartTime = openTime + CLOSE_DELAY;

        closeTime = closingStartTime + TIME_TO_OPEN;

        isOpening = true;
    }

    public void update(){

        if (isOpening){

            double time = (double) Time.getTime()/ (double) Time.SECOND;

            if (time < openTime){

                float lerpFactor = (float) ((time - openingStartTime) /  TIME_TO_OPEN);
                getTransform().setTranslation(VectorLerp(closePos, openPos, lerpFactor));

            } else if (time < closingStartTime){
                getTransform().setTranslation(openPos);
            } else if (time < closeTime){
                float lerpFactor = (float) ((time - closingStartTime) /  TIME_TO_OPEN);
                getTransform().setTranslation(VectorLerp(openPos, closePos, lerpFactor));
            } else {
                getTransform().setTranslation(closePos);
                isOpening = false;
            }

        }

    }


    public void render(){

        Shader shader = Game.getLevel().getShader();
        shader.bind();
        shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), doorMat);
        doorMesh.draw();
    }

    public Transform getTransform() {
        return transform;
    }

    public Vector2F getDoorSize(){

        if (getTransform().getRotation().getY() == 90){
            return new Vector2F(DOOR_WIDTH, DOOR_LENGTH);
        } else {
            return new Vector2F(DOOR_LENGTH, DOOR_WIDTH);
        }


    }

    public AudioSource getSoundSource() {
        return soundSource;
    }
}
