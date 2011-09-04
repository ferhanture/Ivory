package ivory.regression.basic;

import ivory.core.eval.Qrels;
import ivory.regression.GroundTruth;
import ivory.regression.GroundTruth.Metric;
import ivory.smrf.retrieval.Accumulator;
import ivory.smrf.retrieval.BatchQueryRunner;

import java.util.Map;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.google.common.collect.Maps;

import edu.umd.cloud9.collection.DocnoMapping;

public class Wt10g_Basic {
  private static final Logger LOG = Logger.getLogger(Wt10g_Basic.class);

  private static String[] sDirBaseRawAP = new String[] { "451", "0.5737", "452", "0.1679", "453",
          "0.3391", "454", "0.3907", "455", "0.3809", "456", "0.0143", "457", "0.1027", "458",
          "0.0176", "459", "0.2144", "460", "0.1308", "461", "0.7067", "462", "0.0976", "463",
          "0.2745", "464", "0.0007", "465", "0.0093", "466", "0.2623", "467", "0.1471", "468",
          "0.0129", "469", "0.2457", "470", "0.1530", "471", "0.0561", "472", "0.0804", "473",
          "0.0000", "474", "0.0098", "475", "0.2392", "476", "0.4080", "477", "0.0359", "478",
          "0.0113", "479", "0.2865", "480", "0.0022", "481", "0.1946", "482", "0.0708", "483",
          "0.0290", "484", "0.1279", "485", "1.0000", "486", "0.6220", "487", "0.0000", "488",
          "0.0752", "489", "0.0771", "490", "0.1823", "491", "0.0064", "492", "0.2413", "493",
          "0.0059", "494", "0.2218", "495", "0.2012", "496", "0.3029", "497", "0.3810", "498",
          "0.0879", "499", "0.6782", "500", "0.0865", "501", "0.3504", "502", "0.0950", "503",
          "0.1494", "504", "0.4456", "505", "0.3715", "506", "0.1279", "507", "0.2721", "508",
          "0.2428", "509", "0.3795", "510", "0.5734", "511", "0.3559", "512", "0.1816", "513",
          "0.0954", "514", "0.1943", "515", "0.2135", "516", "0.0945", "517", "0.0265", "518",
          "0.1751", "519", "0.1184", "520", "0.1003", "521", "0.0201", "522", "0.3166", "523",
          "0.4673", "524", "0.0830", "525", "0.1283", "526", "0.0965", "527", "0.4087", "528",
          "0.5203", "529", "0.3263", "530", "0.5479", "531", "0.0154", "532", "0.2257", "533",
          "0.1060", "534", "0.0153", "535", "0.0438", "536", "0.2023", "537", "0.0507", "538",
          "0.3095", "539", "0.0698", "540", "0.1286", "541", "0.2877", "542", "0.0308", "543",
          "0.0073", "544", "0.5858", "545", "0.1833", "546", "0.1123", "547", "0.1632", "548",
          "0.4167", "549", "0.2251", "550", "0.0964" };

