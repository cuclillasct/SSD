package Interfaces;

/*
 * Incluye todos las peticiones posibles que podr� hacer el cliente
 * al servidor en forma de m�todos.
 */

public interface IProxy {
	
	/*
	 * Peticion al servidor: lista de archivos
	 */
	public void getFileList(String path, IFuturo futuro);

}
