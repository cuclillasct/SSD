package Models;

import java.util.Date;
import java.util.Hashtable;

public class FileListObject {

	Hashtable<String, Byte[]> tablahash = new Hashtable<>();//pares NombreArchivo-Hash
	Hashtable<String, Date> tablahoras = new Hashtable<>();//pares NombreArchivo-Hora

	public FileListObject(Hashtable<String, Byte[]> hash, Hashtable <String, Date> temps) {
		this.tablahash = hash;
		this.tablahoras = temps;
	}
	
	public void setTimes(Hashtable<String, Date> timestable){
		this.tablahoras = timestable;
	}
	public void setHashes(Hashtable<String, Byte[]> hashestable){
		this.tablahash = hashestable;
	}
	
}
