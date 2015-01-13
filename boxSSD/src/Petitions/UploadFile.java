package Petitions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Controllers.Server;
import Controllers.ServerThread;
import Interfaces.IFuturo;
import Interfaces.IMethodRequest;
import Models.ChunkedFile;
import Models.DataChunk;
import Util.GeneralUtils;
import Util.IOUtils;
import Views.Client;

public class UploadFile implements IMethodRequest {

	ChunkedFile chunkedFile;
	
	public UploadFile(IFuturo future) {
		this.chunkedFile = (ChunkedFile) future;
	}
	
	@Override
	public void execute() throws IOException {
		Socket socket = new Socket("127.0.0.1", 52534);
		System.out.println("Conectando con: 127.0.0.1 Puerto: 52534");
		
		try {
			ArrayList<DataChunk> filePackages = IOUtils.readFile(Client.folderPath + chunkedFile.getRelativePath());
			
			//Out
			ObjectOutputStream outstr = new ObjectOutputStream(socket.getOutputStream());
			
			outstr.writeObject(ServerThread.SUBIR_FICHERO);
			outstr.writeObject(chunkedFile.getRelativePath()); // "C:/Users/Jorge/Desktop\nexit");
			outstr.writeInt(filePackages.size());
			outstr.flush();
			
			System.out.println("Petición enviada: Subir fichero " + chunkedFile.getRelativePath());

			for (DataChunk dataChunk : filePackages) {
				System.out.println("Enviando paquetes... " + (dataChunk.getnOrd()+1) + " de " + filePackages.size());
				outstr.writeObject(dataChunk);
			}
			outstr.flush();
			
			//In
			ObjectInputStream instr = new ObjectInputStream(socket.getInputStream());
			
			chunkedFile.setResult(null); // Este paquete no tiene observador
			System.out.println("Fichero "+ chunkedFile.getRelativePath() + " enviado" );
			
			System.out.println("Esperando cierre de conexión con el servidor...");
			outstr.writeObject("exit");
			outstr.flush();
//			while(!((String) instr.readObject()).equals("exit")){} //Esperamos que cierre el servidor
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Host remoto desconocido.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			socket.close();
			System.out.println("Conexión cerrada.");
		}
	}
}
