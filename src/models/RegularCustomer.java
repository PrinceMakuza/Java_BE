package models;

public class RegularCustomer extends Customer {

    public RegularCustomer(String name, int age, String contact, String address) {
        super(name, age, contact, address);
    }

    @Override
    public void displayCustomerDetails() {
        System.out.println("Customer ID: " + getCustomerId());
        System.out.println("Name: " + getName());
        System.out.println("Type: Regular");
        System.out.println("Age: " + getAge());
        System.out.println("Contact: " + getContact());
        System.out.println("Address: " + getAddress());
        System.out.println("Benefits: Standard banking services");
    }

    @Override
    public String getCustomerType() {
        return "Regular";
    }
    @Override
    public boolean hasWaivedFees() { return false; }
}