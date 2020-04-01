package com.star.parking;

public class UserPayment {
    private double PaymentAmount;
    private String Paid;
    private double Time;
    private double ToBePaid;
    private double PendingTime;

    public UserPayment(){

    }

    public UserPayment(double payAmount, String paid, double time, double toBePaid,double pendingTime){
        this.PaymentAmount = payAmount;
        this.Paid = paid;
        this.Time = time;
        this.ToBePaid = toBePaid;
        this.PendingTime = pendingTime;
    }

    public double getPaymentAmount() {
        return PaymentAmount;
    }

    public void setPaymentAmount(Integer paymentAmount) {
        PaymentAmount = paymentAmount;
    }

    public String getPaid() {
        return Paid;
    }

    public void setPaid(String paid) {
        Paid = paid;
    }

    public double getTime() { return Time; }

    public void setTime(Integer time) { Time = time; }

    public double getToBePaid() { return ToBePaid; }

    public void setToBePaid(double toBePaid) { ToBePaid = toBePaid; }

    public double getPendingTime() { return PendingTime; }

    public void setPendingTime(double pendingTime) { PendingTime = pendingTime; }
}
