package View;

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
        this(columns, data, null, null);
    }

    public CustomTableGenerator(String[] columns, Object[][] data,
                                ActionListener editAction, ActionListener deleteAction) {
        model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == getColumnCount() - 1 && editAction != null;
            }
        };

        table = new JTable(model);
        configureAppearance();

        if (editAction != null && deleteAction != null) {
            int actionColumn = columns.length - 1;
            TableColumn col = table.getColumnModel().getColumn(actionColumn);
            col.setCellRenderer(new ButtonRenderer());
            col.setCellEditor(new ButtonEditor(editAction, deleteAction));
            col.setPreferredWidth(120);
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

    public DefaultTableModel getModel() {
        return model;
    }

    private void configureAppearance() {
        table.setRowHeight(40);
        table.setShowGrid(true);
    }

    static class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnEdit, btnDelete;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            btnEdit = createButton("");
            btnDelete = createButton("");

            loadButtonImage(btnEdit, "/img/edit.png", 25, 25);
            loadButtonImage(btnDelete, "/img/eliminar.png", 25, 25);

            add(btnEdit);
            add(btnDelete);
        }

        private JButton createButton(String text) {
            JButton btn = new JButton(text);
            btn.setPreferredSize(new Dimension(45, 30));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
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

    static class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private int currentRow;

        public ButtonEditor(ActionListener editAction, ActionListener deleteAction) {
            super(new JCheckBox());
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

            JButton btnEdit = createButton("Editar");
            JButton btnDelete = createButton("Eliminar");


            btnEdit.addActionListener(e -> {
                editAction.actionPerformed(new java.awt.event.ActionEvent(
                        this, currentRow, "edit"));
                fireEditingStopped();
            });

            btnDelete.addActionListener(e -> {
                deleteAction.actionPerformed(new java.awt.event.ActionEvent(
                        this, currentRow, "delete"));
                fireEditingStopped();
            });

            panel.add(btnEdit);
            panel.add(btnDelete);
        }

        private JButton createButton(String text) {
            JButton btn = new JButton(text);
            btn.setPreferredSize(new Dimension(45, 30));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            return btn;
        }


        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

    }
}