package web;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.http.HttpSession;
import static org.springframework.http.RequestEntity.post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
class Web{
    //Configuration DATABASE Connect;
    String dbUrl = "jdbc:mysql://35.185.140.182/web?characterEncoding=UTF-8";
    String dbUser = "social";
    String dbPassword = "123456";
    
    Web(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }catch(Exception e){System.out.println(e);}
    }
    boolean item[] = new boolean[100];
    @RequestMapping("/reserve") @ResponseBody
    synchronized String reserveItem(int id){
        if(item[id] == false){
            item[id] = true;
            return "Success";
        } else {
            return "Fail";
        }
    }
    @RequestMapping("/login")
    String showLogin(){
        return "login";
    }
    @RequestMapping("/logout")
    String showLogout(HttpSession session){
        session.removeAttribute("member");
        return "logout";
    }
    
    @RequestMapping("/profile")
    String showProfile(HttpSession session, Model model){
        if(session.isNew()){
            return "redirect:/login";
        }
        Member m = (Member)session.getAttribute("member");
        if(m == null){
            return "redirect:/login";
        }
        model.addAttribute("user", m.name);
        model.addAttribute("member",m);
        return "profile";
    }//Show profile in URL localhost:8080/profile pageใชษไทยได้แต่บรรทัดไม่เห็น้้้
    
    @RequestMapping(value="/login",method=RequestMethod.POST)
    String checkLogin(HttpSession session,String email,String password){
        System.out.println(email);
        System.out.println(password);
        try{
            Connection c = DriverManager.getConnection(dbUrl,dbUser, dbPassword);
            PreparedStatement p = c.prepareStatement(
            "select * from member where email=? and password=sha2(?,512)");
            p.setString(1, email);
            p.setString(2, password);
            ResultSet r = p.executeQuery();
            if(r.next()){
                Member m = new Member();
                m.id = r.getLong("id");
                m.name = r.getString("name");
                m.email = r.getString("email");
                session.setAttribute("member", m);
                String name = r.getString("name");
                System.out.println(name + "Login Passed");
                return "redirect:/profile";
            }else {
                System.out.println("Login fail");
            }                               
        }catch(Exception e){System.out.println(e);}
    //System.out.println(email + password);
        return "redirect:/login";
    }
    
    @RequestMapping("/")
    String showIndex(){
        return "index";
    }
    
    @RequestMapping("/register")
    String showRegister(){
        return "register";
    }
    @RequestMapping("/new")
    String showNew(HttpSession session){
        Member m = (Member)session.getAttribute("member");
        if(m == null){
            return "redirect:/login";
        } else {
            return "new";
        }
    }//End New.html
    @RequestMapping(value="/new", method=RequestMethod.POST)
    String postNew(String topic,String detail, HttpSession session, MultipartFile photo){
        Member m = (Member)session.getAttribute("member");
        if(m == null){
            return "redirect:/login";
        }else {
            try{
                String fileName = null;
             if(photo != null){
                 fileName = "./src/main/resources/static/" 
                        + "photo-" + (int)(Math.random() * 1000000000) + ".jpg";
                 FileOutputStream fos = new FileOutputStream(fileName);
                 byte data[] = photo.getBytes();
                 for(byte b : data){
                     fos.write(b);     
                 }
                 fos.close();
             }
             String sql = "INSERT INTO post(topic, detail,member,time,photo)"
                         + "values(?,?,?, now(),?)";
             Connection connect = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
             PreparedStatement prepare = connect.prepareStatement(sql);
             prepare.setString(1, topic);
             prepare.setString(2, detail);
             prepare.setLong(3, m.id);
             prepare.execute();
             prepare.close();connect.close();
            }catch(Exception e){System.out.println(e);}
        return "redirect:/profile";
        }            
    }  //Ending POST PROJECT 
    
    @RequestMapping("/all")
        String showAll(Model model){
        java.util.ArrayList arry = new java.util.ArrayList<>();
        try{
            String sql = "SELECT * FROM post";
            Connection con = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                Post post = new Post();
                post.id = rs.getLong("id");
                post.topic = rs.getString("topic");
                post.detail = rs.getString("detail");
                post.member = rs.getLong("member");
                post.time = rs.getString("time");
                post.photo = rs.getString("photo");
                arry.add(post);               
            }
            rs.close();// close Connection to Databases;
            con.close(); // close Connection to Databases;
            stmt.close(); // close Connection to Databases;
        }catch(Exception e){System.out.println(e);}
        model.addAttribute("post",arry);
        return "showtopic-all";
    }// End Method Call AllPost Page to Display
    @RequestMapping("/detail")
    String showDetail(Model model ,long id){
        Post post = new Post();
        try{
            String sql = "SELECT * FROM post WHERE id=?";
           Connection con = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
           PreparedStatement p = con.prepareStatement(sql);
           p.setLong(1,id);
           ResultSet rs = p.executeQuery();
           if(rs.next()){              
               post.id = rs.getLong("id");
               post.topic = rs.getString("topic");
               post.detail = rs.getString("detail");
               post.member = rs.getLong("member");
               post.time = rs.getString("time");
               post.photo = rs.getString("photo").substring(27);
           }
           con.close();
           p.close();
           rs.close();
        }catch(Exception e){System.out.println(e);}
        model.addAttribute("post", post);
        return "detail";
    }
    @RequestMapping("/view/{id}")
    String viewTopic(Model model, @PathVariable long id){
        return showDetail(model, id);
    }
        
    
    @RequestMapping(value="/register",method=RequestMethod.POST)
    String registerUser(String name , String email,String password){
    try{
        Connection c = DriverManager.getConnection(dbUrl,dbUser, dbPassword);
        PreparedStatement p = c.prepareStatement("INSERT INTO member(name,email,password)" + 
                               "values(?,?,sha2(?,512))");
        p.setString(1, name);
        p.setString(2, email);
        p.setString(3, password);
        p.execute();
        p.close();
        c.close();
    }catch(Exception e){ 
        System.out.println(e);  
        System.out.println(name + password + email);
    }
        return "login";
    }
    
    /* ใช้ในกรณี Request Notfound*/
    @RequestMapping("*")
    String showError(){
        return "error";
    }
    @RequestMapping("/success")
    String showSuccess(){
        return "success";
    }
    
}
