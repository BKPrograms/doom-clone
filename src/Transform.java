public class Transform {

    private static Camera camera;



    // These act as "bounds" in a sense:
    private static float zNear;
    private static float zFar;
    private static float width;
    private static float height;
    private static float fov;

    private Vector3F translation;
    private Vector3F rotation;
    private Vector3F scaling;

    private Vector3F shearFactor;



    public Transform(){

        translation = new Vector3F(0, 0, 0);
        rotation = new Vector3F(0, 0, 0);
        scaling = new Vector3F(1, 1, 1); // Default scalars

    }



    public Matrix4F getTransformation(){
        Matrix4F translationMatrix = new Matrix4F().makeTranslation(this.translation.getX(), this.translation.getY(), this.translation.getZ());
        Matrix4F rotationMatrix = new Matrix4F().makeRotation(this.rotation.getX(), this.rotation.getY(), this.rotation.getZ());
        Matrix4F scalingMatrix = new Matrix4F().makeScaling(this.scaling.getX(), this.scaling.getY(), this.scaling.getZ());

        return translationMatrix.mul(rotationMatrix.mul(scalingMatrix));
    }

    public Matrix4F getProjectedTransformation(){

        Matrix4F transformation = getTransformation();
        Matrix4F projection = new Matrix4F().makeProjection(fov, width, height, zNear, zFar);

        // Deals with rotating the current screen to make it fit with the current camera details
        Matrix4F cameraRotationMatrix = new Matrix4F().makeCameraMatrix(camera.getForward(), camera.getUp());
        // These are negative because we want the world to move in the opposite direction of the camera's movement.
        Matrix4F cameraTranslation = new Matrix4F().makeTranslation(-camera.getPosition().getX(), -camera.getPosition().getY(), -camera.getPosition().getZ());

        // Matrix4F shearMatrix = new Matrix4F().makeShearMatrix(new Vector3F(10, 0, 0));

        return projection.mul(cameraRotationMatrix.mul(cameraTranslation.mul(transformation)));
    }

    public static void setProjection(float fov, float width, float height, float zNear, float zFar){
        Transform.fov = fov;
        Transform.width = width;
        Transform.height = height;
        Transform.zNear = zNear;
        Transform.zFar = zFar;
    }

    public Vector3F getTranslation() {
        return translation;
    }

    public void setTranslation(Vector3F translation) {
        this.translation = translation;
    }

    public void setTranslation(float x, float y, float z) {
        this.translation = new Vector3F(x, y, z);
    }

    public Vector3F getRotation() {
        return rotation;
    }

    public void setRotation(Vector3F rotation) {
        this.rotation = rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation = new Vector3F(x, y, z);
    }

    public Vector3F getScaling() {
        return scaling;
    }

    public void setScaling(Vector3F scaling) {
        this.scaling = scaling;
    }

    public void setScaling(float x, float y, float z) {
        this.scaling = new Vector3F(x, y, z);
    }

    public Vector3F getShearFactor() { return shearFactor;
    }

    public void setShearFactor(float x, float y, float z) {
        this.shearFactor = new Vector3F(x, y, z);
    }

    public static Camera getCamera() {
        return camera;
    }

    public static void setCamera(Camera camera) {
        Transform.camera = camera;
    }
}
