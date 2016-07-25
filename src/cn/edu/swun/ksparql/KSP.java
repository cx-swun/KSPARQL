package cn.edu.swun.ksparql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import cn.edu.swun.setting.Settings;
import edu.asu.emit.qyan.test.ForPLOSONEPaper;
import edu.asu.emit.qyan.test.Main;

//import edu.asu.emit.qyan.test.*;

public class KSP {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		int startIndex = getEntityIndex(Settings.START_NODE_STRING, Settings.ENTITY_FILE_PATH);
//		int targetIndex = getEntityIndex(Settings.TARGET_NODE_STRING, Settings.ENTITY_FILE_PATH);
//		
//		
//		Main.testKSP(startIndex, targetIndex, Settings.K, Settings.GRAPH_PATH);
//		
//		Main.testKSP(18910, 13491, Settings.K, Settings.GRAPH_PATH);
		
//		Main.testKSP(18910, 13491, 2*Settings.K, Settings.GRAPH_PATH);
		
		for(int k = 10; k <=16; k++){
			String resultFilePath = Settings.KSPARQL_RESULTS_FILE_BASE_PATH + k + "paths_all_in_one.txt";
			for(int iter = 1; iter <= 10; iter++){
				ForPLOSONEPaper.testKSP(18910, 13491, k, Settings.GRAPH_PATH, resultFilePath);
			}
		}
		
		
//		for(int k = 10; k <=16; k++){
//			String resultFilePath = "new_ksparql_" + k + "paths_all_in_one.txt";
//			for(int iter = 1; iter <= 2; iter++){
//				ForPLOSONEPaper.testKSP(1, 100, k, "graph_50types_100000ops.txt", resultFilePath);
//			}
//		}
		
	}
	
	public static int getEntityIndex(String entityString, String entityFile){
		FileReader fr;
		BufferedReader br;
		try {
			fr = new FileReader(new File(entityFile));
			br = new BufferedReader(fr);
			
			String line = null;
			while((line = br.readLine()) != null){
				
//				System.out.println(pair.length);
//				System.out.println(pair[0] + pair[1]);
				
//				for(String str:pair){
//					System.out.println(str);
//				}
				
				
				
				try{
					String [] pair = line.split("###___###");
					String indexStr = pair[0];
					String entityStr = pair[1];
					
					if(entityStr.equals(entityString)){
						Integer index = new Integer(indexStr);
						System.out.println(indexStr + "\t" + index + "\t" + entityString);
						
						br.close();
						fr.close();
						
						return index;
					}
				}catch(Exception ex){
					ex.printStackTrace();
					continue;
				}
				
				
				
			}
			
			br.close();
			fr.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return -1;
	}

}
