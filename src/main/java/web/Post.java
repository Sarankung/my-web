package web;

public class Post{
    long id;
    String topic;
    String detail;
    long member;
    String time;
    String photo;
    
    public long getId(){
        return id;
    }
    public String getTopic(){
        return topic;
    }
    public String getDetail(){
        return detail;
    }
    public long getMember(){
        return member;
    }
    public String getTime(){
        return time;
    }
    public String getPhoto(){
        return photo;
    }
    
}
