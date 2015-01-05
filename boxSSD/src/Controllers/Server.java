package Controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import Models.ServerThread;

public class Server {
	
	// Server, crea descriptor socket y hace bind a puerto
	boolean bool = true;
	Socket soc = null;
	ServerSocket ss = null;
	ArrayList<ServerThread> serverThreads = new ArrayList<>();
	int port = 52534;
	
	
	public Server() throws IOException{
		// Server, crea descriptor socket y hace bind a puerto
		int id = 0;
		try {
			ss = new ServerSocket(port);
			System.out.println("Servidor listo en puerto "+port+"\n");
			ExecutorService exec = Executors.newFixedThreadPool(2);
			while(true){
				try {
					soc = ss.accept();// Bloquea si no hay peticiones
					ServerThread thread = new ServerThread(soc,id++,this);
					serverThreads.add(thread);
					exec.execute(thread);
					System.out.println(ss.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			ss.close();
		}
	}

//	public static void main(String[] args) throws IOException {
//		
//		MultipleThreadServerChat mth = new MultipleThreadServerChat();
//
//	}
	
	public void chat(String str, int ident) throws IOException{
		for (ServerThread rnbl : serverThreads) {
			if (rnbl.id != ident) {
				OutputStream output = rnbl.soc.getOutputStream();
				Writer isr = new OutputStreamWriter(output);
				BufferedWriter buff = new BufferedWriter(isr);
				buff.write(ident+" dice: "+str+"\n");
				buff.flush();
			}
		}
	}
	
	//Métodos de servidor
	public DirectoryStream<Path> listOfFiles(Path path){
		try {
			return Files.newDirectoryStream(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (DirectoryStream<Path>) new ArrayList<Path>();
	}

	
}
