package Views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import Interfaces.IFuturo;
import Interfaces.IObservadorFuturo;
import Controllers.Server;
import Controllers.ServerProxy;
import Interfaces.IProxy;
import Models.CristianFuturo;
import Models.FileList;
import Util.GeneralUtils;
import Util.TextAreaOutputStream;

public class Client extends JFrame implements IObservadorFuturo{
	private static final long serialVersionUID = 1L;
	
	public static final String [] 
    		clavesVistas = {"SINCRONIZAR", "MONITORIZAR Y SINCRONIZAR", 
		"DESCONECTAR", "CAMBIAR DIRECTORIOS", "SALIR"};
	public static final String [] kindsOfFuture = {"FileList", "CristianFuturo"};

	String claveSeleccionada = null;
	
	JFrame frame;
	JLabel author;
	JTextArea console;
	public static JTextArea list;

	public static final String folderPath = System.getProperty("user.home") + "/Desktop/SSDClient/";
	
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
	
	/**
	 * Constructor del cliente.
	 *
	 * @param prox = proxy del servidor con el que se conecta
	 * @throws IOException
	 */
	public Client(IProxy prox) throws IOException{
		
       super("Trabajo Final SSD 2014/2015 3º Grado Ing. Telemática UPCT");
       
       proxy = prox;
       fileList = new FileList();
       
       frame = this;
       
       // Fijamos tamaño de la ventana.       
       setSize (800,450);
       setContentPane(new JLabel(new ImageIcon("path")));
       setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
       
       //Panel principal
       Box mainBox = new Box(BoxLayout.LINE_AXIS);
       getContentPane().add(mainBox);
       
       author = new JLabel();
       author.setText("Jorge Mendoza Saucedo y Elena Martín Seoane ©Copyright 2015");
       author.setPreferredSize(new Dimension(620,20));
	   author.setAlignmentX(CENTER_ALIGNMENT);
	   author.setForeground(Color.lightGray);
	   getContentPane().add(author); 
       
       // Creamos los tres paneles.
       PanelBotones butt = new PanelBotones();
       butt.setPreferredSize(new Dimension(220, 330));
       
       console = new JTextArea();
       console.setEditable(false);
       console.setLineWrap(true);
       console.setWrapStyleWord(true);
       console.setMargin(new Insets(10, 5, 10, 5));
    
	    // Now create a new TextAreaOutputStream to write to our JTextArea control and wrap a
	    // PrintStream around it to support the println/printf methods.
	    PrintStream out = new PrintStream( new TextAreaOutputStream( console ) );

	    // redirect standard output stream to the TextAreaOutputStream
	    System.setOut( out );

	    // redirect standard error stream to the TextAreaOutputStream
	    System.setErr( out );
    
       System.out.println("Registro del programa: \n>>");
       JScrollPane consolePane = new JScrollPane(console);
       consolePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
       
       list = new JTextArea();
       JScrollPane listPane = new JScrollPane(list);
       listPane.setHorizontalScrollBar(null);
       listPane.setPreferredSize(new Dimension(150,0));
       listPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
       list.append("Lista de archivos: \n");
       list.setMargin(new Insets(10, 5, 10, 5));
       
       // Añadimos paneles a la ventana (a su contentPane).
       mainBox.add(butt);
       mainBox.add(consolePane);
       mainBox.add(listPane);
       
       // Hacemos la ventana visible.
       setVisible(true);  
       
       // Forzamos a que la aplicación termine al cerrar la ventana.     
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

       new Server();
    }
	    
	    /**
		    * Clase interna: panel de botones
		    * Utiliza layout por defecto.
		    */
		    class PanelBotones extends JPanel implements ActionListener{
			private static final long serialVersionUID = 1L;

				JLabel lb;
		         
		         JButton btControl [] = new JButton[clavesVistas.length];
		         
