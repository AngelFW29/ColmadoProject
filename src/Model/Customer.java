package Model;

public class Customer extends Person {
    private final String typePerson = "Cliente";
    private String fiscalIdentification;

    public Customer(int id, String name, String address, String phone, String fiscalIdentification) {
        super(id, name, address, phone);
        this.fiscalIdentification = fiscalIdentification;
    }

    public Customer() {
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
