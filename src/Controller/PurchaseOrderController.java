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


}
