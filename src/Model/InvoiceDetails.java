package Model;

import Service.ISubTotal;

public class InvoiceDetails implements ISubTotal {
    private int id;
    private Invoice invoice;
    private Product product;
    private int quantity;
    private double unitPrice;
    private double subtTotal;

    public InvoiceDetails(Invoice invoice, Product product, int quantity, double unitPrice) {
        this.id = 0;
        this.invoice = invoice;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtTotal = quantity * unitPrice;
    }

    public int getIdInvoiceDetails() {
        return id;
    }

    public void setIdInvoiceDetails(int id) {
        this.id = id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateSubTotal();
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubTotal();
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
