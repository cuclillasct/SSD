package Petitions;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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

import Controllers.Server;
import Controllers.ServerThread;
import Interfaces.IFuturo;
import Interfaces.IMethodRequest;
import Models.ChunkedFile;
import Models.DataChunk;
import Util.GeneralUtils;
import Util.IOUtils;
import Views.Client;

public class DownloadFile implements IMethodRequest {

	String relativePath;
	
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
			
			outObj.writeObject(ServerThread.DESCARGAR_FICHERO);
			outObj.writeObject(relativePath); // "C:/Users/Jorge/Desktop\nexit");
			outObj.flush();
			
			System.out.println("Petición enviada: Descargar fichero " + relativePath);
			
			//InSocket
			ObjectInputStream instr = new ObjectInputStream(socket.getInputStream());
	
			long size = instr.readLong();
			System.out.println("Voy a recibir un archivo con "+ size +"paquete(s)");

			// Stream para escribir
			File file = new File(Client.folderPath + relativePath);
			FileOutputStream out = new FileOutputStream(file);
			BufferedOutputStream outstr = new BufferedOutputStream(out);
			DataChunk chunk;
			for (int i = 0; i < size; i++) {
				try {
					chunk = (DataChunk) instr.readObject();
					System.out.println("Leo chunk " + i);
					outstr.write(chunk.getData());
					chunk = null;
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			outstr.close();
			
			System.out.println("Cerrando conexión con el servidor...");
			outObj.writeObject("exit");
			outObj.flush();		
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
