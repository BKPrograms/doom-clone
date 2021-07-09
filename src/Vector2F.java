import org.lwjgl.util.vector.Vector2f;

import java.util.Objects;

public class Vector2F {

    private float x;
    private float y;

    public Vector2F(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float length() {

        return (float) Math.sqrt((x * x) + (y * y));

    }

    public float dotProd(Vector2F other) {

        return (this.x * other.getX()) + (this.y * other.getY());

    }

    public Vector2F normalize() {
        double len = this.length();

        x /= len;
        y /= len;

        return this;
    }

    public Vector2F rotate(float angle) {
        double rad = Math.toRadians(angle);

        double cos = Math.cos(rad);

        double sin = Math.sin(rad);

        return new Vector2F((float) (x * cos - y * sin), (float) (x * sin + y * cos));
    }

    public Vector2F add(Vector2F other) {

        return new Vector2F(this.x + other.getX(), this.y + other.getY());

    }

    public Vector2F add(float other) {

        return new Vector2F(this.x + other, this.y + other);
    }


    public Vector2F sub(Vector2F other) {

        return new Vector2F(this.x - other.getX(), this.y - other.getY());

    }

    public Vector2F sub(float other) {

        return new Vector2F(this.x - other, this.y - other);
    }

    public Vector2F mult(Vector2F other) {

        return new Vector2F(this.x * other.getX(), this.y * other.getY());

    }

    public Vector2F mult(float other) {

        return new Vector2F(this.x * other, this.y * other);
    }

    public Vector2F div(Vector2F other) {

        return new Vector2F(this.x / other.getX(), this.y / other.getY());

    }

    public Vector2F div(float other) {

        return new Vector2F(this.x / other, this.y / other);
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

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2F vector2F = (Vector2F) o;
        return this.x == vector2F.x && this.y == vector2F.y;
    }

}
