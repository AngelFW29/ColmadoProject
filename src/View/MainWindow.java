package View;

import Controller.*;
import DAO.*;
import Model.*;
import Util.*;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class MainWindow extends JFrame {
    // --- Controllers ---
    private ProductController productController;
    private CustomerController customerController;
    private SupplierCotroller supplierController;
    private InventoryLogController inventoryLogController;
    private InvoiceController invoiceController;
    private PurchaseOrderController purchaseOrderController;

    // --- Data & UI ---
    private Object[][] currentTableData;
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

        // 2. Inicializar Controladores
        productController = new ProductController(new ProductDAO());
        customerController = new CustomerController(new CustomerDAO());
        supplierController = new SupplierCotroller(new SupplierDAO());
        inventoryLogController = new InventoryLogController(new InventoryLogDAO());
        invoiceController = new InvoiceController(new  InvoiceDAO(new InvoiceDetailsDAO()));
        purchaseOrderController = new PurchaseOrderController(new PurchaseOrderDAO());

        // 3. Cargar Imágenes
        loadLabelImage(logoAppLabel, "/img/appLogo.png", 40, 40);
        loadButtonImage(btnInventory, "/img/inventario.png", 35, 35);
        loadButtonImage(btnSearch, "/img/lupa.png", 30, 30);
        loadButtonImage(btnSales, "/img/ventas.png", 35, 35);
        loadButtonImage(btnOrders, "/img/pedidos.png", 35, 35);
        loadButtonImage(btnProducts, "/img/producto.png", 35, 35);
        loadButtonImage(btnSuppliers, "/img/proveedor.png", 35, 35);
        loadButtonImage(btnCustomers, "/img/cliente.png", 35, 35);

        // 4. Listeners de Menú
        btnInventory.addActionListener(e -> selectMenu("inventory"));
        btnSales.addActionListener(e -> selectMenu("sell"));
        btnOrders.addActionListener(e -> selectMenu("orders"));
        btnProducts.addActionListener(e -> selectMenu("products"));
        btnSuppliers.addActionListener(e -> selectMenu("suppliers"));
        btnCustomers.addActionListener(e -> selectMenu("customers"));


        selectMenu("inventory");
    }

    private void selectMenu(String option) {
        // Reset visual
        btnInventory.setSelected(false);
        btnSales.setSelected(false);
        btnOrders.setSelected(false);
        btnProducts.setSelected(false);
        btnSuppliers.setSelected(false);
        btnCustomers.setSelected(false);


        //Reset Buffer
        searchTextField.setText("");

        firstStatsPanel.setVisible(true);
        secondStatsPanel.setVisible(true);
        thirdStatsPanel.setVisible(true);

        switch (option) {
            case "inventory":
                btnInventory.setSelected(true);
                loadInventoryView();
                break;
            case "sell":
                btnSales.setSelected(true);
                loadSalesView();
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

    // --- VISTAS -----------------------------------------------------------

    private void loadInventoryView() {
        // Actualizar Stats  - Inventory
        int totalEntries = inventoryLogController.getTotalEntries();
        int totalExits = inventoryLogController.getTotalExits();
        int totalMovement = inventoryLogController.getTotalMovements();

        updateStatPanel(firstStatsPanel, "Total de entradas", String.valueOf(totalEntries));
        updateStatPanel(secondStatsPanel, "Total de salidas", String.valueOf(totalExits));
        updateStatPanel(thirdStatsPanel, "Total de movimientos", String.valueOf(totalMovement));

        for (ActionListener l : btnSearch.getActionListeners()) btnSearch.removeActionListener(l);

        btnSearch.addActionListener(e -> {
            String text = searchTextField.getText().trim();
            List<InventoryLog> logs = text.isEmpty() ? inventoryLogController.getAllLogs() : inventoryLogController.getSearchInventories(text);
            renderInventoryTable(logs);
        });

        renderInventoryTable(inventoryLogController.getAllLogs());
        openAddWindow(btnAdd, "Inventario", new String[]{"ID Producto", "Tipo de movimiento", "Cantidad"}, this::loadInventoryView);
    }
    private void renderInventoryTable(List<InventoryLog> logs) {
        currentTableData = updateTable(
                tablesContainer, logs,
                new String[]{"ID LOG", "ID Producto", "Tipo", "Cantidad", "Fecha", "Acciones"},
                l -> new Object[]{
                        l.getIdLog(), l.getIdProduct(), l.getMovementType(), l.getQuantityChange(), l.getMovementDate(), ""
                },
                null, // Ver
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
    }

    private void loadProductsView() {
        // Actualizar Stats  - Products
        int totalProducts = productController.getTotalProducts();
        int totalLowStockProducts = productController.getLowStockCount();

        updateStatPanel(firstStatsPanel, "Total de productos", String.valueOf(totalProducts));
        updateStatPanel(secondStatsPanel, "Productos con stock bajo", String.valueOf(totalLowStockProducts));
        thirdStatsPanel.setVisible(false);

        for (ActionListener l : btnSearch.getActionListeners()) btnSearch.removeActionListener(l);

        btnSearch.addActionListener(e -> {
            String text = searchTextField.getText().trim();
            List<Product> products = text.isEmpty() ? productController.getAllProducts() : productController.getSearchProducts(text);
            renderProductTable(products);
        });

        renderProductTable(productController.getAllProducts());

        openAddWindow(btnAdd, "Producto", new String[]{"Nombre", "Categoría", "Precio", "Fecha de expiración"}, this::loadProductsView);
    }
    private void renderProductTable(List<Product> products) {
        CategoryController categoryController = new CategoryController(new CategoryProductDAO());

        currentTableData = updateTable(
                tablesContainer, products,
                new String[]{"ID", "Nombre", "Categoría", "Precio", "Stock", "Vencimiento", "Acciones"},
                p -> new Object[]{
                        p.getId(),
                        p.getName(),
                        categoryController.getCategory(p.getCategory()).getNameCategory(),
                        p.getUnitPrice(),
                        p.getInventoryQuantity(),
                        p.getExpirationDate() != null ? p.getExpirationDate().toString() : "N/A", ""
                },
                null, // Ver
                e -> { // Editar
                    int row = e.getID();
                    int id = (int) currentTableData[row][0];
                    String[] values = {
                            String.valueOf(currentTableData[row][1]), // Nombre
                            String.valueOf(currentTableData[row][2]), // Cat
                            String.valueOf(currentTableData[row][3]), // Precio
                            String.valueOf(currentTableData[row][5])  // Fecha
                    };
                    String[] labels = {"Nombre", "Categoría", "Precio", "Fecha de expiración"};
                    openUpdateDialog("Producto", labels, values, id, this::loadProductsView);
                },
                e -> { // Eliminar
                    int row = e.getID();
                    int id = (int) currentTableData[row][0];
                    String name = (String) currentTableData[row][1];
                    confirmAndDelete(name, () -> productController.deleteProduct(id), this::loadProductsView);
                }
        );
    }

    private void loadSuppliersView() {
        // Actualizar Stats  - Suppliers
        int totalsuppliers = supplierController.getCountSuppliers();
//        int totalNewSuppliers = supplierController.getNewSuppliers();
//        updateStatPanel(secondStatsPanel, "Nuevos Proveedores", String.valueOf(totalNewSuppliers));

        updateStatPanel(firstStatsPanel, "Total de Proveedores", String.valueOf(totalsuppliers));
        secondStatsPanel.setVisible(false);
        thirdStatsPanel.setVisible(false);

        for (ActionListener l : btnSearch.getActionListeners()) btnSearch.removeActionListener(l);

        btnSearch.addActionListener(e -> {
            String text = searchTextField.getText().trim();
            List<Supplier> list = text.isEmpty() ? supplierController.getAllSuppliers() : supplierController.getSearchSuppliers(text);
            renderSupplierCards(list);
        });

        renderSupplierCards(supplierController.getAllSuppliers());
        openAddWindow(btnAdd, "Proveedor", new String[]{"Nombre", "Dirección", "Teléfono", "RNC"}, this::loadSuppliersView);
    }
    private void renderSupplierCards(List<Supplier> list) {
        CustomCardGenerator cards = new CustomCardGenerator(
                list,
                e -> { // Editar
                    int id = e.getID();
                    Supplier s = list.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
                    if (s != null) {
                        String[] values = {s.getName(), s.getAddress(), s.getPhone(), s.getFiscalIdentification()};
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
    }

    private void loadCustomersView() {
        // Actualizar Stats  - Customers
        int totalCustomers = customerController.getCountCustomers();
//        int totalNewCustomers = customerController.getNewCustomers();
//        updateStatPanel(secondStatsPanel, "Nuevos Clientes", String.valueOf(totalNewCustomers));

        updateStatPanel(firstStatsPanel, "Total de Clientes", String.valueOf(totalCustomers));
        secondStatsPanel.setVisible(false);
        thirdStatsPanel.setVisible(false);

        for (ActionListener l : btnSearch.getActionListeners()) btnSearch.removeActionListener(l);

        btnSearch.addActionListener(e -> {
            String text = searchTextField.getText().trim();
            List<Customer> list = text.isEmpty() ? customerController.getAllCustomers() : customerController.getSearchCustomers(text);
            renderCustomerCards(list);
        });

        renderCustomerCards(customerController.getAllCustomers());
        openAddWindow(btnAdd, "Cliente", new String[]{"Nombre", "Dirección", "Teléfono", "Cédula"}, this::loadCustomersView);
    }
    private void renderCustomerCards(List<Customer> list) {
        CustomCardGenerator cards = new CustomCardGenerator(
                list,
                e -> { // Editar
                    int id = e.getID();
                    Customer c = list.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
                    if (c != null) {
                        String[] values = {c.getName(), c.getAddress(), c.getPhone(), c.getFiscalIdentification()};
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
    }

    private void loadSalesView() {
        // Actualizar Stats  - Invoice
        int total = productController.getAllProducts().size();
        int sales = invoiceController.getCountInvoicesToday();
        Double totalSales = invoiceController.getTodaySales();

        updateStatPanel(firstStatsPanel, "Total Productos", String.valueOf(total));
        updateStatPanel(secondStatsPanel, "Ventas realizadas", String.valueOf(sales));
        updateStatPanel(thirdStatsPanel, "Ganancias de Hoy", "$" + (totalSales != null ? totalSales : "0.0"));

        for (ActionListener l : btnSearch.getActionListeners()) btnSearch.removeActionListener(l);
        btnSearch.addActionListener(e -> {
            String text = searchTextField.getText().trim();
            List<Invoice> invoices = text.isEmpty() ? invoiceController.getAllInvoices() : invoiceController.getSearchInvoice(text);
            renderSalesTable(invoices);
        });

        for (ActionListener l : btnAdd.getActionListeners()) btnAdd.removeActionListener(l);
        btnAdd.addActionListener(e -> {
            SalesDialog dialog = new SalesDialog(this);
            dialog.setVisible(true);
            loadSalesView();
        });

        renderSalesTable(invoiceController.getAllInvoices());
    }
    private void renderSalesTable(List<Invoice> invoices) {
        currentTableData = updateTable(
                tablesContainer, invoices,
                new String[]{"ID Factura", "Fecha", "ID Persona", "Metodo Pago", "Total", "Acciones"},
                i -> new Object[]{
                        i.getId(), i.getDateTime(), i.getCustomer().getId(), i.getPaymentMethod(), i.getTotal(), ""
                },
                e -> { // VER DETALLES
                    int row = e.getID();
                    int idInvoice = (int) currentTableData[row][0];
                    Invoice invoice = invoiceController.getInvoiceById(idInvoice);

                    if (invoice != null) {
                        Map<String, String> header = new LinkedHashMap<>();
                        header.put("Nro Factura", String.valueOf(invoice.getId()));
                        header.put("Fecha", invoice.getDateTime().toString());
                        header.put("Cliente", invoice.getCustomer().getName());
                        header.put("Método Pago", invoice.getPaymentMethod().toString());
                        header.put("Total", String.format("$%.2f", invoice.getTotal()));

                        String[] cols = {"Producto", "Cantidad", "Precio Unit.", "Subtotal"};
                        Object[][] detailsData = new Object[invoice.getItems().size()][4];
                        for (int k = 0; k < invoice.getItems().size(); k++) {
                            InvoiceDetails item = invoice.getItems().get(k);
                            detailsData[k][0] = item.getProduct().getName();
                            detailsData[k][1] = item.getQuantity();
                            detailsData[k][2] = String.format("$%.2f", item.getUnitPrice());
                            detailsData[k][3] = String.format("$%.2f", item.getSubtTotal());
                        }
                        new DetailsViewDialog(this, "Detalle de Venta", header, cols, detailsData).setVisible(true);
                    }
                },
                null, // Editar desactivado
                e -> { // Eliminar
                    int row = e.getID();
                    int idInvoice = (int) currentTableData[row][0];
                    confirmAndDelete("Factura #" + idInvoice, () -> invoiceController.deleteInvoice(idInvoice), this::loadSalesView);
                }
        );
    }

    private void loadOrdersView() {
        // Actualizar Stats  - Orders
        int pendingOrders =  purchaseOrderController.getPendingOrder();
        int recievedOrders = purchaseOrderController.getReceivedOrder();
        int totalOrders = purchaseOrderController.getCountOrder();

        updateStatPanel(firstStatsPanel, "Total de ordenes", String.valueOf(totalOrders));
        updateStatPanel(secondStatsPanel, "Ordenes pendientes", String.valueOf(pendingOrders));
        updateStatPanel(thirdStatsPanel, "Ordenes recibidas", String.valueOf(recievedOrders));

        for (ActionListener l : btnSearch.getActionListeners()) btnSearch.removeActionListener(l);
        btnSearch.addActionListener(e -> {
            String text = searchTextField.getText().trim();
            List<PurchaseOrder> orders = text.isEmpty() ? purchaseOrderController.getAllPurchaseOrders() : purchaseOrderController.getPurchase(text);
            renderOrdersTable(orders);
        });
        for (ActionListener l : btnAdd.getActionListeners()) btnAdd.removeActionListener(l);
        btnAdd.addActionListener(e -> {
            OrderDialog dialog = new OrderDialog(this);
            dialog.setVisible(true);
            loadOrdersView();
        });

        renderOrdersTable(purchaseOrderController.getAllPurchaseOrders());
    }
    private void renderOrdersTable(List<PurchaseOrder> purchaseOrders) {
        currentTableData = updateTable(
                tablesContainer, purchaseOrders,
                new String[]{"ID Pedido", "Proveedor", "Fecha", "Estado", "Total", "Acciones"},
                p -> new Object[]{
                        p.getIdPurchaseOrder(), p.getSupplier().getName(), p.getOrderDate(), p.getStatus(), p.getTotal(), ""
                },
                e -> { // VER DETALLES
                    int row = e.getID();
                    int idOrder = (int) currentTableData[row][0];
                    PurchaseOrder order = purchaseOrderController.getPurchaseOrderById(idOrder);

                    if (order != null) {
                        Map<String, String> header = new LinkedHashMap<>();
                        header.put("Nro Pedido", String.valueOf(order.getIdPurchaseOrder()));
                        header.put("Fecha", order.getOrderDate().toString());
                        header.put("Proveedor", order.getSupplier().getName());
                        header.put("Estado", order.getStatus().toString());
                        header.put("Total", String.format("$%.2f", order.getTotal()));

                        String[] cols = {"Producto", "Cantidad", "Costo Unit.", "Subtotal"};
                        Object[][] detailsData = new Object[order.getDetails().size()][4];
                        for (int k = 0; k < order.getDetails().size(); k++) {
                            PurchaseOrderDetails item = order.getDetails().get(k);
                            detailsData[k][0] = item.getProduct().getName();
                            detailsData[k][1] = item.getQuantity();
                            detailsData[k][2] = String.format("$%.2f", item.getUnitCost());
                            detailsData[k][3] = String.format("$%.2f", item.getSubTotal());
                        }
                        new DetailsViewDialog(this, "Detalle de Pedido", header, cols, detailsData).setVisible(true);
                    }
                },
                null, // Editar desactivado
                e -> { // Eliminar
                    int row = e.getID();
                    int idOrder = (int) currentTableData[row][0];
                    confirmAndDelete("Pedido #" + idOrder, () -> purchaseOrderController.deletePurchaseOrder(idOrder), this::loadOrdersView);
                }
        );
    }

    // --- UTILS & HELPERS ---

    public void openAddWindow(JButton button, String title, String[] labels, Runnable onWindowClosed) {
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

    public void openUpdateDialog(String title, String[] labels, String[] values, int idToUpdate, Runnable onWindowClosed) {
        UpdateDialog dialog = new UpdateDialog(title, labels, values, idToUpdate);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                if (onWindowClosed != null) onWindowClosed.run();
            }
        });
        dialog.setVisible(true);
    }

    public void confirmAndDelete(String itemName, BooleanSupplier deleteAction, Runnable reloadView) {
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

    public void updateStatPanel(JPanel target, String title, String value) {
        if (target != null) {
            target.removeAll();
            target.setLayout(new BorderLayout());
            target.setOpaque(false);
            target.add(new StatsCard(title, value), BorderLayout.CENTER);
            target.revalidate();
            target.repaint();
        }
    }

    public void loadLabelImage(JLabel label, String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            System.err.println("Img Error: " + e.getMessage());
        }
    }

    public void loadButtonImage(JButton button, String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
            Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaled));
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setIconTextGap(10);
            button.setBorderPainted(false);
        } catch (Exception e) {
            System.err.println("Img Error: " + e.getMessage());
        }
    }

    public  <U> Object[][] updateTable(
            JScrollPane container,
            List<U> items,
            String[] columns,
            RowMapper<U> mapper,
            ActionListener viewListener,
            ActionListener editListener,
            ActionListener deleteListener
    ) {
        Object[][] data = new Object[items.size()][columns.length];
        for (int i = 0; i < items.size(); i++) {
            data[i] = mapper.map(items.get(i));
        }
        CustomTableGenerator table = new CustomTableGenerator(columns, data, viewListener, editListener, deleteListener);
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(MainWindow::new);
    }
}