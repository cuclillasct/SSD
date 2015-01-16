package controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Iterator;

import utils.GeneralUtils;
import models.DataChunk;

/**
 * hilos que sirven peticiones
 * del servidor
 */
public class ServerThread implements Runnable{
	
	//Tipos de peticiones
	public static final String SUBIR_FICHERO = "Subir";
	public static final String DESCARGAR_FICHERO = "Descargar";
	public static final String LISTA_FICHEROS = "Lista";
	public static final String SINCRONIZAR = "Sincronizar";

	public Socket soc;
	public final int id;
	Server sth;
	boolean postExecute;
	
	/**
	 * Constructor de hilo servidor de peticiones
	 * @param sock 
	 * @param id
	 * @param sth servidor asociado
	 */
	public ServerThread(Socket sock, int id, Server sth){
		soc = sock;
		this.id = id;
		this.sth = sth;
		this.postExecute = false;
	}
	
	public void run() {

		try {
			ObjectInputStream instr = new ObjectInputStream(soc.getInputStream());//recibe peticion

			String str = null;
			while(true){
				//Leemos la primera línea, que nos dice la función que queremos hacer
				str = (String) instr.readObject();
				System.out.println("Servidor-> Comando recibido: " + str);
				
				//según la petición, ejecutamos su método asociado
				if (str.equals(LISTA_FICHEROS)) {
					enviarListaFicheros(instr);
				}else if (str.equals(DESCARGAR_FICHERO)) {
					enviarArchivo(instr);
					break;
				}else if(str.equals(SUBIR_FICHERO)){
					recibirArchivo(instr);
					break;
				}else if(str.equals(SINCRONIZAR)){
					obtenerHora(instr);
				}else if(str.equals("exit")){
					System.out.println("Servidor-> Cerrando conexion con el cliente...");
					break;
				}else{
					break;
				}
			}  
		}catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			try {
				soc.close();
				System.out.println("Servidor-> Conexion cerrada");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/*
	 * Métodos internos
	 */
	
	/**
	 * Envía la lista de ficheros que contiene el servidor
	 * @param instr
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void enviarListaFicheros(ObjectInputStream instr) throws IOException, ClassNotFoundException{
		
		Path path = FileSystems.getDefault().getPath((String)instr.readObject());//obtiene la ruta
		DirectoryStream<Path> list = sth.listOfFiles(path);//obtiene los nombres de los archivos
		
		ObjectOutputStream outstr = new ObjectOutputStream(soc.getOutputStream());
		
		HashMap<String, SimpleEntry<byte[], Long>> fileList = new HashMap<>();
		String str;
		SimpleEntry<byte[], Long> valuePair;
		
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Path pth = (Path) iterator.next();
			str = Server.folderPath + pth.getFileName().toString();//toma un archivo
			//guarda su código hash y su fecha de modificación:
			valuePair = new SimpleEntry<byte[], Long>(GeneralUtils.getHash(str), GeneralUtils.getLastModifiedDate(str));
			fileList.put(pth.getFileName().toString(), valuePair);//lo añade a la lista
		}
		outstr.writeObject(fileList);//escribe la lista
		outstr.writeObject("exit");//cierra
		outstr.flush();//envía
	}
	
	/**
	 * Envía un archivo determinado al cliente
	 * @param instr
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void enviarArchivo(ObjectInputStream instr) throws IOException, ClassNotFoundException{
		//OutSocket
		ObjectOutputStream outstr = new ObjectOutputStream(soc.getOutputStream());
		String relativePath = (String) instr.readObject();//lee el nombre del archivo a enviar
		
		//InputStream de lectura
		File file = new File(Server.folderPath + relativePath);
		FileInputStream in = new FileInputStream(file);
		BufferedInputStream input = new BufferedInputStream(in);//buffer de lectura
		
		byte[] b; int i = 0; long sizeInBytes = file.length(), sizeInPackets = sizeInBytes/DataChunk.CHUNK_MAX_SIZE + (sizeInBytes%DataChunk.CHUNK_MAX_SIZE > 0 ? 1 : 0);
		DataChunk chunk;
		
		System.out.println("Servidor-> Enviando archivo " + relativePath + " de tamaño " + sizeInPackets + " paquetes");
		outstr.writeLong(sizeInPackets);
		
		while (input.available() > 0) { //mientras queden bytes por enviar
			if (input.available() >= DataChunk.CHUNK_MAX_SIZE){//si hay suficientes para llenar un paquete entero:
				b = new byte[DataChunk.CHUNK_MAX_SIZE];
				input.read(b, 0, b.length);//lee los bytes correspondientes
				chunk = new DataChunk(i++, b, DataChunk.CHUNK_MAX_SIZE);//crea el paquete
				
			}else{
				b = new byte[input.available()];//crea los datos del tamaño exacto restante
				input.read(b);
				chunk = new DataChunk(i++, b, b.length);//crea el último paquete
			}
			outstr.writeObject(chunk);//envía paquete
			outstr.flush();
			chunk = null; b = null; outstr.reset(); // Liberacion de recursos
		}
		
		input.close();//cierra el buffer de lectura
		
		outstr.writeObject("exit");//cierra el envio
		outstr.flush();
	}
	
	/**
	 * Recibe un archivo desde el cliente y lo escribe en el servidor
	 * @param instr
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void recibirArchivo(ObjectInputStream instr) throws IOException, ClassNotFoundException{
		
		String relativePath = (String) instr.readObject();//lee el nombre del archivo a recibir
		long size = instr.readLong();//lee el tamaño
		System.out.println("Servidor-> Voy a recibir el archivo "+ relativePath +" con "+ size +" paquete(s)");
		DataChunk chunk;
		
		// Stream para escribir
		File file = new File(Server.folderPath + relativePath);
		FileOutputStream out = new FileOutputStream(file);
		BufferedOutputStream outstr = new BufferedOutputStream(out);//buffer de escritura
		
		for (long i = 0; i < size; i++) {
			chunk = (DataChunk) instr.readObject();//lee cada paquete
			outstr.write(chunk.getData());//y lo escribe en el servidor
			chunk = null;
		}
		outstr.close();//cierra la escritura
		
		ObjectOutputStream outObj = new ObjectOutputStream(soc.getOutputStream());
		System.out.println("Servidor-> Recibido archivo. Cerrando conexión con el cliente...");
	}

	/**
	 * Envía la hora del servidor
	 * @param instr
	 * @throws IOException
	 */
	private void obtenerHora(ObjectInputStream instr) throws IOException{
		ObjectOutputStream outstr = new ObjectOutputStream(soc.getOutputStream());
		long servertime = System.currentTimeMillis();//Obtiene la hora del sistema
		outstr.writeObject(servertime);//la envía
		outstr.flush();
	}
}
