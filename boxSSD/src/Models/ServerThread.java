package Models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import Controllers.Server;

public class ServerThread implements Runnable{
	
	public static final String SUBIR_FICHEROS = "Subir";
	public static final String DESCARGAR_FICHEROS = "Descargar";
	public static final String LISTA_FICHEROS = "Lista";
	public static final String SINCRONIZAR = "Sincronizar";

	public Socket soc;
	public final int id;
	Server sth;
	
	public ServerThread(Socket sock, int id, Server sth){
		soc = sock;
		this.id = id;
		this.sth = sth;		
	}
	
	public void run() {
		InputStream input = null;
		try {
			input = soc.getInputStream();
			Reader isr = new InputStreamReader(input);
			BufferedReader buff = new BufferedReader(isr);
			boolean bool = true;
			String str = "";
			while(bool){
				//Leemos la primera línea, que nos dice la función que queremos hacer
				str = buff.readLine();
				if (str.equals(LISTA_FICHEROS)) {
					System.out.println("Comando recibido: " + str);
					enviarListaFicheros(buff);
				}else if(str.equals(SUBIR_FICHEROS)){
					sth.chat(str, id);
				}else if(str.equals("exit")){
					System.out.println("Cerrando conexion con el cliente...");
					break;
				}else{
					break;
				}
			}  
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				soc.close();
				System.out.println("Conexion cerrada");
			} catch (IOException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Métodos internos
	 */
	
	private void enviarListaFicheros(BufferedReader buff) throws IOException{
		Path path = FileSystems.getDefault().getPath(buff.readLine());
		System.out.println(path.toString()+"\n");
		DirectoryStream<Path> list = sth.listOfFiles(path);
		
		OutputStream outstr = soc.getOutputStream();
		Writer out = new OutputStreamWriter(outstr);
		PrintWriter outbuff = new PrintWriter(out);
		
		for (Iterator iterator = list.iterator(); iterator
				.hasNext();) {
			Path pth = (Path) iterator.next();
			outbuff.println(pth.getFileName().toString());
			System.out.println(pth.getFileName().toString());
		}
		outbuff.println("exit");
		outbuff.flush();
	}
}
