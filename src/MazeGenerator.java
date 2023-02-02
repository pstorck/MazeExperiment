import java.util.ArrayList;
import java.util.Random;

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
        MazeGenerator mazeGen = new MazeGenerator(20, 20);
        mazeGen.generateMaze(0, 0);
        mazeGen.printMaze();
    }

    public MazeCell[][] getMaze() {
        return maze;
    }

    public void generateMaze(int px, int py) {
        MazeCell cell = maze[px][py];
        cell.setVisited(true);
        ArrayList<Successor> successors = getSuccessors(px, py);
        while (!successors.isEmpty()) {
            int r = random.nextInt(successors.size());
            Successor s = successors.get(r);
            successors.remove(r);
            if (s == Successor.TOP) {
                MazeCell top = maze[px][py - 1];
                if (!top.isVisited()) {
                    cell.setTop(false);
                    top.setBottom(false);
                    generateMaze(px, py - 1);
                }
            }
            if (s == Successor.BOTTOM) {
                MazeCell bottom = maze[px][py + 1];
                if (!bottom.isVisited()) {
                    cell.setBottom(false);
                    bottom.setTop(false);
                    generateMaze(px, py + 1);
                }
            }
            if (s == Successor.LEFT) {
                MazeCell left = maze[px - 1][py];
                if (!left.isVisited()) {
                    cell.setLeft(false);
                    left.setRight(false);
                    generateMaze(px - 1, py);
                }
            }
            if (s == Successor.RIGHT) {
                MazeCell right = maze[px + 1][py];
                if (!right.isVisited()) {
                    cell.setRight(false);
                    right.setLeft(false);
                    generateMaze(px + 1, py);
                }
            }
        }
    }

    public void printMaze() {
        for (int c = 0; c < height; c++) {
            for (int r = 0; r < width; r++) {
                System.out.print(maze[r][c].hasTop() ? "+---" : "+   ");
            }
            System.out.println("+");

            for (int r = 0; r < width; r++) {
                if (r == 0 && c == 0) {
                    System.out.print(maze[r][c].hasLeft() ? "| S " : "  S ");
                } else if (r == width - 1 && c == height - 1) {
                    System.out.print(maze[r][c].hasLeft() ? "| E " : "  E ");
                } else {
                    System.out.print(maze[r][c].hasLeft() ? "|   " : "    ");
                }
            }
            System.out.println("|");
        }
        // draw the bottom line
        for (int r = 0; r < width; r++) {
            System.out.print("+---");
        }
        System.out.println("+");
    }

    private ArrayList<Successor> getSuccessors(int px, int py) {
        ArrayList<Successor> successors = new ArrayList<Successor>(4);
        if (px > 0) {
            successors.add(Successor.LEFT);
        }
        if (py > 0) {
            successors.add(Successor.TOP);
        }
        if (px + 1 < width) {
            successors.add(Successor.RIGHT);
        }
        if (py + 1 < height) {
            successors.add(Successor.BOTTOM);
        }
        return successors;
    }
}
