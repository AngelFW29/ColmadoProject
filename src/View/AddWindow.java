package View;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class AddWindow extends JFrame {
    private String title;
    private JPanel mainPanel;
    private JPanel titlePanel;
    private JPanel inputsPanel;
    private JPanel buttonPanel;
    private JLabel titleLabel;
    private JButton enviarButton;
    private JButton cancelarButton;
    private DynamicFormPanel formPanel;

    public AddWindow(String title, String[] labels) {
        this.title = title;
        titleLabel.setText(title);

        setTitle("Agregar " + title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(600, 400);
        setResizable(false);
        setLocationRelativeTo(null);

        inizializeWindow(labels);
        setVisible(true);
    }

    private void inizializeWindow(String[] labels) {
        formPanel = new DynamicFormPanel(labels);
        inputsPanel.setLayout(new BorderLayout());
        inputsPanel.add(formPanel, BorderLayout.CENTER);

        cancelarButton.addActionListener(e -> dispose());
        enviarButton.addActionListener(e -> onEnviar());
    }

    private void onEnviar() {
        Map<String, String> data = formPanel.getFormData();
        data.forEach((k, v) -> System.out.println(k + ": " + v));
        dispose();
    }
}