package Controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JTextArea;

public class Client {

	JTextArea list;
	private Path remotePath, localPath;
	Socket socket;	
	
	public Client(){
		remotePath = Paths.get("C:/Users/All Users/Destkop");
		localPath = Paths.get("C:/Users/Jorge/Desktop/SSD");
	}
	
	public Client(JTextArea list, String remotePath, String localPath){
		this.list = list;
		this.remotePath = Paths.get(remotePath);
		this.localPath = Paths.get(localPath);
		System.out.println("Iniciando cliente...\n");
	}
	
	public void getFileList() throws IOException{
		try {
			socket = new Socket("127.0.0.1", 52534);
			System.out.println("Conectando con: 127.0.0.1 Puerto: 52534\n");
			
			//Out
			OutputStream outstr = socket.getOutputStream();
			Writer out = new OutputStreamWriter(outstr);
			BufferedWriter outbuff = new BufferedWriter(out);
			PrintWriter pwr = new PrintWriter(outbuff);
			
			//In
			InputStream instr = socket.getInputStream();
			Reader in = new InputStreamReader(instr);
			BufferedReader inbuff = new BufferedReader(in);
			
			pwr.write("Lista\n"+remotePath+"\nexit"); // "C:/Users/Jorge/Desktop\nexit");
			pwr.flush();
			System.out.println("Petición enviada: Lista de archivos\n");
			while(true){
				String str = inbuff.readLine();
				System.out.println("Leo: " +str+"\n");
				if (str.equals("exit")) {
					break;
				} else {
					list.append(str+"\n");
					System.out.println("Recibido: "+ str);
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			socket.close();
		}
	}

}
