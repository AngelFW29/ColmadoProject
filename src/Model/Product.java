package Model;

import java.time.LocalDate;

public class Product {
    private int id;
    private String name;
    private int category;
    private double unitPrice;
    private int inventoryQuantity;
    private LocalDate expirationDate;

    public Product() {}

    public Product(int id, String name, int category, double unitPrice,
                   int inventoryQuantity, LocalDate expirationDate) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.inventoryQuantity = inventoryQuantity;
        this.expirationDate = expirationDate;
    }

    public Product(String name, int category, double unitPrice,
                   int inventoryQuantity, LocalDate expirationDate) {
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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
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

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
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