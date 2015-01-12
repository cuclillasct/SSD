package Models;

import java.io.Serializable;

public class DataChunk implements Serializable{

	public static int CHUNK_SIZE = 1024;
	private int nOrd;
	private byte[] data;
	
	public DataChunk(int nOrd, byte[] data) {
		this.nOrd = nOrd;
		this.data = data;
	}

	public int getnOrd() {
		return nOrd;
	}

	public void setnOrd(int nOrd) {
		this.nOrd = nOrd;
	}
	
}
