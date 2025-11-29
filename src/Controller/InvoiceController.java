package Controller;

import DAO.InvoiceDAO;
import Model.Customer;
import Model.Invoice;
import Model.PaymentMethod;

import java.util.List;

public class InvoiceController {
    private final InvoiceDAO invoiceDAO;

    public InvoiceController(InvoiceDAO invoiceDAO) {
        this.invoiceDAO = invoiceDAO;
    }

    public Invoice createInvoice(Customer customer, PaymentMethod paymentMethod) {
        return new Invoice(customer, paymentMethod);
    }

    public boolean deleteInvoice(int id) {
        return invoiceDAO.delete(id);
    }


    public boolean saveInvoice(Invoice invoice) {
        invoice.calculateTotal();

        return invoiceDAO.create(invoice);
    }

    public double calculateTotal(Invoice invoice) {
        return invoice.calculateTotal();
    }

    public Invoice getInvoiceById(int id) {
        return invoiceDAO.read(id);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceDAO.findAll();
    }

    public double getTodaySales() {
        return invoiceDAO.getTodaySales();
    }

    public int getCountInvoices() {
        return invoiceDAO.countInvoices();
    }

    public int getCountInvoicesToday() {
        return invoiceDAO.countInvoicesToday();
    }

    public List<Invoice> getSearchInvoice(String filter) {
        return invoiceDAO.searchInvoice(filter);
    }
}
