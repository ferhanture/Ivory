package ivory.regression;

import ivory.eval.Qrels;
import ivory.eval.Qrels_new;
import ivory.regression.GroundTruth.Metric;
import ivory.smrf.retrieval.Accumulator;
import ivory.smrf.retrieval.BatchQueryRunner;
import ivory.smrf.retrieval.CascadeBatchQueryRunner;

import java.util.HashMap;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.junit.Test;

import edu.umd.cloud9.collection.DocnoMapping;

public class Gov2_SIGIR2011 {

	private static final Logger sLogger = Logger.getLogger(Gov2_SIGIR2011.class);

	private static String[] QL = new String[] {
         "776", "0.2297",  "777", "0.2168",  "778", "0.4434",  "779", "0.8206",  "780", "0.4392",
         "781", "0.4516",  "782", "0.4546",  "783", "0.4763",  "784", "0.7317",  "785", "0.6622",
         "786", "0.4232",  "787", "0.7718",  "788", "0.7537",  "789", "0.3516",  "790", "0.8069",
         "791", "0.6621",  "792", "0.2104",  "793", "0.3758",  "794", "0.1348",  "795", "0.0",    
         "796", "0.3863",  "797", "0.4411",  "798", "0.2733",  "799", "0.1685",  "800", "0.1414", 
         "801", "0.5912",  "802", "0.7735",  "803", "0.0",  "804", "0.3919",  "805", "0.0844",
         "806", "0.113",  "807", "0.8668",  "808", "0.999",  "809", "0.2584",  "810", "0.3086",
         "811", "0.4901",  "812", "0.809",  "813", "0.8941",  "814", "0.7003",  "815", "0.1236",
         "816", "0.6839",  "817", "0.8519",  "818", "0.3939",  "819", "0.9821",  "820", "0.7109",
         "821", "0.1757",  "822", "0.2279",  "823", "0.6743",  "824", "0.3156",  "825", "0.1065",
         "826", "0.4355",  "827", "0.6512",  "828", "0.4171",  "829", "0.2689",  "830", "0.1837",
         "831", "0.8051",  "832", "0.5456",  "833", "0.6858",  "834", "0.5333",  "835", "0.0675",
         "836", "0.1825",  "837", "0.0306",  "838", "0.6537",  "839", "0.5113",  "840", "0.1744",
         "841", "0.55",  "842", "0.1059",  "843", "0.6511",  "844", "0.0619",  "845", "0.3234",
         "846", "0.5644",  "847", "0.4759",  "848", "0.2927",  "849", "0.5632",  "850", "0.3406"};


	private static String[] CASCADE = new String[] {
	 "776", "0.2009",  "777", "0.1204",  "778", "0.4935",  "779", "0.818",  "780", "0.426", 
	 "781", "0.4328",  "782", "0.4858",  "783", "0.5473",  "784", "0.7395",  "785", "0.7571", 
	 "786", "0.2876",  "787", "0.826",  "788", "0.7559",  "789", "0.3708",  "790", "0.779", 
	 "791", "0.6353",  "792", "0.2766",  "793", "0.536",  "794", "0.4965",  "795", "0.0", 
	 "796", "0.3649",  "797", "0.2748",  "798", "0.3107",  "799", "0.4342",  "800", "0.2004", 
	 "801", "0.5646",  "802", "0.9636",  "803", "0.0",  "804", "0.4886",  "805", "0.2663", 
	 "806", "0.4602",  "807", "0.9347",  "808", "0.9972",  "809", "0.2702",  "810", "0.4071", 
	 "811", "0.5542",  "812", "0.7972",  "813", "0.7544",  "814", "0.7059",  "815", "0.1729", 
	 "816", "0.6187",  "817", "0.7951",  "818", "0.4648",  "819", "0.9238",  "820", "0.7309", 
	 "821", "0.2754",  "822", "0.1471",  "823", "0.6242",  "824", "0.3296",  "825", "0.2644", 
	 "826", "0.4148",  "827", "0.6896",  "828", "0.5188",  "829", "0.3364",  "830", "0.3133", 
	 "831", "0.7694",  "832", "0.8041",  "833", "0.3527",  "834", "0.4122",  "835", "0.1793", 
	 "836", "0.248",  "837", "0.0243",  "838", "0.6906",  "839", "0.6329",  "840", "0.1744", 
	 "841", "0.6478",  "842", "0.1061",  "843", "0.6843",  "844", "0.158",  "845", "0.2703", 
	 "846", "0.5815",  "847", "0.2706",  "848", "0.1836",  "849", "0.6963",  "850", "0.3452"};