  private static String[] sDirBaseRawP10 = new String[] { "451", "0.8000", "452", "0.6000",
          "453", "0.8000", "454", "0.8000", "455", "0.8000", "456", "0.1000", "457", "0.0000",
          "458", "0.0000", "459", "0.5000", "460", "0.3000", "461", "0.3000", "462", "0.3000",
          "463", "0.3000", "464", "0.0000", "465", "0.0000", "466", "0.1000", "467", "0.3000",
          "468", "0.0000", "469", "0.4000", "470", "0.2000", "471", "0.1000", "472", "0.2000",
          "473", "0.0000", "474", "0.0000", "475", "0.4000", "476", "0.5000", "477", "0.0000",
          "478", "0.0000", "479", "0.5000", "480", "0.0000", "481", "0.2000", "482", "0.1000",
          "483", "0.0000", "484", "0.2000", "485", "0.2000", "486", "0.4000", "487", "0.0000",
          "488", "0.1000", "489", "0.2000", "490", "0.4000", "491", "0.0000", "492", "0.5000",
          "493", "0.0000", "494", "0.6000", "495", "0.8000", "496", "0.4000", "497", "0.4000",
          "498", "0.1000", "499", "0.2000", "500", "0.1000", "501", "0.3000", "502", "0.5000",
          "503", "0.2000", "504", "0.7000", "505", "0.6000", "506", "0.1000", "507", "0.2000",
          "508", "0.3000", "509", "0.8000", "510", "0.9000", "511", "0.6000", "512", "0.2000",
          "513", "0.0000", "514", "0.4000", "515", "0.2000", "516", "0.0000", "517", "0.2000",
          "518", "0.4000", "519", "0.6000", "520", "0.2000", "521", "0.1000", "522", "0.3000",
          "523", "0.9000", "524", "0.2000", "525", "0.3000", "526", "0.1000", "527", "0.9000",
          "528", "0.3000", "529", "0.6000", "530", "0.8000", "531", "0.0000", "532", "0.3000",
          "533", "0.4000", "534", "0.1000", "535", "0.1000", "536", "0.4000", "537", "0.3000",
          "538", "0.2000", "539", "0.2000", "540", "0.1000", "541", "0.7000", "542", "0.1000",
          "543", "0.1000", "544", "1.0000", "545", "0.2000", "546", "0.2000", "547", "0.2000",
          "548", "0.2000", "549", "0.7000", "550", "0.4000" };

  private static String[] sDirSDRawAP = new String[] { "451", "0.5775", "452", "0.1117", "453",
          "0.3391", "454", "0.4009", "455", "0.4119", "456", "0.0744", "457", "0.1150", "458",
          "0.0176", "459", "0.2083", "460", "0.1311", "461", "0.7060", "462", "0.1067", "463",
          "0.2745", "464", "0.0023", "465", "0.0093", "466", "0.2625", "467", "0.1395", "468",
          "0.0203", "469", "0.3033", "470", "0.1530", "471", "0.1472", "472", "0.0717", "473",
          "0.0000", "474", "0.0108", "475", "0.2471", "476", "0.4083", "477", "0.0349", "478",
          "0.0113", "479", "0.3159", "480", "0.0013", "481", "0.2097", "482", "0.0760", "483",
          "0.0293", "484", "0.1216", "485", "1.0000", "486", "0.9167", "487", "0.0000", "488",
          "0.0778", "489", "0.0771", "490", "0.1853", "491", "0.0087", "492", "0.3111", "493",
          "0.0059", "494", "0.2218", "495", "0.1581", "496", "0.3029", "497", "0.3810", "498",
          "0.0967", "499", "0.5662", "500", "0.0827", "501", "0.3413", "502", "0.1259", "503",
          "0.1322", "504", "0.4283", "505", "0.4073", "506", "0.1281", "507", "0.2723", "508",
          "0.2713", "509", "0.3800", "510", "0.6576", "511", "0.3831", "512", "0.1852", "513",
          "0.0954", "514", "0.1857", "515", "0.2286", "516", "0.0945", "517", "0.0182", "518",
          "0.1929", "519", "0.1188", "520", "0.0975", "521", "0.0364", "522", "0.3502", "523",
          "0.4673", "524", "0.0773", "525", "0.1564", "526", "0.0965", "527", "0.4779", "528",
          "0.5203", "529", "0.2933", "530", "0.5115", "531", "0.0316", "532", "0.2257", "533",
          "0.1506", "534", "0.0127", "535", "0.0587", "536", "0.2232", "537", "0.0550", "538",
          "0.3095", "539", "0.0500", "540", "0.1218", "541", "0.3246", "542", "0.0304", "543",
          "0.0124", "544", "0.5718", "545", "0.2655", "546", "0.1222", "547", "0.1540", "548",
          "0.4167", "549", "0.2456", "550", "0.0991" };

