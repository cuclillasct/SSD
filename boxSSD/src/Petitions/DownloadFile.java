package Petitions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Interfaces.IMethodRequest;
import Models.DataChunk;
import Util.IOUtils;

public class DownloadFile implements IMethodRequest {

	String relativePath;
	Socket socket;	
	
	public DownloadFile(String relativePath, Socket socket) {
		this.relativePath = relativePath;
		this.socket = socket;
	}
	
	@Override
	public void execute() throws IOException {
		System.out.println("Conectando con: 127.0.0.1 Puerto: 52534");
		try {			
			//Out
			ObjectOutputStream outstr = new ObjectOutputStream(socket.getOutputStream());
			ArrayList<DataChunk> filePackages = IOUtils.readFile(relativePath);
			System.out.println("Enviando paquete de tamaño " + filePackages.size());
			outstr.writeInt(filePackages.size());
			for (DataChunk dataChunk : filePackages) {
				outstr.writeObject(dataChunk);
			}
			outstr.flush();
			System.out.println("Petición enviada: Lista de archivos");
			String str, result = "";
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			System.out.println("Cerrando conexión de archivo con el cliente...");
			socket.close();
			System.out.println("Conexión cerrada.");
		}
	}

}
