package Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrder {
    private int idPurchaseOrder;
    private Supplier supplier;
    private LocalDateTime orderDate;
    private PurchaseOrderStatus status;
    private List<PurchaseOrderDetail> details = new ArrayList<>();

    public PurchaseOrder() {}

    public PurchaseOrder(int idPurchaseOrder, Supplier supplier, LocalDateTime orderDate, PurchaseOrderStatus status) {
        this.idPurchaseOrder = idPurchaseOrder;
        this.supplier = supplier;
        this.orderDate = orderDate;
        this.status = status;
    }

    public double getTotal() {
        return details.stream()
                .mapToDouble(PurchaseOrderDetail::getSubTotal)
                .sum();
    }

    public int getIdPurchaseOrder() {
        return idPurchaseOrder;
    }

    public void setIdPurchaseOrder(int idPurchaseOrder) {
        this.idPurchaseOrder = idPurchaseOrder;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public PurchaseOrderStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseOrderStatus status) {
        this.status = status;
    }

    public List<PurchaseOrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<PurchaseOrderDetail> details) {
        this.details = details;
    }
}
