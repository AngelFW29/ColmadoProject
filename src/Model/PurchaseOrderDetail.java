package Model;

public class PurchaseOrderDetail {
    private int idPurchaseOrderDetail;
    private PurchaseOrder purchaseOrder;
    private Product product;
    private int quantity;
    private double unitCost;

    public PurchaseOrderDetail() {}

    public PurchaseOrderDetail(Product product, int quantity, double unitCost) {
        this.product = product;
        this.quantity = quantity;
        this.unitCost = unitCost;
    }

    public int getIdPurchaseOrderDetail() {
        return idPurchaseOrderDetail;
    }

    public void setIdPurchaseOrderDetail(int idPurchaseOrderDetail) {
        this.idPurchaseOrderDetail = idPurchaseOrderDetail;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
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
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public double getSubTotal() {
        return quantity * unitCost;
    }

}
