package com.example.marketxcell.Class;

public class AgentClass {

    public String name;
    public String email;
    public String password;
    public String phone;
    public String idNumber;
    public String address;
    public String sequrityQuesOne;
    public String sequrityAnswerOne;
    public String sequrityQuesTwo;
    public String sequrityAnswerTwo;
    public String photo;
    public String sales;

    public AgentClass() {
    }


    public AgentClass(String name, String email, String password, String phone, String idNumber, String address, String sequrityQuesOne, String sequrityAnswerOne, String sequrityQuesTwo, String sequrityAnswerTwo, String photo, String sales) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.idNumber = idNumber;
        this.address = address;
        this.sequrityQuesOne = sequrityQuesOne;
        this.sequrityAnswerOne = sequrityAnswerOne;
        this.sequrityQuesTwo = sequrityQuesTwo;
        this.sequrityAnswerTwo = sequrityAnswerTwo;
        this.photo = photo;
        this.sales = sales;
    }

}
