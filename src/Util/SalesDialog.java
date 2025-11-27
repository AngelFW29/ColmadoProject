package View;

import Controller.ProductController;
import Controller.CustomerController;
import DAO.ProductDAO;
import DAO.CustomerDAO;
import Model.*;
import Service.InvoiceService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SalesDialog extends JDialog {
    private JComboBox<Customer> comboCustomers;
    private JComboBox<Product> comboProducts;
    private JComboBox<PaymentMethod> comboPayment;
    private JTextField txtQuantity;
    private JTable cartTable;
    private JLabel lblTotal;
    private DefaultTableModel tableModel;

    private List<CartItem> cart;
    private InvoiceService invoiceService;
    private ProductController productController;
    private CustomerController customerController;

    public SalesDialog(JFrame parent) {
        super(parent, "Nueva Venta", true);
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        cart = new ArrayList<>();
        invoiceService = new InvoiceService();
        productController = new ProductController(new ProductDAO());
        customerController = new CustomerController(new CustomerDAO());

        initUI();
        loadData();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Datos de Facturación"));

        comboCustomers = new JComboBox<>();
        comboPayment = new JComboBox<>(PaymentMethod.values());

        topPanel.add(new JLabel("Cliente:"));
        topPanel.add(comboCustomers);
        topPanel.add(new JLabel("Método de Pago:"));
        topPanel.add(comboPayment);

        add(topPanel, BorderLayout.NORTH);

        // --- PANEL CENTRAL (Agregar Productos y Tabla) ---
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Sub-panel para selección de producto
        JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboProducts = new JComboBox<>();
        // Personalizamos cómo se ve el producto en el combo
        comboProducts.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) {
                    Product p = (Product) value;
                    setText(p.getName() + " - Stock: " + p.getInventoryQuantity() + " - $" + p.getUnitPrice());
                }
                return this;
            }
        });

        txtQuantity = new JTextField(5);
        JButton btnAdd = new JButton("Agregar al Carrito");

        productPanel.add(new JLabel("Producto:"));
        productPanel.add(comboProducts);
        productPanel.add(new JLabel("Cant:"));
        productPanel.add(txtQuantity);
        productPanel.add(btnAdd);

        // Tabla de carrito
        String[] cols = {"Producto", "Precio Unit.", "Cantidad", "Subtotal"};
        tableModel = new DefaultTableModel(cols, 0);
        cartTable = new JTable(tableModel);

        centerPanel.add(productPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // --- PANEL INFERIOR (Total y Confirmar) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnConfirm = new JButton("CONFIRMAR VENTA");
        btnConfirm.setBackground(new Color(0, 153, 76));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 14));

        bottomPanel.add(lblTotal);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(btnConfirm);

        add(bottomPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addToCart());
        btnConfirm.addActionListener(e -> confirmSale());
    }

    private void loadData() {
        List<Customer> customers = customerController.getAllCustomers();
        for (Customer c : customers) comboCustomers.addItem(c);

        // Cargar Productos (Solo los que tienen stock > 0)
        List<Product> products = productController.getAllProducts();
        for (Product p : products) {
            if (p.getInventoryQuantity() > 0) {
                comboProducts.addItem(p);
            }
        }
    }

    private void addToCart() {
        try {
            Product selectedProduct = (Product) comboProducts.getSelectedItem();
            int quantity = Integer.parseInt(txtQuantity.getText());

            if (selectedProduct == null) return;

            if (quantity > selectedProduct.getInventoryQuantity()) {
                JOptionPane.showMessageDialog(this, "No hay suficiente stock. Disponible: " + selectedProduct.getInventoryQuantity());
                return;
            }


            cart.add(new CartItem(selectedProduct, quantity));

            updateCartTable();

            txtQuantity.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida.");
        }
    }

    private void updateCartTable() {
        tableModel.setRowCount(0);
        double total = 0;

        for (CartItem item : cart) {
            double subtotal = item.getSubTotal();
            total += subtotal;

            Object[] row = {
                    item.getProduct().getName(),
                    item.getProduct().getUnitPrice(),
                    item.getQuantity(),
                    String.format("$%.2f", subtotal)
            };
            tableModel.addRow(row);
        }
        lblTotal.setText(String.format("Total: $%.2f", total));
    }

    private void confirmSale() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.");
            return;
        }

        Customer customer = (Customer) comboCustomers.getSelectedItem();
        PaymentMethod method = (PaymentMethod) comboPayment.getSelectedItem();

        int confirm = JOptionPane.showConfirmDialog(this, "¿Procesar venta?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = invoiceService.registrarVenta(customer, cart, method);

                if (success) {
                    JOptionPane.showMessageDialog(this, "¡Venta registrada exitosamente!");
                    dispose();
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al procesar venta: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}