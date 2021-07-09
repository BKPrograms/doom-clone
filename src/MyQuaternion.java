// Quaternions are essentially encodings for R^3 rotations

public class MyQuaternion {
    private float x;
    private float y;
    private float z;
    private float w;


    public MyQuaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float length() {
        return (float) Math.sqrt((x * x) + (y * y) + (z * z) + (w * w));
    }

    public MyQuaternion normalize() {

        float len = this.length();


        x /= len;
        y /= len;
        z /= len;
        w /= len;


        return this;


    }


    public MyQuaternion conjugate() {

        return new MyQuaternion(-this.x, -this.y, -this.z, this.w);

    }

    public MyQuaternion mul(MyQuaternion other) {
        float new_x = x * other.getW() + w * other.getX() + y * other.getZ() - z * other.getY();
        float new_y = y * other.getW() + w * other.getY() + z * other.getX() - x * other.getZ();
        float new_z = z * other.getW() + w * other.getZ() + x * other.getY() - y * other.getX();
        float new_w = w * other.getW() - x * other.getX() - y * other.getY() - z * other.getZ();


        return new MyQuaternion(new_x, new_y, new_z, new_w);
    }


    public MyQuaternion mul(Vector3F other) {

        float w_ = -x * other.getX() - y * other.getY() - z * other.getZ();
        float x_ =  w * other.getX() + y * other.getZ() - z * other.getY();
        float y_ =  w * other.getY() + z * other.getX() - x * other.getZ();
        float z_ =  w * other.getZ() + x * other.getY() - y * other.getX();

        return new MyQuaternion(x_, y_, z_, w_);
//        float new_w = -x * other.getX() - y * other.getY() - z * other.getZ();
//        float new_x = w * other.getX() + y * other.getZ() - z * other.getY();
//        float new_y = w * other.getY() + z * other.getX() - x * other.getZ();
//        float new_z = w * other.getZ() + x * other.getY() - y * other.getX();
//
//        return new MyQuaternion(new_x, new_y, new_z, new_w);
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }
}
