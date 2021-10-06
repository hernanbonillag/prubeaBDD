package leer_json_jsonsimple;

import java.net.*;
import java.io.*;
import java.util.Date;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewMultiServerThread extends Thread {
   private Socket socket = null;

   String maquinaX="localhost";
   int puertoX=13777;
   Date fecha=new Date();
   PrintWriter escritorX = null;
   String DatosEnviadosX = null;
   BufferedReader entradaX =null;
   Socket clienteX = null;
   DateFormat hora=new SimpleDateFormat("hh:mm:ss");

   public NewMultiServerThread(Socket socket) {
      super("NewMultiServerThread");
      this.socket = socket;
      ServerMultiClient.NoClients++;
      System.out.println("After Init");
   }

   public void run() {

      try {
          Hashtable<Integer,String> diccglobal = new Hashtable<Integer,String>(); 
          Hashtable<Integer,String> diccglobal2 = new Hashtable<Integer,String>();
        
        
        JSONParser parser = new JSONParser();
        
            
            Object obj = parser.parse(new FileReader("dictdist.json"));
            JSONObject jsonObject = (JSONObject) obj;
    
            JSONArray array = (JSONArray) jsonObject.get("Servicios");
            for(int i = 0 ; i < array.size() ; i++) {
                JSONObject jsonObject1 = (JSONObject) array.get(i);
                diccglobal.put(i,jsonObject1.get("nameOfService").toString());
                diccglobal2.put(i,jsonObject1.get("port").toString());    
            }
         PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         String lineIn, lineOut;

	     while((lineIn = entrada.readLine()) != null){
            System.out.println("Received: "+lineIn);
            escritor.flush();
            if(lineIn.equals(diccglobal.get(6))){
                ServerMultiClient.NoClients--;
			          break;
			      }else if(lineIn.equals(diccglobal.get(5))){
                                  escritor.println("NC: "+ServerMultiClient.NoClients);
                                  escritor.flush();
                              } else if(lineIn.equals(diccglobal.get(4))){
                                  escritor.println("Crypt:#r-crypt-r#");
                                  escritor.flush();
                              }else if(lineIn.equals(diccglobal.get(1))){
                                  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                               
              escritor.println("Este servicio se encuentra en el servidor 1 con el puerto: 12345\nResponse from Server 1 : "+ dtf.format(LocalDateTime.now())+"#");
              escritor.flush();
            }else if(lineIn.equals(diccglobal.get(0))){
                                    
                                    System.out.println("Connecting to another server");
                                    try{
                                        clienteX = new Socket (maquinaX,puertoX);
                                        System.out.println("Connected to another server");
                                        }catch (Exception e){
                                            System.out.println ("Fallo : "+ e.toString());
                                            System.exit (0);
                                        }
                                        System.out.println("Trying to send data to another server");

                                    try{
                                    escritorX = new PrintWriter(clienteX.getOutputStream(), true);
                                    entradaX=new BufferedReader(new InputStreamReader(clienteX.getInputStream()));
                                    }catch (Exception e){
                                    System.out.println ("Fallo : "+ e.toString());
                                    clienteX.close();
                                    System.exit (0);
                                    }     
              String lineX;
              String DatosEnviadosX;
              DateFormat fechita=new SimpleDateFormat("dd/MM/yy");
                               
              System.out.println("Sending connecting to another server");
              DatosEnviadosX = "Crypt:#r-crypt-r#";
              escritorX.println (DatosEnviadosX);
              lineX = entradaX.readLine();
              System.out.println("Server1: "+lineX);
              DatosEnviadosX = "FIN";
              escritorX.println (DatosEnviadosX);
              System.out.println("Closing another server");
              clienteX.close();
              escritorX.close();
              entradaX.close();
              escritor.println("Este servicio se encuentra en el servidor 1 con el puerto: 12345\nResponse from Server1...#r-fecha#1#:"+ fechita.format(fecha)+"#" );
              escritor.flush();
              
            }else{
                escritor.println("Echo... "+lineIn);
                escritor.flush();
            }
        }
        try{
            entrada.close();
            escritor.close();
            socket.close();
         }catch(Exception e){
            System.out.println ("Error : " + e.toString());
            socket.close();
            System.exit (0);
   	   }
      }catch (IOException e) {
        System.out.println("Error---");
         e.printStackTrace();
      } catch (ParseException ex) {
           Logger.getLogger(NewMultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
}
