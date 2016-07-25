package cn.edu.swun.sparql;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import cn.edu.swun.setting.Settings;

public class SPARQL11 {
	
	/*run once for model*/
	public static void findPaths(Model model, int steps, int k, String resultFilePath){
		findPaths(model, steps, k, 1, resultFilePath);
	}
	
	/*run iterations for model*/
	public static void findPaths(Model model, int steps, int k, int iterations, String resultFilePath){

//		String resultFilePath = Settings.SPARQL_RESULTS_FILE_BASE_PATH + k + "paths_" + steps + "steps_" + iterations + "iterations.txt";
		String q = generateQueryString(steps, k);
		
		System.out.println("#################### Experiment: Searching " + k + " paths, " + steps + " steps, " + iterations + " iterations. " + " ####################");
//		System.out.println("-------------------- Source and target nodes are not random.");
		System.out.println("-------------------- Qeury String: ");
		System.out.println(q);
		System.out.println("-------------------- Results Fils: " + resultFilePath);
		
		List<Long> qTimeList = new ArrayList<>();
		ResultSet results = null;
		Query query = null;
		
		for(int i = 1; i <= iterations; i++){
			long t1 = System.currentTimeMillis();
			
			query = QueryFactory.create(q);
			QueryExecution qexec = QueryExecutionFactory.create(query, model);
			results = qexec.execSelect();
			
			long t2 = System.currentTimeMillis();
			
			long queryTime = t2 - t1;
			System.out.println("\tIteration "+i+" query time: " + queryTime);
			System.out.println();
			qTimeList.add(queryTime);
			
		}
		
		FileOutputStream fs = null;
		Writer w = null;
		
		try{
			fs = new FileOutputStream(new File(resultFilePath), true);
			w = new OutputStreamWriter(fs); 
//			ResultSetFormatter.out(fs, results, query);
			
			for(Long queryTime : qTimeList){
				String queryTimeStr = queryTime.toString();
				w.write(queryTimeStr + "\n");
			}
			
			w.close();
			fs.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		/*the main method just run query once and exit to prevent possible cache acceleration used multiple runs*/
		if(args.length < 3){
			System.out.println("at least 3 parameters must be provided!");
			return;
		}
		
		String rdfFilePath = args[0];
		/*note the order of the k and steps!!!!!!!!!!!*/
		int k = new Integer(args[1]);
		int steps = new Integer(args[2]);
		
		String resultFilePath = Settings.SPARQL_RESULTS_FILE_BASE_PATH + k + "paths_" + steps + "steps_" + "all_in_one.txt";
		
		Model model = ModelFactory.createDefaultModel();
		model.read(rdfFilePath);
		
		findPaths(model, steps, k, resultFilePath);
		
	}
	
	public static String generateQueryString(String start, String target, int steps, int k){
		String queryString = "";
		int last = steps + 1;
		queryString = generateQueryString(start, steps, k);
		queryString = queryString.replaceAll("\\?s"+ last, "");
		queryString = queryString.replaceAll("\\?p" + steps + " \\.", "\\?p" + steps + " " + target);
		return queryString;
	}

	public static String generateQueryString(String start, int steps, int k){
		String queryString = "";
		
		
		queryString = generateQueryString(steps, k).replaceAll("SELECT distinct \\?s1", "SELECT distinct ");
		queryString = queryString.replaceAll("GROUP BY \\?s1", "GROUP BY ");
		queryString = queryString.replaceAll("ORDER BY \\?s1", "ORDER BY ");
		queryString = queryString.replaceAll("\\?s" + (steps+1) + " \\?s1", "\\?s" + (steps+1));
		queryString = queryString.replaceAll("\\?s1", start);
		
//		System.out.println(queryString);
		return queryString;
	}
	
	public static String generateQueryString(int steps, int k, String target){
		String queryString = "";
		int last = steps + 1;
//		queryString = generateQueryString(start, steps, k);
		queryString = generateQueryString(steps, k).replaceAll("\\?s"+ last, "");
		queryString = queryString.replaceAll("\\?p" + steps + " \\.", "\\?p" + steps + " " + target);
		return queryString;
	}
	
	public static String generateQueryString(int steps, int k){
		String selectClaus = "SELECT distinct";
		String groupByClaus = "\nGROUP BY";
		String orderByClaus = "\nORDER BY ?s1 ?s" + (steps + 1);
		String limitClaus = "\nLIMIT " + k;
		String whereClaus = " WHERE{\n";
		
		String queryString = "";
		
		for(int i = 1; i <= steps; i++){
			selectClaus += " ?s" + i + " ?p" + i;
			groupByClaus += " ?s" + i + " ?p" + i;
			orderByClaus += " ?s" + i + " ?p" + i;
			whereClaus += "?s" + i + " ?p" + i + " ?s" + (i+1)+ ".\n";
		}
		selectClaus += " ?s" + (steps + 1);
		groupByClaus += " ?s" + (steps + 1);
		orderByClaus += " ?s" + (steps + 1);
//		orderByClaus = " ?s1" + " ?s" + (steps + 1) + orderByClaus;
//		orderByClaus += " ?s1" + " ?s" + (steps + 1);
		whereClaus += "}";
		
		queryString = selectClaus + whereClaus + groupByClaus + orderByClaus + limitClaus;
		
//		System.out.println(queryString);
		
		return queryString;
	}
}
