package Interfaces;

import java.io.IOException;

/**
 * Representa una llamada a un método en el objeto activo.
 * Proporciona guardas para chequear si puede ejecutarse.
 *
 */
public interface IMethodRequest {
	/**
	 * Ejecuta la petición de servicio.
	 * @throws IOException 
	 */
	void execute() throws IOException;
	
}
