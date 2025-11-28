package Model;

public enum PurchaseOrderStatus {
    PENDIENTE("Pendiente"),
    RECIBIDO("Recibido"),
    CANCELADO("Cancelado");

    private final String dbValue;

    PurchaseOrderStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    @Override
    public String toString() {
        return dbValue;
    }
}