package Controller;

import DAO.PurchaseOrderDAO;
import DAO.PurchaseOrderDetailsDAO;
import Model.PurchaseOrder;
import Model.PurchaseOrderDetails;
import Model.Product;

public class PurchaseOrderDetailsController {
    private final PurchaseOrderDetailsDAO detailDAO;
    private final PurchaseOrderDAO orderDAO;


    public PurchaseOrderDetailsController(PurchaseOrderDetailsDAO detailDAO, PurchaseOrderDAO orderDAO) {
        this.detailDAO = detailDAO;
        this.orderDAO = orderDAO;
    }

    public boolean addItem(PurchaseOrder order, Product product, int quantity, double unitCost) {
        if (order == null || product == null) throw new IllegalArgumentException
                ("Order and Product are required.");
        if (order.getIdPurchaseOrder() == 0) {
            boolean created = orderDAO.create(order);
            if (!created) return false;
        }

        PurchaseOrderDetails detail = new PurchaseOrderDetails(product, quantity, unitCost);
        detail.setPurchaseOrder(order);

        if (!detailDAO.create(detail)) return false;

        order.getDetails().add(detail);
        return orderDAO.update(order);
    }

    public boolean updateItem(PurchaseOrderDetails detail, int quantity, double unitCost) {
        if (detail == null) throw new IllegalArgumentException("Detail is required.");

        detail.setQuantity(quantity);
        detail.setUnitCost(unitCost);

        if (!detailDAO.update(detail)) return false;

        PurchaseOrder order = detail.getPurchaseOrder();
        if (order != null) {
            return orderDAO.update(order);
        }
        return true;
    }

    public boolean deleteItem(PurchaseOrder order, PurchaseOrderDetails detail) {
        if (order == null || detail == null) throw new IllegalArgumentException("Order and detail are required.");

        if (!detailDAO.delete(detail.getIdPurchaseOrderDetail())) return false;

        order.getDetails().remove(detail);
        return orderDAO.update(order);

    }
}
