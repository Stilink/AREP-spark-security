package edu.eci.arep;

import static spark.Spark.get;
import static spark.Spark.secure;

import java.util.Date;

import spark.Request;
import spark.Response;

/**
 * Hello world!
 *
 */
public class DateServer 
{
    public static void main( String[] args )
    {
        secure("sssc.jks", "password", null, null);
        get("/date", (req, res) -> new Date(System.currentTimeMillis()).toString());
    }
}
