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

import Models.ChunkedFile;
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
		byte[] b = new byte[DataChunk.CHUNK_MAX_SIZE]; int i = 0;
		try{
			while (in.available() != 0) {
				if (in.available() >= DataChunk.CHUNK_MAX_SIZE){
					in.read(b);
					packages.add(new DataChunk(i++, b, DataChunk.CHUNK_MAX_SIZE));
				}else{
					b = new byte[in.available()];
					in.read(b);
					packages.add(new DataChunk(i++, b, b.length));
				}				
			}
		}catch (Exception e) {
			// TODO: handle exception
			in.close();
			return packages;
		}
		in.close();
		return packages;
	}
	
	/**
	 * Escribe un ChunkedFile en un fichero.
	 * @param fichero destino
	 * @param chunkedfile
	 */
	public static void writeFile (String path, ChunkedFile file) throws IOException{
		ArrayList<DataChunk> packages = (ArrayList<DataChunk>) file.getResult();
		FileOutputStream out = new FileOutputStream(path);
		for (DataChunk dataChunk : packages) {
			out.write(dataChunk.getData());
		}
		out.close();
	}

	
}
