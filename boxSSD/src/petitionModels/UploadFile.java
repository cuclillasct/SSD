package petitionModels;

import interfaces.IMethodRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import views.OldClient;
import controllers.ServerThread;
import models.DataChunk;

/**
 * Clase modeladora de la petición 
 * de subida de fichero
 */
public class UploadFile implements IMethodRequest {

	String relativePath;
	
	public UploadFile(String path) {
		this.relativePath = path;
	}
	
	@Override
	public void execute() throws IOException {
		Socket socket = new Socket("127.0.0.1", 52534);
		System.out.println("Conectando con: 127.0.0.1 Puerto: 52534");
		
		try {
			//InputStream de lectura
			File file = new File(OldClient.folderPath + relativePath);
			FileInputStream in = new FileInputStream(file);
			BufferedInputStream input = new BufferedInputStream(in);//buffer de lectura para subir
			byte[] b; int i = 0; long sizeInBytes = file.length(), sizeInPackets = sizeInBytes/DataChunk.CHUNK_MAX_SIZE + (sizeInBytes%DataChunk.CHUNK_MAX_SIZE > 0 ? 1 : 0);
			DataChunk chunk;
			
			//OutSocket
			ObjectOutputStream outstr = new ObjectOutputStream(socket.getOutputStream());
			
			outstr.writeObject(ServerThread.SUBIR_FICHERO);//petición de subir archivo
			outstr.writeObject(relativePath); //argumento: ruta de archivo
			outstr.writeLong(sizeInPackets); //argumento: paquetes (DataChunk) que va a recibir
			outstr.flush();
			
			System.out.println("Petición enviada: Subir fichero " + relativePath);
			
			while (input.available() > 0) {
				if (input.available() >= DataChunk.CHUNK_MAX_SIZE){//si quedan para llenar un paquete entero:
					b = new byte[DataChunk.CHUNK_MAX_SIZE];
					input.read(b, 0, b.length);//lee esos bytes
					chunk = new DataChunk(i++, b, DataChunk.CHUNK_MAX_SIZE);//crea un paquete con ellos
				}else{//el último paquete
					b = new byte[input.available()];//del tamaño exacto restante
					input.read(b);
					chunk = new DataChunk(i++, b, b.length);//crea el paquete restante
				}
				outstr.writeObject(chunk);//envía el paquete
				outstr.flush();
				chunk = null; b = null; outstr.reset(); // Liberacion de recursos
			}
			input.close();//cierra el buffer
			
			System.out.println("Fichero "+ relativePath + " enviado" );
			
			outstr.writeObject("exit");
			outstr.flush();//cerramos la conexión

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
