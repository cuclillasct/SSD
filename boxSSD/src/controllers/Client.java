package controllers;

import interfaces.IFuturo;
import interfaces.IObservadorFuturo;
import interfaces.IProxy;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.AbstractMap.SimpleEntry;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import utils.GeneralUtils;
import views.OldClient;
import models.CristianFuturo;
import models.FileList;
import net.contentobjects.jnotify.JNotifyListener;

public class Client implements IObservadorFuturo, JNotifyListener{
	
	public static final String [] kindsOfFuture = {"FileList", "CristianFuturo"};

	public static final String folderPath = GeneralUtils.getDirectory(0);
	
	Client client;
	IProxy proxy;
	FileList fileList;
	private Hashtable<String, IFuturo> tablaFuturos = new Hashtable<String, IFuturo>();
	
	//variables para Cristian
	public static final int iteraciones = 5;
	//long serversTime [] = new long[iteraciones];
	ArrayList<Long> serversTime = new ArrayList<>();
	long serverTimeSet;
	long difference;//donde guardamos la diferencia entre la hora cliente y servidor
	long initialTimes [] = new long [iteraciones];
	long finalTimes [] = new long [iteraciones];
	boolean cristianDone = false;
	
	//Lista de archivos donde lo verá el cliente
	JTextArea list;
	
	/**
	 * Constructor del cliente.
	 *
	 * @param prox = proxy del servidor con el que se conecta
	 * @throws IOException
	 */
	public Client(IProxy prox, JTextArea lst){
		proxy = prox;
	    fileList = new FileList();
	    client = this;
	    list = lst;
	}
	
	/**
	 * Método para sincronizar el cliente, único método visible de la clase
	 * @return void
	 */
	public void Sync (){
		cristianPetitions(); //Para sincronizar empezamos por algoritmo de Cristian
		//los métodos que se invocan como consecuencia de éste, realizan las operaciones para sincronizar
		//tanto la hora como los archivos
	}
		
	
	/*****************************************
	 * 
	 * Métodos de estructura OBJETO ACTIVO
	 * 
	 *****************************************
	 */
		  
