import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.Random;

import static java.lang.Thread.sleep;

public class MazeDrawer {
    public static final int WIDTH = 100;
    public static final int HEIGHT = 100;
    public static final boolean RANDOM = false;
    public static final int NUM_MAZES = 3;

    public static void main(String[] args) {
        Random random = new Random();
        JFrame frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        frame.add(panel);
        JLabel label = new JLabel("Maze Generator");
        panel.add(label);
        int width = WIDTH;
        int height = HEIGHT;
        int startX = RANDOM ? random.nextInt(width) : 0;
        int startY = RANDOM ? random.nextInt(height) : 0;
        for (int i = 0; i < NUM_MAZES; i++) {
            MazePanel mazePanel = new MazePanel(width, height);
            mazePanel.setPreferredSize(new Dimension(800 / Math.max(width, height) * width + 10,
                    800 / Math.max(width, height) * height + 10));
            panel.add(mazePanel);
            frame.setVisible(true);
            try {
                label.setText(String.format("Maze %s of %s - Depth First Search", i + 1, NUM_MAZES));
                mazePanel.solveDFS(startX, startY);
                mazePanel.repaint();
                sleep(3000);
                label.setText(String.format("Maze %s of %s - Breadth First Search", i + 1, NUM_MAZES));
                mazePanel.clear();
                mazePanel.solveBFS(startX, startY);
                mazePanel.repaint();
                sleep(3000);
                label.setText(String.format("Maze %s of %s - A* Search", i + 1, NUM_MAZES));
                mazePanel.clear();
                mazePanel.solveAStar(startX, startY);
                mazePanel.repaint();
                sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            panel.remove(mazePanel);
            frame.repaint();
        }
        System.exit(0);
    }
}
