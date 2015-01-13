package Controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import Models.ChunkedFile;
import Models.DataChunk;
import Util.IOUtils;
import Views.Client;

public class ServerThread implements Runnable{
	
	public static final String SUBIR_FICHERO = "Subir";
	public static final String DESCARGAR_FICHERO = "Descargar";
	public static final String LISTA_FICHEROS = "Lista";
	public static final String SINCRONIZAR = "Sincronizar";

	public Socket soc;
	public final int id;
	Server sth;
	boolean postExecute;
	
	public ServerThread(Socket sock, int id, Server sth){
		soc = sock;
		this.id = id;
		this.sth = sth;
		this.postExecute = false;
	}
	
	public void run() {
		ChunkedFile receivedFile = null;
		try {
			ObjectInputStream instr = new ObjectInputStream(soc.getInputStream());
			System.out.println("Servidor-> input stream...");
			boolean bool = true;
			String str = "";
			while(bool){
				//Leemos la primera línea, que nos dice la función que queremos hacer
				str = (String) instr.readObject();
				System.out.println("Servidor-> Comando recibido: " + str);
				if (str.equals(LISTA_FICHEROS)) {
					enviarListaFicheros(instr);
				}else if (str.equals(DESCARGAR_FICHERO)) {
					enviarArchivo(instr);
					break;
				}else if(str.equals(SUBIR_FICHERO)){
					recibirArchivo(instr);
					break;
				}else if(str.equals("exit")){
					System.out.println("Servidor-> Cerrando conexion con el cliente...");
					break;
				}else{
					break;
				}
			}  
		}catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				soc.close();
				System.out.println("Servidor-> Conexion cerrada");
			} catch (IOException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	/*
	 * Métodos internos
	 */
	
	private void enviarListaFicheros(ObjectInputStream instr) throws IOException, ClassNotFoundException{
		Path path = FileSystems.getDefault().getPath((String)instr.readObject());
		System.out.println("Servidor-> " + path.toString()+"\n");
		DirectoryStream<Path> list = sth.listOfFiles(path);
		
		ObjectOutputStream outstr = new ObjectOutputStream(soc.getOutputStream());
		
		for (Iterator iterator = list.iterator(); iterator
				.hasNext();) {
			Path pth = (Path) iterator.next();
			outstr.writeObject(pth.getFileName().toString());
			System.out.println("Servidor-> "+ pth.getFileName().toString());
		}
		outstr.writeObject("exit");
		outstr.flush();
	}
	
	private void enviarArchivo(ObjectInputStream instr) throws IOException, ClassNotFoundException{
		//OutSocket
		ObjectOutputStream outstr = new ObjectOutputStream(soc.getOutputStream());
		String relativePath = (String) instr.readObject();
		
		//InputStream de lectura
		File file = new File(Server.folderPath + relativePath);
		FileInputStream in = new FileInputStream(file);
		BufferedInputStream input = new BufferedInputStream(in);
		byte[] b; int i = 0; long sizeInBytes = file.length(), sizeInPackets = sizeInBytes/DataChunk.CHUNK_MAX_SIZE + (sizeInBytes%DataChunk.CHUNK_MAX_SIZE > 0 ? 1 : 0);
//		if (sizeInBytes > DataChunk.CHUNK_MAX_SIZE) {
//			sizeInPackets = (sizeInBytes%DataChunk.CHUNK_MAX_SIZE)+1;
//		}else sizeInPackets = 1; 
		DataChunk chunk;
		
		System.out.println("Servidor-> Enviando archivo de tamaño " + sizeInPackets + " paquetes");
		outstr.writeLong(sizeInPackets);
		
		while (input.available() > 0) {
			System.out.println("Servidor-> Quedan: " + input.available());
			if (input.available() >= DataChunk.CHUNK_MAX_SIZE){
				b = new byte[DataChunk.CHUNK_MAX_SIZE];
				input.read(b, 0, b.length);
				chunk = new DataChunk(i++, b, DataChunk.CHUNK_MAX_SIZE);
				System.out.println("Servidor-> Leido localmente: " + (i-1));
			}else{
				b = new byte[input.available()];
				input.read(b);
				chunk = new DataChunk(i++, b, b.length);
				System.out.println("Servidor-> Leido localmente ultimo paquete: " + (i-1));
			}
			System.out.println("Servidor-> Enviando paquetes... " + (chunk.getnOrd()+1) + " de " + sizeInPackets);
			outstr.writeObject(chunk);
			outstr.flush();
			chunk = null; b = null;
		}
		input.close();
		
		outstr.writeObject("exit");
		outstr.flush();
	}
	
	private void recibirArchivo(ObjectInputStream instr) throws IOException, ClassNotFoundException{
		String relativePath = (String) instr.readObject();
		long size = instr.readLong();
		System.out.println("Servidor-> Voy a recibir el archivo "+ relativePath +" con "+ size +" paquete(s)");
		DataChunk chunk;
		
		// Stream para escribir
		File file = new File(Server.folderPath + relativePath);
		FileOutputStream out = new FileOutputStream(file);
		BufferedOutputStream outstr = new BufferedOutputStream(out);
		for (long i = 0; i < size; i++) {
			chunk = (DataChunk) instr.readObject();
			System.out.println("Servidor-> Recibido paquete de tamaño " + chunk.getSize() + " byte(s)");
			outstr.write(chunk.getData());
			chunk = null;
		}
		outstr.close();
		
		ObjectOutputStream outObj = new ObjectOutputStream(soc.getOutputStream());
		System.out.println("Servidor-> Cerrando conexión con el cliente...");
		outObj.writeObject("exit");
		outObj.flush();
	}
}
