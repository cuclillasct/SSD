package Models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import Interfaces.IFuturo;
import Interfaces.IObservadorFuturo;

public class FileList implements IFuturo{

	String result;
	boolean hecho;
	String descripcion;
	IObservadorFuturo obs;
	String id;
	
	
	public FileList (){
		id = new Long(Calendar.getInstance().getTimeInMillis()).toString();
	}
	
	
	public FileList (IObservadorFuturo obs){
		attach(obs);
	}
	
	/*
	public Futuro (int key, IObservadorFuturo obs){
		attach(obs);
	}
	*/

	@Override
	public String getResult() {
//		String str = "";
//		for (String string : result) {
//			str.concat(string + "\n");
//		}
		return result;
	}
	
	@Override
	public void setResult(Object result){
//		String str;
//		this.result = new ArrayList<String>();
//		try {
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("Respuesta en formato incorrecto, no se ha podido leer.");
//		}
		this.result = (String) result;
		hecho = true;
		if (obs != null) obs.done(id);
	}


	@Override
	public boolean isDone() {
		return hecho;
	}

	@Override
	public void attach(IObservadorFuturo obs) {
		this.obs = obs;		
	}

	@Override
	public String getId() {
		return id;
	}

}
