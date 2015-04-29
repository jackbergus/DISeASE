/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package disease.web;


import com.sun.net.httpserver.Headers;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import disease.Dataset.Real.ICD9CMTable;
import disease.Phase.GUIResult;
import disease.Phase.Orchestrator;
import disease.datatypes.ConcreteMapIterator;
import disease.ontologies.ICD9CMCode;
import disease.utils.datatypes.Pair;
import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Giacomo Bergami <giacomo@openmailbox.org>
 */
public class Server {
    
  public static void main(String[] args) throws Exception {
    Orchestrator.load_the_model(false,false);
    HttpServer server = HttpServer.create(new InetSocketAddress(8711), 0);
    server.createContext("/get", new MyHandler());
    server.createContext("/", new Main());
    server.setExecutor(null); // creates a default executor
    server.start();
  }

  static class MyHandler implements HttpHandler {
      
    private ICD9CMTable it = ICD9CMTable.init();
      
    private Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
      
    @Override
    public void handle(HttpExchange t) throws IOException {
      Headers h = t.getResponseHeaders();
      
      Map<String, String> params = queryToMap(t.getRequestURI().getQuery()); 
      JSONObject reply = new JSONObject();
      if (params.containsKey("request")) {
          GUIResult gr = Orchestrator.run_the_algorithm(params.get("request"));
          int count = 0;
          for (Pair<Double, Set<String>> p : new ConcreteMapIterator<>(gr.getRankedResults())) {
              for (String code : p.getSecond()) {
                  try {
                      JSONObject arg = new JSONObject();
                      arg.append("code", (String)code);
                      
                      arg.append("score",(String)p.getFirst().toString());
                      arg.append("descr", (String)it.getCodeDescription(new ICD9CMCode(code)));
                      reply.put(""+count, arg);
                      count++;
                  } catch (JSONException ex) {
                      Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                  }
              }
          }
      }
      String response = reply.toString();
      System.out.println(response);
      t.sendResponseHeaders(200, 0);
      h.add("Content-Type", "application/json");
      OutputStream os = t.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
  }

    private static class Main implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            File file = new File("Index.html").getCanonicalFile();
            t.sendResponseHeaders(200, 0);
            OutputStream os = t.getResponseBody();
            FileInputStream fs = new FileInputStream(file);
            final byte[] buffer = new byte[0x10000];
            int count = 0;
            while ((count = fs.read(buffer)) >= 0) {
                os.write(buffer,0,count);
            }
            fs.close();
            os.close();
        }
    }
}
