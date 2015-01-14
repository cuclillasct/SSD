package Petitions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Views.Client;
import Controllers.ServerThread;
import Interfaces.IFuturo;
import Interfaces.IMethodRequest;
import Interfaces.IObservadorFuturo;
import Models.CristianFuturo;
import Models.FileList;

public class GetCristianResult implements IMethodRequest {
	
	Date remoteDate, localDate, cristianDate;
	
	public GetCristianResult() {
		localDate = Calendar.getInstance().getTime();
	}

	@Override
	public void execute() throws IOException {
		Socket socket = new Socket("127.0.0.1", 52534);
		System.out.println("Conectando con: 127.0.0.1 Puerto: 52534");
		try {			

			//Out
			ObjectOutputStream outstr = new ObjectOutputStream(socket.getOutputStream());
			
			outstr.writeObject(ServerThread.SINCRONIZAR);
			outstr.flush();
			System.out.println("Petición enviada: Sincronizar con Cristian");
			
			//In
			ObjectInputStream instr = new ObjectInputStream(socket.getInputStream());
			
			remoteDate = (Date) instr.readObject();
			
			System.out.println("Cerrando conexión con el servidor...");
			outstr.writeObject("exit"); 
			outstr.flush();	
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Host remoto desconocido.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			socket.close();
			System.out.println("Conexión cerrada.");
		}
	}

	public Date getSynchronizedDate(){
		try {
			execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Algoritmo de Cristian
		remoteDate = remoteDate;
		localDate = localDate;
		//...
		//...
		//...
		
		return cristianDate;
	}
	
}


