package Model;

import java.util.Date;

public class Product {
    private int id;
    private String name;
    private String category;
    private double unitPrice;
    private int inventoryQuantity;
    private Date expirationDate;

    public Product(int id, String name, String category, double unitPrice,
                   int inventoryQuantity, Date expirationDate) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.inventoryQuantity = inventoryQuantity;
        this.expirationDate = expirationDate;
    }

    // Constructor without ID
    public Product(String name, String category, double unitPrice,
                   int inventoryQuantity, Date expirationDate) {
        this(0, name, category, unitPrice, inventoryQuantity, expirationDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getInventoryQuantity() {
        return inventoryQuantity;
    }

    public void setInventoryQuantity(int inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void adjustStock(int qty) {
        int newQuantity = this.inventoryQuantity + qty;
        if (newQuantity < 0) {
            throw new IllegalArgumentException(
                    "There is insufficient inventory to perform this operation."
            );
        }

        this.inventoryQuantity = newQuantity;
    }
}
