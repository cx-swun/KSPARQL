package cn.edu.swun.setting;

public class Settings {
	public static String RDF_PATH = "service_graph_100types_100000ops.rdf";
	public static String GRAPH_PATH = "graph_data";
	public static String ENTITY_FILE_PATH = "entity_data";
	public static String SPARQL_RESULTS_FILE_BASE_PATH = "sparql_stats_";
	public static String KSPARQL_RESULTS_FILE_BASE_PATH = "ksparql_stats_";
	public static String START_NODE_STRING = "<http://dbpedia.org/ontology/soccerTournamentTopScorer>";
	public static String TARGET_NODE_STRING = "<http://www.w3.org/2002/07/owl#Class>";
	public static int STEPS = 8;
	public static int K = 16;
}