  private static String[] sDirSDRawP10 = new String[] { "451", "0.9000", "452", "0.5000", "453",
          "0.8000", "454", "0.8000", "455", "0.7000", "456", "0.1000", "457", "0.2000", "458",
          "0.0000", "459", "0.5000", "460", "0.2000", "461", "0.3000", "462", "0.3000", "463",
          "0.3000", "464", "0.0000", "465", "0.0000", "466", "0.1000", "467", "0.3000", "468",
          "0.0000", "469", "0.4000", "470", "0.2000", "471", "0.1000", "472", "0.3000", "473",
          "0.0000", "474", "0.0000", "475", "0.4000", "476", "0.5000", "477", "0.0000", "478",
          "0.0000", "479", "0.6000", "480", "0.0000", "481", "0.3000", "482", "0.2000", "483",
          "0.0000", "484", "0.2000", "485", "0.2000", "486", "0.4000", "487", "0.0000", "488",
          "0.1000", "489", "0.2000", "490", "0.6000", "491", "0.0000", "492", "0.5000", "493",
          "0.0000", "494", "0.6000", "495", "0.4000", "496", "0.4000", "497", "0.4000", "498",
          "0.1000", "499", "0.2000", "500", "0.1000", "501", "0.2000", "502", "0.6000", "503",
          "0.2000", "504", "0.6000", "505", "0.6000", "506", "0.1000", "507", "0.3000", "508",
          "0.4000", "509", "0.9000", "510", "1.0000", "511", "0.7000", "512", "0.1000", "513",
          "0.0000", "514", "0.4000", "515", "0.2000", "516", "0.0000", "517", "0.2000", "518",
          "0.4000", "519", "0.6000", "520", "0.2000", "521", "0.2000", "522", "0.3000", "523",
          "0.9000", "524", "0.1000", "525", "0.3000", "526", "0.1000", "527", "0.9000", "528",
          "0.3000", "529", "0.5000", "530", "0.8000", "531", "0.1000", "532", "0.3000", "533",
          "0.7000", "534", "0.0000", "535", "0.1000", "536", "0.3000", "537", "0.3000", "538",
          "0.2000", "539", "0.2000", "540", "0.1000", "541", "0.7000", "542", "0.1000", "543",
          "0.1000", "544", "1.0000", "545", "0.3000", "546", "0.2000", "547", "0.2000", "548",
          "0.2000", "549", "0.6000", "550", "0.4000" };

  private static String[] sDirFDRawAP = new String[] { "451", "0.5775", "452", "0.1039", "453",
          "0.3391", "454", "0.4009", "455", "0.3492", "456", "0.0101", "457", "0.1150", "458",
          "0.0176", "459", "0.2069", "460", "0.1311", "461", "0.7060", "462", "0.1825", "463",
          "0.2745", "464", "0.0023", "465", "0.0093", "466", "0.2627", "467", "0.1298", "468",
          "0.0212", "469", "0.3033", "470", "0.1530", "471", "0.0456", "472", "0.0691", "473",
          "0.0000", "474", "0.0077", "475", "0.2471", "476", "0.4083", "477", "0.0222", "478",
          "0.0113", "479", "0.2504", "480", "0.0005", "481", "0.2063", "482", "0.0537", "483",
          "0.0293", "484", "0.1216", "485", "1.0000", "486", "1.0000", "487", "0.0000", "488",
          "0.0754", "489", "0.0771", "490", "0.2003", "491", "0.0087", "492", "0.3111", "493",
          "0.0059", "494", "0.2218", "495", "0.1069", "496", "0.3029", "497", "0.3810", "498",
          "0.0967", "499", "0.5662", "500", "0.0827", "501", "0.3134", "502", "0.1259", "503",
          "0.1322", "504", "0.4283", "505", "0.4056", "506", "0.1281", "507", "0.2723", "508",
          "0.2923", "509", "0.3800", "510", "0.6576", "511", "0.3960", "512", "0.1852", "513",
          "0.0954", "514", "0.1857", "515", "0.2253", "516", "0.0945", "517", "0.0184", "518",
          "0.2190", "519", "0.1188", "520", "0.0975", "521", "0.0611", "522", "0.3759", "523",
          "0.4673", "524", "0.0773", "525", "0.1564", "526", "0.0965", "527", "0.4779", "528",
          "0.5184", "529", "0.2933", "530", "0.4973", "531", "0.0316", "532", "0.2257", "533",
          "0.1886", "534", "0.0119", "535", "0.0520", "536", "0.2233", "537", "0.0700", "538",
          "0.3095", "539", "0.0720", "540", "0.1214", "541", "0.3304", "542", "0.0304", "543",
          "0.0124", "544", "0.5718", "545", "0.4305", "546", "0.1222", "547", "0.1540", "548",
          "0.5000", "549", "0.2291", "550", "0.0991" };

