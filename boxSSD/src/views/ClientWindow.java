package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;

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

import utils.TextAreaOutputStream;
import controllers.Client;
import controllers.ServerProxy;
import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;


public class ClientWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public static final String IPServer = "127.0.0.1";
	
	public static final String [] 
    		clavesVistas = {"SINCRONIZAR", "MONITORIZAR Y SINCRONIZAR", "SALIR"};

	String claveSeleccionada = null;
	
	ClientWindow frame;
	JLabel author;
	JTextArea console;
	JTextArea list;
	
	// Cliente
	Client client;
	
	public ClientWindow() throws IOException{
		
	       super("Trabajo Final SSD 2014/2015 3º Grado Ing. Telemática UPCT");
	       
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
//		    System.setOut( out );
	
//		    // redirect standard error stream to the TextAreaOutputStream
//		    System.setErr( out );
	    
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
	       
	       client = new Client(new ServerProxy(), list);
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
						//Manda al cliente que se sincronice
						client.Sync();						
						break;
						
					case "MONITORIZAR Y SINCRONIZAR":
						System.out.println("Monitorizando cambios...");
						JNotify notify = new JNotify();
						// Hacemos observador de la carpeta al cliente, de forma que cuando suceda algún cambio, se sincronice inmediatamente
						try {
							notify.addWatch(Client.folderPath, 6, false, client);
						} catch (JNotifyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("No se ha podido establecer el observador a la carpeta sincronizada.");
						}
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
     *  Metodo main que inicia el programa
     */
    public static void main(String [] args) throws IOException{
		ClientWindow window = new ClientWindow();
    }
    
}