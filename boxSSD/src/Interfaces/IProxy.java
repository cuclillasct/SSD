package Interfaces;

import java.util.ArrayList;

import Models.ChunkedFile;

/*
 * Incluye todos las peticiones posibles que podrá hacer el cliente
 * al servidor en forma de métodos.
 */

public interface IProxy {
	
	/*
	 * Peticion al servidor: lista de archivos
	 */
	public void getFileList(String path, IFuturo futuro);

	/*
	 * Peticion al servidor: descarga de archivos
	 */
	public void downloadFiles(ArrayList<ChunkedFile> futuros);
	
	/*
	 * Peticion al servidor: subida de archivos
	 */
	public void uploadFiles(ArrayList<ChunkedFile> futuros);
	
}
