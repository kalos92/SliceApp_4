package it.polito.mad17.viral.sliceapp;

/**
 * Created by abdel on 24/05/2017.
 */

public class Commento {

    private String commentoID;
    private String commento;
    private String userID;
    private String userName;
    private Long timestamp;




    public Commento(){

    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCommentoID() {
        return commentoID;
    }

    public void setCommentoID(String commentoID) {
        this.commentoID = commentoID;
    }

    public String getCommento() {
        return commento;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
}
