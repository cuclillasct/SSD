package Util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.xml.sax.InputSource;

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
		File file = new File(path);
		FileInputStream in = new FileInputStream(file);
		BufferedInputStream input = new BufferedInputStream(in);
		byte[] b; int i = 0;
		DataChunk chunk;
			while (input.available() > 0) {
				System.out.println("Quedan: " + input.available());
				if (input.available() >= DataChunk.CHUNK_MAX_SIZE){
					b = new byte[DataChunk.CHUNK_MAX_SIZE];
					input.read(b, 0, b.length);
					chunk = new DataChunk(i++, b, DataChunk.CHUNK_MAX_SIZE);
					packages.add(chunk);
					System.out.println("Leido localmente: " + chunk.getData().hashCode());
				}else{
					b = new byte[input.available()];
					input.read(b);
					chunk = new DataChunk(i++, b, b.length);
					packages.add(chunk);
					System.out.println("Leido localmente ultimo paquete: " + chunk.getData().hashCode());
				}				
			}
		input.close();
		return packages;
	}
	
}
