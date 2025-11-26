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

    // Save invoice
    public boolean saveInvoice(Invoice invoice) {
        invoice.calculateTotal();

        return invoiceDAO.create(invoice);
    }

    // Calculate final total
    public double calculateTotal(Invoice invoice) {
        return invoice.calculateTotal();
    }

    // Read invoice
    public Invoice getInvoiceById(int id) {
        return invoiceDAO.read(id);
    }

    // List all invoice
    public List<Invoice> getAllInvoices() {
        return invoiceDAO.findAll();
    }

    // Returns today's total sales
    public double getTodaySales(){
        return invoiceDAO.TodaySales();
    }
}
