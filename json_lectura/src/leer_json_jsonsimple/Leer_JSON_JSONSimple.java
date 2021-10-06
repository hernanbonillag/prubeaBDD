/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leer_json_jsonsimple;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.*;
/**
 *
 * @author xcheko51x
 */
public class Leer_JSON_JSONSimple {

    public static void main(String[] args) {
        Hashtable<Integer,String> diccglobal = 
                      new Hashtable<Integer,String>(); 
        Hashtable<Integer,String> diccglobal2 = 
                      new Hashtable<Integer,String>();
        
        
        JSONParser parser = new JSONParser();
        
        try {
            
            Object obj = parser.parse(new FileReader("dictdist.json"));
            JSONObject jsonObject = (JSONObject) obj;
            System.out.println("JSON LEIDO: " + jsonObject);
            
            JSONArray array = (JSONArray) jsonObject.get("Servicios");
            System.out.println("");
            
            for(int i = 0 ; i < array.size() ; i++) {
                JSONObject jsonObject1 = (JSONObject) array.get(i);
                
                System.out.println("DATOS DE LOS SERVICIOS");
                System.out.println("Servicio: " + jsonObject1.get("nameOfService"));
                System.out.println("IP: " + jsonObject1.get("ip"));
                System.out.println("Puerto: " + jsonObject1.get("port"));
                System.out.println("Factor: " + jsonObject1.get("factor"));
                diccglobal.put(i,jsonObject1.get("nameOfService").toString());
                diccglobal2.put(i,jsonObject1.get("port").toString());
                System.out.println("");
            }
            System.out.println("Valores en el DiscDist: "+diccglobal);
            System.out.println("Valores en el DiscDist: "+diccglobal2);
        } catch(FileNotFoundException e) { }
        catch(IOException | ParseException e) { }
    }
    
}
