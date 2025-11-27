package View;

import Controller.*;
import DAO.*;
import Model.*;
import Util.DynamicFormPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

public class UpdateDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel inputPanel;

    private DynamicFormPanel formPanel;
    private String entityTitle;
    private int idToUpdate;

    public UpdateDialog(String title, String[] fields, String[] values, int idToUpdate) {
        this.entityTitle = title;
        this.idToUpdate = idToUpdate;

        setContentPane(contentPane);
        setModal(true);
        setTitle("Actualizar " + title);

        if (!(inputPanel.getLayout() instanceof BorderLayout)) {
            inputPanel.setLayout(new BorderLayout());
        }

        String[] formFields;
        String[] formValues;

        if (fields != null && fields.length > 0) {
            formFields = Arrays.copyOfRange(fields, 1, fields.length);
            if (values != null && values.length == fields.length) {
                formValues = Arrays.copyOfRange(values, 1, values.length);
            } else {
                formValues = new String[formFields.length];
            }
        } else {
            formFields = new String[]{};
            formValues = new String[]{};
        }

        formPanel = new DynamicFormPanel(formFields);
        formPanel.setFormValues(formFields, formValues);

        inputPanel.add(formPanel, BorderLayout.CENTER);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setSize(500, 450);
        setLocationRelativeTo(null);
    }

    private void onOK() {
        Map<String, String> newData = formPanel.getFormData();
        boolean success = false;

        try {
            switch (entityTitle) {
                case "Producto":
                    ProductDAO pDao = new ProductDAO();
                    ProductController pc = new ProductController(pDao);

                    Product originalProd = pDao.read(idToUpdate);

                    if (originalProd != null) {
                        String name = newData.containsKey("Nombre") ? newData.get("Nombre") : originalProd.getName();

                        int idCat = newData.containsKey("ID Categoría")
                                ? Integer.parseInt(newData.get("ID Categoría"))
                                : originalProd.getCategory();

                        double price = newData.containsKey("Precio")
                                ? Double.parseDouble(newData.get("Precio"))
                                : originalProd.getUnitPrice();

                        int stock = newData.containsKey("Stock")
                                ? Integer.parseInt(newData.get("Stock"))
                                : originalProd.getInventoryQuantity();

                        LocalDate expDate = originalProd.getExpirationDate();
                        if (newData.containsKey("Fecha de expiración")) {
                            String dateStr = newData.get("Fecha de expiración");
                            if (dateStr != null && !dateStr.isEmpty() && !dateStr.equals("N/A")) {
                                expDate = LocalDate.parse(dateStr);
                            }
                        }

                        success = pc.updateProduct(idToUpdate, name, idCat, price, stock, expDate);
                    }
                    break;

                case "Proveedor":
                    SupplierDAO sDao = new SupplierDAO();
                    SupplierCotroller sc = new SupplierCotroller(sDao);
                    Supplier originalSup = sDao.read(idToUpdate);

                    if (originalSup != null) {
                        String name = newData.containsKey("Nombre") ? newData.get("Nombre") : originalSup.getName();
                        String addr = newData.containsKey("Dirección") ? newData.get("Dirección") : originalSup.getAddress();
                        String phone = newData.containsKey("Teléfono") ? newData.get("Teléfono") : originalSup.getPhone();
                        String rnc = newData.containsKey("RNC") ? newData.get("RNC") : originalSup.getFiscalIdentification();

                        success = sc.updateSupplier(idToUpdate, name, addr, phone, rnc);
                    }
                    break;

                case "Cliente":
                    CustomerDAO cDao = new CustomerDAO();
                    CustomerController cc = new CustomerController(cDao);
                    Customer originalCust = cDao.read(idToUpdate);

                    if (originalCust != null) {
                        String name = newData.containsKey("Nombre") ? newData.get("Nombre") : originalCust.getName();
                        String addr = newData.containsKey("Dirección") ? newData.get("Dirección") : originalCust.getAddress();
                        String phone = newData.containsKey("Teléfono") ? newData.get("Teléfono") : originalCust.getPhone();
                        String cedula = newData.containsKey("Cédula") ? newData.get("Cédula") : originalCust.getFiscalIdentification();

                        success = cc.updateCustomer(idToUpdate, name, addr, phone, cedula);
                    }
                    break;

                case "Inventario":
                    InventoryLogDAO iDao = new InventoryLogDAO();
                    InventoryLogController ic = new InventoryLogController(iDao);
                    InventoryLog originalLog = iDao.read(idToUpdate);

                    if (originalLog != null) {
                        int idProd = newData.containsKey("ID Producto")
                                ? Integer.parseInt(newData.get("ID Producto"))
                                : originalLog.getIdProduct();

                        MovementType type = newData.containsKey("Tipo de movimiento")
                                ? MovementType.valueOf(newData.get("Tipo de movimiento"))
                                : originalLog.getMovementType();

                        int qty = newData.containsKey("Cantidad")
                                ? Integer.parseInt(newData.get("Cantidad"))
                                : originalLog.getQuantityChange();

                        LocalDateTime date = originalLog.getMovementDate();
                        success = ic.updateLog(idToUpdate, idProd, type, qty, date);
                    }
                    break;
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Actualización exitosa.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar o el registro ya no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error de formato numérico. Revise los campos.", "Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error crítico: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        dispose();
    }
}