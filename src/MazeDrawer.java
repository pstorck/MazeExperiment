import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

import static java.lang.Thread.sleep;

public class MazeDrawer {
    public static final int WIDTH = 100;
    public static final int HEIGHT = 100;
    public static final boolean RANDOM = false;

    public static void main(String[] args) {
        Random random = new Random();
        JFrame frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        int width = WIDTH;
        int height = HEIGHT;
        int startX = RANDOM ? random.nextInt(width) : 0;
        int startY = RANDOM ? random.nextInt(height) : 0;
        for (int i = 0; i < 3; i++) {
            MazePanel mazePanel = new MazePanel(width, height);
            mazePanel.setPreferredSize(new Dimension(800 / Math.max(width, height) * width + 10, 800 / Math.max(width, height) * height + 10));
            frame.add(mazePanel);
            frame.setVisible(true);
            try {
                mazePanel.solveDFS(startX, startY);
                mazePanel.repaint();
                sleep(3000);
                mazePanel.clear();
                mazePanel.solveBFS(startX, startY);
                mazePanel.repaint();
                sleep(3000);
                mazePanel.clear();
                mazePanel.solveAStar(startX, startY);
                mazePanel.repaint();
                sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            frame.remove(mazePanel);
            frame.repaint();
        }
        System.exit(0);
    }

    public static class MazePanel extends JPanel {
        private int size;
        private int timer;
        private int width;
        private int height;
        private MazeCell[][] maze;
        private boolean solved = false;

        public class MazeCellComparator implements Comparator<MazeCell> {
            public int compare(MazeCell a, MazeCell b) {
                return Double.compare(a.getF(), b.getF());
            }
        }

        public MazePanel(int width, int height) {
            this.size = Math.max(width, height);
            this.timer = (int) Math.sqrt(width * height) * 2;
            this.width = width;
            this.height = height;
            MazeGenerator mazeGen = new MazeGenerator(width, height);
            mazeGen.generateMaze(0, 0);
            this.maze = mazeGen.getMaze();
            clear();
            maze[width - 1][height - 1].setSolution(true);
        }

        public void solveDFS(int px, int py) throws InterruptedException {
            repaint();
            sleep(500 / timer);
            maze[px][py].setVisited(true);
            if (px == width - 1 && py == height - 1) {
                solved = true;
                return;
            }
            for (Successor s : getSuccessor(px, py)) {
                if (s == Successor.BOTTOM && !maze[px][py + 1].isVisited() && !solved) {
                    solveDFS(px, py + 1);
                }
                if (s == Successor.RIGHT && !maze[px + 1][py].isVisited() && !solved) {
                    solveDFS(px + 1, py);
                }
                if (s == Successor.TOP && !maze[px][py - 1].isVisited() && !solved) {
                    solveDFS(px, py - 1);
                }
                if (s == Successor.LEFT && !maze[px - 1][py].isVisited() && !solved) {
                    solveDFS(px - 1, py);
                }
            }
            if (solved) {
                maze[px][py].setSolution(true);
                repaint();
                sleep(200 / timer);
            }
        }

        public void solveBFS(int px, int py) throws InterruptedException {
            LinkedList<MazeCell> queue = new LinkedList<MazeCell>();
            HashMap<Integer, MazeCell> closed = new HashMap<Integer, MazeCell>();
            MazeCell current = maze[px][py];
            current.setVisited(true);
            current.setParent(-1);
            closed.put(current.hashCode(), current);
            queue.add(current);
            while (!queue.isEmpty()) {
                if (current.getX() == width - 1 && current.getY() == height - 1) {
                    setBFSSolution(closed);
                    return;
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
                        neighbor.setParent(current.hashCode());
                        closed.put(neighbor.hashCode(), neighbor);
                        queue.add(neighbor);
                        repaint();
                        sleep(500 / timer);
                    }
                }
                queue.removeFirst();
                current = queue.getFirst();
            }
        }

        public void solveAStar(int px, int py) throws InterruptedException {
            PriorityQueue<MazeCell> open = new PriorityQueue<MazeCell>(new MazeCellComparator());
            ArrayList<MazeCell> closed = new ArrayList<MazeCell>();
            int g = 0;

            MazeCell cell = maze[px][py];
            cell.setF(h(cell.getX(), cell.getY()));
            cell.setParent(-1);
            open.add(cell);

            while (!open.isEmpty()) {
                repaint();
                sleep(500 / timer);
                MazeCell current = open.poll();
                current.setVisited(true);
                closed.add(current);
                if (current.getX() == width - 1 && current.getY() == height - 1) {
                    setAStarSolution(closed);
                    return;
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
                        neighbor.setParent(closed.size() + 1);
                        open.add(neighbor);
                    }
                }
            }
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int r = 0; r < width; r++) {
                for (int c = 0; c < height; c++) {
                    if (maze[r][c].isSolution()) {
                        g.setColor(Color.green);
                        g.fillRect(r * 800 / size, c * 800 / size, 800 / size, 800 / size);
                    } else if (maze[r][c].isVisited()) {
                        g.setColor(Color.blue);
                        g.fillRect(r * 800 / size, c * 800 / size, 800 / size, 800 / size);
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
                        line = new Line2D.Float(800 / size * r, 800 / size * c, 800 / size * (r + 1), 800 / size * c);
                        g2.draw(line);
                    }
                    if (cell.hasLeft()) {
                        line = new Line2D.Float(800 / size * r, 800 / size * c, 800 / size * r, 800 / size * (c + 1));
                        g2.draw(line);
                    }
                }
                line = new Line2D.Float(800 / size * r, 800 / size * height, 800 / size * (r + 1), 800 / size * height);
                g2.draw(line);
            }
            line = new Line2D.Float(800 / size * width, 0, 800 / size * width, 800 / size * height);
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
            ArrayList<Successor> successors= new ArrayList<Successor>(4);
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

        private void setBFSSolution(HashMap<Integer, MazeCell> closed) throws InterruptedException {
            int parent = maze[width - 1][height - 1].getParent();
            while (parent != -1) {
                MazeCell cell = closed.get(parent);
                cell.setSolution(true);
                parent = cell.getParent();
                repaint();
                sleep(200 / timer);
            }
        }

        private void setAStarSolution(ArrayList<MazeCell> closed) throws InterruptedException {
            MazeCell cell = closed.get(closed.size() - 1);
            cell.setSolution(true);
            while (cell.getParent() != -1) {
                cell = closed.get(cell.getParent() - 2);
                cell.setSolution(true);
                repaint();
                sleep(200 / timer);
            }
        }
    }
}
