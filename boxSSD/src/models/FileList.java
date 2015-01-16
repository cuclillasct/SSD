package models;

import interfaces.IFuturo;
import interfaces.IObservadorFuturo;

import java.util.AbstractMap.SimpleEntry;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Clase que modela el futuro 
 * (lugar donde se almacena el resultado)
 * de una petición para obtener la lista
 * de archivos del Servidor
 */
public class FileList implements IFuturo{
	private static final long serialVersionUID = 1L;
	
	HashMap<String, SimpleEntry<byte[], Long>> results;//HashMap<filePath, SimpleEntry<hashCode, lastModifiedDate>>
	boolean hecho;
	IObservadorFuturo obs;
	String id;	
	
	/*
	 * Constructores
	 */
		
	public FileList (){
		id = new Long(Calendar.getInstance().getTimeInMillis()).toString();
	}
	
	public FileList (IObservadorFuturo obs){
		attach(obs);
	}

	/*
	 * Métodos de IFuturo
	 */
	
	@Override
	public Object getResult() {
		return results;
	}
	
	@Override
	public void setResult(Object result){
		this.results = (HashMap<String, SimpleEntry<byte[], Long>>) result;
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

	/**
	 * Incorrecto para este futuro
	 * @see getId()
	 * @return -1 si error
	 */
	@Override
	public int getIntId() {
		return -1;
	}

	
}
