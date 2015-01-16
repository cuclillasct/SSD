package Models;

import Interfaces.IFuturo;
import Interfaces.IObservadorFuturo;

/**
 * Clase que modela el futuro
 * (lugar donde se almacenará el resultado)
 * de las peticiones de hora de servidor
 * (Usada para la ejecución del algoritmo de Cristian)
 */
public class CristianFuturo implements IFuturo{
	private static final long serialVersionUID = 1L;
	
	Long serverDate;
	boolean done;
	IObservadorFuturo observer;
	int id;//el identificador es un entero para poder escribirlo como el número de la iteración
	
	/*
	 * Constructores
	 */
	public CristianFuturo(int id){
		this.id = id;
	}
	
	public CristianFuturo(int id, IObservadorFuturo obs) {
		observer = obs;
		this.id = id;
	}

	/*
	 * Métodos de IFuturo
	 */
	
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
		if(observer != null) observer.done(getId());//avisa al cliente
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
