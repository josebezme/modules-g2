package g2.model;

import java.util.*;

public class DigraphTester {
  
  public static void main(String[] args) {
    Course c1 = new Course("Math 101", "Intro to Math", "");
    Course c2 = new Course("Math 200", "Linear Algebra", "");
    Course c3 = new Course("Math 201", "Multivariable Calculus", "");
    
    c2.setPrereq(c1);
    c3.setPrereq(c1);
    c3.setPrereq(c2);
    
    ArrayList<Course> courses = new ArrayList<Course>();
    courses.add(c1);
    courses.add(c2);
    courses.add(c3);
    
    Digraph.DigraphToFile("digr", courses);
  }
}