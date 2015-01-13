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

import Controllers.ServerThread;
import Interfaces.IFuturo;
import Interfaces.IMethodRequest;
import Models.ChunkedFile;
import Models.DataChunk;
import Util.GeneralUtils;
import Util.IOUtils;

public class DownloadFile implements IMethodRequest {

	ChunkedFile chunkedFile;
	
	public DownloadFile(IFuturo future) {
		this.chunkedFile = (ChunkedFile) future;
	}
	
	@Override
	public void execute() throws IOException {
		Socket socket = new Socket("127.0.0.1", 52534);
		System.out.println("Conectando con: 127.0.0.1 Puerto: 52534");
		
		try {
			//Out
			ObjectOutputStream outstr = new ObjectOutputStream(socket.getOutputStream());
			
			outstr.writeObject(ServerThread.DESCARGAR_FICHERO);
			outstr.writeObject(chunkedFile.getRelativePath()); // "C:/Users/Jorge/Desktop\nexit");
			outstr.flush();
			
			System.out.println("Petici�n enviada: Descargar fichero " + chunkedFile.getRelativePath());
			
			//In
			ObjectInputStream instr = new ObjectInputStream(socket.getInputStream());
	
			int size = instr.readInt();
			System.out.println("Voy a recibir un archivo con "+ size +"paquete(s)");
			ArrayList<DataChunk> packages = new ArrayList<DataChunk>();
			for (int i = 0; i < size; i++) {
				try {
					System.out.println("Leo chunk");
					packages.add((DataChunk) instr.readObject());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			chunkedFile.setResult(GeneralUtils.orderPackages(packages));
			
			System.out.println("Cerrando conexi�n con el servidor...");
			outstr.writeObject("exit");
			outstr.flush();		
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Host remoto desconocido.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			socket.close();
			System.out.println("Conexi�n cerrada.");
		}
	}

}
