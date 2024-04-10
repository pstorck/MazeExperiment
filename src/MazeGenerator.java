import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MazeGenerator {
    private final int width;
    private final int height;
    private MazeCell[][] maze;
    private Random random;

    public MazeGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        maze = new MazeCell[width][height];
        for (int r = 0; r < width; r++) {
            for (int c = 0; c < height; c++) {
                maze[r][c] = new MazeCell(r, c);
            }
        }
        random = new Random();
    }

    public static void main(String[] args) {
        System.out.println("MAZE GENERATOR");
        MazeGenerator mazeGen = new MazeGenerator(6500, 6500);
        mazeGen.generateMaze(0, 0);
        System.out.println(mazeGen);
    }

    public MazeCell[][] getMaze() {
        return maze;
    }

    public void generateMaze(int px, int py) {
        Stack<MazeCell> stack = new Stack<>();
        stack.push(maze[px][py]);
        while (!stack.isEmpty()) {
            MazeCell cell = stack.peek();
            cell.setVisited(true);
            px = cell.getX();
            py = cell.getY();
            ArrayList<Successor> successors = getSuccessors(px, py);

            if(successors.isEmpty()) {
                stack.pop();
                continue;
            }

            int r = random.nextInt(successors.size());
            Successor s = successors.get(r);
            successors.remove(r);
            if (s == Successor.TOP) {
                MazeCell top = maze[px][py - 1];
                if (!top.isVisited()) {
                    cell.setTop(false);
                    top.setBottom(false);
                    stack.push(maze[px][py - 1]);
                    continue;
                }
            }
            if (s == Successor.BOTTOM) {
                MazeCell bottom = maze[px][py + 1];
                if (!bottom.isVisited()) {
                    cell.setBottom(false);
                    bottom.setTop(false);
                    stack.push(maze[px][py + 1]);
                    continue;
                }
            }
            if (s == Successor.LEFT) {
                MazeCell left = maze[px - 1][py];
                if (!left.isVisited()) {
                    cell.setLeft(false);
                    left.setRight(false);
                    stack.push(maze[px - 1][py]);
                    continue;
                }
            }
            if (s == Successor.RIGHT) {
                MazeCell right = maze[px + 1][py];
                if (!right.isVisited()) {
                    cell.setRight(false);
                    right.setLeft(false);
                    stack.push(maze[px + 1][py]);
                }
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < height; c++) {
            for (int r = 0; r < width; r++) {
                sb.append(maze[r][c].hasTop() ? "+---" : "+   ");
            }
            sb.append("+\n");

            for (int r = 0; r < width; r++) {
                if (r == 0 && c == 0) {
                    sb.append(maze[r][c].hasLeft() ? "| S " : "  S ");
                } else if (r == width - 1 && c == height - 1) {
                    sb.append(maze[r][c].hasLeft() ? "| E " : "  E ");
                } else {
                    sb.append(maze[r][c].hasLeft() ? "|   " : "    ");
                }
            }
            sb.append("|\n");
        }
        // draw the bottom line
        for (int r = 0; r < width; r++) {
            sb.append("+---");
        }
        sb.append("+\n");
        return sb.toString();
    }

    private ArrayList<Successor> getSuccessors(int px, int py) {
        ArrayList<Successor> successors = new ArrayList<Successor>(4);
        if (px > 0 && !maze[px - 1][py].isVisited()) {
            successors.add(Successor.LEFT);
        }
        if (py > 0 && !maze[px][py - 1].isVisited()) {
            successors.add(Successor.TOP);
        }
        if (px + 1 < width && !maze[px + 1][py].isVisited()) {
            successors.add(Successor.RIGHT);
        }
        if (py + 1 < height && !maze[px][py + 1].isVisited()) {
            successors.add(Successor.BOTTOM);
        }
        return successors;
    }
}
