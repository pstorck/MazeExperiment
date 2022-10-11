import java.util.ArrayList;
import java.util.Random;

public class MazeGenerator {
    private final int width;
    private final int height;
    private MazeCell[][] maze;
    private Random random;

    private enum SUCCESSORS {TOP, BOTTOM, LEFT, RIGHT};

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

    private ArrayList<SUCCESSORS> getSuccessors(int px, int py) {
        ArrayList<SUCCESSORS> successors = new ArrayList<SUCCESSORS>(4);
        if(px > 0) {
            successors.add(SUCCESSORS.LEFT);
        }
        if (py > 0) {
            successors.add(SUCCESSORS.TOP);
        }
        if (px + 1 < width) {
            successors.add(SUCCESSORS.RIGHT);
        }
        if (py + 1 < height) {
            successors.add(SUCCESSORS.BOTTOM);
        }
        return successors;
    }

    public void generateMaze(int px, int py) {
        MazeCell cell = maze[px][py];
        cell.setVisited(true);
        ArrayList<SUCCESSORS> successors = getSuccessors(px, py);
        while (!successors.isEmpty()) {
            int r = random.nextInt(successors.size());
            SUCCESSORS s = successors.get(r);
            successors.remove(r);
            if (s == SUCCESSORS.TOP) {
                MazeCell top = maze[px][py-1];
                if (!top.isVisited()) {
                    cell.setTop(false);
                    top.setBottom(false);
                    generateMaze(px, py - 1);
                }
            }
            if (s == SUCCESSORS.BOTTOM) {
                MazeCell bottom = maze[px][py+1];
                if (!bottom.isVisited()) {
                    cell.setBottom(false);
                    bottom.setTop(false);
                    generateMaze(px, py + 1);
                }
            }
            if (s == SUCCESSORS.LEFT) {
                MazeCell left = maze[px-1][py];
                if (!left.isVisited()) {
                    cell.setLeft(false);
                    left.setRight(false);
                    generateMaze(px - 1, py);
                }
            }
            if (s == SUCCESSORS.RIGHT) {
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

    public MazeCell[][] getMaze() {
        return maze;
    }

    public static void main(String[] args) {
        System.out.println("MAZE GENERATOR");
        MazeGenerator mazeGen = new MazeGenerator(20,20);
        mazeGen.generateMaze(0,0);
        mazeGen.printMaze();
    }
}
