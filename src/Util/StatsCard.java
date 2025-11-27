package Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatsCard extends JPanel {

    private final int ARC_SIZE = 15;
    private JLabel lblTitle;
    private JLabel lblValue;
    private Color borderColor;

    public StatsCard(String title, String value) {
        this(title, value, Color.BLACK);
    }

    public StatsCard(String title, String value, Color valueColor) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setPreferredSize(new Dimension(200, 90));

        initializeComponents(title, value, valueColor);
    }

    private void initializeComponents(String title, String value, Color valueColor) {
        lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(new Color(100, 100, 100));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        Component gap = Box.createVerticalStrut(10);

        lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setForeground(valueColor);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(lblTitle);
        add(gap);
        add(lblValue);
    }

    public void setValue(String newValue) {
        lblValue.setText(newValue);
        repaint();
    }

    public void setValueColor(Color color) {
        lblValue.setForeground(color);
        repaint();
    }

    public void setTitle(String newTitle) {
        lblTitle.setText(newTitle);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC_SIZE, ARC_SIZE);

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC_SIZE, ARC_SIZE);

        g2.dispose();
        super.paintComponent(g);
    }
}