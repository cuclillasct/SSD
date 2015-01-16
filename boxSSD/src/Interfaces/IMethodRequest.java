package Interfaces;

import java.io.IOException;

/**
 * Representa una llamada a un método en el objeto activo.

 */
public interface IMethodRequest {
	
	/**
	 * Ejecuta la petición de servicio.
	 * @throws IOException 
	 */
	void execute() throws IOException;
	
}
