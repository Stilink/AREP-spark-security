package edu.eci.arep;

import static spark.Spark.get;
import static spark.Spark.secure;

import java.util.Date;

import spark.Request;
import spark.Response;

public class DateServer 
{
    public static void main( String[] args )
    {
        secure(System.getProperty("user.dir")+"/src/main/resources/sssc.jks", "password", null, null);
        get("/date", (req, res) -> new Date(System.currentTimeMillis()).toString());
    }
}