  private static String[] sDirFDRawP10 = new String[] { "451", "0.9000", "452", "0.6000", "453",
          "0.8000", "454", "0.8000", "455", "0.7000", "456", "0.1000", "457", "0.2000", "458",
          "0.0000", "459", "0.6000", "460", "0.2000", "461", "0.3000", "462", "0.3000", "463",
          "0.3000", "464", "0.0000", "465", "0.0000", "466", "0.1000", "467", "0.3000", "468",
          "0.0000", "469", "0.4000", "470", "0.2000", "471", "0.1000", "472", "0.2000", "473",
          "0.0000", "474", "0.0000", "475", "0.4000", "476", "0.5000", "477", "0.0000", "478",
          "0.0000", "479", "0.5000", "480", "0.0000", "481", "0.3000", "482", "0.1000", "483",
          "0.0000", "484", "0.2000", "485", "0.2000", "486", "0.4000", "487", "0.0000", "488",
          "0.0000", "489", "0.2000", "490", "0.5000", "491", "0.0000", "492", "0.5000", "493",
          "0.0000", "494", "0.6000", "495", "0.4000", "496", "0.4000", "497", "0.4000", "498",
          "0.1000", "499", "0.2000", "500", "0.1000", "501", "0.3000", "502", "0.6000", "503",
          "0.2000", "504", "0.6000", "505", "0.6000", "506", "0.1000", "507", "0.3000", "508",
          "0.5000", "509", "0.9000", "510", "1.0000", "511", "0.7000", "512", "0.1000", "513",
          "0.0000", "514", "0.4000", "515", "0.2000", "516", "0.0000", "517", "0.1000", "518",
          "0.3000", "519", "0.6000", "520", "0.2000", "521", "0.3000", "522", "0.3000", "523",
          "0.9000", "524", "0.1000", "525", "0.3000", "526", "0.1000", "527", "0.9000", "528",
          "0.3000", "529", "0.5000", "530", "0.8000", "531", "0.1000", "532", "0.3000", "533",
          "0.9000", "534", "0.0000", "535", "0.1000", "536", "0.4000", "537", "0.2000", "538",
          "0.2000", "539", "0.2000", "540", "0.1000", "541", "0.8000", "542", "0.1000", "543",
          "0.1000", "544", "1.0000", "545", "0.6000", "546", "0.2000", "547", "0.2000", "548",
          "0.2000", "549", "0.7000", "550", "0.4000" };

  private static String[] sBm25BaseRawAP = new String[] { "451", "0.5705", "452", "0.1638",
          "453", "0.3413", "454", "0.4316", "455", "0.4255", "456", "0.0048", "457", "0.1173",
          "458", "0.0178", "459", "0.1868", "460", "0.1456", "461", "0.7047", "462", "0.1417",
          "463", "0.2745", "464", "0.0012", "465", "0.0091", "466", "0.2628", "467", "0.1400",
          "468", "0.0138", "469", "0.2502", "470", "0.1517", "471", "0.0293", "472", "0.0455",
          "473", "0.0000", "474", "0.0178", "475", "0.2502", "476", "0.3878", "477", "0.0267",
          "478", "0.0114", "479", "0.3247", "480", "0.0070", "481", "0.2236", "482", "0.0351",
          "483", "0.0248", "484", "0.1129", "485", "1.0000", "486", "0.7875", "487", "0.0000",
          "488", "0.1109", "489", "0.0759", "490", "0.2014", "491", "0.0029", "492", "0.2783",
          "493", "0.0061", "494", "0.2196", "495", "0.2407", "496", "0.2976", "497", "0.3804",
          "498", "0.1030", "499", "0.6738", "500", "0.0666", "501", "0.4123", "502", "0.0709",
          "503", "0.1656", "504", "0.4026", "505", "0.3535", "506", "0.0302", "507", "0.4091",
          "508", "0.2407", "509", "0.3824", "510", "0.6253", "511", "0.3496", "512", "0.1782",
          "513", "0.0965", "514", "0.1790", "515", "0.1688", "516", "0.0948", "517", "0.0208",
          "518", "0.2024", "519", "0.0938", "520", "0.1072", "521", "0.0220", "522", "0.2315",
          "523", "0.4681", "524", "0.0940", "525", "0.1007", "526", "0.0961", "527", "0.4014",
          "528", "0.5138", "529", "0.3388", "530", "0.5287", "531", "0.0420", "532", "0.1984",
          "533", "0.2098", "534", "0.0128", "535", "0.0344", "536", "0.1080", "537", "0.0437",
          "538", "0.3250", "539", "0.0768", "540", "0.1339", "541", "0.1872", "542", "0.0067",
          "543", "0.0028", "544", "0.6288", "545", "0.0819", "546", "0.1268", "547", "0.1882",
          "548", "0.5000", "549", "0.1855", "550", "0.0701" };

