package Interfaces;

import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Modela el lugar donde el servidor escribe la respuesta 
 * al servicio que posteriormente recogerá al cliente.
 * 
 *
 */
public interface IFuturo extends Serializable{

	/**
	 * Devuelve el resultado almacenado en el objeto futuro.
	 * 
	 * @return resultado almacenado en el objeto futuro
	 */
	public Object getResult();
	
	/**
	 * Devuelve true si el servicio ha terminado de 
	 * actualizar el resultado almacenado en el futuro
	 * 
	 * @return true si el servicio ha terminado.
	 */
	public boolean isDone();
	
	/**
	 * Almacena un resultado en el futuro.
	 * 
	 * @param result resultado a almacenar en el futuro.
	 */
	public void setResult(Object result);
	
	/**
	 * Devuelve el identificador del futuro. 
	 * Se recomienda que cada futuro tenga un id único.
	 * 
	 * @return identificador del futuro.
	 */
	public String getId();
	public int getIntId();
	
	/**
	 * Método de suscripción. Los observadores de los futuros
	 * son notificados cuando el resultado está listo
	 * para ser recogido.
	 * 
	 * @param obs observador del futuro.
	 */
	public void attach(IObservadorFuturo obs);


}
