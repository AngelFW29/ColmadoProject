package Model;

public class Supplier extends Person {
    private final String typePerson = "Proveedor";
    private String fiscalIdentification;

    public Supplier(int id, String name, String address, String phone, String fiscalIdentification) {
        super(id, name, address, phone);
        this.fiscalIdentification = fiscalIdentification;
    }

    public Supplier() {
        super();
    }

    public String getTypePerson() {
        return typePerson;
    }

    public String getFiscalIdentification() {
        return fiscalIdentification;
    }

    public void setFiscalIdentification(String fiscalIdentification) {
        this.fiscalIdentification = fiscalIdentification;
    }
}
