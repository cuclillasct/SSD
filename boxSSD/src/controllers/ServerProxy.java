package controllers;

import interfaces.IFuturo;
import interfaces.IProxy;

import java.io.IOException;
import java.util.ArrayList;

import petitionModels.DownloadFile;
import petitionModels.GetCristianTime;
import petitionModels.GetFileList;
import petitionModels.UploadFile;

/**
 * Clase que modela el proxy del servidor
 * Presenta al cliente los métodos que el servidor tiene disponibles para él
 */
public class ServerProxy implements IProxy {

	@Override
	public void getFileList(String path, IFuturo futuro) {
		GetFileList method = new GetFileList(path, futuro);//llama al constructor de la petición
		try {
			method.execute();//la ejecuta
		} catch (IOException e) {
			System.out.println("Ha habido un error comunicándose con el servidor.");
			e.printStackTrace();
		}
	}

	@Override
	public void downloadFiles(ArrayList<String> futuros) {
		for (String file : futuros) {
			DownloadFile method = new DownloadFile(file);//llama al constructor de la petición
			try {
				method.execute();//la ejecuta
			} catch (IOException e) {
				System.out.println("Ha habido un error comunicándose con el servidor.");
				e.printStackTrace();
			}
		}		
	}
	
	@Override
	public void uploadFiles(ArrayList<String> futuros) {

		for (String file : futuros) {
			UploadFile method = new UploadFile(file);//llama al constructor de la petición
			try {
				method.execute();//la ejecuta
			} catch (IOException e) {
				System.out.println("Ha habido un error comunicándose con el servidor.");
				e.printStackTrace();
			}
		}		
	}

	@Override
	public void getCristianTime(IFuturo future) {
		GetCristianTime method = new GetCristianTime(future);//llama al constructor de la petición
		try {
			method.execute();//la ejecuta
		} catch (IOException e) {
			System.out.println("Ha habido un error comunicándose con el servidor.");
			e.printStackTrace();
		}
	}
	
	
}
