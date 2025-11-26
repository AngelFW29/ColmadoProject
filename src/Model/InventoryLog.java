package Model;

import java.time.LocalDateTime;

public class InventoryLog {
    private int idLog;
    private int idProduct;
    private MovementType movementType;
    private int quantityChange;
    private LocalDateTime movementDate;

    public InventoryLog(int idLog, int idProduct, MovementType typeMovement, int quantityChange, LocalDateTime movementDate) {
        this.idLog = idLog;
        this.idProduct = idProduct;
        this.movementType = typeMovement;
        this.quantityChange = quantityChange;
        this.movementDate = movementDate;
    }

    public InventoryLog(int idProduct, MovementType typeMovement, int quantityChange) {
        this.idProduct = idProduct;
        this.movementType = typeMovement;
        this.quantityChange = quantityChange;
        this.movementDate = LocalDateTime.now();
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

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
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