	private static String [] featurePrune = new String [] {
	 "776", "0.268",  "777", "0.1257",  "778", "0.4791",  "779", "0.9087",  "780", "0.4601", 
	 "781", "0.5673",  "782", "0.5214",  "783", "0.4787",  "784", "0.7574",  "785", "0.8446", 
	 "786", "0.2088",  "787", "0.8758",  "788", "0.7909",  "789", "0.3502",  "790", "0.8563", 
	 "791", "0.611",  "792", "0.2369",  "793", "0.5467",  "794", "0.4725",  "795", "0.0", 
	 "796", "0.3535",  "797", "0.2122",  "798", "0.1414",  "799", "0.3632",  "800", "0.1413", 
	 "801", "0.5867",  "802", "0.9572",  "803", "0.0",  "804", "0.4972",  "805", "0.1564", 
	 "806", "0.5721",  "807", "0.9393",  "808", "0.9549",  "809", "0.3055",  "810", "0.4375", 
	 "811", "0.5875",  "812", "0.7242",  "813", "0.7551",  "814", "0.7059",  "815", "0.1877", 
	 "816", "0.5724",  "817", "0.9046",  "818", "0.4776",  "819", "0.9239",  "820", "0.7114", 
	 "821", "0.2575",  "822", "0.1366",  "823", "0.6292",  "824", "0.3156",  "825", "0.1378", 
	 "826", "0.4913",  "827", "0.5058",  "828", "0.5376",  "829", "0.4397",  "830", "0.3218", 
	 "831", "0.7272",  "832", "0.5761",  "833", "0.3533",  "834", "0.5356",  "835", "0.7072", 
	 "836", "0.1959",  "837", "0.0",  "838", "0.6889",  "839", "0.6344",  "840", "0.1744", 
	 "841", "0.5466",  "842", "0.1061",  "843", "0.6907",  "844", "0.1686",  "845", "0.2825", 
	 "846", "0.478",  "847", "0.0472",  "848", "0.148",  "849", "0.6821",  "850", "0.3229"};

	private static String [] adaRank = new String[] {
	"776", "0.3606",  "777", "0.1974",  "778", "0.4772",  "779", "0.8196",  "780", "0.4171", 
	 "781", "0.4515",  "782", "0.4662",  "783", "0.4907",  "784", "0.7343",  "785", "0.7582", 
	 "786", "0.2957",  "787", "0.8193",  "788", "0.7903",  "789", "0.3039",  "790", "0.8513", 
	 "791", "0.6344",  "792", "0.1388",  "793", "0.5342",  "794", "0.4899",  "795", "0.0", 
	 "796", "0.4625",  "797", "0.2696",  "798", "0.2067",  "799", "0.3818",  "800", "0.1652", 
	 "801", "0.5664",  "802", "0.9572",  "803", "0.0",  "804", "0.4868",  "805", "0.2565", 
	 "806", "0.4471",  "807", "0.9393",  "808", "0.9972",  "809", "0.2847",  "810", "0.3979", 
	 "811", "0.5465",  "812", "0.7894",  "813", "0.7534",  "814", "0.6837",  "815", "0.141", 
	 "816", "0.6565",  "817", "0.9552",  "818", "0.4182",  "819", "0.9452",  "820", "0.7114", 
	 "821", "0.2357",  "822", "0.1465",  "823", "0.6107",  "824", "0.3296",  "825", "0.223", 
	 "826", "0.4304",  "827", "0.6748",  "828", "0.5174",  "829", "0.3902",  "830", "0.3167", 
	 "831", "0.6904",  "832", "0.6852",  "833", "0.3654",  "834", "0.1874",  "835", "0.6145", 
	 "836", "0.2666",  "837", "0.0508",  "838", "0.5806",  "839", "0.6776",  "840", "0.1744", 
	 "841", "0.6566",  "842", "0.0876",  "843", "0.6896",  "844", "0.132",  "845", "0.2874", 
	 "846", "0.5721",  "847", "0.2113",  "848", "0.2156",  "849", "0.7059",  "850", "0.3569"};



	@Test
	public void runRegression() throws Exception {
		Map<String, GroundTruth> g = new HashMap<String, GroundTruth>();

		g.put("gov2-ql", new GroundTruth("gov2-ql", Metric.NDCG20, 75, QL, 0.4457f));

		g.put("gov2-cascade", new GroundTruth("gov2-cascade", Metric.NDCG20, 75, CASCADE, 0.4744f));

		g.put("gov2-adaRank", new GroundTruth("gov2-adaRank", Metric.NDCG20, 75, adaRank, 0.4737f));

		g.put("gov2-featureprune", new GroundTruth("gov2-featureprune", Metric.NDCG20, 75, featurePrune, 0.4716f));

		Qrels_new qrels = new Qrels_new("data/gov2/qrels.gov2.all");

    String[] params = new String[] {
            "data/gov2/run.gov2.SIGIR2011.xml",
            "data/gov2/gov2.title.776-850" };

		FileSystem fs = FileSystem.getLocal(new Configuration());

		CascadeBatchQueryRunner qr = new CascadeBatchQueryRunner(params, fs);

		long start = System.currentTimeMillis();
		qr.runQueries();
		long end = System.currentTimeMillis();

		sLogger.info("Total query time: " + (end - start) + "ms");

		DocnoMapping mapping = qr.getDocnoMapping();

		for (String model : qr.getModels()) {
			sLogger.info("Verifying results of model \"" + model + "\"");

			Map<String, Accumulator[]> results = qr.getResults(model);
			g.get(model).verify(results, mapping, qrels);

			sLogger.info("Done!");
		}
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(Gov2_SIGIR2011.class);
	}
}
