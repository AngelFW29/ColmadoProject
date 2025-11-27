package Model;

import Service.IFacturable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Invoice implements IFacturable {
    private int id;
    private LocalDateTime dateTime;
    private Customer customer;
    private List<InvoiceDetails> items;
    private double total;
    private PaymentMethod paymentMethod;

    public Invoice() {
        this.items = new ArrayList<>();
        this.dateTime = LocalDateTime.now();
    }

    public Invoice(int id, LocalDateTime dateTime, Customer customer, List<InvoiceDetails> items,
                   double total, PaymentMethod paymentMethod) {
        this.id = id;
        this.dateTime = dateTime;
        this.customer = customer;
        this.items = items;
        this.total = total;
        this.paymentMethod = paymentMethod;
    }


    public Invoice(Customer customer, PaymentMethod paymentMethod) {
        this.id = 0;
        this.dateTime = LocalDateTime.now();
        this.customer = customer;
        this.items = new ArrayList<>();
        this.total = 0.0;
        this.paymentMethod = paymentMethod;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public List<InvoiceDetails> getItems() {
        return items;
    }

    public void setItems(List<InvoiceDetails> items) {
        this.items = items;
        calculateTotal();
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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // --- Métodos de Lógica ---
    public void addItem(InvoiceDetails item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        calculateTotal();
    }

    @Override
    public double calculateTotal() {
        double sum = 0;
        if (items != null) {
            for (InvoiceDetails detail : items) {
                sum += detail.getSubtTotal();
            }
        }
        this.total = sum;
        return sum;
    }

    @Override
    public String generateInvoiceNumber() {
        long timestamp = System.currentTimeMillis();
        return String.valueOf(timestamp);
    }
}