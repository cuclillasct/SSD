package Views;

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
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;

import Controllers.Client;
import Controllers.Server;
import Controllers.TextAreaOutputStream;

public class MainWindow extends JFrame {

	
	public static final String [] 
    		clavesVistas = {"SINCRONIZAR", "MONITORIZAR Y SINCRONIZAR", "DESCONECTAR", "CAMBIAR DIRECTORIOS", "SALIR"};
	
    /**
	 * 
	 * 
	 * 
	 */

	String claveSeleccionada = null;
	
	JFrame frame;
	JLabel author;
	JTextArea console, list;
	    
	public MainWindow() throws IOException{
		
       super("Trabajo Final SSD 2014/2015 3º Grado Ing. Telemática UPCT");
    
       frame = this;
       
       // Fijamos tamaño de la ventana.       
       setSize (700,400);
       setContentPane(new JLabel(new ImageIcon("path")));
       setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
       
       //Panel principal
       Box mainBox = new Box(BoxLayout.LINE_AXIS);
       getContentPane().add(mainBox);
       
       author = new JLabel();
       author.setText("Jorge Mendoza Saucedo y Elena Martín Seonae ©Copyright 2014");
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
    
       System.out.println("Registro del programa: \n \n>>");
       JScrollPane consolePane = new JScrollPane(console);
       consolePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
       
       list = new JTextArea();
       JScrollPane listPane = new JScrollPane(list);
       listPane.setHorizontalScrollBar(null);
       listPane.setPreferredSize(new Dimension(150,0));
       listPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
       list.append("Lista de archivos: \n \n");
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
		    * 
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
					System.out.println("Click en: " + claveSeleccionada + "\n");
					switch (claveSeleccionada) {
					case "SINCRONIZAR":
						System.out.println("Sincronizando...\n");
							Client client = new Client(list, "C:/Users/Jorge/Desktop/SSD", "");
						try {
							client.getFileList();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case "MONITORIZAR Y SINCRONIZAR":
						break;
					case "DESCONECTAR":
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
    
		    
    public static void main(String [] args) throws IOException{
    	MainWindow gui = new MainWindow();
    }

}