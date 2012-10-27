package g2.model;

import java.util.List;

public class Module {
  
  private String name;
  private List<Module> prereqs;
  private List<Module> postreqs;
  
  public Module() {
  }
  
}