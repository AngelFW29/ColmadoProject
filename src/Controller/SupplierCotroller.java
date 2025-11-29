package Controller;

import DAO.SupplierDAO;
import Model.Supplier;

import java.util.List;

public class SupplierCotroller {
    private final SupplierDAO supplierDAO;

    public SupplierCotroller(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }


    // Create Supplier
    public boolean addSupplier(String name, String address, String phone, String fiscalIdentification) {
        Supplier supplier = new Supplier(0, name, address, phone, fiscalIdentification);
        return supplierDAO.create(supplier);
    }

    // Read Supplier by ID
    public Supplier getSupplierById(int id) {
        return supplierDAO.read(id);
    }

    // Update Supplier
    public boolean updateSupplier(int id, String name, String address, String phone, String fiscalIdentification) {
        Supplier supplier = new Supplier(id, name, address, phone, fiscalIdentification);
        return supplierDAO.update(supplier);
    }

    // Delete Supplier
    public boolean deleteSupplier(int id) {
        return supplierDAO.delete(id);
    }

    // List all suppliers
    public List<Supplier> getAllSuppliers() {
        return supplierDAO.findAll();
    }

    public List<Supplier> getSearchSuppliers(String filter) {
        return supplierDAO.searchSuppliers(filter);
    }

    public int getCountSuppliers(){
        return supplierDAO.countSuppliers();
    }

    public int getNewSuppliers(){
        return supplierDAO.newSuppliers();
    }
}
