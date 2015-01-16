package interfaces;

import java.util.ArrayList;


/**
 * Incluye todos las peticiones posibles que podrá hacer el cliente
 * al servidor en forma de métodos.
 */
public interface IProxy {
	
	/**
	 * Peticion al servidor: lista de archivos
	 */
	public void getFileList(String path, IFuturo futuro);

	/**
	 * Peticion al servidor: descarga de archivos
	 */
	public void downloadFiles(ArrayList<String> futuros);
	
	/**
	 * Peticion al servidor: subida de archivos
	 */
	public void uploadFiles(ArrayList<String> futuros);
	
	/**
	 * Peticion al servidor: obtener hora del servidor
	 */
	public void getCristianTime(IFuturo futuro);
	
}
