package Model;

import Service.ISubTotal;
import Service.IFacturable;

import java.util.ArrayList;
import java.util.Date;

public class Invoice implements IFacturable {
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

    // Constructor for a new invoice
    public Invoice(Customer customer, String paymentMethod) {
        this.id = 0;
        this.date = new Date();
        this.customer = customer;
        this.items = new ArrayList<>();
        this.total = 0;
        this.paymentMethod = paymentMethod;
    }

    public class InvoiceDetails implements ISubTotal {
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

        // Constructor for new details
        public InvoiceDetails(Invoice invoice, int quantity, double unitPrice) {
            this.id = 0;
            this.invoice = invoice;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtTotal = quantity * unitPrice;
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
        public double calculateSubTotal() {
            subtTotal = quantity * unitPrice;
            return subtTotal;
        }

    }


    // Methods Invoice
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

    public void addItem(InvoiceDetails item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);

        // Actualiza el total sumando el detalle
        this.total += item.getSubtTotal();
    }

    @Override
    public double calculateTotal() {
        double sum = 0;

        if (items != null) {
            for (InvoiceDetails detail : items) {
                sum += detail.getSubtTotal();
            }
        }

        this.total = sum; // actualiza el campo total de la factura
        return sum;
    }

    @Override
    public String generateInvoiceNumber() {
        long timestamp = System.currentTimeMillis();
        return String.valueOf(timestamp);
    }

    public void save() {
        // TODO agregarlo cuando se cree el DAO
        //  InvoiceDAO.save(this);
    }
}
