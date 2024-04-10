import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;

import static java.lang.Thread.sleep;

public class MazePanel extends JPanel {
    private int pixels;
    private int size;
    private int timer;
    private int width;
    private int height;
    private MazeCell[][] maze;

    public class MazeCellComparator implements Comparator<MazeCell> {
        public int compare(MazeCell a, MazeCell b) {
            return Double.compare(a.getF(), b.getF());
        }
    }

    public MazePanel(int width, int height, int pixels) {
        this.pixels = pixels;
        this.size = Math.max(width, height);
        this.timer = Math.min(500, Math.max(width, height));
        this.width = width;
        this.height = height;
        MazeGenerator mazeGen = new MazeGenerator(width, height);
        mazeGen.generateMaze(0, 0);
        this.maze = mazeGen.getMaze();
        clear();
        maze[width - 1][height - 1].setSolution(true);
    }

    public void solveDFS(int px, int py) throws InterruptedException {
        Stack<MazeCell> stack = new Stack<>();
        MazeCell cell = maze[px][py];
        stack.push(cell);
        cell.setParent(null);
        while (!stack.isEmpty()) {
            repaint();
            sleep(500 / timer);
            cell = stack.pop();
            px = cell.getX();
            py = cell.getY();
            cell.setVisited(true);
            if (px == width - 1 && py == height - 1) {
                break;
            }
            for (Successor s : getSuccessor(px, py)) {
                if (s == Successor.BOTTOM && !maze[px][py + 1].isVisited()) {
                    MazeCell newCell = maze[px][py + 1];
                    stack.push(newCell);
                    newCell.setParent(cell);
                }
                if (s == Successor.RIGHT && !maze[px + 1][py].isVisited()) {
                    MazeCell newCell = maze[px + 1][py];
                    stack.push(newCell);
                    newCell.setParent(cell);
                }
                if (s == Successor.TOP && !maze[px][py - 1].isVisited()) {
                    MazeCell newCell = maze[px][py - 1];
                    stack.push(newCell);
                    newCell.setParent(cell);
                }
                if (s == Successor.LEFT && !maze[px - 1][py].isVisited()) {
                    MazeCell newCell = maze[px - 1][py];
                    stack.push(newCell);
                    newCell.setParent(cell);
                }
            }
        }
        setSolution();
    }

    public void solveBFS(int px, int py) throws InterruptedException {
        LinkedList<MazeCell> queue = new LinkedList<MazeCell>();
        MazeCell current = maze[px][py];
        current.setVisited(true);
        current.setParent(null);
        queue.add(current);
        while (!queue.isEmpty()) {
            if (current.getX() == width - 1 && current.getY() == height - 1) {
                break;
            }
            for (Successor s : getSuccessor(current.getX(), current.getY())) {
                MazeCell neighbor = null;
                if (s == Successor.TOP) {
                    neighbor = maze[current.getX()][current.getY() - 1];
                }
                if (s == Successor.BOTTOM) {
                    neighbor = maze[current.getX()][current.getY() + 1];
                }
                if (s == Successor.LEFT) {
                    neighbor = maze[current.getX() - 1][current.getY()];
                }
                if (s == Successor.RIGHT) {
                    neighbor = maze[current.getX() + 1][current.getY()];
                }
                if (!neighbor.isVisited()) {
                    neighbor.setVisited(true);
                    neighbor.setParent(current);
                    queue.add(neighbor);
                    repaint();
                    sleep(500 / timer);
                }
            }
            queue.removeFirst();
            current = queue.getFirst();
        }
        setSolution();
    }

    public void solveAStar(int px, int py) throws InterruptedException {
        PriorityQueue<MazeCell> open = new PriorityQueue<MazeCell>(new MazeCellComparator());
        int g = 0;

        MazeCell cell = maze[px][py];
        cell.setF(h(cell.getX(), cell.getY()));
        cell.setParent(null);
        open.add(cell);

        while (!open.isEmpty()) {
            repaint();
            sleep(500 / timer);
            MazeCell current = open.poll();
            current.setVisited(true);
            if (current.getX() == width - 1 && current.getY() == height - 1) {
                break;
            }
            g++;
            for (Successor s : getSuccessor(current.getX(), current.getY())) {
                MazeCell neighbor = null;
                if (s == Successor.TOP) {
                    neighbor = maze[current.getX()][current.getY() - 1];
                }
                if (s == Successor.BOTTOM) {
                    neighbor = maze[current.getX()][current.getY() + 1];
                }
                if (s == Successor.LEFT) {
                    neighbor = maze[current.getX() - 1][current.getY()];
                }
                if (s == Successor.RIGHT) {
                    neighbor = maze[current.getX() + 1][current.getY()];
                }
                if (!neighbor.isVisited()) {
                    neighbor.setF(g + h(neighbor.getX(), neighbor.getY()));
                    neighbor.setParent(current);
                    open.add(neighbor);
                }
            }
        }
        setSolution();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int r = 0; r < width; r++) {
            for (int c = 0; c < height; c++) {
                if (maze[r][c].isSolution()) {
                    g.setColor(Color.green);
                    g.fillRect(r * pixels / size, c * pixels / size, pixels / size, pixels / size);
                } else if (maze[r][c].isVisited()) {
                    g.setColor(Color.blue);
                    g.fillRect(r * pixels / size, c * pixels / size, pixels / size, pixels / size);
                }
            }
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke((int) Math.ceil(100 / size)));
        g2.setColor(Color.black);
        Line2D line;
        for (int r = 0; r < width; r++) {
            for (int c = 0; c < height; c++) {
                MazeCell cell = maze[r][c];
                if (cell.hasTop()) {
                    line = new Line2D.Float(pixels / size * r, pixels / size * c, pixels / size * (r + 1), pixels / size * c);
                    g2.draw(line);
                }
                if (cell.hasLeft()) {
                    line = new Line2D.Float(pixels / size * r, pixels / size * c, pixels / size * r, pixels / size * (c + 1));
                    g2.draw(line);
                }
            }
            line = new Line2D.Float(pixels / size * r, pixels / size * height, pixels / size * (r + 1), pixels / size * height);
            g2.draw(line);
        }
        line = new Line2D.Float(pixels / size * width, 0, pixels / size * width, pixels / size * height);
        g2.draw(line);
    }

    public void clear() {
        for (int r = 0; r < this.width; r++) {
            for (int c = 0; c < this.height; c++) {
                maze[r][c].setVisited(false);
                maze[r][c].setSolution(false);
            }
        }
        maze[width - 1][height - 1].setSolution(true);
    }

    private ArrayList<Successor> getSuccessor(int px, int py) {
        ArrayList<Successor> successors = new ArrayList<Successor>(4);
        MazeCell cell = maze[px][py];
        if (px > 0 && !cell.hasLeft()) {
            successors.add(Successor.LEFT);
        }
        if (py > 0 && !cell.hasTop()) {
            successors.add(Successor.TOP);
        }
        if (px + 1 < width && !cell.hasRight()) {
            successors.add(Successor.RIGHT);
        }
        if (py + 1 < height && !cell.hasBottom()) {
            successors.add(Successor.BOTTOM);
        }
        return successors;
    }

    private double h(int px, int py) {
        return Math.sqrt(Math.pow((width - 1) - px, 2) + Math.pow((height - 1) - py, 2));
    }

    private void setSolution() throws InterruptedException {
        MazeCell cell = maze[width - 1][height - 1].getParent();
        while (cell != null) {
            cell.setSolution(true);
            cell = cell.getParent();
            repaint();
            sleep(500 / timer);
        }
    }
}
