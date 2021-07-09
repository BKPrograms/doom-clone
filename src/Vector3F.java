import java.util.Objects;

public class Vector3F {
    private float x;
    private float y;
    private float z;


    public Vector3F(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float length() {

        return (float) Math.sqrt((x * x) + (y * y) + (z * z));

    }

    public float dotProd(Vector3F other) {

        return (this.x * other.getX()) + (this.y * other.getY()) + (this.z * other.getZ());

    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public Vector3F crossProd(Vector3F other){

        float new_x = y * other.getZ() - z * other.getY();
        float new_y = z * other.getX() - x * other.getZ();
        float new_z = x * other.getY() - y * other.getX();

        return new Vector3F(new_x, new_y, new_z);

    }

    public Vector3F normalized() {
        float len = this.length();

        return new Vector3F(x /len, y/len, z/len); // this;
    }


    public Vector3F rotate(float angle, Vector3F axisToRotateAround) {
        float sinHalfAngle = (float)Math.sin(Math.toRadians(angle / 2));
        float cosHalfAngle = (float)Math.cos(Math.toRadians(angle / 2));

        float rX = axisToRotateAround.getX() * sinHalfAngle;
        float rY = axisToRotateAround.getY() * sinHalfAngle;
        float rZ = axisToRotateAround.getZ() * sinHalfAngle;
        float rW = cosHalfAngle;

        MyQuaternion rotation = new MyQuaternion(rX, rY, rZ, rW);
        MyQuaternion conjugate = rotation.conjugate();

        MyQuaternion w = rotation.mul(this).mul(conjugate);

        return new Vector3F(w.getX(), w.getY(), w.getZ());
    }


    public Vector3F add(Vector3F other) {

        return new Vector3F(this.x + other.getX(), this.y + other.getY(), this.z + other.getZ());

    }

    public Vector3F add(float other) {

        return new Vector3F(this.x + other, this.y + other, this.z + other);
    }


    public Vector3F sub(Vector3F other) {

        return new Vector3F(this.x - other.getX(), this.y - other.getY(), this.z - other.getZ());

    }

    public Vector3F sub(float other) {

        return new Vector3F(this.x - other, this.y - other, this.z - other);
    }

    public Vector3F mult(Vector3F other) {

        return new Vector3F(this.x * other.getX(), this.y * other.getY(), this.z * other.getZ());

    }

    public Vector3F mult(float other) {

        return new Vector3F(this.x * other, this.y * other, this.z * other);
    }

    public Vector3F div(Vector3F other) {

        return new Vector3F(this.x / other.getX(), this.y / other.getY(), this.z / other.getZ());

    }

    public Vector3F div(float other) {

        return new Vector3F(this.x / other, this.y / other, this.z / other);
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

    public Vector3F abs(){ return new Vector3F(Math.abs(x), Math.abs(y), Math.abs(z)); };


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3F vector3F = (Vector3F) o;
        return Float.compare(vector3F.x, x) == 0 && Float.compare(vector3F.y, y) == 0 && Float.compare(vector3F.z, z) == 0;
    }


    public Vector2F getXY() {

        return new Vector2F(x, y);

    }

    public Vector2F getYX() {

        return new Vector2F(y, x);

    }

    public Vector2F getXZ() {

        return new Vector2F(x, z);

    }

    public Vector2F getZX() {

        return new Vector2F(z, x);

    }

    public Vector2F getYZ() {

        return new Vector2F(y, z);

    }


    public Vector2F getZY() {

        return new Vector2F(z, y);

    }

}
