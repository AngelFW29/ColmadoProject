package View;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel dashboardPanel;
    private JPanel searchPanel;
    private JTextField searchTextField;
    private JButton btnAddProduct;
    private JPanel topPanel;
    private JLabel logoAppLabel;
    private JButton btnOrders;
    private JButton btnSell;
    private JButton button3;
    private JButton btnInventory;
    private JPanel accessBtnPanel;

    MainWindow() {
        setTitle("Sistema para Colmado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(950, 600);

        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);

        inizializeWindow();
    }

private void inizializeWindow(){

        loadLabelImage(logoAppLabel, "/img/appLogo.png", 40, 40);
        loadButtonImage(btnInventory, "/img/inventario.png", 25,25);
        loadButtonImage(btnSell, "/img/inventario.png", 25,25);
        loadButtonImage(btnOrders, "/img/inventario.png", 25,25);


}


    private void loadLabelImage(JLabel label, String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(
                    getClass().getResource(path)
            ));

            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
    }

    private void loadButtonImage(JButton button, String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledImage));

            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setVerticalTextPosition(SwingConstants.CENTER);

            button.setIconTextGap(10);

            button.setFocusPainted(false);
            button.setBorderPainted(false);
        } catch (Exception e) {
            System.err.println("Error loading image button: " + e.getMessage());
        }
    }

}


class ProgramExecute{
    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("Table.cellMargins", new Insets(8, 8, 8, 8));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}