package View;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel dashboardPanel;
    private JPanel searchPanel;
    private JTextField searchTextField;
    private JButton btnAdd;
    private JPanel topPanel;
    private JLabel logoAppLabel;
    private JButton btnOrders;
    private JButton btnSell;
    private JButton btnInventory;
    private JPanel accessBtnPanel;
    private JScrollPane tablesContainer;

    MainWindow() {
        setTitle("Sistema para Colmado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(950, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        inizializeWindow();

        setVisible(true);
    }

    private void inizializeWindow(){
        loadLabelImage(logoAppLabel, "/img/appLogo.png", 40, 40);
        loadButtonImage(btnInventory, "/img/inventario.png", 35,35);
        loadButtonImage(btnSell, "/img/ventas.png", 35,35);
        loadButtonImage(btnOrders, "/img/pedidos.png", 35,35);

        btnInventory.addActionListener(e -> selectMenu("inventory"));
        btnSell.addActionListener(e -> selectMenu("sell"));
        btnOrders.addActionListener(e -> selectMenu("orders"));

        selectMenu("inventory");
    }

    private void selectMenu(String option) {
        btnInventory.setSelected(false);
        btnSell.setSelected(false);
        btnOrders.setSelected(false);

        switch (option) {
            case "inventory":
                btnInventory.setSelected(true);
                loadInventoryView();
                break;

            case "sell":
                btnSell.setSelected(true);
                loadSellView();
                break;

            case "orders":
                btnOrders.setSelected(true);
                loadOrdersView();
                break;
        }
    }

    private void loadInventoryView() {
        String[] columns = {"ID", "Nombre", "Categoría", "Precio", "Stock", "Proveedor", "Fecha ingreso", "Acciones"};
        Object[][] data = {
                {1, "Arroz Cristal 5kg", "Alimentos", "$230.00", 20, "Proveedor A", "2023-01-22", ""},
                {2, "Aceite 1L", "Bebidas", "$150.00", 12, "Proveedor B", "2023-01-20", ""},
                {3, "Azúcar 2kg", "Alimentos", "$80.00", 4, "Proveedor C", "2023-01-01", ""},
                {4, "Leche en polvo", "Lácteos", "$350.00", 8, "Proveedor D", "2023-01-22", ""}
        };

        CustomTableGenerator inventoryTable = new CustomTableGenerator(
                columns,
                data,
                e -> {
                    int row = e.getID();
                    System.out.println("Editar producto ID: " + data[row][0] + " - " + data[row][1]);
                    JOptionPane.showMessageDialog(
                            this,
                            "Editando: " + data[row][1],
                            "Editar Producto",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                },
                e -> {
                    int row = e.getID();
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "¿Eliminar " + data[row][1] + "?",
                            "Confirmar Eliminación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        System.out.println("Eliminado producto ID: " + data[row][0]);
                        JOptionPane.showMessageDialog(
                                this,
                                "Producto eliminado correctamente",
                                "Eliminación Exitosa",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
        );

        tablesContainer.setViewportView(inventoryTable.getTable());
        openAddWindow(btnAdd, "Producto", new String[]{"Nombre", "Precio", "Stock"});
    }
    private void loadSellView() {
        String[] columns = {"ID", "Fecha", "Cliente", "Total", "Método Pago", "Acciones"};
        Object[][] data = {
                {1, "2023-01-22 10:30", "Juan Pérez", "$450.00", "Efectivo", ""},
                {2, "2023-01-22 11:15", "María García", "$320.50", "Tarjeta de credito", ""},
                {3, "2023-01-22 14:20", "Pedro López", "$180.00", "Tarjeta de debito", ""},
                {4, "2023-01-23 09:45", "Ana Rodríguez", "$520.75", "Efectivo", ""}
        };

        CustomTableGenerator sellTable = new CustomTableGenerator(
                columns,
                data,
                e -> {
                    int row = e.getID();
                    System.out.println("Ver detalles de venta ID: " + data[row][0]);
                    JOptionPane.showMessageDialog(
                            this,
                            "Cliente: " + data[row][2] + "\n" +
                                    "Fecha: " + data[row][1] + "\n" +
                                    "Total: " + data[row][3] + "\n" +
                                    "Método: " + data[row][4],
                            "Detalles de Venta #" + data[row][0],
                            JOptionPane.INFORMATION_MESSAGE
                    );
                },
                e -> {
                    int row = e.getID();
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "¿Anular factura #" + data[row][0] + "?\n" +
                                    "Cliente: " + data[row][2] + "\n" +
                                    "Total: " + data[row][3],
                            "Confirmar Anulación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        System.out.println("Factura anulada ID: " + data[row][0]);
                        JOptionPane.showMessageDialog(
                                this,
                                "Factura anulada correctamente",
                                "Anulación Exitosa",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
        );

        tablesContainer.setViewportView(sellTable.getTable());
        openAddWindow(btnAdd, "Nueva Venta", new String[]{"Cliente", "Productos", "Total"});
    }

    private void loadOrdersView() {
        String[] columns = {"ID", "Fecha", "Proveedor", "Total", "Estado", "Acciones"};
        Object[][] data = {
                {1, "2023-01-20 08:00", "Distribuidora Central", "$2,500.00", "Completado", ""},
                {2, "2023-01-21 09:30", "Alimentos del Norte", "$1,800.50", "Completado", ""},
                {3, "2023-01-22 10:15", "Productos Frescos SA", "$950.00", "Pendiente", ""},
                {4, "2023-01-23 11:00", "Bebidas Express", "$1,200.00", "En proceso", ""}
        };

        CustomTableGenerator ordersTable = new CustomTableGenerator(
                columns,
                data,
                e -> {
                    int row = e.getID();
                    System.out.println("Ver detalles de pedido ID: " + data[row][0]);
                    JOptionPane.showMessageDialog(
                            this,
                            "Proveedor: " + data[row][2] + "\n" +
                                    "Fecha: " + data[row][1] + "\n" +
                                    "Total: " + data[row][3] + "\n" +
                                    "Estado: " + data[row][4],
                            "Detalles de Pedido #" + data[row][0],
                            JOptionPane.INFORMATION_MESSAGE
                    );
                },
                e -> {
                    int row = e.getID();
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "¿Cancelar pedido #" + data[row][0] + "?\n" +
                                    "Proveedor: " + data[row][2] + "\n" +
                                    "Total: " + data[row][3],
                            "Confirmar Cancelación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        System.out.println("Pedido cancelado ID: " + data[row][0]);
                        JOptionPane.showMessageDialog(
                                this,
                                "Pedido cancelado correctamente",
                                "Cancelación Exitosa",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
        );

        tablesContainer.setViewportView(ordersTable.getTable());
        openAddWindow(btnAdd, "Nuevo Pedido", new String[]{"Proveedor", "Productos", "Estado"});
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

    private void openAddWindow(JButton button, String title, String[] labels) {
        for (ActionListener al : button.getActionListeners()) {
            button.removeActionListener(al);
        }

        button.addActionListener(e -> {
            button.setEnabled(false);
            AddWindow addWindow = new AddWindow(title, labels);

            addWindow.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    button.setEnabled(true);
                }
            });
        });
    }
}

class ProgramExecute{
    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 14);
            UIManager.put("Table.cellMargins", new Insets(8, 8, 8, 8));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(MainWindow::new);
    }
}