package View;

import Controller.CustomerController;
import Controller.InventoryLogController;
import Controller.ProductController;
import Controller.SupplierCotroller;
import DAO.*;
import Model.Customer;
import Model.InventoryLog;
import Model.Product;
import Model.Supplier;
import Util.CustomCardGenerator;
import Util.CustomTableGenerator;
import Util.RowMapper;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class MainWindow extends JFrame {
    private ConnectionMySQL connection;

    //DAOs
    private ProductDAO productDAO;
    private CustomerDAO customerDAO;
    private SupplierDAO supplierDAO;
    private InventoryLogDAO inventoryLogDAO;

    //Controllers
    private ProductController productController;
    private CustomerController customerController;
    private SupplierCotroller supplierController;
    private InventoryLogController inventoryLogController;


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
    private JButton btnSearch;
    private JButton btnProducts;
    private JButton btnSuppliers;
    private JButton btnCustomers;

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

        // Inicializar DAOs
        productDAO = new ProductDAO();
        customerDAO = new CustomerDAO();
        supplierDAO = new SupplierDAO();
        inventoryLogDAO = new InventoryLogDAO();

        // Inicializar Controladores
        productController = new ProductController(productDAO);
        customerController = new CustomerController(customerDAO);
        supplierController = new SupplierCotroller(supplierDAO);
        inventoryLogController = new InventoryLogController(inventoryLogDAO);

        // Cargar Imágenes
        loadLabelImage(logoAppLabel, "/img/appLogo.png", 40, 40);
        loadButtonImage(btnInventory, "/img/inventario.png", 35, 35);
        loadButtonImage(btnSearch, "/img/lupa.png", 30, 30);
        loadButtonImage(btnSell, "/img/ventas.png", 35, 35);
        loadButtonImage(btnOrders, "/img/pedidos.png", 35, 35);
        loadButtonImage(btnProducts, "/img/producto.png", 35, 35);
        loadButtonImage(btnSuppliers, "/img/proveedor.png", 35, 35);
        loadButtonImage(btnCustomers, "/img/cliente.png", 35, 35);

        // Eventos de Menú
        btnInventory.addActionListener(e -> selectMenu("inventory"));
        btnSell.addActionListener(e -> selectMenu("sell"));
        btnOrders.addActionListener(e -> selectMenu("orders"));
        btnProducts.addActionListener(e -> selectMenu("products"));
        btnSuppliers.addActionListener(e -> selectMenu("suppliers"));
        btnCustomers.addActionListener(e -> selectMenu("customers"));
        selectMenu("inventory");
        //Test
        btnSearch.addActionListener(e -> loadProductsView());

    }

    private void selectMenu(String option) {
        btnInventory.setSelected(false);
        btnSell.setSelected(false);
        btnOrders.setSelected(false);
        btnProducts.setSelected(false);
        btnSuppliers.setSelected(false);
        btnCustomers.setSelected(false);

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


    private void loadInventoryView() {
        String[] columns = {"ID LOG", "ID Producto", "Tipo", "Cantidad", "Fecha", "Acciones"};
        List<InventoryLog> inventoryLogs = inventoryLogController.getAllLogs();

        Object[][] data = new Object[inventoryLogs.size()][6];

        for (int i = 0; i < inventoryLogs.size(); i++) {
            InventoryLog log = inventoryLogs.get(i);
            data[i][0] = log.getIdLog();
            data[i][1] = log.getIdProduct();
            data[i][2] = log.getMovementType();
            data[i][3] = log.getQuantityChange();
            data[i][4] = log.getMovementDate().toString();
            data[i][5] = "";
        }

        CustomTableGenerator inventoryTable = new CustomTableGenerator(
                columns,
                data,
                e -> {
                },
                e -> {
                }
        );

        tablesContainer.setViewportView(inventoryTable.getTable());

        openAddWindow(btnAdd, "Inventario", new String[]{"ID Producto", "Tipo de movimiento", "Cantidad"}, this::loadInventoryView);
    }

    private void loadProductsView() {
        String text = searchTextField.getText().trim();
        List<Product> products = text.isEmpty()
                ? productController.getAllProducts()
                : productController.getSearchProducts(text);

        Object[][] data = new Object[products.size()][7];

        updateTable(tablesContainer, products,
                new String[]{"ID", "Nombre", "Categoría", "Precio", "Stock", "Vencimiento", "Acciones"},
                p -> new Object[]{
                        p.getId(),
                        p.getName(),
                        p.getCategory(),
                        p.getUnitPrice(),
                        p.getInventoryQuantity(),
                        p.getExpirationDate() != null ? p.getExpirationDate().toString() : "N/A",
                        ""
                });
        openAddWindow(btnAdd, "Producto", new String[]{"Nombre", "ID Categoría", "Precio", "Stock", "Fecha de expiración"}, this::loadProductsView);
    }

    private void loadSuppliersView() {
        List<Supplier> suppliers = supplierController.getAllSuppliers();

        CustomCardGenerator cards = new CustomCardGenerator(
                suppliers,
                e -> System.out.println("Editar ID: " + e.getID()),
                e -> { /* Lógica eliminar */ }
        );

        tablesContainer.setViewportView(cards.getContainer());

        openAddWindow(btnAdd, "Proveedor", new String[]{"Nombre", "Dirección", "Teléfono", "RNC"}, this::loadSuppliersView);
    }

    private void loadCustomersView() {
        List<Customer> customers = customerController.getAllCustomers();

        CustomCardGenerator cards = new CustomCardGenerator(
                customers,
                e -> System.out.println("Editar ID: " + e.getID()),
                e -> System.out.println("Eliminar ID: " + e.getID())
        );

        tablesContainer.setViewportView(cards.getContainer());

        openAddWindow(btnAdd, "Cliente", new String[]{"Nombre", "Dirección", "Teléfono", "Cédula"}, this::loadCustomersView);
    }

    private void loadSellView() {
        tablesContainer.setViewportView(new JPanel());
        openAddWindow(btnAdd, "Nueva Venta", new String[]{"Cliente", "Productos", "Total", "Método Pago"}, () -> {
        });
    }

    private void loadOrdersView() {
        tablesContainer.setViewportView(new JPanel());
        openAddWindow(btnAdd, "Nuevo Pedido", new String[]{"Proveedor", "Productos", "Estado"}, () -> {
        });
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

    private void openAddWindow(JButton button, String title, String[] labels, Runnable onWindowClosed) {
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
                    if (onWindowClosed != null) {
                        onWindowClosed.run();
                    }
                }
            });
        });
    }

    private <U> void updateTable(JScrollPane container, List<U> items, String[] columns, RowMapper<U> mapper) {
        Object[][] data = new Object[items.size()][columns.length];

        for (int i = 0; i < items.size(); i++) {
            data[i] = mapper.map(items.get(i));
        }

        CustomTableGenerator table = new CustomTableGenerator(
                columns,
                data,
                e -> {
                },
                e -> {
                }
        );

        container.setViewportView(table.getTable());
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