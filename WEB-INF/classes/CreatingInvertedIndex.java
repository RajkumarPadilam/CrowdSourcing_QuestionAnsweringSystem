import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.io.*;


public class CreatingInvertedIndex implements Serializable {
	
	HashMap<String,ArrayList<Integer>> mapping=new HashMap<String,ArrayList<Integer>>();
	
	HashMap<Integer,String> triples=new HashMap<Integer,String>();
	ArrayList<HashMap>returnObjects=new ArrayList<HashMap>();
	
	
	public ArrayList returnIndex()
	{
		String[] values;
		
		HashSet<String> stopWords=new HashSet<String>();
		
		  try
	 		{
	 		
	 		System.out.println("stopwords");
	 		FileReader input = new FileReader("D://Project/stopwords.txt");
	 		BufferedReader bufRead = new BufferedReader(input);
	 		String myLine = null;
	     	//int lineno = 0;
	     	
	     	
	     	while ( (myLine = bufRead.readLine()) != null){
	     		stopWords.add(myLine);
	     		
	     	}
	 		}
	     	
	     	
	     	catch(Exception e)
	     	{
	     		e.printStackTrace();
	     	}
		
		
		 try
	    	{
			 
			 String url = "jdbc:mysql://localhost:3306/";
		     String dbName ="demo";
		     String driver = "com.mysql.jdbc.Driver";
		     String userName = "root";
		     String password = "prathu";
		     Statement pstmt1;
		     ResultSet resultSet1;
		     
		     Class.forName(driver).newInstance();
		   Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/freebase_basketball","root","prathu");
		     
		  //  Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviebase","root","prathu");
		     
		    Statement st = conn.createStatement();
	   	 
	   	 ResultSet res = st.executeQuery("SELECT * FROM  basketballdataset");
		  // ResultSet res = st.executeQuery("SELECT * FROM testing");
		 //   ResultSet res = st.executeQuery("SELECT * FROM  movies");
	    
	     int rowNumber=1;
	     
	     File file = new File("D://Project/InvertedIndex.doc");

	  // if file doesnt exists, then create it
	  if (!file.exists()) {
	  	file.createNewFile();
	  }

	  FileWriter fw = new FileWriter(file.getAbsoluteFile());
	  BufferedWriter bw = new BufferedWriter(fw);

	    
	    while (res.next()) {
	    	 
	         int flagSource=0;
		     int flagProperty=0;
		     int flagObject=0;
	    	 
	       //  String source  = res.getString("source");
	        // String property=res.getString("property");
	    //     String object=res.getString("destination");
		     String source=res.getString(1);
		     String property=res.getString(2);
		     String object=res.getString(3);
	         String tuple=source+","+" "+property+","+" "+object;
	         System.out.println(tuple);
	        
	         	triples.put(rowNumber, tuple);
	          String[] spre=source.split(" ");
	         String[] ppre = property.split(" ");
	          String[] opre=object.split(" ");
	         
	         ArrayList<String> s=new ArrayList<String>();
	         ArrayList<String> p=new ArrayList<String>();
	         ArrayList<String> o=new ArrayList<String>();
	        
	         
	        for(int i=0;i<spre.length;i++)
	         {
	        	
	        	 
	        	 if(!stopWords.contains(spre[i].toLowerCase()))
	         	{
	        		 s.add(spre[i].toLowerCase());
	         	}
	        	 
	         	
	        }
	         for(int i=0;i<ppre.length;i++)
	        	 
	         {
	        	 if(!stopWords.contains(ppre[i].toLowerCase()))
		         	{
	        		p.add(ppre[i].toLowerCase());
		         	}
	        	 
	        		
	        	 
	         }
	         for(int i=0;i<opre.length;i++)
	         {
	        	 if(!stopWords.contains(opre[i].toLowerCase()))
		         	{
	        		 o.add(opre[i].toLowerCase());
		         	}
	        	
	        	
	         }
	         
	        
		    ArrayList<String> sourceTemp=new ArrayList<String>();
	         ArrayList<String> propertyTemp=new ArrayList<String>();
	         ArrayList<String> objectTemp=new ArrayList<String>();
	         
	         
	         
	        	 for(int i=0;i<s.size();i++)
	        	 {
	        		 if(mapping.containsKey(s.get(i)))
	        		 {
	        			// System.out.println(mapping.get(s[i]).size());
	        			 sourceTemp.add(s.get(i));
	        			 mapping.get(s.get(i)).add(rowNumber);
	        			// System.out.println(mapping.get(s[i]).size());
	        			// ArrayList<Integer> al=mapping.get(s[i]);
	        			// al.add(rowNumber);
	        			 
	        		 }
	        		 
	        		 
	        	 }
	        	 
	        	
	        	 for(int i=0;i<p.size();i++)
	        	 {
	        		 if(mapping.containsKey(p.get(i)))
	        		 {
	        			
	        			 propertyTemp.add(p.get(i));
	        			 mapping.get(p.get(i)).add(rowNumber);
	        			
	        			 
	        		 } 
	        	 }
	        	 
	      
	        	 
	        	 for(int i=0;i<o.size();i++)
	        	 {
	        		 if(mapping.containsKey(o.get(i)))
	        		 {
	        			
	        			 objectTemp.add(o.get(i));
	        			 mapping.get(o.get(i)).add(rowNumber);
	        		
	        			 
	        		 } 
	        	 }
	        	 
	        	 
	         for(int i=0;i<s.size();i++)
	         {
	        	 for(int j=0;j<sourceTemp.size();j++)
	        	 {
	        		if(s.get(i).equals(sourceTemp.get(j)))
	        		{
	        			flagSource=1;
	        			break;
	        		}
	        	 }
	        	 if(flagSource==1)
	        		 break;
	        	 ArrayList<Integer> sources=new ArrayList<Integer>();
	        	 sources.add(rowNumber);
	        	 
	        	 mapping.put(s.get(i),sources );
	         }
	         
	         
	         
	         for(int j=0;j<p.size();j++)
	         {
	        	 
	        	 for(int k=0;k<propertyTemp.size();k++)
	        	 {
	        		
	        		if(p.get(j).equals(propertyTemp.get(k)))
	        		{
	        			flagProperty=1;
	        			break;
	        		}
	        	 }
	        	 
	        	 if(flagProperty==1)
	        		 break;
	        	  ArrayList<Integer> propertys=new ArrayList<Integer>();
	        	  propertys.add(rowNumber);
	        	 
	        	 mapping.put(p.get(j), propertys);
	         }
	         
	         
	         
	         
	         for(int k=0;k<o.size();k++)
	         {
	        	 for(int i=0;i<objectTemp.size();i++)
	        	 {
	        		if(o.get(k).equals(objectTemp.get(i)))
	        		{
	        			flagObject=1;
	        			break;
	        		}
	        	 }
	        	 
	        	 if(flagObject==1)
	        		 break;
	        	  ArrayList<Integer> objects=new ArrayList<Integer>();
	        	  objects.add(rowNumber);
	 		     
	 		     
	        	 mapping.put(o.get(k), objects);
	         }
	         
	       
	         
	         
	         rowNumber++;
	     }
	     
	     returnObjects.add(mapping);
	     returnObjects.add(triples);
	     
	     Set list=mapping.entrySet();
	     Iterator itr=list.iterator();
	     while(itr.hasNext())
	     {
	    	 
	    	 bw.write(itr.next().toString());
	    	// System.out.println(itr.next());
	    	 bw.newLine();
	     }
	     
	     bw.close();
	   
	     Set list1=triples.entrySet();
	     Iterator itr1=list1.iterator();
	     while(itr1.hasNext())
	     {
	    	 System.out.println(itr1.next());
	     }
	     
	   
	   return returnObjects;
	    	}
	     catch(Exception e)
	     {
	    	 e.printStackTrace();
	    	 return null;
	     }
	    	}
	     
	         
	      public static void main(String[] args) {
	        	 CreatingInvertedIndex object=new CreatingInvertedIndex();
	        	 object.returnIndex();
	        	 FileOutputStream fos = null;
	        	    ObjectOutputStream out = null;
	        	    try {
	        	      fos = new FileOutputStream("D://Project/Serialize11.txt");
	        	      out = new ObjectOutputStream(fos);
	        	      out.writeObject(object);

	        	      out.close();
	        	    } catch (Exception ex) {
	        	      ex.printStackTrace();
	        	    }
		
		
		 }

}
