package View;

import DAO.ConnectionMySQL;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class MainWindow extends JFrame {
    private ConnectionMySQL connection;

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

    public MainWindow() {
        setTitle("Sistema para Colmado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(1200, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        initializeWindow();

        setVisible(true);
    }

    private void initializeWindow() {
        this.connection = new ConnectionMySQL("localhost:3306", "root", "Brijo-0505", "Colmado");
        this.connection.connect();

        //Aqui agregaremos los controladores cuando los terminemos

        loadLabelImage(logoAppLabel, "/img/appLogo.png", 40, 40);
        loadButtonImage(btnInventory, "/img/inventario.png", 35, 35);
        loadButtonImage(btnSell, "/img/ventas.png", 35, 35);
        loadButtonImage(btnOrders, "/img/pedidos.png", 35, 35);

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
                    int id = (int) data[row][0];
                    String name = (String) data[row][1];

                    UpdateWindow updateWindow = new UpdateWindow(columns);
                    System.out.println("Editar producto ID: " + id);
                    updateWindow.setVisible(true);
                },
                e -> {
                    int row = e.getID();
                    String name = (String) data[row][1];

                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "¿Eliminar " + name + "?",
                            "Confirmar Eliminación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        System.out.println("Producto eliminado ID: " + data[row][0]);
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
                {2, "2023-01-22 11:15", "María García", "$320.50", "Tarjeta de crédito", ""},
                {3, "2023-01-22 14:20", "Pedro López", "$180.00", "Tarjeta de débito", ""},
                {4, "2023-01-23 09:45", "Ana Rodríguez", "$520.75", "Efectivo", ""}
        };

        CustomTableGenerator sellTable = new CustomTableGenerator(
                columns,
                data,
                e -> {
                    int row = e.getID();
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
                                    "Cliente: " + data[row][2],
                            "Confirmar Anulación",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        System.out.println("Factura anulada ID: " + data[row][0]);
                    }
                }
        );

        tablesContainer.setViewportView(sellTable.getTable());
        openAddWindow(btnAdd, "Nueva Venta", new String[]{"Cliente", "Productos", "Total"});
    }

    private void loadOrdersView() {
        String[] columns = {"ID", "Fecha", "Proveedor", "Total", "Estado", "Acciones"};
        Object[][] data = {
                {1, "2023-01-20", "Distribuidora Central", "$2,500.00", "Completado", ""},
                {2, "2023-01-21", "Alimentos del Norte", "$1,800.50", "Completado", ""},
                {3, "2023-01-22", "Productos Frescos SA", "$950.00", "Pendiente", ""},
                {4, "2023-01-23", "Bebidas Express", "$1,200.00", "En proceso", ""}
        };

        CustomTableGenerator ordersTable = new CustomTableGenerator(
                columns,
                data,
                e -> {
                    int row = e.getID();
                    JOptionPane.showMessageDialog(
                            this,
                            "Proveedor: " + data[row][2] + "\n" +
                                    "Fecha: " + data[row][1] + "\n" +
                                    "Total: " + data[row][3] + "\n" +
                                    "Estado: " + data[row][4],
                            "Detalles Pedido #" + data[row][0],
                            JOptionPane.INFORMATION_MESSAGE
                    );
                },
                e -> {
                    int row = e.getID();
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "¿Cancelar pedido #" + data[row][0] + "?",
                            "Confirmar Cancelación",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        System.out.println("Pedido cancelado ID: " + data[row][0]);
                    }
                }
        );

        tablesContainer.setViewportView(ordersTable.getTable());
        openAddWindow(btnAdd, "Nuevo Pedido", new String[]{"Proveedor", "Productos", "Estado"});
    }

    private void loadLabelImage(JLabel label, String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            System.err.println("Error loading label image: " + e.getMessage());
        }
    }

    private void loadButtonImage(JButton button, String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaled));

            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setIconTextGap(10);
            button.setBorderPainted(false);
        } catch (Exception e) {
            System.err.println("Error loading button image: " + e.getMessage());
        }
    }

    private void openAddWindow(JButton button, String title, String[] labels) {
        for (ActionListener l : button.getActionListeners()) {
            button.removeActionListener(l);
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

class ProgramExecute {
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
