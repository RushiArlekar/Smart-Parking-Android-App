package com.star.parking;

public class UserInformation {
    public String Name;
    public String Address;
    public String CarName;
    public String CarNumber;
    public String RFID;
    public String EmailId;
    public String Contact;

    public UserInformation(){

    }

    public UserInformation(String name, String address, String carName, String carNumber, String rfid, String emailId, String contact){

        this.Name=name;
        this.Address=address;
        this.CarName=carName;
        this.CarNumber=carNumber;
        this.RFID=rfid;
        this.EmailId=emailId;
        this.Contact=contact;
    }

}
