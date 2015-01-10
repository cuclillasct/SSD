package Interfaces;

/*
 * Incluye todos las peticiones posibles que podrá hacer el cliente
 * al servidor en forma de métodos.
 */

public interface IProxy {
	
	/*
	 * Peticion al servidor: lista de archivos
	 */
	public void getFileList(String path, IFuturo futuro);

}
