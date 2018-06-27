package sardor.odev3.mobil.phoneorientation;

/**
 * Created by Sardor on 13/05/2017.
 */

public class MyGraphPoints {
    private int x;
    private int y;
    private int z;
    private int t;

    public MyGraphPoints(int x, int y, int z, int t){
        setT(t);
        setX(x);
        setY(y);
        setZ(z);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public String getAll(){
        return "X:"+x+" Y:"+y+" Z:"+z+" T:"+t;
    }
}
