package DAO;

import Model.PurchaseOrder;

import java.util.List;

public class PurchaseOrderDAO implements ICRUD<PurchaseOrder> {

    private ConnectionMySQL connection;

    public PurchaseOrderDAO(ConnectionMySQL connection) {
        this.connection = connection;
    }


    @Override
    public boolean create(PurchaseOrder entity) {
        return false;
    }

    @Override
    public PurchaseOrder read(int id) {
        return null;
    }

    @Override
    public boolean update(PurchaseOrder entity) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public List<PurchaseOrder> findAll() {
        return List.of();
    }
}
