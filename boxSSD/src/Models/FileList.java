package Models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;

import Interfaces.IFuturo;
import Interfaces.IObservadorFuturo;

public class FileList implements IFuturo{

	//ArrayList<String> result = new ArrayList();
	String result;
	boolean hecho;
	String descripcion;
	IObservadorFuturo obs;
	static int contador = 1;
	String id;
	
	
	public FileList (){
		id = new Integer(contador++).toString();
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
	public void setResult(String result){
//		String str;
//		this.result = new ArrayList<String>();
//		try {
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("Respuesta en formato incorrecto, no se ha podido leer.");
//		}
		this.result = result;
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

	
	public String toString(){
		return "FT, Cmd = " + descripcion + ", result = " + result.toString();
	}

	@Override
	public String getId() {
		return id;
	}

}
