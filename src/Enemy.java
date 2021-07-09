import java.util.ArrayList;
import java.util.Random;

public abstract class Enemy {

    protected Transform transform;
    protected Material enemyMat;
    protected ArrayList<Texture> animations;

    protected static final float WIDTH = 0.2f;
    protected static final float LENGTH = 0.2f;

    protected static final float TEX_MAX_X = 0;
    protected static final float TEX_MIN_X = -1;

    protected static final float TEX_MAX_Y = 0;
    protected static final float TEX_MIN_Y = -1;

    protected int currentState;

    protected int health;

    public static final int IDLE = 0;
    public static final int CHASING = 1;
    public static final int ATTACKING = 2;
    public static final int DYING = 3;
    public static final int DED = 4;

    protected Random rand;


    public Enemy(Transform transformer){
        this.transform = transformer;

        this.rand = new Random();

        currentState = IDLE;
    }

    public abstract void update();

    public abstract void render();

    public Transform getTransform(){
        return transform;
    }

    public Vector2F getSize(){

        return new Vector2F(WIDTH, LENGTH);

    }

    public abstract void damageEnemy(int dmgAmt);

    public int getState(){
        return currentState;
    }

}