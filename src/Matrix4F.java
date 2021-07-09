public class Matrix4F {

    private float[][] m;

    public Matrix4F() {
        this.m = new float[4][4];
    }

    public Matrix4F makeIdentity() {
        for (int i = 0; i < 4; i++) {
            this.m[i][i] = 1;
        }

        return this;
    }


    public Matrix4F makeTranslation(float x, float y, float z) {

        this.makeIdentity();

        this.m[0][3] = x;
        this.m[1][3] = y;
        this.m[2][3] = z;

        return this;
    }


    public Matrix4F makeRotation(float x, float y, float z) {
        Matrix4F rx = new Matrix4F();
        Matrix4F ry = new Matrix4F();
        Matrix4F rz = new Matrix4F();

        x = (float) Math.toRadians(x);

        y = (float) Math.toRadians(y);

        z = (float) Math.toRadians(z);



        rz.m[0][0] = (float)Math.cos(z);rz.m[0][1] = -(float)Math.sin(z);rz.m[0][2] = 0;				rz.m[0][3] = 0;
        rz.m[1][0] = (float)Math.sin(z);rz.m[1][1] = (float)Math.cos(z);rz.m[1][2] = 0;					rz.m[1][3] = 0;
        rz.m[2][0] = 0;					rz.m[2][1] = 0;					rz.m[2][2] = 1;					rz.m[2][3] = 0;
        rz.m[3][0] = 0;					rz.m[3][1] = 0;					rz.m[3][2] = 0;					rz.m[3][3] = 1;

        rx.m[0][0] = 1;					rx.m[0][1] = 0;					rx.m[0][2] = 0;					rx.m[0][3] = 0;
        rx.m[1][0] = 0;					rx.m[1][1] = (float)Math.cos(x);rx.m[1][2] = -(float)Math.sin(x);rx.m[1][3] = 0;
        rx.m[2][0] = 0;					rx.m[2][1] = (float)Math.sin(x);rx.m[2][2] = (float)Math.cos(x);rx.m[2][3] = 0;
        rx.m[3][0] = 0;					rx.m[3][1] = 0;					rx.m[3][2] = 0;					rx.m[3][3] = 1;

        ry.m[0][0] = (float)Math.cos(y);ry.m[0][1] = 0;					ry.m[0][2] = -(float)Math.sin(y);ry.m[0][3] = 0;
        ry.m[1][0] = 0;					ry.m[1][1] = 1;					ry.m[1][2] = 0;					ry.m[1][3] = 0;
        ry.m[2][0] = (float)Math.sin(y);ry.m[2][1] = 0;					ry.m[2][2] = (float)Math.cos(y);ry.m[2][3] = 0;
        ry.m[3][0] = 0;					ry.m[3][1] = 0;					ry.m[3][2] = 0;					ry.m[3][3] = 1;

        m = rz.mul(ry.mul(rx)).getM(); // Finalizing all 3 axis rotations into one matrix

        return this;

    }

    public Matrix4F makeScaling(float x, float y, float z){
        this.makeIdentity();

        this.m[0][0] = x;
        this.m[1][1] = y;
        this.m[2][2] = z;

        return this;
    }



    public Matrix4F makeProjection(float fov, float width, float height, float zNear, float zFar){

        float aspectRatio = width/height;
        float tanHalfFOV = (float) Math.tan(Math.toRadians(fov/2));
        float zRange = zNear - zFar;

        this.makeIdentity();

        this.m[0][0] = 1.0f/(tanHalfFOV * aspectRatio);
        this.m[1][1] = 1.0f/ tanHalfFOV;
        this.m[2][2] = (-zNear - zFar)/zRange;
        this.m[3][3] = 0;
        this.m[3][2] = 1;
        this.m[2][3] = 2 * zFar * zNear/zRange;


        return this;
    }



    public Matrix4F makeCameraMatrix(Vector3F forward, Vector3F up){

        Vector3F f = forward;
        f = f.normalized();

        Vector3F r = up;
        r = r.normalized();
        r = r.crossProd(f);

        Vector3F u = f.crossProd(r);

        m[0][0] = r.getX();	m[0][1] = r.getY();	m[0][2] = r.getZ();	m[0][3] = 0;
        m[1][0] = u.getX();	m[1][1] = u.getY();	m[1][2] = u.getZ();	m[1][3] = 0;
        m[2][0] = f.getX();	m[2][1] = f.getY();	m[2][2] = f.getZ();	m[2][3] = 0;
        m[3][0] = 0;		m[3][1] = 0;		m[3][2] = 0;		m[3][3] = 1;

        return this;

    }

    public Matrix4F makeShearMatrix(Vector3F shearData){

        this.makeIdentity();

        m[0][2] = shearData.getX();

        return this;

    }

    public Matrix4F mul(Matrix4F other) {

        Matrix4F res = new Matrix4F();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                res.set(i, j, this.m[i][0] * other.get(0, j)
                        + this.m[i][1] * other.get(1, j)
                        + this.m[i][2] * other.get(2, j)
                        + this.m[i][3] * other.get(3, j));
            }

        }

        return res;
    }

    public float[][] getM() {
        return m;
    }

    public float get(int x, int y) {
        return m[x][y];
    }

    public void set(int x, int y, float val) {
        this.m[x][y] = val;
    }

    public void setM(float[][] m) {
        this.m = m;
    }
}
