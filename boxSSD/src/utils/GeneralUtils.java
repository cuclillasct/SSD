package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import models.DataChunk;

public class GeneralUtils {
	
	/**
	 * Método para ordenar paquetes en caso de que llegasen desordenados
	 * @param packages
	 * @return ArrayList ordenado
	 */
	public static ArrayList<DataChunk> orderPackages (ArrayList<DataChunk> packages){
		ArrayList<DataChunk> packs = new ArrayList<DataChunk>();
		for (int i = 0; i < packages.size(); i++) {
			for (DataChunk dataChunk : packages) {
				if (dataChunk.getnOrd() == i) {
					System.out.println("ordenando " + i);
					packs.add(dataChunk);
					break;
				}
			}
		}
		return packs;
	}
	
	/**
	 * Método para obtener el código hash de un archivo
	 * @param pathFile
	 * @return byte[] con el hash
	 */
	public static byte[] getHash(String pathFile){
	    MessageDigest md = null;
	    FileInputStream file;
	    byte[] hashBytes;
	    
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No existe ese algoritmo de encriptado");
			e.printStackTrace();
		}
		try {
			file = new FileInputStream(pathFile);//coge el fichero seleccionado
		    byte[] data = new byte[1024*100];
			
		    int nread = 0; //bytes leidos. almacenará -1 si no quedan bytes por leer
		    while ((nread = file.read(data)) != -1) {//lee los bytes del tamaño de data y los almacena en él.
		      md.update(data, 0, nread);	//actualiza con ese bloque de datos
		    }
		    file.close();
		} catch (IOException e) {
			System.out.println("No se ha encontrado el archivo: " + pathFile);
			e.printStackTrace();
		}

	    hashBytes = md.digest();//vuelco el hash en el array de bytes
	    
	    return hashBytes;
	}

	/**
	 * Obtiene la fecha de modificación del archivo
	 * @param path
	 * @return Long modification date
	 */
	public static Long getLastModifiedDate (String path){
		File file = new File(path);
		return new Long(file.lastModified());
	}
	
	/**
	 * Obtiene la ruta de la carpeta sincronizada
	 * @param client/server
	 * @return String folderPath
	 */
	public static String getDirectory(int n){	// n = 0 -> Cliente, n = 1 -> Servidor
		String str = System.getProperty("user.home") + "/Desktop/SSDClient/";
		if (n == 0) {
			str = System.getProperty("user.home") + "/Desktop/SSDClient/";
		}else if (n == 1) {
			str = System.getProperty("user.home") + "/Desktop/SSDServer/";
		}
		File file = new File(str);
		if (file.exists()) {
			return str;
		}else {  //Si el directorio no existe, lo crea
			file.mkdir();
			return str;
		}
	}
}
