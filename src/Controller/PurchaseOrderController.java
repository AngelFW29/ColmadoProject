package Controller;

import DAO.PurchaseOrderDAO;
import Model.Supplier;
import Model.PurchaseOrder;

import java.util.List;

public class PurchaseOrderController {
    private final PurchaseOrderDAO purchaseOrderDAO;

    public PurchaseOrderController(PurchaseOrderDAO purchaseOrderDAO) {
        this.purchaseOrderDAO = purchaseOrderDAO;
    }

    public PurchaseOrder createPurchaseOrder(Supplier supplier) {
        return new PurchaseOrder(0, supplier, java.time.LocalDateTime.now(), null);
    }

    public boolean savePurchaseOrder(PurchaseOrder order) {
        return purchaseOrderDAO.create(order);
    }

    public PurchaseOrder getPurchaseOrderById(int id) {
        return purchaseOrderDAO.read(id);
    }

    public boolean updatePurchaseOrder(PurchaseOrder order) {
        return purchaseOrderDAO.update(order);
    }

    public boolean deletePurchaseOrder(int id) {
        return purchaseOrderDAO.delete(id);
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderDAO.findAll();
    }

    public int getPendingOrder() {
        return purchaseOrderDAO.pendingOrder();
    }

    public int getCountOrder() {
        return purchaseOrderDAO.countPurchaseOrder();
    }
    public int getReceivedOrder() {
        return purchaseOrderDAO.receivedOrder();
    }

    public List<PurchaseOrder> getPurchase(String filter) {
        return purchaseOrderDAO.searchPurchaseOrder(filter);
    }

}
