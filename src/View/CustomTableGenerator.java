package View;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CustomTableGenerator {
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;

    public CustomTableGenerator(String[] columns, Object[][] data) {
        this(columns, data, null, null);
    }

    /**
     * Constructor: creates table with columns, data and action buttons
     *
     * @param columns Column names (include "Acciones" at the end)
     * @param data Data matrix
     * @param editAction Action when pressing edit
     * @param deleteAction Action when pressing delete
     */
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
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.DARK_GRAY);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));

        table.setDefaultRenderer(Object.class, new AlternateRowRenderer());
        table.setSelectionBackground(new Color(184, 207, 229));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
    }

    static class AlternateRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 249, 249));
            }
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return c;
        }
    }

    static class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnEdit, btnDelete;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            btnEdit = createButton("✏️", new Color(66, 133, 244));
            btnDelete = createButton("🗑️", new Color(234, 67, 53));
            add(btnEdit);
            add(btnDelete);
        }

        private JButton createButton(String text, Color color) {
            JButton btn = new JButton(text);
            btn.setBackground(color);
            btn.setForeground(Color.WHITE);
            btn.setPreferredSize(new Dimension(45, 30));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 249, 249));
            return this;
        }
    }

    static class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private int currentRow;

        public ButtonEditor(ActionListener editAction, ActionListener deleteAction) {
            super(new JCheckBox());
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

            JButton btnEdit = createButton("✏️", new Color(66, 133, 244));
            JButton btnDelete = createButton("🗑️", new Color(234, 67, 53));

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

        private JButton createButton(String text, Color color) {
            JButton btn = new JButton(text);
            btn.setBackground(color);
            btn.setForeground(Color.WHITE);
            btn.setPreferredSize(new Dimension(45, 30));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            return btn;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 249, 249));
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

    }
}