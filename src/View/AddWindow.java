package View;

import Controller.CustomerController;
import Controller.InventoryLogController;
import Controller.ProductController;
import Controller.SupplierCotroller;
import DAO.CustomerDAO;
import DAO.InventoryLogDAO;
import DAO.ProductDAO;
import DAO.SupplierDAO;
import Model.MovementType;
import Util.DynamicFormPanel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

public class AddWindow extends JFrame {

    private String title;

    private JPanel mainPanel;
    private JPanel titlePanel;
    private JPanel inputsPanel;
    private JPanel buttonPanel;
    private JLabel titleLabel;
    private JButton enviarButton;
    private JButton cancelarButton;
    private DynamicFormPanel formPanel;

    public AddWindow(String title, String[] labels) {
        this.title = title;

        String displayTitle = title.startsWith("Nuevo") ? title : "Nuevo " + title;
        titleLabel.setText(displayTitle);
        setTitle("Agregar - " + displayTitle);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(500, 450);
        setResizable(false);
        setLocationRelativeTo(null);

        inizializeWindow(labels);
        setVisible(true);
    }

    private void inizializeWindow(String[] labels) {
        formPanel = new DynamicFormPanel(labels);
        inputsPanel.setLayout(new BorderLayout());
        inputsPanel.add(formPanel, BorderLayout.CENTER);

        cancelarButton.addActionListener(e -> dispose());
        enviarButton.addActionListener(e -> onEnviar());
    }

    private void onEnviar() {
        Map<String, String> data = formPanel.getFormData();
        boolean exito = false;

        try {
            switch (title) {
                case "Producto":
                    ProductDAO productDAO = new ProductDAO();
                    ProductController productController = new ProductController(productDAO);

                    String name = data.get("Nombre");

                    int idCategory = Integer.parseInt(data.get("ID Categoría"));
                    Double unitPrice = Double.parseDouble(data.get("Precio"));
                    int inventoryQuantity = Integer.parseInt(data.get("Stock"));

                    String fechaTexto = data.get("Fecha de expiración");
                    LocalDate expirationDate = (fechaTexto != null && !fechaTexto.isEmpty())
                            ? LocalDate.parse(fechaTexto)
                            : null;

                    exito = productController.addProduct(name, idCategory, unitPrice, inventoryQuantity, expirationDate);
                    break;

                case "Proveedor":
                    SupplierDAO supplierDAO = new SupplierDAO();
                    SupplierCotroller supplierController = new SupplierCotroller(supplierDAO);

                    exito = supplierController.addSupplier(
                            data.get("Nombre"),
                            data.get("Dirección"),
                            data.get("Teléfono"),
                            data.get("RNC")
                    );
                    break;

                case "Cliente":
                    CustomerDAO customerDAO = new CustomerDAO();
                    CustomerController customerController = new CustomerController(customerDAO);

                    exito = customerController.addCustomer(
                            data.get("Nombre"),
                            data.get("Dirección"),
                            data.get("Teléfono"),
                            data.get("Cédula")
                    );
                    break;

                case "Inventario":
                    InventoryLogDAO inventoryLogDAO = new InventoryLogDAO();
                    InventoryLogController inventoryLogController = new InventoryLogController(inventoryLogDAO);

                    int idProducto = Integer.parseInt(data.get("ID Producto"));

                    String tipoTexto = data.get("Tipo de movimiento");
                    MovementType type = MovementType.valueOf(tipoTexto);

                    int cantidad = Integer.parseInt(data.get("Cantidad"));

                    exito = inventoryLogController.addLog(idProducto, type, cantidad);
                    break;
            }

            if (exito) {
                JOptionPane.showMessageDialog(this, title + " agregado correctamente.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar en base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor revise los campos numéricos.", "Error de Formato", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }
}