package Model;

import Service.ISubTotal;
import Service.IFacturable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Invoice implements IFacturable {
    private int id;
    private Date date;
    private Customer customer;
    private ArrayList<InvoiceDetails> items;
    private double total;
    private String paymentMethod;

    public Invoice() {
    }

    public Invoice(int id, Date date, Customer customer, ArrayList<InvoiceDetails> items,
                   double total, String paymentMethod) {
        this.id = id;
        this.date = date;
        this.customer = customer;
        this.items = items;
        this.total = total;
        this.paymentMethod = paymentMethod;
    }

    public Invoice(Customer customer, String paymentMethod) {
        this.id = 0;
        this.date = new Date();
        this.customer = customer;
        this.items = new ArrayList<>();
        this.total = 0;
        this.paymentMethod = paymentMethod;
    }


    public int getIdInvoice() {
        return id;
    }

    public void setIdInvoice(int id) {
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
        if (items == null) items = new ArrayList<>();
        items.add(item);
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

        this.total = sum;
        return sum;
    }

    @Override
    public String generateInvoiceNumber() {
        long timestamp = System.currentTimeMillis();
        return String.valueOf(timestamp);
    }
}
