// Class that represents a Camera object

public class Camera {

    public static final Vector3F yAxis = new Vector3F(0, 1, 0);

    private Vector3F position;

    // Orientation details: (Note that forward and up will always need to be perpendicular to each other so cross
    // product will be very handy here)
    private Vector3F forward;
    private Vector3F up;

    public Camera() { // "Default" Camera, like the initial view.
        this(new Vector3F(0, 0, 0), new Vector3F(0, 0, 1), new Vector3F(0, 1, 0));
    }

    public Camera(Vector3F position, Vector3F forward, Vector3F up) {
        this.position = position;
        this.forward = forward.normalized();
        this.up = up.normalized();


//        up = up.normalized();
//        forward = forward.normalized();
    }

    public void move(Vector3F directionToMoveIn, float amount) {

        position = position.add(directionToMoveIn.mult(amount));

    }


    public void userMove() {

    }

    public Vector3F getLeft() { // Cross product of up and forward gives the left vector by definition.

        return forward.crossProd(up).normalized();

    }


    public Vector3F getRight() { // This is essentially multiplying the result of getLeft() by -1

        return up.crossProd(forward).normalized();

    }


    public void rotateX(float angle) { // Rotating Camera around x-axis
        Vector3F xAxis = yAxis.crossProd(forward).normalized();

        forward = forward.rotate(angle, xAxis).normalized();

        up = forward.crossProd(xAxis).normalized();
    }

    public void rotateY(float angle) {

        Vector3F xAxis = yAxis.crossProd(forward).normalized();


        forward = forward.rotate(angle, yAxis).normalized();

        up = forward.crossProd(xAxis).normalized();

    }


    public Vector3F getPosition() {
        return position;
    }

    public void setPosition(Vector3F position) {
        this.position = position;
    }

    public Vector3F getForward() {
        return forward;
    }

    public void setForward(Vector3F forward) {
        this.forward = forward;
    }

    public Vector3F getUp() {
        return up;
    }

    public void setUp(Vector3F up) {
        this.up = up;
    }
}
