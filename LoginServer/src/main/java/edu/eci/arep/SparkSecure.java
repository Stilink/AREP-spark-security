package edu.eci.arep;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.secure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.StringTokenizer;

import com.google.common.hash.Hashing;

import spark.Request;
import spark.Response;


public class SparkSecure {
    // View example at https://localhost:4567/secureHello
    

    public static void main(String[] args) {
        port(1111);
        secure(System.getProperty("user.dir")+"/src/main/resources/sscc.jks", "password", null, null);
        String date;
        try {
            URL url = new URL("https://localhost:4567/date");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            date = br.readLine();
            
        } catch (Exception e) {
            date = "Falle prro!";
        }
        System.out.println(date);
        get("/secureHello", (req, res) -> "Hello Secure World");
        get("/login", (req, res) -> login(req,res));
        post("/login", (req, res) -> signIn(req, res));
        get("/formUser", (req, res) -> formUser(req, res));
        post("/user", (req, res) -> createUser(req, res));
        get("/user" , (req, res) -> index(req, res));
        get("/logout", (req, res) -> signOut(req, res));
    }

    private static String signOut(Request req, Response res) {
        req.session().attribute("logged","false");
        res.redirect("/login");
        return "";
    }

    private static String signIn(Request req, Response res) {
        try {
            if(req.session().attribute("logged").equals("true")){
                res.redirect("/user");
                return "";
            }    
        } catch (Exception e) {
            res.redirect("/login");
        }
        try {
            String path = System.getProperty("user.dir")+"/src/main/resources/db";
            String username = req.queryParams("username");
            String password = req.queryParams("password");
            String pwdWithHash = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(path));
            } catch (Exception e) {
                throw new Exception("Not found DB");
            }
            try {
                String line = "";
                StringTokenizer stk;
                while((line = br.readLine())!=null){
                    stk = new StringTokenizer(line);
                    if(stk.nextToken().equals(username)){
                        stk.nextToken();
                        if(stk.nextToken().equals(pwdWithHash)){
                            req.session().attribute("user",username);
                            req.session().attribute("logged","true");
                            res.redirect("/user");
                        }else{
                            throw new Exception("Invalid password");
                        }
                    }                    
                }
                throw new Exception("User not found");
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }
    private static String createUser(Request req, Response res) {
        try {
            if(req.session().attribute("logged").equals("true")){
                return index(req, res);
            }    
        } catch (Exception e) {
            res.redirect("/login");
        }
        try {
            String path = System.getProperty("user.dir")+"/src/main/resources/db";
            String username = req.queryParams("username");
            String password = req.queryParams("password");
            String pwdWithHash = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(path));
            } catch (Exception e) {
                throw new Exception("Not found DB");
            }
            try {
                String line = "";
                StringTokenizer stk;
                while((line = br.readLine())!=null){
                    stk = new StringTokenizer(line);
                    if(stk.nextToken().equals(username)){
                        throw new Exception("User name already exists");
                    }                    
                }
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path), true));
                bw.write(username+" : "+pwdWithHash+'\n');
                bw.flush();
                bw.close();
            } catch (Exception e) {
                throw new Exception("Not created");
            }
            return index(null, null);
        } catch (Exception e) {
            System.out.println(e.getMessage());    
            return e.getMessage();
         
        }
    }

    private static String index(Request req, Response res) {
        try {  
            if(!req.session().attribute("logged").equals("true")){
                res.redirect("/login");
                return "";
            }else{
                String pageContent = "<!DOCTYPE html>" + "<html>" + "<body>" + 
                req.session().attribute("user") + "<br>"+ new Date(System.currentTimeMillis()).toString() + 
                "<form action=\"/logout\">" +"  <input type=\"submit\" value=\"Sign out\">" + "</form>"+
                "</body>" + "</html>";
                return pageContent;
            }
            
        } catch (Exception e) {
            return login(req, res);
        }
    }
    private static String login(Request req, Response res) {
        String pageContent = "<!DOCTYPE html>" + "<html>" + "<body>" + "<form action=\"/login\" method=\"POST\">" + " Username:<br>"
            + "  <input type=\"text\" name=\"username\"" + "<br> <p>Password:</p>"
            + "  <input type=\"password\" name=\"password\"" + "  <br>"
            + "  <input type=\"submit\" value=\"Log in\">" + "</form>" 
            + "<form action=\"/formUser\">" +"  <input type=\"submit\" value=\"Register\">" + "</form>" 
            + "</body>" + "</html>";
        try {
            if(req.session().attribute("logged").equals("true")){
                res.redirect("/user");
            }    
        } catch (Exception e) {
            
            return pageContent;
        }
        return pageContent;
        
        
    }
    private static String formUser(Request req, Response res) {
        String pageContent = "<!DOCTYPE html>" + "<html>" + "<body>" + "<form action=\"/user\" method=\"POST\">" + " Username:<br>"
            + "  <input type=\"text\" name=\"username\"" + "<br> <p>Password:</p>"
            + "  <input type=\"password\" name=\"password\"" + "  <br> <br>"+ "  <input type=\"submit\" value=\"Create User\">" +  "</form>" 
            + "</body>" + "</html>";  
        try {
            if(req.session().attribute("logged").equals("true")){
                res.redirect("/user");
            }    
        } catch (Exception e) {
            
            return pageContent; 
        }
        return pageContent; 
         
    }

}