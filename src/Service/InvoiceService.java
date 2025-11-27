package Service;

import DAO.*;
import Model.*;
import java.util.List;

public class InvoiceService {

    private final InvoiceDAO invoiceDAO;
    private final InvoiceDetailsDAO invoiceDetailDAO;
    private final ProductDAO productDAO;
    private final InventoryLogDAO inventoryLogDAO;

    public InvoiceService() {
        this.invoiceDetailDAO = new InvoiceDetailsDAO();
        this.invoiceDAO = new InvoiceDAO(this.invoiceDetailDAO);
        this.productDAO = new ProductDAO();
        this.inventoryLogDAO = new InventoryLogDAO();
    }

    public boolean registrarVenta(Customer customer, List<CartItem> carrito, PaymentMethod paymentMethod) throws Exception {

        for (CartItem item : carrito) {
            Product p = item.getProduct();
            if (p.getInventoryQuantity() < item.getQuantity()) {
                throw new Exception("Stock insuficiente para: " + p.getName() +
                        ". Disponible: " + p.getInventoryQuantity());
            }
        }

        Invoice invoice = new Invoice(customer, paymentMethod);

        for (CartItem item : carrito) {
            InvoiceDetails detail = new InvoiceDetails(
                    invoice,
                    item.getProduct(),
                    item.getQuantity(),
                    item.getProduct().getUnitPrice()
            );
            invoice.addItem(detail);
        }

        boolean invoiceSaved = invoiceDAO.create(invoice);
        if (!invoiceSaved) {
            throw new Exception("Error al guardar la cabecera de la factura.");
        }

        int generatedInvoiceId = invoiceDAO.getLastInsertedId();

        invoice.setId(generatedInvoiceId);

        for (CartItem item : carrito) {
            Product p = item.getProduct();
            int qty = item.getQuantity();

            InvoiceDetails detailDB = new InvoiceDetails(
                    invoice,
                    p,
                    qty,
                    p.getUnitPrice()
            );

            invoiceDetailDAO.create(detailDB);

            // B) Descontar Inventario
            int nuevoStock = p.getInventoryQuantity() - qty;
            p.setInventoryQuantity(nuevoStock);
            productDAO.update(p);

            InventoryLog log = new InventoryLog(
                    p.getId(),
                    MovementType.Venta,
                    -qty
            );
            inventoryLogDAO.create(log);
        }

        return true;
    }
}