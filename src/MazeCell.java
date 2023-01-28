public class MazeCell {
    private boolean top;
    private boolean bottom;
    private boolean left;
    private boolean right;
    private boolean visited;
    private boolean solution;
    private int x;
    private int y;
    private int parent;
    private double f;

    public MazeCell(int x, int y) {
        top = true;
        bottom = true;
        left = true;
        right = true;
        visited = false;
        solution = false;
        this.x = x;
        this.y = y;
    }

    public boolean hasTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    public boolean hasBottom() {
        return bottom;
    }

    public void setBottom(boolean bottom) {
        this.bottom = bottom;
    }

    public boolean hasLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean hasRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isSolution() {
        return solution;
    }

    public void setSolution(boolean solution) {
        this.solution = solution;
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

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public String toString() {
        return String.format("[f: %f, position: (%d,%d), visited: %b, parent: %d]", f, x, y, visited, parent);
    }

    public static void main(String[] args) {
        MazeCell cell = new MazeCell(0, 0);
        System.out.println(cell);
    }
}
