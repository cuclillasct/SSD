package Controllers;

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
				}else if(str.equals(SUBIR_FICHERO)){
					receivedFile = recibirArchivo(instr);
					postExecute = true;
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
				
				//Postexecute para acelerar la comunicacion
				if (postExecute) {
					IOUtils.writeFile(Server.folderPath + receivedFile.getRelativePath(), receivedFile);
				}
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
		//Out
		ObjectOutputStream outstr = new ObjectOutputStream(soc.getOutputStream());
		String relativePath = (String) instr.readObject();
		ArrayList<DataChunk> filePackages = IOUtils.readFile(Server.folderPath + relativePath);
		System.out.println("Servidor-> Enviando paquete de tamaño " + filePackages.size());
		outstr.writeInt(filePackages.size());
		for (DataChunk dataChunk : filePackages) {
			System.out.println("Servidor-> Escribo chunk");
			outstr.writeObject(dataChunk);
		}
		outstr.flush();
	}
	
	private ChunkedFile recibirArchivo(ObjectInputStream instr) throws IOException, ClassNotFoundException{
		String relativePath = (String) instr.readObject();
		int size = instr.readInt();
		System.out.println("Servidor-> Voy a recibir el archivo "+ relativePath +" con "+ size +" paquete(s)");
		ArrayList<DataChunk> filePackages = new ArrayList<DataChunk>();
		for (int i = 0; i < size; i++) {
			filePackages.add((DataChunk) instr.readObject());
		}
		System.out.println("Servidor-> Recibido paquete de tamaño " + filePackages.size());
		
		ObjectOutputStream outstr = new ObjectOutputStream(soc.getOutputStream());
		System.out.println("Servidor-> Cerrando conexión con el cliente...");
		outstr.writeObject("exit");
		outstr.flush();
		
		ChunkedFile receivedFile =  new ChunkedFile(relativePath, null);
		receivedFile.setResult(filePackages);
		
		return receivedFile;
	}
}
