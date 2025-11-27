package View;

import Controller.*;
import DAO.*;
import Model.*;
import Util.CustomCardGenerator;
import Util.CustomTableGenerator;
import Util.RowMapper;
import Util.StatsCard;
import com.formdev.flatlaf.FlatLightLaf;
import Util.SalesDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class MainWindow extends JFrame {

    // DAOs
    private ProductDAO productDAO;
    private CustomerDAO customerDAO;
    private SupplierDAO supplierDAO;
    private InventoryLogDAO inventoryLogDAO;
    private InvoiceDAO invoiceDAO;
    private InvoiceDetailsDAO invoiceDetailsDAO;

    // Controllers
    private ProductController productController;
    private CustomerController customerController;
    private SupplierCotroller supplierController;
    private InventoryLogController inventoryLogController;
    private InvoiceController invoiceController;

    // Data Table
    private Object[][] currentTableData;


    // UI Components
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel dashboardPanel;
    private JPanel searchPanel;
    private JTextField searchTextField;
    private JButton btnAdd;
    private JPanel topPanel;
    private JLabel logoAppLabel;
    private JButton btnOrders;
    private JButton btnSales;
    private JButton btnInventory;
    private JPanel accessBtnPanel;
    private JScrollPane tablesContainer;
    private JButton btnSearch;
    private JButton btnProducts;
    private JButton btnSuppliers;
    private JButton btnCustomers;
    private JPanel firstStatsPanel;
    private JPanel secondStatsPanel;
    private JPanel thirdStatsPanel;

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
        // Inicialización de Backend
        productDAO = new ProductDAO();
        customerDAO = new CustomerDAO();
        supplierDAO = new SupplierDAO();
        inventoryLogDAO = new InventoryLogDAO();
        invoiceDAO = new InvoiceDAO(invoiceDetailsDAO);


        productController = new ProductController(productDAO);
        customerController = new CustomerController(customerDAO);
        supplierController = new SupplierCotroller(supplierDAO);
        inventoryLogController = new InventoryLogController(inventoryLogDAO);
        invoiceController = new InvoiceController(invoiceDAO);

        // Carga de Recursos UI
        loadLabelImage(logoAppLabel, "/img/appLogo.png", 40, 40);
        loadButtonImage(btnInventory, "/img/inventario.png", 35, 35);
        loadButtonImage(btnSearch, "/img/lupa.png", 30, 30);
        loadButtonImage(btnSales, "/img/ventas.png", 35, 35);
        loadButtonImage(btnOrders, "/img/pedidos.png", 35, 35);
        loadButtonImage(btnProducts, "/img/producto.png", 35, 35);
        loadButtonImage(btnSuppliers, "/img/proveedor.png", 35, 35);
        loadButtonImage(btnCustomers, "/img/cliente.png", 35, 35);

        // Listeners de Navegación
        btnInventory.addActionListener(e -> selectMenu("inventory"));
        btnSales.addActionListener(e -> selectMenu("sell"));
        btnOrders.addActionListener(e -> selectMenu("orders"));
        btnProducts.addActionListener(e -> selectMenu("products"));
        btnSuppliers.addActionListener(e -> selectMenu("suppliers"));
        btnCustomers.addActionListener(e -> selectMenu("customers"));

        btnSearch.addActionListener(e -> loadProductsView());

        selectMenu("inventory");
    }

    private void selectMenu(String option) {
        btnInventory.setSelected(false);
        btnSales.setSelected(false);
        btnOrders.setSelected(false);
        btnProducts.setSelected(false);
        btnSuppliers.setSelected(false);
        btnCustomers.setSelected(false);

        int total = productController.getAllProducts().size();
        int lowStock = productDAO.findLowStock().size();
        Double totalSales = invoiceController.getTodaySales();

        updateStatPanel(firstStatsPanel, "Total Productos", String.valueOf(total));
        updateStatPanel(secondStatsPanel, "Stock Bajo", String.valueOf(lowStock));
        updateStatPanel(thirdStatsPanel, "Ventas Hoy", "$" + totalSales.toString());

        // Navegación entre ventanas
        switch (option) {
            case "inventory":
                btnInventory.setSelected(true);
                loadInventoryView();
                break;
            case "sell":
                btnSales.setSelected(true);
                loadSellView();
                break;
            case "orders":
                btnOrders.setSelected(true);
                loadOrdersView();
                break;
            case "products":
                btnProducts.setSelected(true);
                loadProductsView();
                break;
            case "suppliers":
                btnSuppliers.setSelected(true);
                loadSuppliersView();
                break;
            case "customers":
                btnCustomers.setSelected(true);
                loadCustomersView();
                break;
        }
    }

    // --- VISTAS
    private void loadInventoryView() {
        List<InventoryLog> logs = inventoryLogController.getAllLogs();

        currentTableData = updateTable(
                tablesContainer,
                logs,
                new String[]{"ID LOG", "ID Producto", "Tipo", "Cantidad", "Fecha", "Acciones"},

                l -> new Object[]{
                        l.getIdLog(),
                        l.getIdProduct(),
                        l.getMovementType(),
                        l.getQuantityChange(),
                        l.getMovementDate(),
                        ""
                },

                e -> { // Editar
                    int row = e.getID();
                    int idLog = (int) currentTableData[row][0];
                    String[] values = {
                            String.valueOf(currentTableData[row][1]),
                            String.valueOf(currentTableData[row][2]),
                            String.valueOf(currentTableData[row][3])
                    };
                    String[] labels = {"ID Producto", "Tipo de movimiento", "Cantidad"};
                    openUpdateDialog("Inventario", labels, values, idLog, this::loadInventoryView);
                },
                e -> { // Eliminar
                    int row = e.getID();
                    int idLog = (int) currentTableData[row][0];
                    confirmAndDelete("Log #" + idLog, () -> inventoryLogController.deleteLog(idLog), this::loadInventoryView);

                }
        );


        openAddWindow(btnAdd,
                "Inventario", new String[]{"ID Producto", "Tipo de movimiento", "Cantidad"},
                this::loadInventoryView);
    }

    private void loadProductsView() {
        String text = searchTextField.getText().trim();
        List<Product> products = text.isEmpty()
                ? productController.getAllProducts()
                : productController.getSearchProducts(text);

        currentTableData = updateTable(
                tablesContainer,
                products,
                new String[]{"ID", "Nombre", "Categoría", "Precio", "Stock", "Vencimiento", "Acciones"},

                p -> new Object[]{
                        p.getId(),
                        p.getName(),
                        p.getCategory(),
                        p.getUnitPrice(),
                        p.getInventoryQuantity(),
                        p.getExpirationDate() != null ? p.getExpirationDate().toString() : "N/A",
                        ""
                },

                e -> {
                    int row = e.getID();
                    int id = (int)currentTableData[row][0];

                    String[] values = {
                            String.valueOf(currentTableData[row][1]),
                            String.valueOf(currentTableData[row][2]),
                            String.valueOf(currentTableData[row][3]),
                            String.valueOf(currentTableData[row][4]),
                            String.valueOf(currentTableData[row][5])
                    };

                    String[] labels = {"Nombre", "ID Categoría", "Precio", "Stock", "Fecha de expiración"};
                    openUpdateDialog("Producto", labels, values, id, this::loadProductsView);
                },

                e -> {
                    int row = e.getID();
                    int id = (int) currentTableData[row][0];
                    String name = (String) currentTableData[row][1];

                    confirmAndDelete(name,
                            () -> productController.deleteProduct(id),
                            this::loadProductsView
                    );
                }
        );

        openAddWindow(btnAdd, "Producto",
                new String[]{"Nombre", "ID Categoría", "Precio", "Stock", "Fecha de expiración"},
                this::loadProductsView);
    }

    private void loadSuppliersView() {
        List<Supplier> list = supplierController.getAllSuppliers();
        CustomCardGenerator cards = new CustomCardGenerator(
                list,
                e -> { // Editar
                    int id = e.getID();
                    Supplier s = list.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
                    if(s != null) {
                        String[] values = { s.getName(), s.getAddress(), s.getPhone(), s.getFiscalIdentification() };
                        String[] labels = {"Nombre", "Dirección", "Teléfono", "RNC"};
                        openUpdateDialog("Proveedor", labels, values, id, this::loadSuppliersView);
                    }
                },
                e -> { // Eliminar
                    int id = e.getID();
                    confirmAndDelete("Proveedor #" + id, () -> supplierController.deleteSupplier(id), this::loadSuppliersView);
                }
        );
        tablesContainer.setViewportView(cards.getContainer());
        openAddWindow(btnAdd, "Proveedor", new String[]{"Nombre", "Dirección", "Teléfono", "RNC"}, this::loadSuppliersView);
    }

    private void loadCustomersView() {
        List<Customer> list = customerController.getAllCustomers();
        CustomCardGenerator cards = new CustomCardGenerator(
                list,
                e -> { // Editar
                    int id = e.getID();
                    Customer c = list.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
                    if(c != null) {
                        String[] values = { c.getName(), c.getAddress(), c.getPhone(), c.getFiscalIdentification() };
                        String[] labels = {"Nombre", "Dirección", "Teléfono", "Cédula"};
                        openUpdateDialog("Cliente", labels, values, id, this::loadCustomersView);
                    }
                },
                e -> { // Eliminar
                    int id = e.getID();
                    confirmAndDelete("Cliente #" + id, () -> customerController.deleteCustomer(id), this::loadCustomersView);
                }
        );
        tablesContainer.setViewportView(cards.getContainer());
        openAddWindow(btnAdd, "Cliente", new String[]{"Nombre", "Dirección", "Teléfono", "Cédula"}, this::loadCustomersView);
    }

    private void loadSellView() {
        for (ActionListener l : btnAdd.getActionListeners()) {
            btnAdd.removeActionListener(l);
        }

        btnAdd.addActionListener(e -> {
            SalesDialog dialog = new SalesDialog(this);
            dialog.setVisible(true);

            loadSellView();
        });

        List<Invoice> invoices = invoiceController.getAllInvoices();
        currentTableData = updateTable(
                tablesContainer,
                invoices,
                new String[]{"ID Factura", "Fecha", "ID Persona", "Metodo Pago", "Total", "Acciones"},

                i -> new Object[]{
                        i.getId(),
                        i.getDateTime(),
                        i.getCustomer().getId(),
                        i.getPaymentMethod(),
                        i.getTotal(),
                        ""
                },

                e -> { // Editar
                    int row = e.getID();
                    int idLog = (int) currentTableData[row][0];
                    String[] values = {
                            String.valueOf(currentTableData[row][1]),
                            String.valueOf(currentTableData[row][2]),
                            String.valueOf(currentTableData[row][3]),
                            String.valueOf(currentTableData[row][4]),
                    };
                    String[] labels = {"ID Producto", "Tipo de movimiento", "Cantidad"};
                    openUpdateDialog("Ventas", labels, values, idLog, this::loadSellView);
                },
                e -> { // Eliminar
                    int row = e.getID();
                    int idInvoice = (int) currentTableData[row][0];
                    confirmAndDelete("Log #" + idInvoice, () -> invoiceController.deleteInvoice(idInvoice), this::loadSellView);
                }
        );

    }

    private void loadOrdersView() {
        tablesContainer.setViewportView(new JPanel());
        openAddWindow(btnAdd, "Nuevo Pedido", new String[]{"Proveedor", "Productos", "Estado"}, () -> {});
    }

    // --- UTILS & HELPERS ---

    private void openAddWindow(JButton button, String title, String[] labels, Runnable onWindowClosed) {
        for (ActionListener l : button.getActionListeners()) button.removeActionListener(l);
        button.addActionListener(e -> {
            button.setEnabled(false);
            AddWindow w = new AddWindow(title, labels);
            w.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent ev) {
                    button.setEnabled(true);
                    if (onWindowClosed != null) onWindowClosed.run();
                }
            });
        });
    }

    private void openUpdateDialog(String title, String[] labels, String[] values, int idToUpdate, Runnable onWindowClosed) {
        UpdateDialog dialog = new UpdateDialog(title, labels, values, idToUpdate);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                if (onWindowClosed != null) onWindowClosed.run();
            }
        });
        dialog.setVisible(true);
    }

    private void confirmAndDelete(String itemName, BooleanSupplier deleteAction, Runnable reloadView) {
        int opt = JOptionPane.showConfirmDialog(this,
                "¿Eliminar " + itemName + "?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (opt == JOptionPane.YES_OPTION) {
            try {
                if (deleteAction.getAsBoolean()) {
                    JOptionPane.showMessageDialog(this, "Eliminado correctamente.");
                    reloadView.run();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error de integridad: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateStatPanel(JPanel target, String title, String value) {
        if (target != null) {
            target.removeAll();
            target.setLayout(new BorderLayout());
            target.setOpaque(false);
            target.add(new StatsCard(title, value), BorderLayout.CENTER);
            target.revalidate();
            target.repaint();
        }
    }

    private void loadLabelImage(JLabel label, String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
        } catch (Exception e) { System.err.println("Img Error: " + e.getMessage()); }
    }

    private void loadButtonImage(JButton button, String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaled));
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setIconTextGap(10);
            button.setBorderPainted(false);
        } catch (Exception e) { System.err.println("Img Error: " + e.getMessage()); }
    }

    private <U> Object[][] updateTable(
            JScrollPane container,
            List<U> items,
            String[] columns,
            RowMapper<U> mapper,
            ActionListener actionEditListener,
            ActionListener actionDeleteListener
    ) {

        Object[][] data = new Object[items.size()][columns.length];

        for (int i = 0; i < items.size(); i++) {
            data[i] = mapper.map(items.get(i));
        }

        CustomTableGenerator table = new CustomTableGenerator(
                columns,
                data,
                actionEditListener,
                actionDeleteListener
        );

        container.setViewportView(table.getTable());
        return data;
    }
}

class ProgramExecute {
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 14);
            UIManager.put("Table.cellMargins", new Insets(8, 8, 8, 8));
        } catch (Exception ex) { ex.printStackTrace(); }
        SwingUtilities.invokeLater(MainWindow::new);
    }
}