		         /**
		          * construye un panel de botones con los determinados en el array de clavesVistas
		          */
			     public PanelBotones(){
			    	 
			    	 setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			    	 
			    	 setBackground(new Color(200,200,200,0));
			    	 add(Box.createVerticalGlue());
			    	 for (int i = 0; i < btControl.length; i++){
		        	        btControl[i] = new JButton(clavesVistas[i]);
		        	        btControl[i].addActionListener(this);
		        	        btControl[i].setAlignmentX(CENTER_ALIGNMENT);
		        	        btControl[i].setAlignmentY(CENTER_ALIGNMENT);
		        	        btControl[i].setMargin(new Insets(10, 10, 10, 10));
		 	            add(btControl[i]); 
		 	           add(Box.createVerticalGlue());
		 	        }
 	 
		         } 
		         
			     /**
			      * manejadores de los botones
			      * acciones a realizar cuando se pulsa un determinado botón
			      */
				@Override
				public void actionPerformed(ActionEvent arg0) {

					claveSeleccionada = arg0.getActionCommand();
					System.out.println("Click en: " + claveSeleccionada);
					
					switch (claveSeleccionada) {
					case "SINCRONIZAR":
						System.out.println("Sincronizando...");
						//comienza el algoritmo de Cristian
						cristianPetitions();						
						//los métodos que se invocan como consecuencia de éste, realizan las operaciones para sincronizar
						//tanto la hora como los archivos
						break;
						
					case "MONITORIZAR Y SINCRONIZAR":
						
						//código de monitorización

						break;
						
					case "DESCONECTAR":
						
						//es necesario solo si está monitorizando (?)
						
						break;
						
					case "SALIR":
						frame.dispose();
						System.exit(1);
						break;
						
					default:
						break;
					}
				}                	     	    
			}
		  
		    
	/*
	 * Métodos de estructura Objeto Activo
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
			
			Path path = FileSystems.getDefault().getPath(Client.folderPath);//Obtiene la ruta de la carpeta local
			DirectoryStream<Path> list = listOfFiles(path);//toma los nombres de los archivos de la carpeta
			
			HashMap<String, AbstractMap.SimpleEntry<byte[], Long>> fileList = new HashMap<>();//crea la lista de los datos de archivos del cliente
			String str; 
			SimpleEntry<byte[], Long> valuePair;
			
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Path pth = (Path) iterator.next();
				str = Client.folderPath + pth.getFileName().toString();//extrae la ruta de cada archivo
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
		if (petition.equals(clavesVistas[0])){//si se ha pulsado en sincronizar
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
	 * obtiene los archivos que hay en un directorio dado (cliente)
	 * @param path
	 * @return DirectoryStream con los archivos de ese directorio
	 */
	public DirectoryStream<Path> listOfFiles(Path path){
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
    public ArrayList<String> filesToDownload (HashMap<String, SimpleEntry<byte[], Long>> filesInServer, HashMap<String, SimpleEntry<byte[], Long>> filesInClient){
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
    public ArrayList<String> filesToUpload (HashMap<String, SimpleEntry<byte[], Long>> filesInServer, HashMap<String, SimpleEntry<byte[], Long>> filesInClient){
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
     * Realiza el número de peticiones de hora al servidor indicado en "iteraciones"
     */
    public void cristianPetitions(){
    	
    	for (int i = 0; i < iteraciones; i++) {
			initialTimes[i] = System.currentTimeMillis();//guarda la hora a la que realiza la petición
			IFuturo future = prepararFuturo(clavesVistas[0],kindsOfFuture[1], i);
			proxy.getCristianTime(future);//solicita la hora
		}
    
    }
    
    /**
     * Implementa el algoritmo de Cristian mediante el que se sincronizan cliente y servidor.
     * Actualiza la variable difference en la que se guarda la diferencia horaria entre cliente y servidor
     */
    public void cristianAlgorithm(){
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
     * Realiza las acciones necesarias después del algoritmo de cristian para sincronizar los archivos con los del servidor
     */
    public void getLists(){

		proxy.getFileList(Server.folderPath, prepararFuturo(clavesVistas[0],kindsOfFuture[0], 0));//pide al servidor su lista (y descarga los necesarios)-> ver done(FileList)
		DirectoryStream<Path> dirList = listOfFiles(FileSystems.getDefault().getPath(folderPath));//obtiene la lista del cliente

    }
    
    /*
     *  Metodo main que inicia el programa
     */
    public static void main(String [] args) throws IOException{
		ServerProxy pr = new ServerProxy();
		Client cliente = new Client(pr);
    }
    
}