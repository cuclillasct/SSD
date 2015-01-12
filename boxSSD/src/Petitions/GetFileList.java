package Petitions;

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
import java.net.UnknownHostException;

import Views.Client;
import Interfaces.IFuturo;
import Interfaces.IMethodRequest;
import Models.FileList;

public class GetFileList implements IMethodRequest {
	
	String path;
	FileList fileList;
	
	public GetFileList(String path, IFuturo futuro) {
		// TODO Auto-generated constructor stub
		this.path = path;
		this.fileList = (FileList) futuro;
	}

	@Override
	public void execute() throws IOException {
		Socket socket = new Socket("127.0.0.1", 52534);
		System.out.println("Conectando con: 127.0.0.1 Puerto: 52534");
		try {			
			//Out
			OutputStream outstr = socket.getOutputStream();
			Writer out = new OutputStreamWriter(outstr);
			BufferedWriter outbuff = new BufferedWriter(out);
			PrintWriter pwr = new PrintWriter(outbuff);
			
			//In
			InputStream instr = socket.getInputStream();
			Reader in = new InputStreamReader(instr);
			BufferedReader inbuff = new BufferedReader(in);
			
			pwr.write("Lista\n"+path+"\nexit\n"); // "C:/Users/Jorge/Desktop\nexit");
			pwr.flush();
			System.out.println("Petición enviada: Lista de archivos");
			String str, result = "";
			while (!(str = inbuff.readLine()).equals("exit")) {
				result = result + str + "\n";
				System.out.println("Leido: " + str);
			}
			fileList.setResult(result);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			System.out.println("Cerrando conexión con el servidor...");
			socket.close();
			System.out.println("Conexión cerrada.");
		}
	}
}


