package Controllers;

import java.io.IOException;
import java.util.ArrayList;

import Interfaces.IFuturo;
import Interfaces.IProxy;
import Models.ChunkedFile;
import Petitions.DownloadFile;
import Petitions.GetFileList;
import Petitions.UploadFile;

public class ServerProxy implements IProxy {

	@Override
	public void getFileList(String path, IFuturo futuro) {
		// TODO Auto-generated method stub
		GetFileList method = new GetFileList(path, futuro);
		try {
			method.execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Ha habido un error comunicándose con el servidor.");
			//Hacer algo con el future?
		}
	}

	@Override
	public void downloadFiles(ArrayList<ChunkedFile> futuros) {
		// TODO Auto-generated method stub
		for (IFuturo file : futuros) {
			DownloadFile method = new DownloadFile(file);
			try {
				method.execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Ha habido un error comunicándose con el servidor.");
				//Hacer algo con el future?
			}
		}		
	}
	
	@Override
	public void uploadFiles(ArrayList<ChunkedFile> futuros) {
		// TODO Auto-generated method stub
		for (IFuturo file : futuros) {
			UploadFile method = new UploadFile(file);
			try {
				method.execute();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Ha habido un error comunicándose con el servidor.");
				//Hacer algo con el future?
			}
		}		
	}
	
}
