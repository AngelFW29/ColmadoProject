package Controller;

import Model.Customer;
import DAO.CustomerDAO;

import java.util.List;


public class CustomerController {
    private final CustomerDAO customerDAO;

    public CustomerController(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    // Create Customer
    public boolean addCustomer(String name, String address, String phone, String fiscalIdentification) {
        Customer customer = new Customer(0, name, address, phone, fiscalIdentification
        );
        return customerDAO.create(customer);
    }

    // Read Customer
    public Customer getCustomerById(int id) {
        return customerDAO.read(id);
    }

    // Update Customer
    public boolean updateCustomer(int id, String name, String address, String phone, String fiscalIdentification) {
        Customer customer = new Customer(id, name, address, phone, fiscalIdentification
        );
        return customerDAO.update(customer);
    }

    // Delete Customer by ID
    public boolean deleteCustomer(int id) {
        return customerDAO.delete(id);
    }

    // List all Customer
    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }

    // Find customer by name
    public Customer getCustomerByName(String name) {
        return customerDAO.findCustomerByName(name);
    }

}
