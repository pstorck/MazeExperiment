import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Thread.sleep;

public class MazeDrawer {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        for (int i = 0; i < 100; i++) {
            MazePanel mazePanel = new MazePanel(100);
            mazePanel.setPreferredSize(new Dimension(810,810));
            frame.add(mazePanel);
            frame.setVisible(true);
            try {
                mazePanel.solveDFS(0, 0);
                mazePanel.repaint();
                sleep(3000);
                mazePanel.clear();
                mazePanel.solveBFS(0, 0);
                mazePanel.repaint();
                sleep(3000);
                mazePanel.clear();
                mazePanel.solveAStar(0,0);
                mazePanel.repaint();
                sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            frame.remove(mazePanel);
        }
        System.exit(0);
    }

    public static class MazePanel extends JPanel {
        private int size;
        private MazeCell[][] maze;
        private boolean solved = false;

        private enum SUCCESSORS {TOP, BOTTOM, LEFT, RIGHT};

        public class MazeCellComparator implements Comparator<MazeCell> {
            public int compare(MazeCell a, MazeCell b) {
                return Double.compare(a.getF(), b.getF());
            }
        }

        public MazePanel(int size) {
            this.size = size;
            MazeGenerator mazeGen = new MazeGenerator(size, size);
            mazeGen.generateMaze(0,0);
            this.maze = mazeGen.getMaze();
            clear();
            maze[size-1][size-1].setSolution(true);
        }

        public void clear() {
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    maze[r][c].setVisited(false);
                    maze[r][c].setSolution(false);
                }
            }
            maze[size-1][size-1].setSolution(true);
        }

        private ArrayList<SUCCESSORS> getSuccessors(int px, int py) {
            ArrayList<SUCCESSORS> successors = new ArrayList<SUCCESSORS>(4);
            MazeCell cell = maze[px][py];
            if(px > 0 && !cell.hasLeft()) {
                successors.add(SUCCESSORS.LEFT);
            }
            if (py > 0 && !cell.hasTop()) {
                successors.add(SUCCESSORS.TOP);
            }
            if (px + 1 < size && !cell.hasRight()) {
                successors.add(SUCCESSORS.RIGHT);
            }
            if (py + 1 < size && !cell.hasBottom()) {
                successors.add(SUCCESSORS.BOTTOM);
            }
            return successors;
        }

        public void solveDFS(int px, int py) throws InterruptedException {
            repaint();
            sleep(500 / size);
            maze[px][py].setVisited(true);
            if (px == size - 1 && py == size - 1) {
                solved = true;
                return;
            }
            for (SUCCESSORS s: getSuccessors(px, py)) {
                if (s == SUCCESSORS.BOTTOM && !maze[px][py+1].isVisited() && !solved) {
                    solveDFS(px, py + 1);
                }
                if (s == SUCCESSORS.RIGHT && !maze[px+1][py].isVisited() && !solved) {
                    solveDFS(px + 1, py);
                }
                if (s == SUCCESSORS.TOP && !maze[px][py-1].isVisited() && !solved) {
                    solveDFS(px, py - 1);
                }
                if (s == SUCCESSORS.LEFT && !maze[px-1][py].isVisited() && !solved) {
                    solveDFS(px - 1, py);
                }
            }
            if (solved) {
                maze[px][py].setSolution(true);
                repaint();
                sleep(100 / size);
            }
        }

        private void setBFSSolution(HashMap<Integer, MazeCell> closed) throws InterruptedException {
            int parent = maze[size-1][size-1].getParent();
            while (parent != -1) {
                MazeCell cell = closed.get(parent);
                cell.setSolution(true);
                parent = cell.getParent();
                repaint();
                sleep(100 / size);
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
                if (current.getX() == size - 1 && current.getY() == size - 1) {
                    setBFSSolution(closed);
                    return;
                }
                for (SUCCESSORS s: getSuccessors(current.getX(), current.getY())) {
                    MazeCell neighbor = null;
                    if (s == SUCCESSORS.TOP) {
                        neighbor = maze[current.getX()][current.getY() - 1];
                    }
                    if (s == SUCCESSORS.BOTTOM) {
                        neighbor = maze[current.getX()][current.getY() + 1];
                    }
                    if (s == SUCCESSORS.LEFT) {
                        neighbor = maze[current.getX() - 1][current.getY()];
                    }
                    if (s == SUCCESSORS.RIGHT) {
                        neighbor = maze[current.getX() + 1][current.getY()];
                    }
                    if(!neighbor.isVisited()) {
                        neighbor.setVisited(true);
                        neighbor.setParent(current.hashCode());
                        closed.put(neighbor.hashCode(), neighbor);
                        queue.add(neighbor);
                        repaint();
                        sleep(500 / size);
                    }
                }
                queue.removeFirst();
                current = queue.getFirst();
            }
        }

        private double h(int px, int py) {
            return Math.sqrt(Math.pow((size - 1) - px, 2) + Math.pow((size - 1) - py,2));
        }

        private void setAStarSolution(ArrayList<MazeCell> closed) throws InterruptedException {
            MazeCell cell = closed.get(closed.size() - 1);
            cell.setSolution(true);
            while (cell.getParent() != -1) {
                cell = closed.get(cell.getParent() - 2);
                cell.setSolution(true);
                repaint();
                sleep(100 / size);
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
                sleep(500 / size);
                MazeCell current = open.poll();
                current.setVisited(true);
                closed.add(current);
                if (current.getX() == size -1 && current.getY() == size -1) {
                    setAStarSolution(closed);
                    return;
                }
                g++;
                for (SUCCESSORS s: getSuccessors(current.getX(), current.getY())) {
                    MazeCell neighbor = null;
                    if (s == SUCCESSORS.TOP) {
                        neighbor = maze[current.getX()][current.getY() - 1];
                    }
                    if (s == SUCCESSORS.BOTTOM) {
                        neighbor = maze[current.getX()][current.getY() + 1];
                    }
                    if (s == SUCCESSORS.LEFT) {
                        neighbor = maze[current.getX() - 1][current.getY()];
                    }
                    if (s == SUCCESSORS.RIGHT) {
                        neighbor = maze[current.getX() + 1][current.getY()];
                    }
                    if(!neighbor.isVisited()) {
                        neighbor.setF(g + h(neighbor.getX(), neighbor.getY()));
                        neighbor.setParent(closed.size() + 1);
                        open.add(neighbor);
                    }
                }
            }
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    if (maze[r][c].isSolution()) {
                        g.setColor(Color.green);
                        g.fillRect(r * 800/size, c * 800/size, 800/size, 800/size);
                    } else if (maze[r][c].isVisited()) {
                        g.setColor(Color.blue);
                        g.fillRect(r * 800/size, c * 800/size, 800/size, 800/size);
                    }
                }
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke((int)Math.ceil(100 / size)));
            g2.setColor(Color.black);
            Line2D line;
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    MazeCell cell = maze[r][c];
                    if (cell.hasTop()) {
                        line = new Line2D.Float(800/size * r,  800/size * c,   800/size * (r+1), 800/size * c);
                        g2.draw(line);
                    }
                    if (cell.hasLeft()) {
                        line = new Line2D.Float(800/size * r,  800/size * c,   800/size * r, 800/size * (c+1));
                        g2.draw(line);
                    }
                }
                line = new Line2D.Float(800/size * r, 800, 800/size * (r+1), 800);
                g2.draw(line);
            }
            line = new Line2D.Float(800, 0, 800, 800);
            g2.draw(line);
        }
    }
}
