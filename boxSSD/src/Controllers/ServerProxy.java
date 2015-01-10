package Controllers;

import java.io.IOException;

import Interfaces.IFuturo;
import Interfaces.IProxy;
import Petitions.GetFileList;

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
	
}
