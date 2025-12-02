import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import javax.swing.*;
import java.awt.*;
import Auth.AppFrame;
import Utils.DatabaseHelper;

public class Main {
    public static void main(String[] args) {

        DatabaseHelper.initializeDatabase();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatIntelliJLaf());
                UIManager.put("Button.arc", 20); // Membuat tombol lebih bulat
                UIManager.put("Component.arc", 20); // Membuat komponen lebih bulat
                UIManager.put("TextComponent.arc", 20); // Membuat input field lebih bulat
                UIManager.put("Panel.background", new Color(245, 245, 245)); // Warna latar belakang panel
                UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 14)); // Font default untuk label
            } catch (Exception e) {
                e.printStackTrace();
            }

            AppFrame appFrame = new AppFrame();
            appFrame.setVisible(true);
        });
    }
}