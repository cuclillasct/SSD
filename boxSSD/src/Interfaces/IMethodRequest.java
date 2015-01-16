package Interfaces;

import java.io.IOException;

/**
 * Representa una llamada a un m�todo en el objeto activo.

 */
public interface IMethodRequest {
	
	/**
	 * Ejecuta la petici�n de servicio.
	 * @throws IOException 
	 */
	void execute() throws IOException;
	
}
