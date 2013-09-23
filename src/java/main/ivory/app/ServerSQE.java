package ivory.app;

import ivory.sqe.querygenerator.ProbabilisticStructuredQueryGenerator;
import ivory.sqe.querygenerator.QueryGenerator;
import ivory.sqe.retrieval.RunQueryEngine;
import ivory.sqe.querygenerator.Utils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHolder;
import javax.servlet.http.HttpServlet;
import java.io.PrintWriter;

public class ServerSQE
{
  QueryGenerator generator;
  static FileSystem fs;
  static Configuration conf;
  
  public static void main(String[] args) throws Exception
  {
    try{
      Server server = new Server(Integer.parseInt(args[0]));
      String[] ivoryArgs = new String[2];
      ivoryArgs[0] = "--xml";
      ivoryArgs[1] = args[1];

      conf = RunQueryEngine.parseArgs(ivoryArgs);
      if (conf == null) {
        System.exit(-1);
      }

      fs = FileSystem.getLocal(conf);
      QueryGenerator generator = new ProbabilisticStructuredQueryGenerator();
      generator.init(fs, conf);
      
      org.mortbay.jetty.servlet.Context root = new org.mortbay.jetty.servlet.Context(server, "/",
          org.mortbay.jetty.servlet.Context.SESSIONS);
      root.addServlet(new ServletHolder(new TokenBasedTranslateServlet(generator, fs, conf)), "/translate");
      try {
        server.start();
      } catch (Exception e) {
        e.printStackTrace();
      }

      while (true);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public static class TokenBasedTranslateServlet extends HttpServlet {
    QueryGenerator generator;
    FileSystem fs;
    Configuration conf;
    
    static final long serialVersionUID = 8253865405L;

    public TokenBasedTranslateServlet(QueryGenerator generator, FileSystem fs, Configuration conf) {
      this.generator = generator;
      this.fs = fs;
      this.conf = conf;
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
    IOException {
      doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,
    IOException {
      res.setCharacterEncoding("UTF-8");
      res.setContentType("text/html");
      PrintWriter out = res.getWriter();
      req.setCharacterEncoding("UTF-8");
      if (req.getParameterValues("query") != null) {
	
	String queryText = generator.parseQuery(req.getParameterValues("query")[0], fs, conf).getQuery().toString();
	queryText = Utils.ivory2Indri(queryText);
        out.println(queryText);
      }
    }
  }
}
