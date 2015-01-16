package petitionModels;

import interfaces.IMethodRequest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import views.OldClient;
import controllers.ServerThread;
import models.DataChunk;

/**
 * Clase modeladora de la petición 
 * de descarga de fichero
 */
public class DownloadFile implements IMethodRequest {

	String relativePath;
	
	/**constructor de la petición*/
	public DownloadFile(String path) {
		this.relativePath = path;
	}
	
	@Override
	public void execute() throws IOException {
		Socket socket = new Socket("127.0.0.1", 52534);
		System.out.println("Conectando con: 127.0.0.1 Puerto: 52534");
		
		try {
			//OutSocket
			ObjectOutputStream outObj = new ObjectOutputStream(socket.getOutputStream());
			
			outObj.writeObject(ServerThread.DESCARGAR_FICHERO); //envía la petición al server
			outObj.writeObject(relativePath); // envía el argumento que necesitará (archivo a descargar)
			outObj.flush();
			
			System.out.println("Petición enviada: Descargar fichero " + relativePath);
			
			//InSocket
			ObjectInputStream instr = new ObjectInputStream(socket.getInputStream());//recibe respuesta
	
			long size = instr.readLong();
			System.out.println("Voy a recibir un archivo con "+ size +"paquete(s)");

			// Stream para escribir
			File file = new File(OldClient.folderPath + relativePath);
			FileOutputStream out = new FileOutputStream(file);//para escribir el archivo
			BufferedOutputStream outstr = new BufferedOutputStream(out);//usamos bufferado por la memoria
			DataChunk chunk;
			for (int i = 0; i < size; i++) {
				try {
					chunk = (DataChunk) instr.readObject();//coge el paquete
					outstr.write(chunk.getData());//y lo escribe
					chunk = null;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			outstr.close();
			
			System.out.println("Cerrando conexión con el servidor...");
			outObj.writeObject("exit");
			outObj.flush();		
		} catch (UnknownHostException e) {
			System.out.println("Host remoto desconocido.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			socket.close();
			System.out.println("Conexión cerrada.");
		}
	}

}
