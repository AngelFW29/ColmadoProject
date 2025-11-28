package Service;

import DAO.*;
import Model.*;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseOrderService {

    private final PurchaseOrderDAO orderDAO;
    private final PurchaseOrderDetailsDAO detailDAO;
    private final ProductDAO productDAO;
    private final InventoryLogDAO inventoryLogDAO;

    public PurchaseOrderService() {
        this.orderDAO = new PurchaseOrderDAO();
        this.detailDAO = new PurchaseOrderDetailsDAO();
        this.productDAO = new ProductDAO();
        this.inventoryLogDAO = new InventoryLogDAO();
    }

    public boolean registrarPedido(Supplier supplier, List<CartItem> items, PurchaseOrderStatus status) throws Exception {

        PurchaseOrder order = new PurchaseOrder();
        order.setSupplier(supplier);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(status);

        double totalCalculado = 0;
        for (CartItem item : items) {

            totalCalculado += item.getSubTotal();
        }
        order.setTotal(totalCalculado);

        boolean orderSaved = orderDAO.create(order);
        if (!orderSaved) {
            throw new Exception("Error al guardar la cabecera del pedido.");
        }

        int orderId = orderDAO.getLastInsertedId();
        order.setIdPurchaseOrder(orderId);

        for (CartItem item : items) {
            Product p = item.getProduct();
            int qty = item.getQuantity();
            double cost = p.getUnitPrice();

            PurchaseOrderDetails detail = new PurchaseOrderDetails();
            detail.setPurchaseOrder(order);
            detail.setProduct(p);
            detail.setQuantity(qty);
            detail.setUnitCost(cost);
            detail.setSubTotal(qty * cost);

            detailDAO.create(detail);

            if (status == PurchaseOrderStatus.RECIBIDO) {

                int nuevoStock = p.getInventoryQuantity() + qty;
                p.setInventoryQuantity(nuevoStock);
                productDAO.update(p);

                InventoryLog log = new InventoryLog(
                        p.getId(),
                        MovementType.Compra,
                        qty
                );
                inventoryLogDAO.create(log);
            }
        }

        return true;
    }
}