package Controller;

import DAO.InvoiceDAO;
import Model.Customer;
import Model.Invoice;

import java.util.List;

public class InvoiceController {
    private final InvoiceDAO invoiceDAO;

    public InvoiceController(InvoiceDAO invoiceDAO) {
        this.invoiceDAO = invoiceDAO;
    }

    // Create Invoice
    public Invoice createInvoice(Customer customer, String paymentMethod) {
        return new Invoice(customer, paymentMethod);
    }

    // Add item to invoice
    public void addItemInvoice(Invoice invoice, int quantity, double unitPrice) {
        Invoice.InvoiceDetails item = invoice.new InvoiceDetails(invoice, quantity, unitPrice);

        invoice.addItem(item);
    }

    // Delete item
    public void deleteItem(Invoice invoice, Invoice.InvoiceDetails item) {
        invoice.getItems().remove(item);
        invoice.calculateTotal();
    }

    // Update item
    public void updateItem(Invoice.InvoiceDetails item, int quantity, double unitPrice) {
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.calculateSubTotal();
    }

    // Calculate final total
    public double calculateTotal(Invoice invoice) {
        return invoice.calculateTotal();
    }

    // Save invoice to database
    public boolean saveInvoice(Invoice invoice) {
        invoice.calculateTotal();

        return invoiceDAO.create(invoice);
    }

    // Read invoice
    public Invoice getInvoiceById(int id) {
        return invoiceDAO.read(id);
    }

    // List all invoice
    public List<Invoice> getAllInvoices() {
        return invoiceDAO.findAll();
    }


}
