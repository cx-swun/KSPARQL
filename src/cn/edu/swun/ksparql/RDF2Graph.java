package cn.edu.swun.ksparql;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import cn.edu.swun.setting.Settings;

public class RDF2Graph {
	
	public static void main(String [] args){
		long t1 = System.currentTimeMillis();
		List<Integer> indexList = transform2(Settings.START_NODE_STRING, Settings.TARGET_NODE_STRING, Settings.RDF_PATH, Settings.GRAPH_PATH);
		long t2 = System.currentTimeMillis();
		long time = t2 - t1;
		System.out.println(indexList.get(0) + " " + indexList.get(1) + " " + indexList.size());
		System.out.println("time for conversion: " + time + " ms");
	}

	public static List<Integer> transform(String start, String target, String rdfPath, String filePath) {
		
		List<Integer> indexList = new ArrayList<>();
		
		BufferedWriter bw = null;
		BufferedWriter subBw = null;
		BufferedWriter objBw = null;
		try {
			bw = new BufferedWriter(new FileWriter(filePath));
			subBw = new BufferedWriter(new FileWriter("sub_data"));
			objBw = new BufferedWriter(new FileWriter("obj_data"));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Model model = ModelFactory.createDefaultModel();
		
		long t0 = System.currentTimeMillis();
		
		model.read(rdfPath);
		
		long t1 = System.currentTimeMillis();
		
		System.out.println("loading time: " + (t1-t0));
		
		StmtIterator stmtIter = model.listStatements();
//		List<Statement> stmtList = stmtIter.toList();
//		int numStmt = stmtList.size();
		
		ResIterator subIter = model.listSubjects();
		List<Resource> subList = subIter.toList();
		for(int i = 0; i < subList.size(); i++){
			String str = i + " " + subList.get(i).toString() + "\n";
			if(("<"+subList.get(i).toString()+">").equals(start)){
				indexList.add(i);
			}
			try {
				subBw.write(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		
//		System.out.println("subject set and list the same size? " + (subSet.size() == subList.size()));
//		System.out.println("subject set size: " + subSet.size());
//		System.out.println("subject list size: " + subList.size());
		
		
		
		NodeIterator objIter = model.listObjects();
		List<RDFNode> objList = objIter.toList();
		for(int i = 0; i < objList.size(); i++){
			String str = (int)(subList.size() + i) + " " + objList.get(i).toString() + "\n";
			if(("<"+objList.get(i).toString()+">").equals(target)){
				indexList.add(i);
			}
			try {
				objBw.write(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("subList check passed? " + checkSubList(subList));
		System.out.println("objList check passed? " + checkObjList(objList));
		
		List<RDFNode> nodeList = new ArrayList<>();
		nodeList.addAll(subList);
		nodeList.addAll(objList);
		
		System.out.println("number of nodes:" + nodeList.size());		
		System.out.println("number of statements: " + model.size());
		System.out.println("number of subjects: " + subList.size());
		System.out.println("number of objects: " + objList.size());

		try {
			bw.write(nodeList.size() + "\n\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(stmtIter.hasNext()){
			Statement stmt = stmtIter.next();
			
			Resource subject = stmt.getSubject();
			int subIndex = subList.indexOf(subject);
//			System.out.println(subject + " " + start);
			
			RDFNode object = stmt.getObject();
			int objIndex = subList.size() + objList.indexOf(object);
			
			String entry = subIndex + " " + objIndex + " 1\n";
			
			try {
				bw.write(entry);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		try {
			bw.close();
			subBw.close();
			objBw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		System.out.println(subList.get(indexList.get(0)));
		System.out.println(objList.get(indexList.get(1)));
		
		return indexList;
	}
	
	public static List<Integer> transform2(String start, String target, String rdfPath, String filePath) {
		
		List<Integer> indexList = new ArrayList<>();
		
		BufferedWriter bw = null;
		BufferedWriter entityBw = null;
		try {
			bw = new BufferedWriter(new FileWriter(filePath));
			entityBw = new BufferedWriter(new FileWriter(Settings.ENTITY_FILE_PATH));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Model model = ModelFactory.createDefaultModel();
		
		long t0 = System.currentTimeMillis();
		
		model.read(rdfPath);
		
		long t1 = System.currentTimeMillis();
		
		System.out.println("loading time: " + (t1-t0));
		
		StmtIterator stmtIter = model.listStatements();
		
		ResIterator subIter = model.listSubjects();
		Set nodeSet = subIter.toSet();
		
		NodeIterator objIter = model.listObjects();
		Set<RDFNode> objSet = objIter.toSet();
		
		nodeSet.addAll(objSet);
		
		List<RDFNode> nodeList = new ArrayList<>();
		nodeList.addAll(nodeSet);
		for(int i = 0; i < nodeList.size(); i++){
			RDFNode node = nodeList.get(i);
			String nodeString = node.toString();
			String entry = i + "###___###" + nodeString + "\n";
//			System.out.println(entry);
			try {
				entityBw.write(entry);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(Settings.START_NODE_STRING.equals("<" + nodeString + ">")){
				indexList.add(i);
			}
			if(Settings.TARGET_NODE_STRING.equals("<" + nodeString + ">")){
				indexList.add(i);
			}
		}
		
//		System.out.println("sub set size " + nodeSet.size());
//		System.out.println("obj set size " + objSet.size());		
		System.out.println("number of nodes: " + nodeList.size());
		System.out.println("number of statements: " + model.size());

		try {
			bw.write(nodeList.size() + "\n\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(stmtIter.hasNext()){
			Statement stmt = stmtIter.next();
			
			Resource subject = stmt.getSubject();
			int subIndex = nodeList.indexOf(subject);
			
			RDFNode object = stmt.getObject();
			int objIndex = nodeList.indexOf(object);
			
			String entry = subIndex + " " + objIndex + " 1\n";
			
			try {
				bw.write(entry);
				entityBw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		
		return indexList;
	}
	
	public static boolean checkSubList(List<Resource> list){
		for(int i = 0; i < list.size() - 1; i++){
			int nextIndex = i + 1;
			Resource prev = list.get(i);
			Resource next = list.get(nextIndex);
			System.out.println(prev + " " + next);
			if(prev.toString().equalsIgnoreCase(next.toString())){
				System.out.println(i + " and " + nextIndex + " are the same");
				return false;
			}
//			else{
//				System.out.println(i + " and " + nextIndex + " are NOT the same");
//				
//			}
		}
		return true;
	}
	
	public static boolean checkObjList(List<RDFNode> list){
		for(int i = 0; i < list.size() - 1; i++){
			int nextIndex = i + 1;
			RDFNode prev = list.get(i);
			RDFNode next = list.get(nextIndex);
			System.out.println(prev + " " + next);
			if(prev.toString().equalsIgnoreCase(next.toString())){
				System.out.println(i + " and " + nextIndex + " are the same");
				return false;
			}
//			else{
//				System.out.println(i + " and " + nextIndex + " are NOT the same");				
//			}
		}
		return true;
	}

}
