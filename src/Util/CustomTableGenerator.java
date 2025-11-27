package Util;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

public class CustomTableGenerator {
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;

    public CustomTableGenerator(String[] columns, Object[][] data) {
        this(columns, data, null, null, null);
    }

    public CustomTableGenerator(String[] columns, Object[][] data,
                                ActionListener editAction, ActionListener deleteAction) {
        this(columns, data, null, editAction, deleteAction);
    }

    public CustomTableGenerator(String[] columns, Object[][] data,
                                ActionListener viewAction, ActionListener editAction, ActionListener deleteAction) {

        boolean hasActions = (viewAction != null || editAction != null || deleteAction != null);

        model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == getColumnCount() - 1 && hasActions;
            }
        };

        table = new JTable(model);
        configureAppearance();

        if (hasActions) {
            int actionColumn = columns.length - 1;
            TableColumn col = table.getColumnModel().getColumn(actionColumn);

            col.setCellRenderer(new ButtonRenderer(viewAction != null, editAction != null, deleteAction != null));
            col.setCellEditor(new ButtonEditor(viewAction, editAction, deleteAction));

            int buttonsCount = (viewAction != null ? 1 : 0) + (editAction != null ? 1 : 0) + (deleteAction != null ? 1 : 0);
            col.setPreferredWidth(buttonsCount * 50 + 10);
        }

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public JTable getTable() {
        return table;
    }

    private void configureAppearance() {
        table.setRowHeight(40);
        table.setShowGrid(true);
    }

    static class ButtonRenderer extends JPanel implements TableCellRenderer {

        public ButtonRenderer(boolean showView, boolean showEdit, boolean showDelete) {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);

            if (showView) {
                JButton btnView = createButton();
                loadButtonImage(btnView, "/img/ver.png", 25, 25);
                add(btnView);
            }
            if (showEdit) {
                JButton btnEdit = createButton();
                loadButtonImage(btnEdit, "/img/edit.png", 25, 25);
                add(btnEdit);
            }
            if (showDelete) {
                JButton btnDelete = createButton();
                loadButtonImage(btnDelete, "/img/eliminar.png", 25, 25);
                add(btnDelete);
            }
        }

        private JButton createButton() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(45, 30));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }

        private void loadButtonImage(JButton button, String path, int width, int height) {
            try {
                if (getClass().getResource(path) == null) {
                    button.setText("?");
                    return;
                }
                ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
                Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                System.err.println("Error loading image button (" + path + "): " + e.getMessage());
            }
        }
    }

    static class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private int currentRow;

        public ButtonEditor(ActionListener viewAction, ActionListener editAction, ActionListener deleteAction) {
            super(new JCheckBox());
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

            if (viewAction != null) {
                JButton btnView = createButton("/img/ver.png"); 
                btnView.addActionListener(e -> {
                    viewAction.actionPerformed(new java.awt.event.ActionEvent(this, currentRow, "view"));
                    fireEditingStopped();
                });
                panel.add(btnView);
            }

            if (editAction != null) {
                JButton btnEdit = createButton("/img/edit.png");
                btnEdit.addActionListener(e -> {
                    editAction.actionPerformed(new java.awt.event.ActionEvent(this, currentRow, "edit"));
                    fireEditingStopped();
                });
                panel.add(btnEdit);
            }

            if (deleteAction != null) {
                JButton btnDelete = createButton("/img/eliminar.png");
                btnDelete.addActionListener(e -> {
                    deleteAction.actionPerformed(new java.awt.event.ActionEvent(this, currentRow, "delete"));
                    fireEditingStopped();
                });
                panel.add(btnDelete);
            }
        }

        private JButton createButton(String iconPath) {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(45, 30));

            loadButtonImage(btn, iconPath, 25, 25);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            return btn;
        }

        private void loadButtonImage(JButton button, String path, int width, int height) {
            try {
                if (getClass().getResource(path) != null) {
                    ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
                    Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(scaledImage));
                } else {
                    button.setText("...");
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}