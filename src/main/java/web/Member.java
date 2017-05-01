
package web;

public class Member {
    public long id;
    public String name;
    public String email;
    
    public String getName(){
        return name;
    }
    public String getEmail(){
        return email;
    }
    public String getFullName(){
        return "Mr" + name;
    }
}
