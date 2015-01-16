package Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import Models.DataChunk;

public class GeneralUtils {
	
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
		} catch (IOException e) {
			System.out.println("No se ha encontrado el archivo: " + pathFile);
			e.printStackTrace();
		}

	    hashBytes = md.digest();//vuelco el hash en el array de bytes
	    
	    return hashBytes;
	}

	public static Long getLastModifiedDate (String path){
		File file = new File(path);
		return new Long(file.lastModified());
	}
}
