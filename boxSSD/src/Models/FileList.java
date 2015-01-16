package Models;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import Interfaces.IFuturo;
import Interfaces.IObservadorFuturo;

public class FileList implements IFuturo{


	HashMap<String, SimpleEntry<byte[], Long>> results;//HashMap<filePath, SimpleEntry<hashCode, lastModifiedDate>>
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

	@Override
	public int getIntId() {
		// TODO Auto-generated method stub
		return -1;
	}

//	results = new HashMap<String, AbstractMap.SimpleEntry<Byte[],Date>>();
//	Set claves =  results.keySet();
//	for (Iterator iterator = claves.iterator(); iterator.hasNext();) {
//		String filename = (String) iterator.next();
//		AbstractMap.SimpleEntry<Byte[], Date> filedata  = results.get(filename);
//		Byte[] hash = filedata.getKey();
//		Date time = filedata.getValue();
//	}
	
}
