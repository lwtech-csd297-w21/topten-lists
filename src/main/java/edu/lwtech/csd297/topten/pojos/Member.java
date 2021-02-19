package edu.lwtech.csd297.topten.pojos;

// Member POJO

public class Member {

    private int recID;             // Database ID (or -1 if it isn't in the database yet)
    private String username;
    private String password;

    public Member(String username, String password) {
        constructMember(-1, username, password);
    }

    public Member(int recID, String username, String password) {
        if (recID <= 0)
            throw new IllegalArgumentException("recID must be a positive integer");

        constructMember(recID, username, password);
    }

    private void constructMember(int recID, String username, String password) {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("username cannot be empty or null");
        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("password cannot be empty or null");

        this.recID = recID;
        this.username = username;
        this.password = password;
    }

    // ----------------------------------------------------------------

    public int getRecID() {
        return recID;
    }

    public void setRecID(int recID) {
        // Updates the recID of members that have just been added to the database
        if (recID <= 0)
            throw new IllegalArgumentException("RecID must be a positive integer.");
        if (this.recID != -1)
            throw new IllegalArgumentException("Member has already been added to the database.");

        this.recID = recID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "[" + username + ", ******** ]";
    }

}