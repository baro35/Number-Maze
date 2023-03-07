public class Coordinate {
    private int x;
    private int y;
    Coordinate previousPosition;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Coordinate(int x, int y, Coordinate previousPosition) {
        this.x = x;
        this.y = y;
        this.previousPosition = previousPosition;
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

    public Coordinate getPreviousPosition() {
        return previousPosition;
    }

    public void setPreviousPosition(Coordinate previousPosition) {
        this.previousPosition = previousPosition;
    }

}
