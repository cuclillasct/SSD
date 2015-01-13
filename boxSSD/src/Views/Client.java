package Views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Hashtable;

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
import Models.DataChunk;
import Models.FileList;
import Util.IOUtils;
import Util.TextAreaOutputStream;

public class Client extends JFrame implements IObservadorFuturo{

	
	
	public static final String [] 
    		clavesVistas = {"SINCRONIZAR", "MONITORIZAR Y SINCRONIZAR", 
		"DESCONECTAR", "CAMBIAR DIRECTORIOS", "SALIR"};
	

	String claveSeleccionada = null;
	
	JFrame frame;
	JLabel author;
	JTextArea console;
	public static JTextArea list;

	public static final String folderPath = System.getProperty("user.home") + "/Desktop/SSDClient";
	
	IProxy proxy;
	FileList fileList;
	private Hashtable<String, IFuturo> tablaFuturos = new Hashtable<String, IFuturo>();
	
	public Client(IProxy prox) throws IOException{
		
       super("Trabajo Final SSD 2014/2015 3º Grado Ing. Telemática UPCT");
    
       proxy = prox;
       
       frame = this;
       
       // Fijamos tamaño de la ventana.       
       setSize (700,400);
       setContentPane(new JLabel(new ImageIcon("path")));
       setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
       
       //Panel principal
       Box mainBox = new Box(BoxLayout.LINE_AXIS);
       getContentPane().add(mainBox);
       
       author = new JLabel();
       author.setText("Jorge Mendoza Saucedo y Elena Martín Seonae ©Copyright 2015");
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
       //System.setOut( out );

    // redirect standard error stream to the TextAreaOutputStream
       //System.setErr( out );
    
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
					int level = 0;
					Object[] opt = {1,2,3,4,5,6,7,8,9,10};
					claveSeleccionada = arg0.getActionCommand();
					System.out.println("Click en: " + claveSeleccionada);
					switch (claveSeleccionada) {
					case "SINCRONIZAR":
						System.out.println("Sincronizando...");
						proxy.getFileList(Server.folderPath, prepararFuturo(clavesVistas[0], ""));
						break;
					case "MONITORIZAR Y SINCRONIZAR":
						ArrayList<ChunkedFile> filesToDownload = new ArrayList<ChunkedFile>();
						ChunkedFile future = (ChunkedFile) prepararFuturo(clavesVistas[1], "/prueba.txt");
						filesToDownload.add(future);
						proxy.downloadFiles(filesToDownload);
						break;
					case "DESCONECTAR":
						ArrayList<ChunkedFile> filesToUpload = new ArrayList<ChunkedFile>();
						ChunkedFile f = (ChunkedFile) prepararFuturo(clavesVistas[2], "/video.mp4");
						filesToUpload.add(f);
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
	 * Métodos de estructura Objeto Activo
	 */
		    
	@Override
	public void done(String idFuturo) {
		System.out.println("Futuro recibido con exito = " + idFuturo);
		IFuturo r = tablaFuturos.get(idFuturo);
		if (r == null){
			System.out.println("Cliente: código de futuro desconocido");
		}else if (r instanceof FileList){
			list.append((String) r.getResult());
			tablaFuturos.remove(r.getId());
		}else{
			System.out.println("Cliente: formato de futuro desconocido");
		}
	}
			    
    
	private IFuturo prepararFuturo(String petition, String argument){
		IFuturo f = null;
		if (petition.equals(clavesVistas[0])){
			f = new FileList();
			f.attach(this);
		}else if (petition.equals(clavesVistas[1])) {
			f = new ChunkedFile(argument, this);
			f.attach(this);
		}else if (petition.equals(clavesVistas[2])) {
			f = new ChunkedFile(argument, null);
		}else {
			return null;
		}
		tablaFuturos.put(f.getId(), f);
		return f;
	}
	
	/*
	 * Métodos internos
	 */
	public DirectoryStream<Path> listOfFiles(Path path){
		try {
			return Files.newDirectoryStream(path);
		} catch (IOException e) {
			System.out.println("Cliente-> Error al leer la lista de archivos en: " + path.toString());
			e.printStackTrace();
		}
		return (DirectoryStream<Path>) new ArrayList<Path>();
	}
	
	public ArrayList<Path> ficherosSoloEnServidor(){
		
		return null;		
	}
	
	public ArrayList<Path> ficherosSoloEnCliente(){
		
		return null;
	}
		    
    public static void main(String [] args) throws IOException{
		ServerProxy pr = new ServerProxy();
		Client cliente = new Client(pr);
    }

}