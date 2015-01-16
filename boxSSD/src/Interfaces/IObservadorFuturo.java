package Interfaces;


/**
 * Modela a los observadores de los futuros
 *
 */
public interface IObservadorFuturo {
	
	/**
	 * Informa al observador de que el resultado está
	 * listo.
	 * @param idFuturo identificador del futuro.
	 */
	public void done(String idFuturo);
}
