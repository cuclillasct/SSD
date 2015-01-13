package Util;

import java.util.ArrayList;

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

}
