package Models;

import java.io.Serializable;

/**
 * Clase que modela un paquete de datos (fracción)
 * Para poder ser leído y enviado por partes
 * sin saturar la red ni la memoria
 */
public class DataChunk implements Serializable{
	private static final long serialVersionUID = 1L;

	public static int CHUNK_MAX_SIZE = 1024*10;
	
	private int size = CHUNK_MAX_SIZE;//tamaño por defecto: el máximo
	private int nOrd;
	private byte[] data;

	/**
	 * Constructor de paquete
	 * @param nOrd número de ordenación del paquete
	 * @param data array de bytes que almacena
	 * @param size tamaño del paquete
	 */
	public DataChunk(int nOrd, byte[] data, int size) {
		this.nOrd = nOrd;
		this.data = data;
		if (size >= 0) this.size = size;
	}

	/**
	 * Obtiene el número de ordenación del paquete
	 * @return int (nOrd)
	 */
	public int getnOrd() {
		return nOrd;
	}

	/**
	 * Fija el número de ordenación del paquete
	 * @param nOrd
	 */
	public void setnOrd(int nOrd) {
		this.nOrd = nOrd;
	}
	
	/**
	 * Extrae los datos del paquete
	 * @return byte[] con los datos
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Escribe los datos que contiene el paquete
	 * @param data
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * Obtiene el tamaño en bytes del paquete
	 * @return int tamaño
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Determina la longitud en bytes del paquete
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
}
