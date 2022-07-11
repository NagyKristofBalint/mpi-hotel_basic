package edu.codespring.mpihotel.backend.model;

import java.sql.Date;

public class Bookings extends BaseEntity {
    private Long userID;
    private Long roomID;

    private Date startDate;
    private Date endDate;

    private Long hotelID;

    public Bookings() {
    }

    public Bookings(Long userID, Long roomID, Date startDate, Date endDate, Long hotelID) {
        this.userID = userID;
        this.roomID = roomID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hotelID = hotelID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getRoomID() {
        return roomID;
    }

    public void setRoomID(Long roomID) {
        this.roomID = roomID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getHotelID() {
        return hotelID;
    }

    public void setHotelID(Long hotelID) {
        this.hotelID = hotelID;
    }

    @Override
    public String toString() {
        return "Bookings{" +
                "userID=" + userID +
                ", roomID=" + roomID +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", hotelID=" + hotelID +
                '}';
    }
}
