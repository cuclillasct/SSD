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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import Views.Client;
import Controllers.ServerThread;
import Interfaces.IFuturo;
import Interfaces.IMethodRequest;
import Models.FileList;

public class GetFileList implements IMethodRequest {
	
	String path;
	FileList fileList;
	
	public GetFileList(String path, IFuturo futuro) {
		// TODO Auto-generated constructor stub
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
			
			outstr.writeObject(ServerThread.LISTA_FICHEROS);
			outstr.writeObject(path); // "C:/Users/Jorge/Desktop\nexit");
			outstr.flush();
			System.out.println("Petición enviada: Lista de archivos");
			
			//In
			ObjectInputStream instr = new ObjectInputStream(socket.getInputStream());
			
			String str;
			HashMap<String, AbstractMap.SimpleEntry<Byte[], Long>> result = (HashMap<String, AbstractMap.SimpleEntry<Byte[], Long>>) instr.readObject();
			fileList.setResult(result);
			
			System.out.println("Cerrando conexión con el servidor...");
			outstr.writeObject("exit"); 
			outstr.flush();	
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Host remoto desconocido.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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


