package g2.model;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Date;

public class Digraph {
  
  public static boolean DigraphToFile(String file, List<Hierarchical> nodes) {
	  Date d = new Date();
	  
	String fileName = file + "-" + d.toString().replace(" ", "-") + ".dot";
    File f = new File(fileName);
    try {
      if (!f.createNewFile())
        return false;
      
      FileWriter w = new FileWriter(fileName);
      w.write("digraph {\n");
      for (Hierarchical to : nodes) {
        w.write("\"" + to.toString() + "\"\n");
        for (Hierarchical from : to.prereqs())
          w.write("\"" + from.toString() + "\"->\"" + to.toString() + "\"\n");
      }
      w.write("}\n");
      w.close();
    }
    catch (Exception e) {
      System.err.println(e.toString());
    }
    return true;
  }
  
}