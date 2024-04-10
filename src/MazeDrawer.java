import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.Random;

import static java.lang.Thread.sleep;

public class MazeDrawer {
    public static final int PIXELS = 1500;
    public static final int WIDTH = 100;
    public static final int HEIGHT = 100;
    public static final boolean RANDOM = false;
    public static final int NUM_MAZES = 1;

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
        for (int i = 0; i < NUM_MAZES; i++) {
            MazePanel mazePanel = new MazePanel(width, height, PIXELS);
            mazePanel.setPreferredSize(new Dimension(PIXELS / Math.max(width, height) * width + 10,
                    PIXELS / Math.max(width, height) * height + 10));
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
}