  private static String[] sBm25BaseRawP10 = new String[] { "451", "0.9000", "452", "0.4000",
          "453", "0.8000", "454", "0.8000", "455", "0.8000", "456", "0.0000", "457", "0.1000",
          "458", "0.0000", "459", "0.3000", "460", "0.2000", "461", "0.3000", "462", "0.3000",
          "463", "0.3000", "464", "0.0000", "465", "0.0000", "466", "0.1000", "467", "0.4000",
          "468", "0.0000", "469", "0.4000", "470", "0.2000", "471", "0.1000", "472", "0.1000",
          "473", "0.0000", "474", "0.0000", "475", "0.5000", "476", "0.5000", "477", "0.0000",
          "478", "0.0000", "479", "0.7000", "480", "0.0000", "481", "0.4000", "482", "0.0000",
          "483", "0.0000", "484", "0.1000", "485", "0.2000", "486", "0.4000", "487", "0.0000",
          "488", "0.1000", "489", "0.2000", "490", "0.6000", "491", "0.0000", "492", "0.5000",
          "493", "0.0000", "494", "0.6000", "495", "1.0000", "496", "0.4000", "497", "0.4000",
          "498", "0.1000", "499", "0.2000", "500", "0.1000", "501", "0.6000", "502", "0.5000",
          "503", "0.4000", "504", "0.6000", "505", "0.6000", "506", "0.0000", "507", "0.5000",
          "508", "0.4000", "509", "0.9000", "510", "0.9000", "511", "0.6000", "512", "0.2000",
          "513", "0.0000", "514", "0.3000", "515", "0.2000", "516", "0.0000", "517", "0.2000",
          "518", "0.5000", "519", "0.4000", "520", "0.2000", "521", "0.2000", "522", "0.3000",
          "523", "0.9000", "524", "0.3000", "525", "0.1000", "526", "0.1000", "527", "0.9000",
          "528", "0.3000", "529", "0.6000", "530", "0.8000", "531", "0.1000", "532", "0.3000",
          "533", "0.8000", "534", "0.0000", "535", "0.1000", "536", "0.2000", "537", "0.3000",
          "538", "0.2000", "539", "0.1000", "540", "0.1000", "541", "0.6000", "542", "0.1000",
          "543", "0.0000", "544", "1.0000", "545", "0.2000", "546", "0.3000", "547", "0.2000",
          "548", "0.2000", "549", "0.5000", "550", "0.4000" };

