package edu.codespring.mpihotel.backend.model;

public class Room extends BaseEntity {
    private Integer capacity;
    private Integer hotelID;

    public Room() {
    }

    public void Room() {
    }

    public Room(Integer capacity, Integer hotelID) {
        this.capacity = capacity;
        this.hotelID = hotelID;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getHotelID() {
        return hotelID;
    }

    public void setHotelID(Integer hotelID) {
        this.hotelID = hotelID;
    }

    @Override
    public String toString() {
        return "Room{" +
                "capacity=" + capacity +
                ", hotelID=" + hotelID +
                '}';
    }
}
