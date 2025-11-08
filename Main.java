import javax.swing.*;
import java.awt.*;
import Admin.MainFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
