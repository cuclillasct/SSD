package Views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
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
import Models.ChunkedFile;
import Models.CristianFuturo;
import Models.DataChunk;
import Models.FileList;
import Util.GeneralUtils;
import Util.TextAreaOutputStream;

public class Client extends JFrame implements IObservadorFuturo{
	
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
	
	
	public Client(IProxy prox) throws IOException{
		
       super("Trabajo Final SSD 2014/2015 3� Grado Ing. Telem�tica UPCT");
       
       proxy = prox;
       fileList = new FileList();
       
       frame = this;
       
       // Fijamos tama�o de la ventana.       
       setSize (800,450);
       setContentPane(new JLabel(new ImageIcon("path")));
       setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
       
       //Panel principal
       Box mainBox = new Box(BoxLayout.LINE_AXIS);
       getContentPane().add(mainBox);
       
       author = new JLabel();
       author.setText("Jorge Mendoza Saucedo y Elena Mart�n Seonae �Copyright 2015");
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
//       System.setOut( out );

    // redirect standard error stream to the TextAreaOutputStream
//       System.setErr( out );
    
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
       
       // A�adimos paneles a la ventana (a su contentPane).
       mainBox.add(butt);
       mainBox.add(consolePane);
       mainBox.add(listPane);
       
       // Hacemos la ventana visible.
       setVisible(true);  
       
       // Forzamos a que la aplicaci�n termine al cerrar la ventana.     
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

       new Server();
    }
	    
	    /**
		    * Clase interna: 
		    * Utiliza layout por defecto.
		    */
		    class PanelBotones extends JPanel implements ActionListener{

		         JLabel lb;
		         
		         JButton btControl [] = new JButton[clavesVistas.length];
		     
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
		         

