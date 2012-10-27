package g2.model;

import java.util.List;
import java.util.ArrayList;

public class Course {
  
  public String id;
  public String name;
  
  private String rawHtml;
  private List<String> terms;
  private List<Course> prereqs;
  
  public Course(String id, String name, String rawHtml) {
    this.id = id;
    this.name = name;
    this.rawHtml = rawHtml;
    prereqs = new ArrayList<Course>();
    terms = new ArrayList<String>();
  }
  
  /* Fill in prereqs and terms. 
   * Pass a list of courses seen at this college to help identify prereqs. */
  public void processHtml(List<Course> seenCourses) {
    for (Course c : seenCourses) {
      if (rawHtml.contains(c.id) || rawHtml.contains(c.name))
        setPrereq(c);
    }
  }
  
  public void setPrereq(Course c) {
    prereqs.add(c);
  }
  
  public List<Course> prereqs() {
    return prereqs;
  }
  
}
