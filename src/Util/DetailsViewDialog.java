package Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class DetailsViewDialog extends JDialog {

    public DetailsViewDialog(JFrame parent, String title, Map<String, String> headerInfo, String[] columnNames, Object[][] data) {
        super(parent, title, true); // Modal true
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // PANEL SUPERIOR
        JPanel headerPanel = new JPanel(new GridLayout(0, 2, 10, 5)); // 2 columnas dinámicas
        headerPanel.setBorder(BorderFactory.createTitledBorder("Información General"));
        headerPanel.setBackground(new Color(245, 245, 245));

        for (Map.Entry<String, String> entry : headerInfo.entrySet()) {
            JLabel lblKey = new JLabel(entry.getKey() + ":");
            lblKey.setFont(new Font("Segoe UI", Font.BOLD, 12));

            JLabel lblValue = new JLabel(entry.getValue());
            lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            headerPanel.add(lblKey);
            headerPanel.add(lblValue);
        }

        add(headerPanel, BorderLayout.NORTH);

        // PANEL CENTRAL
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Detalle de Productos"));

        add(scrollPane, BorderLayout.CENTER);

        // PANEL INFERIOR
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Cerrar");
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnClose);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}