  private static String[] sBm25SDRawAP = new String[] { "451", "0.5861", "452", "0.0669", "453",
          "0.3413", "454", "0.4420", "455", "0.4412", "456", "0.0736", "457", "0.1318", "458",
          "0.0178", "459", "0.1651", "460", "0.1211", "461", "0.7075", "462", "0.1204", "463",
          "0.2745", "464", "0.0079", "465", "0.0091", "466", "0.2625", "467", "0.1387", "468",
          "0.0240", "469", "0.3078", "470", "0.1517", "471", "0.0738", "472", "0.0782", "473",
          "0.0019", "474", "0.0157", "475", "0.2502", "476", "0.4058", "477", "0.0193", "478",
          "0.0114", "479", "0.4475", "480", "0.0067", "481", "0.2329", "482", "0.0482", "483",
          "0.0248", "484", "0.1041", "485", "1.0000", "486", "0.8611", "487", "0.0000", "488",
          "0.0856", "489", "0.0759", "490", "0.2215", "491", "0.0063", "492", "0.4100", "493",
          "0.0061", "494", "0.2196", "495", "0.2107", "496", "0.2976", "497", "0.3804", "498",
          "0.1449", "499", "0.4244", "500", "0.0513", "501", "0.4354", "502", "0.1204", "503",
          "0.1581", "504", "0.4460", "505", "0.4134", "506", "0.0302", "507", "0.3704", "508",
          "0.2659", "509", "0.3950", "510", "0.7502", "511", "0.4076", "512", "0.1812", "513",
          "0.0965", "514", "0.1264", "515", "0.2081", "516", "0.0948", "517", "0.0117", "518",
          "0.2575", "519", "0.1115", "520", "0.1151", "521", "0.0575", "522", "0.4564", "523",
          "0.4681", "524", "0.0860", "525", "0.1253", "526", "0.0961", "527", "0.5141", "528",
          "0.5119", "529", "0.3027", "530", "0.4910", "531", "0.1151", "532", "0.1984", "533",
          "0.2996", "534", "0.0093", "535", "0.0791", "536", "0.1405", "537", "0.0779", "538",
          "0.3250", "539", "0.0370", "540", "0.1205", "541", "0.2302", "542", "0.0064", "543",
          "0.0040", "544", "0.6192", "545", "0.1599", "546", "0.1497", "547", "0.1889", "548",
          "0.5833", "549", "0.2296", "550", "0.0725" };

  private static String[] sBm25SDRawP10 = new String[] { "451", "0.8000", "452", "0.5000", "453",
          "0.8000", "454", "0.8000", "455", "0.7000", "456", "0.1000", "457", "0.3000", "458",
          "0.0000", "459", "0.4000", "460", "0.1000", "461", "0.3000", "462", "0.3000", "463",
          "0.3000", "464", "0.0000", "465", "0.0000", "466", "0.1000", "467", "0.4000", "468",
          "0.0000", "469", "0.4000", "470", "0.2000", "471", "0.1000", "472", "0.2000", "473",
          "0.0000", "474", "0.2000", "475", "0.5000", "476", "0.5000", "477", "0.0000", "478",
          "0.0000", "479", "0.9000", "480", "0.0000", "481", "0.4000", "482", "0.1000", "483",
          "0.0000", "484", "0.1000", "485", "0.2000", "486", "0.4000", "487", "0.0000", "488",
          "0.1000", "489", "0.2000", "490", "0.6000", "491", "0.0000", "492", "0.5000", "493",
          "0.0000", "494", "0.6000", "495", "1.0000", "496", "0.4000", "497", "0.4000", "498",
          "0.1000", "499", "0.2000", "500", "0.1000", "501", "0.6000", "502", "0.6000", "503",
          "0.4000", "504", "0.6000", "505", "0.6000", "506", "0.0000", "507", "0.4000", "508",
          "0.5000", "509", "0.9000", "510", "1.0000", "511", "0.6000", "512", "0.2000", "513",
          "0.0000", "514", "0.2000", "515", "0.2000", "516", "0.0000", "517", "0.1000", "518",
          "0.4000", "519", "0.4000", "520", "0.2000", "521", "0.2000", "522", "0.3000", "523",
          "0.9000", "524", "0.2000", "525", "0.3000", "526", "0.1000", "527", "0.9000", "528",
          "0.3000", "529", "0.5000", "530", "0.8000", "531", "0.3000", "532", "0.3000", "533",
          "0.8000", "534", "0.0000", "535", "0.2000", "536", "0.3000", "537", "0.4000", "538",
          "0.2000", "539", "0.1000", "540", "0.1000", "541", "0.8000", "542", "0.1000", "543",
          "0.0000", "544", "1.0000", "545", "0.2000", "546", "0.2000", "547", "0.2000", "548",
          "0.2000", "549", "0.5000", "550", "0.4000" };

