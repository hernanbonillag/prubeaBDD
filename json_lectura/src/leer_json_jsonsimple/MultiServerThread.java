package leer_json_jsonsimple;

import java.net.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MultiServerThread extends Thread {
   private Socket socket = null;

   String maquinaX="localhost";
   int puertoX=12345;
   Date fecha=new Date();
   PrintWriter escritorX = null;
   String DatosEnviadosX = null;
   BufferedReader entradaX =null;
   Socket clienteX = null;


   public MultiServerThread(Socket socket) {
      super("MultiServerThread");
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
            if(lineIn.equals(diccglobal.get(3))){
                ServerMultiClient.NoClients--;
                System.out.println("Desconnecting from this server");
			          break;
			      }else if(lineIn.equals(diccglobal.get(2))){
                                  escritor.println("NC: "+ServerMultiClient.NoClients);
                                  escritor.flush();
                              }else if(lineIn.equals(diccglobal.get(1))){
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                                escritor.println("#r-hora#1#:"+ dtf.format(LocalDateTime.now())+"#");
                                 escritor.flush();
                              }else if(lineIn.equals(diccglobal.get(0))){
                                DateFormat fechita=new SimpleDateFormat("dd/MM/yy");
                                escritor.println("#r-fecha#1#:"+ fechita.format(fecha)+"#");
                                 escritor.flush();
                              }else if(lineIn.equals(diccglobal.get(4))){
                                    
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
              System.out.println("Sending connecting to another server");
              DatosEnviadosX = "Crypt:#r-crypt-r#";
              escritorX.println (DatosEnviadosX);
              lineX = entradaX.readLine();
              System.out.println("Server2: "+lineX);
              DatosEnviadosX = "FIN";
              escritorX.println (DatosEnviadosX);
              System.out.println("Closing another server");
              clienteX.close();
              escritorX.close();
              entradaX.close();
              escritor.println("Este servicio se encuentra en el servidor 2 con el puerto: 13777\nResponse from Server2... "+lineX);
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
           Logger.getLogger(MultiServerThread.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
}
