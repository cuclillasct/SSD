package Models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

import Interfaces.IFuturo;
import Interfaces.IObservadorFuturo;

public class FileList implements IFuturo{

	// MIRA SI TE VALE ASI, AbstractMap.SimpleEntry es la forma que he encontrado para declarar un PAR DE VALORES
	// Si se te ocurre una forma mejor implementala
	// HashMap<String, AbstractMap.SimpleEntry<String, Date>> results; //HashMap<filePath, AbstractMap.SimpleEntry<hashCode, lastModifiedDate>>
	ArrayList<String> result;
	boolean hecho;
	IObservadorFuturo obs;
	String id;	
	
	public FileList (){
		id = new Long(Calendar.getInstance().getTimeInMillis()).toString();
	}
	
	public FileList (IObservadorFuturo obs){
		attach(obs);
	}

	@Override
	public Object getResult() {
		return result;
	}
	
	@Override
	public void setResult(Object result){
		this.result = (ArrayList<String>) result;
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
