package Controller;

import DAO.InvoiceDAO;
import DAO.InvoiceDetailsDAO;
import Model.Invoice;
import Model.InvoiceDetails;
import Model.Product;

public class InvoiceDetailsController {
    private final InvoiceDetailsDAO invoiceDetailsDAO;
    private final InvoiceDAO invoiceDAO;

    public InvoiceDetailsController(InvoiceDetailsDAO invoiceDetailsDAO, InvoiceDAO invoiceDAO) {
        this.invoiceDetailsDAO = invoiceDetailsDAO;
        this.invoiceDAO = invoiceDAO;
    }

    public boolean addItem(Invoice invoice, Product product, int quantity, double unitPrice) {
        if (invoice == null || product == null) throw new IllegalArgumentException
                ("Invoice and Product are required.");

        if (invoice.getIdInvoice() == 0) {
            boolean created = invoiceDAO.create(invoice);
            if (!created) return false;
        }

        InvoiceDetails detail = new InvoiceDetails(invoice, product, quantity, unitPrice);
        if (!invoiceDetailsDAO.create(detail)) return false;

        invoice.addItem(detail);
        invoice.calculateTotal();
        return invoiceDAO.update(invoice);
    }

    public boolean updateItem(InvoiceDetails item, int quantity, double unitPrice) {
        if (item == null) throw new IllegalArgumentException("Item is required.");

        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);

        if (!invoiceDetailsDAO.update(item)) return false;

        Invoice invoice = item.getInvoice();
        if (invoice != null) {
            invoice.calculateTotal();
            return invoiceDAO.update(invoice);
        }

        return true;
    }

    public boolean deleteItem(Invoice invoice, InvoiceDetails item) {
        if (invoice == null || item == null) throw new IllegalArgumentException("Invoice and Item are required.");

        if (!invoiceDetailsDAO.delete(item.getIdInvoiceDetails())) return false;

        invoice.getItems().remove(item);
        invoice.calculateTotal();
        return invoiceDAO.update(invoice);
    }
}
