package it.polito.mad17.viral.sliceapp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kalos on 25/03/2017.
 */

public class Contestazione {

    private String contestID;
    private  String groupID;
    private String expenseID;
    private String nameExpense;
    private String title;
    private String phoneNumber;
    private String groupName;
    private String userName;
    private Map<String,Commento> commenti = new HashMap<String,Commento>();

    public Contestazione(){

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContestID() {
        return contestID;
    }

    public void setContestID(String contestID) {
        this.contestID = contestID;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getExpenseID() {
        return expenseID;
    }

    public void setExpenseID(String expenseID) {
        this.expenseID = expenseID;
    }

    public Map<String, Commento> getCommenti() {
        return commenti;
    }

    public void setCommenti(Map<String, Commento> commenti) {
        this.commenti = commenti;
    }

    public String getNameExpense() {
        return nameExpense;
    }

    public void setNameExpense(String nameExpense) {
        this.nameExpense = nameExpense;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}
