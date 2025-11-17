package Model;

import java.util.ArrayList;
import java.util.Date;

public class Invoice {
    private int id;
    private Date date;
    private Customer customer;
    private ArrayList<InvoiceDetails> items;
    private double total;
    private String paymentMethod;

    public Invoice(int id, Date date, Customer customer, ArrayList<InvoiceDetails> items,
                   double total, String paymentMethod) {
        this.id = id;
        this.date = date;
        this.customer = customer;
        this.items = items;
        this.total = total;
        this.paymentMethod = paymentMethod;
    }

    public class InvoiceDetails implements ISubTotal{
        private int id;
        private Invoice invoice;
        private int quantity;
        private double unitPrice;
        private double subtTotal;

        public InvoiceDetails(int id, Invoice invoice, int quantity, double unitPrice, double subtTotal) {
            this.id = id;
            this.invoice = invoice;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtTotal = subtTotal;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Invoice getInvoice() {
            return invoice;
        }

        public void setInvoice(Invoice invoice) {
            this.invoice = invoice;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public double getSubtTotal() {
            return subtTotal;
        }

        public void setSubtTotal(double subtTotal) {
            this.subtTotal = subtTotal;
        }

        @Override
        public double calculateTotal() {
            return 0;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<InvoiceDetails> getItems() {
        return items;
    }

    public void setItems(ArrayList<InvoiceDetails> items) {
        this.items = items;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void addItem(InvoiceDetails items) {
        return;
    }

    public double calculateTotal() {
        return total;
    }

    public int generateInvoiceNumber() {
        return id;
    }

    public void save() {
        return;
    }

}
