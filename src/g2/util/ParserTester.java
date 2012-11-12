package g2.util;

import de.tudarmstadt.ukp.wikipedia.api.*;
import de.tudarmstadt.ukp.wikipedia.parser.*;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.*;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ParserTester {
  
  public static void main (String[] args) throws Exception {
// get a ParsedPage object
    MediaWikiParserFactory pf = new MediaWikiParserFactory();
    MediaWikiParser parser = pf.createParser();
    
    String url = "http://en.wikipedia.org/w/api.php?format=xml&action=query&titles=Stack_(abstract_data_type)&prop=revisions&rvprop=content";
    //String host = Utils.getHost(url);
    
	Document doc = Jsoup.connect(url).get();
	String b = doc.toString();
	System.out.println(b);

   // ParsedPage pp = parser.parse(b);
    
   // System.out.println(pp.getText());
    
    // Elements elements = doc.getElementsByTagName();
//get the internal links of each section
    /*for (Section section : pp.getSections()){
      System.out.println("Section: " + section.getTitle());
      
      for (Link link : section.getLinks(Link.type.INTERNAL)) {
        System.out.println("  " + link.getTarget());
      }
    }*/
  }
  
}