package Petitions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import Controllers.ServerThread;
import Interfaces.IFuturo;
import Interfaces.IMethodRequest;
import Models.CristianFuturo;

public class GetCristianTime implements IMethodRequest {
	
	long remoteDate;//result
//	int petitionsNumber = 5;
	CristianFuturo future;
	
	public GetCristianTime(IFuturo futuro){	
		this.future = (CristianFuturo) futuro;
	}
	
//	public GetCristianTime(int numberOfConnections, IFuturo futuro) {
//		this.petitionsNumber = numberOfConnections;
//		this.future = (CristianFuturo) futuro;
//	}

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
			
			remoteDate = (long) instr.readObject();
			
			System.out.println("Cerrando conexión con el servidor...");
			outstr.writeObject("exit"); 
			outstr.flush();	
			
			future.setResult(remoteDate);
			
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

	
}


