package leer_json_jsonsimple;


import java.net.*;
import java.io.*;
public class ServerMultiClient{
   static int NoClients=0;
   public static void main (String[] argumentos)throws IOException{
	ServerSocket socketServidor = null;
	Socket socketCliente = null;

	try{
	   socketServidor = new ServerSocket (12345);
	}catch (Exception e){
	   System.out.println ("Error : "+ e.toString());
	   System.exit (0);
	}

	System.out.println ("Server started... (Socket TCP)");
	int enproceso=1;
	while(enproceso==1){
		try{
	   		socketCliente = socketServidor.accept();
			MultiServerThread controlThread=new MultiServerThread(socketCliente);
			controlThread.start();
	   	}catch (Exception e){
	    	System.out.println ("Error : " + e.toString());
			socketServidor.close();
			System.exit (0);
	   	}
	}
	System.out.println("Finalizando Servidor...");

   }
}
