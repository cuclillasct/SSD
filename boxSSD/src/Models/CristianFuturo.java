package Models;

import java.util.Calendar;
import java.util.Date;

import Interfaces.IFuturo;
import Interfaces.IObservadorFuturo;

public class CristianFuturo implements IFuturo{
	
	Date synchronizedDate;
	boolean done;
	IObservadorFuturo observer;
	long id;
	
	public CristianFuturo(IObservadorFuturo obs) {
		// TODO Auto-generated constructor stub
		observer = obs;
		id = Calendar.getInstance().getTimeInMillis();
	}

	@Override
	public Object getResult() {
		// TODO Auto-generated method stub
		return synchronizedDate;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return done;
	}

	@Override
	public void setResult(Object result) {
		// TODO Auto-generated method stub
		synchronizedDate = (Date) result;
		done = true;
		if(observer != null) observer.done(getId());
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return String.valueOf(id);
	}

	@Override
	public void attach(IObservadorFuturo obs) {
		// TODO Auto-generated method stub
		observer = obs;
	}

}
