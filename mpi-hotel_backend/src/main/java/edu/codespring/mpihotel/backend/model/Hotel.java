package edu.codespring.mpihotel.backend.model;

public class Hotel extends BaseEntity {
    private String name;
    private String city;

    public Hotel() {
    }

    public Hotel(String name, String city) {
        this.name = name;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "name='" + name + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
