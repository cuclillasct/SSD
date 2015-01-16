
package controllers;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JTextArea;

import utils.GeneralUtils;
import views.OldClient;

/**
 * Clase que modela al servidor
 *
 */
public class Server {
	
	//Carpeta de archivos del servidor:
	public static final String folderPath = GeneralUtils.getDirectory(1);
	
	boolean bool = true;
	Socket soc = null;
	ServerSocket ss = null;
	ArrayList<ServerThread> serverThreads = new ArrayList<>();
	int port = 52534;
	
	public Server(){
		
		// Server, crea descriptor socket y hace bind a puerto
		int id = 0;
		try {
			ss = new ServerSocket(port);
			System.out.println("Servidor-> Servidor listo en puerto "+port);
			ExecutorService exec = Executors.newFixedThreadPool(5);
			while(true){
				try {
					soc = ss.accept();// Bloquea si no hay peticiones
					ServerThread thread = new ServerThread(soc,id++,this);
					serverThreads.add(thread);//si hay una nueva petición crea un hilo para servirla
					exec.execute(thread);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			System.out.println("Servidor-> No se ha podido establecer el servidor:");
			e.printStackTrace();
		}
		
		finally {
			try {
				ss.close();//cierra la conexión
			} catch (IOException e) {
				System.out.println("Servidor-> Fallo al cerrar la conexión:");
				e.printStackTrace();
			}
		}
	}

	
	/*
	 * Métodos internos del servidor
	 */
	
	/**
	 * Obtiene una lista con los nombres de los archivos de la carpeta local del servidor
	 * @param path carpeta a leer
	 * @return DirectoryStream con nombres de archivos
	 */
	public DirectoryStream<Path> listOfFiles(Path path){
		try {
			return Files.newDirectoryStream(path);
		} catch (IOException e) {
			System.out.println("Servidor-> Error al leer la lista de archivos en: " + path.toString());
			e.printStackTrace();
		}
		return (DirectoryStream<Path>) new ArrayList<Path>();
	}
	
    public static void main(String [] args) throws IOException{
    	Server server = new Server();
    }
	
}