	/**
	 * Recibe los resultados de los futuros
	 * Dependiendo del tipo de futuro realiza las acciones pertinentes
	 * y elimina el futuro de la lista
	 */
	@Override
	public void done(String idFuturo) {
		System.out.println("Futuro recibido con exito = " + idFuturo);
		IFuturo r = tablaFuturos.get(idFuturo);
		if (r == null){
			System.out.println("Cliente: codigo de futuro desconocido");
			
			//Si recibe una lista de archivos desde el servidor:
		}else if (r instanceof FileList){ 
			list.setText("Lista de archivos: \n");
			for (String string : ((HashMap<String, AbstractMap.SimpleEntry<byte[], Long>>) r.getResult()).keySet()) {
				list.append(string + "\n");// Mostramos la lista de archivos sincronizados
			}
			tablaFuturos.remove(r.getId());//eliminamos el futuro de la lista
			
			Path path = FileSystems.getDefault().getPath(OldClient.folderPath);//Obtiene la ruta de la carpeta local
			DirectoryStream<Path> list = listOfFiles(path);//toma los nombres de los archivos de la carpeta
			
			HashMap<String, AbstractMap.SimpleEntry<byte[], Long>> fileList = new HashMap<>();//crea la lista de los datos de archivos del cliente
			String str; 
			SimpleEntry<byte[], Long> valuePair;
			
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Path pth = (Path) iterator.next();
				str = OldClient.folderPath + pth.getFileName().toString();//extrae la ruta de cada archivo
				valuePair = new AbstractMap.SimpleEntry<byte[], Long>(GeneralUtils.getHash(str), GeneralUtils.getLastModifiedDate(str));
				fileList.put(pth.getFileName().toString(), valuePair);//guarda nombre, hash y hora en la lista de archivos
			}
			
			//encuentra los archivos que deben ser subidos y descargados:
			ArrayList<String> filesToDownload = filesToDownload((HashMap<String, AbstractMap.SimpleEntry<byte[], Long>>) r.getResult(), fileList);
			ArrayList<String> filesToUpload = filesToUpload((HashMap<String, AbstractMap.SimpleEntry<byte[], Long>>) r.getResult(), fileList);
			System.out.println("Descargando " + filesToDownload.size() + " archivos del servidor");
			proxy.downloadFiles(filesToDownload);//descarga los ficheros del server
			proxy.uploadFiles(filesToUpload);//sube al server lo necesario
			
			
		} else if(r instanceof CristianFuturo){//si recibe el resultado de una petición de hora
			
			finalTimes[r.getIntId()] = System.currentTimeMillis();//guarda la hora de llegada del paquete
			serversTime.add(r.getIntId(),(Long) r.getResult()); //guarda la hora que le llega de la peticion X (ID)
			
			if(r.getIntId() == (iteraciones-1)){
				cristianAlgorithm();//llama a la ejecución del algoritmo si ha llegado ya la última petición 
			}
			tablaFuturos.remove(r.getId());//elimina el futuro correspondiente
		}
		else{
			System.out.println("Cliente: formato de futuro desconocido");
		}
	}	    
    
	/**
	 * Prepara los futuros para recibir los datos de una peticion
	 * @param String petition to do
	 * @param String type of petition
	 * @param argument - numero de iteración del algoritmo de Cristian (ID ordenado)
	 * @return IFuturo preparado
	 */
	private IFuturo prepararFuturo(String petition, String type, int argument){
		IFuturo f = null;
		if (petition.equals(OldClient.clavesVistas[0])){//si se ha pulsado en sincronizar
			if(type.equals(kindsOfFuture[1])){//y la petición es de hora de Servidor
				f = new CristianFuturo(argument);
				f.attach(this);//prepara un futuro Cristian y se queda a la escucha
			}
			if(type.equals(kindsOfFuture[0])){//si la peticion es de lista de archivos
				f = new FileList();
				f.attach(this);//prepara y queda a la escucha
			}
		}else {
			return null;
		}
		tablaFuturos.put(f.getId(), f);//lo añade a la lista de futuros activos
		return f;
	}
	

	/*********************************************************************
	 * 
	 * Métodos internos
	 * 
	 *********************************************************************/
	
    /**
     * Realiza el número de peticiones de hora al servidor indicado en "iteraciones"
     */
    private void cristianPetitions(){
    	for (int i = 0; i < iteraciones; i++) {
			initialTimes[i] = System.currentTimeMillis();//guarda la hora a la que realiza la petición
			IFuturo future = prepararFuturo(OldClient.clavesVistas[0],kindsOfFuture[1], i);
			proxy.getCristianTime(future);//solicita la hora
		}
    }
    
    /**
     * Implementa el algoritmo de Cristian mediante el que se sincronizan cliente y servidor.
     * Actualiza la variable difference en la que se guarda la diferencia horaria entre cliente y servidor
     */
    private void cristianAlgorithm(){
    	long differences [] = new long [iteraciones];
    	long lessTime = Long.MAX_VALUE; int minorIter=0;
    	
    	for (int i = 0; i < differences.length; i++) {
			differences[i] = initialTimes[i] - finalTimes[i];//calcula el RTT
			if(differences[i] < lessTime){//si en esta iteración ha tardado menos que en las otras
				lessTime = differences[i];//actualiza el valor
				minorIter = i;//guarda el número de la iteración mejor
			}
		}
    	serverTimeSet = serversTime.get(minorIter) + (lessTime/2);//calcula la aproximación de la hora del servidor (en la mejor iteracion)
    	difference = serverTimeSet - finalTimes[minorIter];//obtiene la variación respecto a la hora cliente que tenía en esa iteracion
    
    	//cuando termina el algoritmo
    	System.out.println("Finalizado con éxito el algoritmo de Cristian");
    	System.out.println("Diferencia horaria entre cliente y servidor: "+ difference);
    	getLists();//realiza las siguientes acciones: comprobación de carpetas y descarga de archivos
    }
	
	/**
	 * obtiene los archivos que hay en un directorio dado (cliente)
	 * @param path
	 * @return DirectoryStream con los archivos de ese directorio
	 */
	private DirectoryStream<Path> listOfFiles(Path path){
		try {
			return Files.newDirectoryStream(path);//obtiene todo lo que contiene el directorio
		} catch (IOException e) {
			System.out.println("Cliente-> Error al leer la lista de archivos en: " + path.toString());
			e.printStackTrace();
		}
		return (DirectoryStream<Path>) new ArrayList<Path>();
	}

	/**
	 * Determina qué archivos necesitan ser descargados desde el servidor
	 * @param filesInServer
	 * @param filesInClient
	 * @return ArrayList con los nombres de los archivos a descargar
	 */
	private ArrayList<String> filesToDownload (HashMap<String, SimpleEntry<byte[], Long>> filesInServer, HashMap<String, SimpleEntry<byte[], Long>> filesInClient){
    	ArrayList<String> filesToDownload = new ArrayList<String>();
    	
    	for (String file : filesInServer.keySet()) {
			if (filesInClient.containsKey(file)) {// Si tenemos el archivo en el cliente, comprobamos
				if (!Arrays.equals(filesInServer.get(file).getKey(), filesInClient.get(file).getKey())) { // Comprueba los hash y si son distintos
					System.out.println("Archivo distinto encontrado en el servidor: " + file);
					long clientSyncTime = filesInClient.get(file).getValue() + difference;//Obtiene la fecha que tendría si el cliente tuviera la misma hora
					if (filesInServer.get(file).getValue() > clientSyncTime) { // Si se modificó después que en el cliente, lo descargamos
						System.out.println("Archivo más actual encontrado en el servidor: " + file);
						filesToDownload.add(file);
					}
				}
			}else {//si no lo tenemos, necesitamos descargarlo
				filesToDownload.add(file);
			}
		}
    	return filesToDownload;
    }
    
    /**
     * Compara las listas de archivos de cliente y servidor y devuelve una lista con los archivos que necesitan ser subidos al Server
     * @param filesInServer
     * @param filesInClient
     * @return files needed to upload
     */
    private ArrayList<String> filesToUpload (HashMap<String, SimpleEntry<byte[], Long>> filesInServer, HashMap<String, SimpleEntry<byte[], Long>> filesInClient){
    	ArrayList<String> filesToUpload = new ArrayList<String>();
    	
    	for (String file : filesInClient.keySet()) {
			if (filesInServer.containsKey(file)) {// Si el servidor tiene el archivo, comprobamos
				if (!Arrays.equals(filesInServer.get(file).getKey(), filesInClient.get(file).getKey())) { // Si son distintos
					System.out.println("Archivo distinto encontrado en el cliente: " + file);
					long serverSyncTime = filesInServer.get(file).getValue() + difference;//toma la hora que tendría en su sistema
					if (filesInClient.get(file).getValue() > serverSyncTime ) { // Si se modificó después que en el servidor
						System.out.println("Archivo más actual encontrado en el cliente: " + file);
						filesToUpload.add(file);
					}
				}
			}else {
				filesToUpload.add(file);//si no, lo subimos
			}
		}
    	return filesToUpload;
    }
    
    /**
     * Realiza las acciones necesarias después del algoritmo de cristian para sincronizar los archivos con los del servidor
     */
    private void getLists(){
		proxy.getFileList(Server.folderPath, prepararFuturo(OldClient.clavesVistas[0],kindsOfFuture[0], 0));//pide al servidor su lista (y descarga los necesarios)-> ver done(FileList)
    }
    
    /*
     ************************************************************
     */
    
    /**
     * Métodos de la interfaz JNotifyManager
     * Convertimos al cliente en obvservador de la carpeta
     */
	@Override
	public void fileCreated(int arg0, String arg1, String arg2) {
		System.out.println("Archivo creado: " + arg1 + " " + arg2);
		System.out.println("Sincronizando...");
		cristianPetitions();
	}

	@Override
	public void fileDeleted(int arg0, String arg1, String arg2) {
		System.out.println("Archivo eliminado: " + arg1 + arg2);
		System.out.println("Sincronizando...");
		cristianPetitions();	
	}

	@Override
	public void fileModified(int arg0, String arg1, String arg2) {
		System.out.println("Archivo modificado: " + arg1 + " " + arg2);
		System.out.println("Sincronizando...");
		cristianPetitions();	
	}

	@Override
	public void fileRenamed(int arg0, String arg1, String arg2, String arg3) {
		System.out.println("Archivo renombrado: " + arg1 + " " + arg2);
		System.out.println("Sincronizando...");
		cristianPetitions();	
	}
	
}
