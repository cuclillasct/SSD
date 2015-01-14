package Models;

import java.util.Calendar;
import java.util.Date;

import Interfaces.IFuturo;
import Interfaces.IObservadorFuturo;

public class CristianFuturo implements IFuturo{
	
	Long serverDate;
	boolean done;
	IObservadorFuturo observer;
	int id;
	
	public CristianFuturo(int id){
		this.id = id;
	}
	
	public CristianFuturo(int id, IObservadorFuturo obs) {
		// TODO Auto-generated constructor stub
		observer = obs;
		this.id = id;
	}

	@Override
	public Object getResult() {
		return serverDate;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public void setResult(Object result) {
		serverDate = (long) result;
		done = true;
		if(observer != null) observer.done(getId());
	}

	@Override
	public String getId() {
		return String.valueOf(id);
	}
	
	public int getIntId(){
		return id;
	}

	@Override
	public void attach(IObservadorFuturo obs) {
		observer = obs;
	}

}
