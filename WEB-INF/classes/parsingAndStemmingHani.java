/* http://macropol.blogspot.com/2012_04_01_archive.html
 * http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/ 
 * */


import java.util.*;
import java.io.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class parsingAndStemmingHani{

//	public static void main(String[] args) throws IOException{
//
//		// Parsing and stemming the questions
//		System.out.println("*** PARSING THE QUESTIONS ***");
//		try{
//			File fXmlFile = new File("mytestquestions.xml.txt");
//			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//			Document doc = dBuilder.parse(fXmlFile);
//		 
//			//optional, but recommended
//			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
//			doc.getDocumentElement().normalize();
//		
//			NodeList nList = doc.getElementsByTagName("target");
//
//			for (int temp = 0; temp < nList.getLength(); temp++) {
//				Node nNode = nList.item(temp);
//				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//					Element eElement = (Element) nNode;
//					NodeList qList = eElement.getElementsByTagName("q");
//					for (int q = 0; q < qList.getLength(); q++){
//						Node qNode = qList.item(q);
//						if (qNode.getNodeType() == Node.ELEMENT_NODE) {
//							Element qElement = (Element) qNode;
//							String qid = qElement.getAttributes().getNamedItem("id").getNodeValue().trim();
//							String qtext = qElement.getTextContent();
//							System.out.println("Question id = " + qid);
//							System.out.println("Question text = " + qtext);
//							parsingAndStemmingHani.parseAndStem(qtext);
//						}
//					}
//				}
//			}
//		}
//		catch (Exception e){
//			e.printStackTrace();
//		}
//		
//
//		// Parsing and Stemming the answers
//		System.out.println("*** PARSING THE ANSWERS ***");
//		FileReader input = new FileReader("mytestanswers.txt");
//		BufferedReader bufRead = new BufferedReader(input);
//		String myLine = null;
//    	int lineno = 0;
//    	
//		while ( (myLine = bufRead.readLine()) != null){
//			StringBuilder builder = new StringBuilder();
//			String[] array = myLine.split(" ");
//			System.out.println("Line " + ++lineno + " contains answer for question " + array[0]);
//			for (int i = 3; i < array.length; i++)
//	    	    builder.append(array[i]+" ");
//				parsingAndStemmingHani.parseAndStem(builder.toString());
//		}
//
//	}
// 
	public static void parseAndStem(String line){
	    for(String word : parsingAndStemmingHani.words(line)){
	    	System.out.println(word);
			//do your stuff
	    }
	}
	
	static HashSet stopwords = new HashSet();
 
	public static void addStopwords(){
		try{
			BufferedReader br = new BufferedReader(new FileReader(MainPart.Path+"/Mystopwords.txt"));
			while(br.ready()){
				stopwords.add(br.readLine());
			}
		}
		catch(Exception e){System.out.println(e);}
	}
 
	public static ArrayList<String> words(String line){
		if(stopwords.size() == 0)
			addStopwords();
		ArrayList result = new ArrayList();
 
		String[] words = line.split("[ \t\n,\\.\"!?$~()\\[\\]\\{\\}:;/\\\\<>+=%*]");
		for(int i=0; i < words.length; i++){
			if(words[i] != null && !words[i].equals("")){
				String word = words[i].toLowerCase();
				if(!stopwords.contains(word)){
					result.add(StemmerHani.stem(word));
				}
			}
		}
 
		return result;
	}
 
}