  private static String[] sBm25FDRawAP = new String[] { "451", "0.5822", "452", "0.1065", "453",
          "0.3413", "454", "0.4404", "455", "0.4368", "456", "0.0419", "457", "0.1285", "458",
          "0.0178", "459", "0.1933", "460", "0.1367", "461", "0.7064", "462", "0.1639", "463",
          "0.2745", "464", "0.0035", "465", "0.0091", "466", "0.2625", "467", "0.1370", "468",
          "0.0205", "469", "0.2502", "470", "0.1517", "471", "0.0194", "472", "0.0549", "473",
          "0.0012", "474", "0.0142", "475", "0.2494", "476", "0.3937", "477", "0.0141", "478",
          "0.0114", "479", "0.4305", "480", "0.0057", "481", "0.2306", "482", "0.0905", "483",
          "0.0248", "484", "0.1101", "485", "1.0000", "486", "0.8750", "487", "0.0000", "488",
          "0.0847", "489", "0.0759", "490", "0.2322", "491", "0.0050", "492", "0.3837", "493",
          "0.0061", "494", "0.2196", "495", "0.2293", "496", "0.2976", "497", "0.3804", "498",
          "0.1234", "499", "0.4744", "500", "0.0616", "501", "0.4211", "502", "0.0976", "503",
          "0.1632", "504", "0.4277", "505", "0.3888", "506", "0.0302", "507", "0.4036", "508",
          "0.2870", "509", "0.3946", "510", "0.6972", "511", "0.4298", "512", "0.1768", "513",
          "0.0965", "514", "0.1494", "515", "0.2036", "516", "0.0948", "517", "0.0130", "518",
          "0.2708", "519", "0.1062", "520", "0.1107", "521", "0.0505", "522", "0.2926", "523",
          "0.4681", "524", "0.0885", "525", "0.1361", "526", "0.0961", "527", "0.4688", "528",
          "0.5119", "529", "0.3152", "530", "0.5120", "531", "0.1275", "532", "0.1984", "533",
          "0.2627", "534", "0.0118", "535", "0.0502", "536", "0.1406", "537", "0.0995", "538",
          "0.3250", "539", "0.0715", "540", "0.1286", "541", "0.2304", "542", "0.0083", "543",
          "0.0037", "544", "0.6224", "545", "0.1588", "546", "0.1621", "547", "0.1878", "548",
          "0.5833", "549", "0.2085", "550", "0.0727" };

  private static String[] sBm25FDRawP10 = new String[] { "451", "0.8000", "452", "0.5000", "453",
          "0.8000", "454", "0.8000", "455", "0.8000", "456", "0.1000", "457", "0.3000", "458",
          "0.0000", "459", "0.6000", "460", "0.2000", "461", "0.3000", "462", "0.3000", "463",
          "0.3000", "464", "0.0000", "465", "0.0000", "466", "0.1000", "467", "0.3000", "468",
          "0.0000", "469", "0.4000", "470", "0.2000", "471", "0.0000", "472", "0.2000", "473",
          "0.0000", "474", "0.2000", "475", "0.5000", "476", "0.5000", "477", "0.0000", "478",
          "0.0000", "479", "0.8000", "480", "0.0000", "481", "0.4000", "482", "0.2000", "483",
          "0.0000", "484", "0.1000", "485", "0.2000", "486", "0.4000", "487", "0.0000", "488",
          "0.1000", "489", "0.2000", "490", "0.7000", "491", "0.0000", "492", "0.5000", "493",
          "0.0000", "494", "0.6000", "495", "1.0000", "496", "0.4000", "497", "0.4000", "498",
          "0.1000", "499", "0.2000", "500", "0.1000", "501", "0.5000", "502", "0.6000", "503",
          "0.4000", "504", "0.6000", "505", "0.6000", "506", "0.0000", "507", "0.4000", "508",
          "0.4000", "509", "0.9000", "510", "1.0000", "511", "0.6000", "512", "0.1000", "513",
          "0.0000", "514", "0.4000", "515", "0.2000", "516", "0.0000", "517", "0.1000", "518",
          "0.5000", "519", "0.5000", "520", "0.2000", "521", "0.3000", "522", "0.3000", "523",
          "0.9000", "524", "0.2000", "525", "0.3000", "526", "0.1000", "527", "0.9000", "528",
          "0.3000", "529", "0.5000", "530", "0.8000", "531", "0.3000", "532", "0.3000", "533",
          "0.8000", "534", "0.0000", "535", "0.2000", "536", "0.4000", "537", "0.4000", "538",
          "0.2000", "539", "0.1000", "540", "0.1000", "541", "0.8000", "542", "0.1000", "543",
          "0.0000", "544", "1.0000", "545", "0.3000", "546", "0.3000", "547", "0.2000", "548",
          "0.2000", "549", "0.6000", "550", "0.4000" };

