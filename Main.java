import javax.swing.*;
import java.awt.*;
import Auth.AppFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            AppFrame appFrame = new AppFrame();
            appFrame.setVisible(true);
        });
    }
}