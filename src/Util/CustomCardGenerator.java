package Util;

import Model.Person;
import Model.Supplier;
import Model.Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CustomCardGenerator {

    private JPanel containerPanel;
    private JScrollPane scrollPane;

    public CustomCardGenerator(List<? extends Person> peopleList, ActionListener editAction, ActionListener deleteAction) {
        containerPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        containerPanel.setOpaque(false);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (Person person : peopleList) {
            JPanel card = createCard(person, editAction, deleteAction);

            JPanel cellWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            cellWrapper.setOpaque(false);
            cellWrapper.add(card);

            containerPanel.add(cellWrapper);
        }

        scrollPane = new JScrollPane(containerPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JPanel getContainer() {
        return containerPanel;
    }

    private JPanel createCard(Person person, ActionListener editAction, ActionListener deleteAction) {
        RoundedPanel card = new RoundedPanel(25, Color.WHITE);
        card.setLayout(null);
        card.setPreferredSize(new Dimension(290, 145));

        JLabel iconLabel = new JLabel();
        loadLabelImage(iconLabel, "/img/clienteIcon.png", 60, 60);
        iconLabel.setBounds(20, 25, 60, 60);

        if (iconLabel.getIcon() == null) {
            iconLabel.setText("IMG");
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setForeground(Color.GRAY);
            iconLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }
        card.add(iconLabel);

        JLabel nameLabel = new JLabel(person.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setBounds(95, 25, 180, 20);
        card.add(nameLabel);

        String role = "Desconocido";
        String rncValue = "N/A";

        if (person instanceof Supplier) {
            role = ((Supplier) person).getTypePerson();
            rncValue = ((Supplier) person).getFiscalIdentification();
        } else if (person instanceof Customer) {
            role = ((Customer) person).getTypePerson();
            rncValue = ((Customer) person).getFiscalIdentification();
        }

        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(Color.GRAY);
        roleLabel.setBounds(95, 48, 180, 15);
        card.add(roleLabel);

        JLabel phoneLabel = new JLabel(person.getPhone());
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        phoneLabel.setForeground(Color.DARK_GRAY);
        phoneLabel.setBounds(95, 75, 180, 20);
        card.add(phoneLabel);

        JLabel rncLabel = new JLabel("RNC: " + rncValue);
        rncLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rncLabel.setForeground(new Color(100, 100, 100));
        rncLabel.setBounds(20, 110, 160, 20);
        card.add(rncLabel);


        JButton btnEdit = createActionButton("/img/edit.png", new Color(88, 101, 242));
        btnEdit.setBounds(190, 105, 35, 30);
        btnEdit.addActionListener(e -> {
            if (editAction != null) editAction.actionPerformed(new ActionEvent(card, person.getId(), "edit"));
        });
        card.add(btnEdit);

        JButton btnDelete = createActionButton("/img/eliminar.png", new Color(237, 66, 69));
        btnDelete.setBounds(235, 105, 35, 30);
        btnDelete.addActionListener(e -> {
            if (deleteAction != null) deleteAction.actionPerformed(new ActionEvent(card, person.getId(), "delete"));
        });
        card.add(btnDelete);

        return card;
    }

    private JButton createActionButton(String iconPath, Color bgColor) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setBackground(bgColor);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loadButtonImage(btn, iconPath, 18, 18);
        return btn;
    }

    private void loadLabelImage(JLabel label, String path, int width, int height) {
        try {
            if (getClass().getResource(path) != null) {
                ImageIcon icon = new ImageIcon(getClass().getResource(path));
                Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception e) {}
    }

    private void loadButtonImage(JButton button, String path, int width, int height) {
        try {
            if (getClass().getResource(path) != null) {
                ImageIcon icon = new ImageIcon(getClass().getResource(path));
                Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception e) {}
    }

    static class RoundedPanel extends JPanel {
        private int arc;
        private Color bgColor;

        public RoundedPanel(int arc, Color bgColor) {
            this.arc = arc;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        public RoundedPanel(int arc) {
            this(arc, Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bgColor != null ? bgColor : Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            g2.setColor(new Color(230, 230, 230));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}