package Util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import Models.DataChunk;

public class IOUtils {

	/////////////////////////////////////////////////////////////////////////////////////////
	// FICHEROS.
	
	/**
	 * Lee los contenidos de un fichero byte a byte.
	 * @param fichero ruta del fichero
	 * @return contenido del fichero dividido en paquetes
	 */
	public static ArrayList<DataChunk> readFile (String path) throws IOException{
		ArrayList<DataChunk> packages = new ArrayList<DataChunk>();
		FileInputStream in = new FileInputStream(path);
		byte[] b = new byte[DataChunk.CHUNK_SIZE]; int i = 0;
		while (in.available() != 0) {
			in.read(b);
			packages.add(new DataChunk(i++, b));
		}
		return packages;
	}
	
	/**
	 * Escribe un string en un fichero de texto.
	 * @param s 
	 * @param fichero fiechero destino
	 */
	public static void escribirEnFichero(String s, String fichero) throws FileNotFoundException {
		
		if (s == null || fichero == null){
			return;
		}
		
		PrintWriter streamWriter = null;
		
	    try {
			streamWriter = new PrintWriter(new FileOutputStream (fichero));
			streamWriter.println(s);
			streamWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Escribe un array de strings en un fichero de texto, cada uno en una línea diferente.
	 * @param s array de strings
	 * @param fichero fichero destino.
	 */
	public static void escribirEnFichero(String [] s, String fichero) {
				
		if (s == null || fichero == null){
			return;
		}
		
		PrintWriter streamWriter = null;
		
	    try {
			streamWriter = new PrintWriter(new FileOutputStream (fichero));
			for (int i = 0; i < s.length; i++){
				streamWriter.println(s[i]);
			}
			streamWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
