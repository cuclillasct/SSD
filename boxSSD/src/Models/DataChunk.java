package Models;

import java.io.Serializable;

public class DataChunk implements Serializable{

	public static int CHUNK_MAX_SIZE = 1024;
	
	private int size = CHUNK_MAX_SIZE;
	private int nOrd;
	private byte[] data;

	public DataChunk(int nOrd, byte[] data, int size) {
		this.nOrd = nOrd;
		this.data = data;
		if (size >= 0) this.size = size;
	}

	public int getnOrd() {
		return nOrd;
	}

	public void setnOrd(int nOrd) {
		this.nOrd = nOrd;
	}
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
}
