package Models;

import java.util.ArrayList;
import java.util.Calendar;

import Interfaces.IFuturo;
import Interfaces.IObservadorFuturo;

public class ChunkedFile implements IFuturo {
	
	boolean hecho;
	String relativePath, id;
	IObservadorFuturo obs;
	ArrayList<DataChunk> chunks;	
	
	public ChunkedFile (){
		id = new Long(Calendar.getInstance().getTimeInMillis()).toString();
	}
	
	public ChunkedFile (String relativePath, IObservadorFuturo obs){
		id = new Long(Calendar.getInstance().getTimeInMillis()).toString();
		this.relativePath = relativePath;
		attach(obs);
	}
	

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return chunks;
	}

	@Override
	public void setResult(Object result) {
		// TODO Auto-generated method stub
		chunks = (ArrayList<DataChunk>) result;
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

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	
	public int getSize(){
		int size = 0;
		for (DataChunk dataChunk : chunks) {
			size += dataChunk.getSize();
		}
		return size;
	}

	public ArrayList<DataChunk> getChunks() {
		return chunks;
	}

	public void setChunks(ArrayList<DataChunk> chunks) {
		this.chunks = chunks;
	}
	
	public void addChunk(DataChunk chunk){
		this.chunks.add(chunk);
	}
	

}