				@Override
				public void actionPerformed(ActionEvent arg0) {

					//Object[] opt = {1,2,3,4,5,6,7,8,9,10};
					claveSeleccionada = arg0.getActionCommand();
					System.out.println("Click en: " + claveSeleccionada);
					switch (claveSeleccionada) {
					case "SINCRONIZAR":
						System.out.println("Sincronizando...");
						//first
						cristianPetitions();
						
						//IMPORTANTE: DEBE ESPERAR A QUE TODAS LAS PETICIONES SE HAYAN CONTESTADO Y SE HAYA EJECUTADO EL ALGORITMO
						
						//scnd
						proxy.getFileList(Server.folderPath, prepararFuturo(clavesVistas[0],kindsOfFuture[0], 0));
						DirectoryStream<Path> dirList = listOfFiles(FileSystems.getDefault().getPath(folderPath));
						System.out.println(dirList);
						break;
					case "MONITORIZAR Y SINCRONIZAR":
						//ArrayList<String> filesToDownload = filesToDownload();
						//filesToDownload.add("prueba.txt");
						//proxy.downloadFiles(filesToDownload);
						break;
					case "DESCONECTAR":
						ArrayList<String> filesToUpload = new ArrayList<String>();
						filesToUpload.add("video.mp4");
						proxy.uploadFiles(filesToUpload);
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
	 * M�todos de estructura Objeto Activo
	 */
		    
	@Override
	public void done(String idFuturo) {
		System.out.println("Futuro recibido con exito = " + idFuturo);
		IFuturo r = tablaFuturos.get(idFuturo);
		if (r == null){
			System.out.println("Cliente: codigo de futuro desconocido");
			
		}else if (r instanceof FileList){ // Mostramos la lista de archivos sincronizados
			list.setText("Lista de archivos: \n");
			for (String string : ((HashMap<String, AbstractMap.SimpleEntry<Byte[], Long>>) r.getResult()).keySet()) {
				list.append(string + "\n");
			}
			tablaFuturos.remove(r.getId());
			
			Path path = FileSystems.getDefault().getPath(Client.folderPath);
			System.out.println("Servidor-> " + path.toString()+"\n");
			DirectoryStream<Path> list = listOfFiles(path);
			
			HashMap<String, AbstractMap.SimpleEntry<byte[], Long>> fileList = new HashMap<String, AbstractMap.SimpleEntry<byte[], Long>>();
			String str; //Long date; Byte[] hash;
			SimpleEntry<byte[], Long> valuePair;
			
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Path pth = (Path) iterator.next();
				str = Client.folderPath + pth.getFileName().toString();
				valuePair = new AbstractMap.SimpleEntry<byte[], Long>(GeneralUtils.getHash(str), GeneralUtils.getLastModifiedDate(str));
				fileList.put(pth.getFileName().toString(), valuePair);
			}
			
			ArrayList<String> filesToDownload = filesToDownload((HashMap<String, AbstractMap.SimpleEntry<byte[], Long>>) r.getResult(), fileList);
			ArrayList<String> filesToUpload = filesToUpload((HashMap<String, AbstractMap.SimpleEntry<byte[], Long>>) r.getResult(), fileList);
			System.out.println("Descargando " + filesToDownload.size() + " archivos del servidor");
			proxy.downloadFiles(filesToDownload);
			proxy.uploadFiles(filesToUpload);
			
		} else if(r instanceof CristianFuturo){
			serversTime.add(r.getIntId(),(Long) r.getResult());
			finalTimes[r.getIntId()] = System.currentTimeMillis();
			cristianAlgorithm(r.getIntId());
			tablaFuturos.remove(r.getId());
		}
		else{
			System.out.println("Cliente: formato de futuro desconocido");
		}
	}	    
    
	private IFuturo prepararFuturo(String petition, String type, int argument){//elimino el ", String argument")
		IFuturo f = null;
		if (petition.equals(clavesVistas[0])){
			if(type.equals(kindsOfFuture[1])){
				f = new CristianFuturo(argument);
				f.attach(this);
			}
			if(type.equals(kindsOfFuture[0])){
				f = new FileList();
				f.attach(this);
			}
		}else {
			return null;
		}
		tablaFuturos.put(f.getId(), f);
		return f;
	}
	
	/*********************************************************************
	 * 
	 * M�todos internos
	 * 
	 *********************************************************************/
	public DirectoryStream<Path> listOfFiles(Path path){
		try {
			return Files.newDirectoryStream(path);
		} catch (IOException e) {
			System.out.println("Cliente-> Error al leer la lista de archivos en: " + path.toString());
			e.printStackTrace();
		}
		return (DirectoryStream<Path>) new ArrayList<Path>();
	}

    public ArrayList<String> filesToDownload (HashMap<String, AbstractMap.SimpleEntry<byte[], Long>> filesInServer, HashMap<String, AbstractMap.SimpleEntry<byte[], Long>> filesInClient){
    	ArrayList<String> filesToDownload = new ArrayList<String>();
    	for (String file : filesInServer.keySet()) {
			if (filesInClient.containsKey(file)) {// Si tenemos el archivo en el cliente, comprobamos
				if (!filesInServer.get(file).getKey().equals(filesInClient.get(file).getKey())) { // Si son distintos
					System.out.println("Archivo distinto encontrado en el servidor: " + file);
					long clientSyncTime = filesInClient.get(file).getValue() + difference;
					if (filesInServer.get(file).getValue() > clientSyncTime) { // Si se modific� despu�s que en el cliente, lo descargamos
						System.out.println("Archivo m�s actual encontrado en el servidor: " + file);
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
    public ArrayList<String> filesToUpload (HashMap<String, AbstractMap.SimpleEntry<byte[], Long>> filesInServer, HashMap<String, AbstractMap.SimpleEntry<byte[], Long>> filesInClient){
    	ArrayList<String> filesToUpload = new ArrayList<String>();
    	for (String file : filesInClient.keySet()) {
			if (filesInServer.containsKey(file)) {// Si el servidor tiene el archivo, comprobamos
				if (!filesInClient.get(file).getKey().equals(filesInServer.get(file).getKey())) { // Si son distintos
					System.out.println("Archivo distinto encontrado en el cliente: " + file);
					long serverSyncTime = filesInServer.get(file).getValue() + difference;
					if (filesInClient.get(file).getValue() > serverSyncTime ) { // Si se modific� despu�s que en el servidor
						System.out.println("Archivo m�s actual encontrado en el cliente: " + file);
						filesToUpload.add(file);
					}
				}
			}else {
				filesToUpload.add(file);//si no, lo subimos
			}
		}
    	return filesToUpload;
    }
    
    public void cristianPetitions(){//cristianPetitions(int iterations) 
    	
    	for (int i = 0; i < iteraciones; i++) {
			initialTimes[i] = System.currentTimeMillis();
			IFuturo future = prepararFuturo(clavesVistas[0],kindsOfFuture[1], i);
			proxy.getCristianTime(future);
		}
    
    }
    
    public void cristianAlgorithm(int number){
    	//synchronized(cristianDone){ no se pueden sincronizar ni booleans ni n�
	    if(number == (iteraciones-1) ){
	    	long differences [] = new long [iteraciones];
	    	long lessTime = Long.MAX_VALUE; int minorIter=0;
	    	for (int i = 0; i < differences.length; i++) {
				differences[i] = initialTimes[i] - finalTimes[i];
				if(differences[i] < lessTime){
					lessTime = differences[i];
					minorIter = i;
				}
			}
	    	serverTimeSet = serversTime.get(minorIter) + (lessTime/2);
	    	difference = serverTimeSet - finalTimes[minorIter];
	    }
    	//}
    }
    
    /*
     *  Metodo main que inicia el programa
     */
    public static void main(String [] args) throws IOException{
		ServerProxy pr = new ServerProxy();
		Client cliente = new Client(pr);
    }
    
}