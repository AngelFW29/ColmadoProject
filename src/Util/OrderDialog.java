package Util;

import Controller.ProductController;
import Controller.SupplierCotroller;
import DAO.ProductDAO;
import DAO.SupplierDAO;
import Model.*;
import Service.PurchaseOrderService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDialog extends JDialog {
    private JComboBox<String> comboSuppliers;
    private JComboBox<Product> comboProducts;
    private JComboBox<PurchaseOrderStatus> comboStatus;
    private JTextField txtQuantity;
    private JTable cartTable;
    private JLabel lblTotal;
    private DefaultTableModel tableModel;

    // Lógica y Datos
    private List<CartItem> cart;
    private PurchaseOrderService purchaseOrderService;
    private ProductController productController;
    private SupplierCotroller supplierController;

    public OrderDialog(JFrame parent) {
        super(parent, "Nuevo Pedido a Proveedor", true);
        setSize(850, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Inicialización
        cart = new ArrayList<>();
        purchaseOrderService = new PurchaseOrderService();
        productController = new ProductController(new ProductDAO());
        supplierController = new SupplierCotroller(new SupplierDAO());

        initUI();
        loadData();
    }

    private void initUI() {
        // PANEL SUPERIOR (Proveedor y Estado)
        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Datos del Pedido"));

        comboSuppliers = new JComboBox<>();
        comboStatus = new JComboBox<>(PurchaseOrderStatus.values());

        topPanel.add(new JLabel("Proveedor:"));
        topPanel.add(comboSuppliers);
        topPanel.add(new JLabel("Estado Inicial:"));
        topPanel.add(comboStatus);

        add(topPanel, BorderLayout.NORTH);

        // PANEL CENTRAL (Selección de Productos)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Sub-panel inputs
        JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        comboProducts = new JComboBox<>();
        comboProducts.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) {
                    Product p = (Product) value;
                    setText(p.getName() + " (Stock Actual: " + p.getInventoryQuantity() + ")");
                }
                return this;
            }
        });

        // búsqueda rápida escribiendo la primera letra
        comboProducts.setKeySelectionManager((key, model) -> {
            for (int i = 0; i < model.getSize(); i++) {
                if (model.getElementAt(i).toString().toLowerCase().startsWith(String.valueOf(key).toLowerCase())) return i;
            }
            return -1;
        });

        txtQuantity = new JTextField(5);
        JButton btnAdd = new JButton("Agregar a la Lista");

        productPanel.add(new JLabel("Producto:"));
        productPanel.add(comboProducts);
        productPanel.add(new JLabel("Cantidad a Pedir:"));
        productPanel.add(txtQuantity);
        productPanel.add(btnAdd);

        // Tabla
        String[] cols = {"Producto", "Costo Unit. (Est.)", "Cantidad", "Subtotal"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(tableModel);

        centerPanel.add(productPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // PANEL INFERIOR (Confirmación)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total Estimado: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnConfirm = new JButton("GENERAR PEDIDO");
        btnConfirm.setBackground(new Color(0, 102, 204));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 14));

        bottomPanel.add(lblTotal);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(btnConfirm);

        add(bottomPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addToCart());
        btnConfirm.addActionListener(e -> confirmOrder());
    }

    private void loadData() {
        List<Supplier> suppliers = supplierController.getAllSuppliers();
        for (Supplier s : suppliers) {
            comboSuppliers.addItem(s.getName());
        }

        List<Product> products = productController.getAllProducts();
        for (Product p : products) {
            comboProducts.addItem(p);
        }
    }

    private void addToCart() {
        try {
            Product selectedProduct = (Product) comboProducts.getSelectedItem();
            String qtyText = txtQuantity.getText();

            if (selectedProduct == null) return;
            if (qtyText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese una cantidad.");
                return;
            }

            int quantity = Integer.parseInt(qtyText);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.");
                return;
            }

            cart.add(new CartItem(selectedProduct, quantity));

            updateCartTable();
            txtQuantity.setText("");
            txtQuantity.requestFocus();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida.", "Error", JOptionPane.WARNING_MESSAGE);
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
        lblTotal.setText(String.format("Total Estimado: $%.2f", total));
    }

    private void confirmOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La lista de pedido está vacía.");
            return;
        }

        String supplierName = (String) comboSuppliers.getSelectedItem();
        Supplier supplier = supplierController.getAllSuppliers().stream()
                .filter(s -> s.getName().equals(supplierName))
                .findFirst()
                .orElse(null);

        if (supplier == null) {
            JOptionPane.showMessageDialog(this, "Error al identificar al proveedor.");
            return;
        }

        PurchaseOrderStatus status = (PurchaseOrderStatus) comboStatus.getSelectedItem();

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Generar pedido a " + supplier.getName() + "?\nEstado: " + status,
                "Confirmar Pedido",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = purchaseOrderService.registrarPedido(supplier, cart, status);

                if (success) {
                    String msg = "Pedido registrado correctamente.";
                    if (status == PurchaseOrderStatus.RECIBIDO) {
                        msg += "\nEl inventario ha sido actualizado.";
                    }
                    JOptionPane.showMessageDialog(this, msg);
                    dispose();
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al guardar el pedido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}