package petitionModels;

import interfaces.IFuturo;
import interfaces.IMethodRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import controllers.ServerThread;
import models.CristianFuturo;

/**
 * Modela la petición de hora
 * necesaria para implementar el algoritmo
 * de Cristian
 */
public class GetCristianTime implements IMethodRequest {
	
	long remoteDate;//result
	CristianFuturo future;
	
	public GetCristianTime(IFuturo futuro){	
		this.future = (CristianFuturo) futuro;
	}


	@Override
	public void execute() throws IOException {
		Socket socket = new Socket("127.0.0.1", 52534);
		System.out.println("Conectando con: 127.0.0.1 Puerto: 52534");
		try {			

			//Out
			ObjectOutputStream outstr = new ObjectOutputStream(socket.getOutputStream());
			
			outstr.writeObject(ServerThread.SINCRONIZAR);//Envía petición
			outstr.flush();
			System.out.println("Petición enviada: Sincronizar con Cristian");
			
			//In
			ObjectInputStream instr = new ObjectInputStream(socket.getInputStream());//recibe respuesta
			
			remoteDate = (long) instr.readObject();
			
			System.out.println("Cerrando conexión con el servidor...");
			outstr.writeObject("exit"); 
			outstr.flush();	
			
			future.setResult(remoteDate);//pasa la respuesta al cliente
			
		} catch (UnknownHostException e) {
			System.out.println("Host remoto desconocido.");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally{
			socket.close();
			System.out.println("Conexión cerrada.");
		}
	}

	
}


