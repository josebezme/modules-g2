package g2.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import g2.api.GoogleDictionary;
import g2.api.GoogleDictionary.SpeechPart;
import g2.model.Course;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class TermFilter {
	private static final Logger logger = Logger.getLogger(TermFilter.class);
	
	private static final Predicate<String> FILTER_SENTENCES = new Predicate<String>() {
		
		@Override
		public boolean apply(String sentence) {
			if(	sentence.contains("prereq") || 
				sentence.contains("semester") ||
				sentence.contains("credit") ||
				sentence.contains("requirements")) {
				return false;
			}
			
			Pattern p = Pattern.compile("[a-z ]");
			
			Matcher m = p.matcher(sentence);
			int letterCount = 0;
			while(m.find()) {
				letterCount++;
			}
			
			if(letterCount < sentence.length() * 0.75) {
				return false;
			}
			
			if(sentence.length() < 4) {
				return false;
			}
			
			return true;
		}
		
	};
	
	private static final Predicate<String> FILTER_TERMS = new Predicate<String>() {
		
		@Override
		public boolean apply(String term) {
			if(term.trim().length() < 3) {
				return false;
			}
			
			return true;
		}
	};
	
	private static final String AND_OF_TEST_SET[] = {
		"principles of logic and proof"
		,"models of genetics and evolution"
		,"pricing of european and american options"
		,"one-dimensional maps of the interval and the circle"
		,"approximate solutions of ordinary and partial differential equations"
		,"continuity of solutions of single equations and systems of equations"
		,"systems of first-degree equations in two variables solved by graphical and algebraic means"
	};
	
	private static final String AND_TEST_SET[] = {
		"existence and uniqueness for cauchy and dirichlet problems"
		,"band-limited and time-limited signals"
		,"filtering and its connection"
		,"discrete and fast fourier transforms"
		,"uniform continuity and uniform convergence"
		,"transformations and their derivatives"
		,"algebra and calculus of vectors"
		,"green's and stokes' theorems"
		,"existence and uniqueness for cauchy and dirichlet problems"
		,"euclid to dedekind focusing on the development of the real number system and its relation to the euclidean line"
		,"graphical and descriptive methods"
		,"hypotheses and statistical inference"
		,"additional techniques and applications of integration"
		,"uniqueness and stability of solutions"
		,"logarithmic and exponential functions"
		,"partial differential equations and boundary value problems"
		,"first order ordinary differential equations and initial value problems"
		,"eigenvectors and eigenvalues"
		,"notable mathematicians and the importance of their discoveries"
		,"testing and application to designs of experiments"
		,"math 3399 and math 4399 must be satisfied in order for any to apply to a degree"
		,"models of genetics and evolution"
		,"pricing of european and american options"
		,"graphical and descriptive methods in statistics"
		,"random variables and distributions"
		,"exploratory and diagnostic methods"
		,"one-dimensional maps of the interval and the circle"
		,"math 3399 and math 4399 must be satisfied in order for any to apply to a degree"
		,"ideas and activities that reinforce interrelationships among topics in mathematics"
		,"principles of logic and proof"
		,"formal and informal geometry"
		,"special devices and approximation methods"
		,"similarity eigenvalues and eigenvectors"
		,"math 1451 will include topics normally covered in math 1432 and 2433"
		,"curve sketching and graphical analysis"
		,"differentiation and integration of elementary functions"
		,"applications in business and the natural and social sciences"
		,"synthetic and algebraic geometry"
		,"hermitian and positive definite matrices"
		,"vector spaces and linear transformations"
		,"eigenvalues and eigenvectors"
		,"statistics for biological and biomedical data"
		,"numerical integration and differentiation"
		,"approximate solutions of ordinary and partial differential equations"
		,"math 3396 and math 4396 must be satisfied in order for any to apply to a degree"
		,"numerical integration and differentiation"
		,"approximate solutions of ordinary and partial differential equations"
		,"conjecture and proof"
		,"math 1451 will include topics normally covered in math 1432 and 2433"
		,"review of ruler and compass construction"
		,"analytic and transformational geometry"
		,"differences and rates of change"
		,"discrete and continuous versions of poisson and markov processes"
		,"branching and renewal processes"
		,"introduction to stochastic calculus and diffusion"
		,"continuity of solutions of single equations and systems of equations"
		,"topics in number theory and abstract algebra"
		,"testing and application to designs of experiments"
		,"first-degree equations and inequalities in one variable"
		,"systems of first-degree equations in two variables solved by graphical and algebraic means"
		,"exponents and radicals"
		,"math 3396 and math 4396 must be satisfied in order for any to apply to a degree"
		,"analysis on written and oral communication"
		,"the gauss-bonnet theorem and the euler-poincare index theorem"
		,"random variables and distributions"
		,"variances and covariances"
		,"basic discrete and continuous distributions"
		,"classical and abstract algebra"
		,"bonds and bank certificates of deposit"
		,"bond and annuity valuation"
		,"the gauss-bonnet theorem and the euler-poincare index theorem"
		,"topics in probability and statistics"
	};
	
	private static final String AND_TEST_MISSING[] = {
		"green's and stokes' theorems"
		,"uniqueness and stability of solutions"
		,"math 3399 and math 4399 must be satisfied in order for any to apply to a degree"
		,"math 1451 will include topics normally covered in math 1432 and 2433"
		,"math 3396 and math 4396 must be satisfied in order for any to apply to a degree"
		,"math 1451 will include topics normally covered in math 1432 and 2433"
		,"math 3396 and math 4396 must be satisfied in order for any to apply to a degree"
	};
	
	public static void main(String[] args) {
		String urls[] = {"http://www.uh.edu/academics/catalog/colleges/nsm/courses/math/"};
		Multimap<String, Course> hosts = C1CourseExtractor.extractCourses(urls);
		filterTerms(hosts);
		
//		for(String term : AND_TEST_SET) {
//			List<String> terms = processAnd(term);
//			System.out.println(term + " -> " + terms);
//		}
	}
	
	public static void filterTerms(Multimap<String, Course> hosts2courses) {
		for(String host : hosts2courses.keySet()) {
			List<Course> courses = new LinkedList<Course>(hosts2courses.get(host));
			for(Course c : courses) {
				logger.info("Getting terms for course: " + c);
				String desc = c.htmlElement.text().toLowerCase();
				logger.debug(desc);
				
				String sentences[] = desc.split("(\\.\\s|\\.$)");
				
				logger.debug("Sentences:");
				for(String sentence : sentences) {
					logger.debug("\t" + sentence);
				}
				
				Iterable<String> it = Iterables.filter(Arrays.asList(sentences), FILTER_SENTENCES);
				sentences = Iterables.toArray(it, String.class);
				
				List<String> termList = new LinkedList<String>();
				for(String sentence : sentences) {
					String terms[] = sentence.split("(,|;|:)");
					
					for(String term : terms) {
						term = term.trim();
						
						// If we left a leading and
						// from spliting remove it and carry on.
						if(term.indexOf("and") == 0) {
							term = term.substring(3);
							
							// maybe a trailing and?  maybe....
						} else if(term.indexOf("and") == term.length() - 3) {
							term = term.substring(0, term.length() - 3);
						}
						
						// Is there still an and in there?
						if(term.contains("and")) {
							termList.addAll(processAnd(term));
							continue;
						}
						
						if(term.contains("with")) {
							String firstTerm = term.substring(0, term.indexOf("with"));
							term = term.substring(term.indexOf("with") + "with".length(), term.length());
							
							termList.add(firstTerm.trim());
							termList.add(term.trim());
						} else if (term.contains("from")) {
							term = term.substring(term.indexOf("from") + "from".length(), term.length());
							termList.add(term.trim());
						} else {
							termList.add(term.trim());
						}
					}
				}
				
				it = Iterables.filter(termList, FILTER_TERMS);
				String terms[] = Iterables.toArray(it, String.class);
				termList.clear();
				termList.addAll(Arrays.asList(terms));
				
				c.getTerms().addAll(termList);
			}
		}
	}
	
	private static void splitAndAddByFirstAnd(String term, List<String> termList, String ofForPrefix) {
		String terms[] = term.split("\\sand\\s");
		
		int index = term.indexOf(" and ");
		termList.add(ofForPrefix + term.substring(index + 5));
		termList.add(ofForPrefix + term.substring(0, index));
	}
	
	public static List<String> processAnd(String term) {
		List<String> termList = new ArrayList<String>(2);
		
		try {
			String bySpace[] = term.split("\\s");
			String ofForPrefix = "";
			for(int i = 0; i < bySpace.length; i++) {
				if(bySpace[i].equals("of")) {
					ofForPrefix = ofForPrefix + term.substring(0, term.indexOf(" of ") + 4);
					bySpace = Arrays.copyOfRange(bySpace, i + 1, bySpace.length);
					i = 0;
					term = term.substring(term.indexOf(" of ") + 4);
					
				} else if(bySpace[i].equals("for")) {
					ofForPrefix = ofForPrefix + term.substring(0, term.indexOf(" for ")).trim();
					bySpace = Arrays.copyOfRange(bySpace, i, bySpace.length);
					i = 0;
					term = term.substring(term.indexOf(" for ") + 4);
					
				} else if(bySpace[i].equalsIgnoreCase("and")) {
					//Found and so check before and after word.
					
					String before = bySpace[i - 1];
					String after = bySpace[i + 1];
					
					int hyphen;
					if((hyphen = before.indexOf('-')) != -1) {
						before = before.substring(hyphen + 1);
					}
					
					if((hyphen = after.indexOf('-')) != -1) {
						after = after.substring(hyphen + 1);
					}
					
					if(after.equalsIgnoreCase("its")) {
						termList.add(term.substring(0, term.indexOf(" and ")).trim());
						
					} else if(
						after.equals("the") || // topics are proper nouns 
						(i + 2 >= bySpace.length && i - 1 <= 0) // not enough words
						) {
						splitAndAddByFirstAnd(term, termList, ofForPrefix);
						
					} else {
						SpeechPart part = GoogleDictionary.getPartofSpeech(before);
						if(part == SpeechPart.ADJECTIVE) {
							for(int j = i + 2; j < bySpace.length; j++) {
								part = GoogleDictionary.getPartofSpeech(bySpace[j]);
								if(part == SpeechPart.NOUN || j == bySpace.length - 1) {
									termList.add(ofForPrefix + term.substring(0, term.indexOf(" and ")) + " " + bySpace[j]);
									termList.add(ofForPrefix + term.substring(term.indexOf(" and ") + 5));
								}
							}
						} else if(before.contains("'") && after.contains("'")) {
							termList.add(ofForPrefix + before + term.substring(term.indexOf(after) + after.length(), term.length()));
							termList.add(ofForPrefix + term.substring(term.indexOf(" and ") + 5));
						} else {
							splitAndAddByFirstAnd(term, termList, ofForPrefix);
						}
					}
					
					break;
				}
			}
			
		} catch (Exception e) {
			// gotta catch'em all!
			logger.error("exception", e);
		}
		
		
		return termList;
	}
}
