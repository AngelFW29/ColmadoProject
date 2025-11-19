package Model;

public class Supplier extends Person {
    private String fiscalIdentification;

    public Supplier(int id, String name, String address, String phone, String fiscalIdentification) {
        super(id, name, address, phone);
        this.fiscalIdentification = fiscalIdentification;
    }

    // Constructor without ID
    public Supplier(String name, String address, String phone, String fiscalIdentification) {
        super(0, name, address, phone);
        this.fiscalIdentification = fiscalIdentification;
    }


    public String getFiscalIdentification() {
        return fiscalIdentification;
    }

    public void setFiscalIdentification(String fiscalIdentification) {
        this.fiscalIdentification = fiscalIdentification;
    }
}