  @Test
  public void runRegression() throws Exception {
    String[] params = new String[] {
            "data/wt10g/run.wt10g.basic.xml",
            "data/wt10g/queries.wt10g.451-500.xml",
            "data/wt10g/queries.wt10g.501-550.xml" };

    FileSystem fs = FileSystem.getLocal(new Configuration());

    BatchQueryRunner qr = new BatchQueryRunner(params, fs);

    long start = System.currentTimeMillis();
    qr.runQueries();
    long end = System.currentTimeMillis();

    LOG.info("Total query time: " + (end - start) + "ms");

    verifyAllResults(qr.getModels(), qr.getAllResults(), qr.getDocnoMapping(),
        new Qrels("data/wt10g/qrels.wt10g.all"));
  }

  public static void verifyAllResults(Set<String> models,
      Map<String, Map<String, Accumulator[]>> results, DocnoMapping mapping, Qrels qrels) {

    Map<String, GroundTruth> g = Maps.newHashMap();
    // One topic didn't contain qrels, so trec_eval only picked up 99.
    g.put("wt10g-dir-base", new GroundTruth(Metric.AP, 99, sDirBaseRawAP, 0.2093f));
    g.put("wt10g-dir-sd", new GroundTruth(Metric.AP, 99, sDirSDRawAP, 0.2187f));
    g.put("wt10g-dir-fd", new GroundTruth(Metric.AP, 99, sDirFDRawAP, 0.2200f));
    g.put("wt10g-bm25-base", new GroundTruth(Metric.AP, 99, sBm25BaseRawAP, 0.2105f));
    g.put("wt10g-bm25-sd", new GroundTruth(Metric.AP, 99, sBm25SDRawAP, 0.2248f));
    g.put("wt10g-bm25-fd", new GroundTruth(Metric.AP, 99, sBm25FDRawAP, 0.2228f));

    Map<String, GroundTruth> h = Maps.newHashMap();
    // One topic didn't contain qrels, so trec_eval only picked up 99.
    h.put("wt10g-dir-base", new GroundTruth(Metric.P10, 99, sDirBaseRawP10, 0.3131f));
    h.put("wt10g-dir-sd", new GroundTruth(Metric.P10, 99, sDirSDRawP10, 0.3192f));
    h.put("wt10g-dir-fd", new GroundTruth(Metric.P10, 99, sDirFDRawP10, 0.3242f));
    h.put("wt10g-bm25-base", new GroundTruth(Metric.P10, 99, sBm25BaseRawP10, 0.3202f));
    h.put("wt10g-bm25-sd", new GroundTruth(Metric.P10, 99, sBm25SDRawP10, 0.3333f));
    h.put("wt10g-bm25-fd", new GroundTruth(Metric.P10, 99, sBm25FDRawP10, 0.3424f));

    for (String model : models) {
      LOG.info("Verifying results of model \"" + model + "\"");

      Map<String, Accumulator[]> r = results.get(model);
      g.get(model).verify(r, mapping, qrels);
      h.get(model).verify(r, mapping, qrels);

      LOG.info("Done!");
    }
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(Wt10g_Basic.class);
  }
}
