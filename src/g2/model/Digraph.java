package g2.model;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class Digraph {
  
  public static boolean DigraphToFile(String file, List<Course> courses) {
    String fileName = file + ".dot";
    File f = new File(fileName);
    try {
      if (!f.createNewFile())
        return false;
      
      FileWriter w = new FileWriter(fileName);
      w.write("digraph {\n");
      for (Course to : courses) {
        for (Course from : to.prereqs())
          w.write("\"" + to.name + "\"->\"" + from.name + "\"\n");
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