package Model;

import java.time.LocalDateTime;

public class InventoryLog {
    private int idLog;
    private int idProduct;
    private String typeMovement;
    private int quantityChange;
    private LocalDateTime movementDate;

    public InventoryLog(int idLog, int idProduct, String typeMovement, int quantityChange, LocalDateTime movementDate) {
        this.idLog = idLog;
        this.idProduct = idProduct;
        this.typeMovement = typeMovement;
        this.quantityChange = quantityChange;
        this.movementDate = movementDate;
    }

    public InventoryLog(int idProduct, String typeMovement, int quantityChange) {
        this.idProduct = idProduct;
        this.typeMovement = typeMovement;
        this.quantityChange = quantityChange;
    }

    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public String getTypeMovement() {
        return typeMovement;
    }

    public void setTypeMovement(String typeMovement) {
        this.typeMovement = typeMovement;
    }

    public int getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(int quantityChange) {
        this.quantityChange = quantityChange;
    }

    public LocalDateTime getMovementDate() {
        return movementDate;
    }
    public void setMovementDate(LocalDateTime movementDate) {
        this.movementDate = movementDate;
    }
}
