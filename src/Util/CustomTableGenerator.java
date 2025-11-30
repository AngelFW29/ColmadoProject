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
            col.setPreferredWidth(buttonsCount * 45 + 20);
        }

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    }

    public JScrollPane getScrollPane() { return scrollPane; }
    public JTable getTable() { return table; }

    private void configureAppearance() {
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    static class ButtonRenderer extends JPanel implements TableCellRenderer {

        public ButtonRenderer(boolean showView, boolean showEdit, boolean showDelete) {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);

            if (showView) {
                JButton btn = createButton("/img/ver.png");
                add(btn);
            }
            if (showEdit) {
                JButton btn = createButton("/img/edit.png");
                add(btn);
            }
            if (showDelete) {
                JButton btn = createButton("/img/eliminar.png");
                add(btn);
            }
        }

        private JButton createButton(String iconPath) {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(40, 30));
            loadButtonImage(btn, iconPath);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            return btn;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }

        private void loadButtonImage(JButton button, String path) {
            try {
                if (getClass().getResource(path) != null) {
                    ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
                    Image scaled = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(scaled));
                } else {
                    button.setText("O");
                }
            } catch (Exception e) { }
        }
    }

    static class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private int currentRow;

        public ButtonEditor(ActionListener viewAction, ActionListener editAction, ActionListener deleteAction) {
            super(new JCheckBox());
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

            // Lógica para el botón VER
            if (viewAction != null) {
                JButton btn = createButton("/img/ver.png");
                btn.addActionListener(e -> {
                    viewAction.actionPerformed(new java.awt.event.ActionEvent(this, currentRow, "view"));
                    fireEditingStopped();
                });
                panel.add(btn);
            }

            if (editAction != null) {
                JButton btn = createButton("/img/edit.png");
                btn.addActionListener(e -> {
                    editAction.actionPerformed(new java.awt.event.ActionEvent(this, currentRow, "edit"));
                    fireEditingStopped();
                });
                panel.add(btn);
            }

            if (deleteAction != null) {
                JButton btn = createButton("/img/eliminar.png");
                btn.addActionListener(e -> {
                    deleteAction.actionPerformed(new java.awt.event.ActionEvent(this, currentRow, "delete"));
                    fireEditingStopped();
                });
                panel.add(btn);
            }
        }

        private JButton createButton(String iconPath) {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(40, 30));
            loadButtonImage(btn, iconPath);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            return btn;
        }

        private void loadButtonImage(JButton button, String path) {
            try {
                if (getClass().getResource(path) != null) {
                    ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
                    Image scaled = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(scaled));
                } else {
                    button.setText("X");
                }
            } catch (Exception e) { }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return ""; }
    }
}