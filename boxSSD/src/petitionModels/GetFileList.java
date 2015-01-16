package petitionModels;

import interfaces.IFuturo;
import interfaces.IMethodRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

import controllers.ServerThread;
import models.FileList;

/**
 * Clase que modela la petición para 
 * obtener la lista de archivos
 */
public class GetFileList implements IMethodRequest {
	
	String path;//ruta servidor
	FileList fileList;//futuro
	
	public GetFileList(String path, IFuturo futuro) {
		this.path = path;
		this.fileList = (FileList) futuro;
	}

	@Override
	public void execute() throws IOException {
		Socket socket = new Socket("127.0.0.1", 52534);
		System.out.println("Conectando con: 127.0.0.1 Puerto: 52534");
		try {			

			//Out
			ObjectOutputStream outstr = new ObjectOutputStream(socket.getOutputStream());
			
			outstr.writeObject(ServerThread.LISTA_FICHEROS);//petición de obtener lista
			outstr.writeObject(path); //argumento: ruta de la carpeta
			outstr.flush();
			System.out.println("Petición enviada: Lista de archivos");
			
			//In
			ObjectInputStream instr = new ObjectInputStream(socket.getInputStream());//recibe respuesta
			
			HashMap<String, SimpleEntry<byte[], Long>> result = (HashMap<String, SimpleEntry<byte[], Long>>) instr.readObject();
			fileList.setResult(result);//pasa el resultado al cliente
			
			System.out.println("Cerrando conexión con el servidor...");
			outstr.writeObject("exit"); 
			outstr.flush();	
		} catch (UnknownHostException e) {
			System.out.println("Host remoto desconocido.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally{
			socket.close();
			System.out.println("Conexión cerrada.");
		}
	}
}


