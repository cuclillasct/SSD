package Controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import Interfaces.IFuturo;
import Interfaces.IProxy;
import Models.ChunkedFile;
import Petitions.DownloadFile;
import Petitions.GetCristianTime;
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
	public void downloadFiles(ArrayList<String> futuros) {
		// TODO Auto-generated method stub
		for (String file : futuros) {
			DownloadFile method = new DownloadFile(file);
			try {
				method.execute();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Ha habido un error comunicándose con el servidor.");
			}
		}		
	}
	
	@Override
	public void uploadFiles(ArrayList<String> futuros) {

		for (String file : futuros) {
			UploadFile method = new UploadFile(file);
			try {
				method.execute();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Ha habido un error comunicándose con el servidor.");
			}
		}		
	}

	@Override
	public void getCristianTime(IFuturo future) {
		GetCristianTime method = new GetCristianTime(future);
		try {
			method.execute();
		} catch (IOException e) {
			System.out.println("Ha habido un error comunicándose con el servidor.");
			e.printStackTrace();
		}
	}
	
	
}
