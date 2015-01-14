package Petitions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Controllers.Server;
import Controllers.ServerThread;
import Interfaces.IFuturo;
import Interfaces.IMethodRequest;
import Models.ChunkedFile;
import Models.DataChunk;
import Util.GeneralUtils;
import Util.IOUtils;
import Views.Client;

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
			File file = new File(Client.folderPath + relativePath);
			FileInputStream in = new FileInputStream(file);
			BufferedInputStream input = new BufferedInputStream(in);
			byte[] b; int i = 0; long sizeInBytes = file.length(), sizeInPackets = sizeInBytes/DataChunk.CHUNK_MAX_SIZE + (sizeInBytes%DataChunk.CHUNK_MAX_SIZE > 0 ? 1 : 0);
			DataChunk chunk;
			
			//OutSocket
			ObjectOutputStream outstr = new ObjectOutputStream(socket.getOutputStream());
			
			outstr.writeObject(ServerThread.SUBIR_FICHERO);
			outstr.writeObject(relativePath); // "C:/Users/Jorge/Desktop\nexit");
			outstr.writeLong(sizeInPackets);
			outstr.flush();
			
			System.out.println("Petición enviada: Subir fichero " + relativePath);
			
			while (input.available() > 0) {
				System.out.println("Quedan: " + input.available());
				if (input.available() >= DataChunk.CHUNK_MAX_SIZE){
					b = new byte[DataChunk.CHUNK_MAX_SIZE];
					input.read(b, 0, b.length);
					chunk = new DataChunk(i++, b, DataChunk.CHUNK_MAX_SIZE);
					System.out.println("Leido localmente: " + (i-1));
				}else{
					b = new byte[input.available()];
					input.read(b);
					chunk = new DataChunk(i++, b, b.length);
					System.out.println("Leido localmente ultimo paquete: " + (i-1));
				}
				System.out.println("Enviando paquetes... " + (chunk.getnOrd()+1) + " de " + sizeInPackets);
				outstr.writeObject(chunk);
				outstr.flush();
				chunk = null; b = null; outstr.reset(); // Liberacion de recursos
			}
			input.close();
			
			//In
			ObjectInputStream instr = new ObjectInputStream(socket.getInputStream());
			
			System.out.println("Fichero "+ relativePath + " enviado" );
			
			System.out.println("Esperando cierre de conexión con el servidor...");
			outstr.writeObject("exit");
			outstr.flush();
//			while(!((String) instr.readObject()).equals("exit")){} //Esperamos que cierre el servidor
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Host remoto desconocido.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			socket.close();
			System.out.println("Conexión cerrada.");
		}
	}
}
