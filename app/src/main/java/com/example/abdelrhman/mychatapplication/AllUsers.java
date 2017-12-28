package com.example.abdelrhman.mychatapplication;


public  class AllUsers {

     private String user_Name;
     private  String User_Image;
     private   String User_Status;
     private String User_thumb_Image;

    public AllUsers(){

    }


    public AllUsers(String user_Name, String user_Image, String user_Status, String user_thumb_Image) {
        this.user_Name = user_Name;
        this.User_Image = user_Image;
        this.User_Status = user_Status;
       this.User_thumb_Image = user_thumb_Image;
    }

    public String getUser_Name() {
        return user_Name;
    }

    public void setUser_Name(String user_Name) {
        this.user_Name = user_Name;
    }

    public String getUser_Image() {
        return User_Image;
    }

    public void setUser_Image(String user_Image) {
        User_Image = user_Image;
    }

    public String getUser_Status() {
        return User_Status;
    }

    public void setUser_Status(String user_Status) {
        User_Status = user_Status;
    }

    public String getUser_thumb_Image() {
        return User_thumb_Image;
    }

    public void setUser_thumb_Image(String user_thumb_Image) {
        User_thumb_Image = user_thumb_Image;
    }
}
