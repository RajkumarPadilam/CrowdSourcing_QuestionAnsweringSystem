import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.*;
import java.sql.*;

public class CollectingData {
	

	public static void main(String[] args) {
		//CollectingData object=new CollectingData();
		
		try
		{
		Class.forName("com.mysql.jdbc.Driver"); 
	     Connection con=DriverManager.getConnection("jdbc:mysql://129.107.118.82:3306/freebase",
            "nandish", "nandish");
	    Statement stmt=con.createStatement();
	    
	    	  
	    ArrayList<HashMap<String,String>> tuplesSource=new ArrayList<HashMap<String,String>>();
	    HashMap<String,String> tuplesDestination=new HashMap<String,String>();
	    
	    ResultSet result=stmt.executeQuery("SELECT distinct freebase.freebase_quad_basketball.destination,"
	    		+ "nandish_freebase_property_partition_full.freebase_lang_en.value FROM  "
	    		+ "nandish_freebase_property_partition_full.freebase_lang_en,"
	    		+ "nandish_freebase_property_partition_full.freebase_source_object_mapping,"
	    		+ "freebase.freebase_quad_basketball "
	    		+ "where freebase.freebase_quad_basketball.destination=nandish_freebase_property_partition_full.freebase_source_object_mapping.source"
	    		+ " and nandish_freebase_property_partition_full.freebase_source_object_mapping.id=nandish_freebase_property_partition_full.freebase_lang_en.source ");

	    
 while (result.next()) {
	
        	
        	String id=result.getString(1);
        	String value=result.getString(2);
        	
        	tuplesDestination.put(id,value);
       
        }
 
 
 ResultSet result1=stmt.executeQuery("SELECT distinct freebase.freebase_quad_basketball.source,"
 		+ "nandish_freebase_property_partition_full.freebase_lang_en.value FROM  "
 		+ "nandish_freebase_property_partition_full.freebase_lang_en,"
 		+ "nandish_freebase_property_partition_full.freebase_source_object_mapping,"
 		+ "freebase.freebase_quad_basketball "
 		+ "where freebase.freebase_quad_basketball.source=nandish_freebase_property_partition_full.freebase_source_object_mapping.source"
 		+ " and nandish_freebase_property_partition_full.freebase_source_object_mapping.id=nandish_freebase_property_partition_full.freebase_lang_en.source ");

    
while (result1.next()) {

 HashMap<String,String> tupleSource=new HashMap<String,String>();
 	
 	String id=result1.getString(1);
 	String value=result1.getString(2);
 	tupleSource.put(id, value);
 	
 	tuplesSource.add(tupleSource);
 	
 	}


ArrayList<HashMap<String,String>> sourceDestination=new ArrayList<HashMap<String,String>>();
ArrayList<HashMap<String,String>> sourceValue=new ArrayList<HashMap<String,String>>();


ResultSet result3 = stmt.executeQuery("Select * from  freebase_quad_basketball");

while (result3.next()) {

String source  =result3.getString(1);
String property = result3.getString(2);
String destination=result3.getString(3);
String tuple=source+" "+property+" "+destination;

HashMap<String,String>sd=new HashMap<String,String>();
HashMap<String,String>sv=new HashMap<String,String>();
sv.put(source, property);
sd.put(source, destination);
sourceDestination.add(sd);
sourceValue.add(sv);

}

con = DriverManager.getConnection("jdbc:mysql://localhost:3306/freebase_basketball","root","prathu");
stmt=con.createStatement();


//ArrayList<HashMap<Integer,String>> listTriples=new ArrayList<HashMap<Integer,String>>();
File file = new File("D://Project/Dataset.doc");

// if file doesnt exists, then create it
if (!file.exists()) {
	file.createNewFile();
}

FileWriter fw = new FileWriter(file.getAbsoluteFile());
BufferedWriter bw = new BufferedWriter(fw);

int increment=1;
for(int i=0;i<tuplesSource.size();i++)
{
HashMap<String,String>tupleSource=tuplesSource.get(i);
Set set=tupleSource.keySet();
Iterator itr=set.iterator();
String sourceKey=itr.next().toString();
for(int j=0;j<sourceDestination.size();j++)
{
	HashMap<String,String> sd=sourceDestination.get(j);
	Set set1=sd.keySet();
	Iterator itr1=set1.iterator();
	String s=itr1.next().toString();
	if(sourceKey.equalsIgnoreCase(s))
	{
		String destinationKey=sd.get(s);
		if(tuplesDestination.containsKey(destinationKey))
		{
		 HashMap<Integer,String> triples=new HashMap<Integer,String>();
			
			String source=tupleSource.get(sourceKey);
			String destination=tuplesDestination.get(destinationKey);
			String property=sourceValue.get(j).get(s);
			String[] p=property.split("/");
			String propertyValue=null;
			if(p[3].equalsIgnoreCase("position_s") )
			{
			String[] propValue=p[3].split("_");
			propertyValue=propValue[0];
			}
			else if( p[3].equalsIgnoreCase("head_coach"))
			{
				String[] propValue=p[3].split("_");
				propertyValue=propValue[1];
			}
			else if(p[3].equalsIgnoreCase("teams"))
			{
				propertyValue="team";
			}
			else if(p[3].equalsIgnoreCase("divisions"))
			{
				propertyValue="division";
			}
			else if(p[3].equalsIgnoreCase("players"))
			{
				propertyValue="player";
			}
			else
			{
				propertyValue=p[3];
			}
			
			

			String tuple=source+" "+propertyValue+" "+destination;
			String sql="Insert into basketballdataset values(?,?,?)";
			PreparedStatement pstmt=con.prepareStatement(sql);
			pstmt.setString(1, source);
			pstmt.setString(2, propertyValue);
			pstmt.setString(3, destination);
			pstmt.executeUpdate();
			bw.write(tuple);
			bw.newLine();
			
			
			
			
		}
	}
	
}

}

bw.close();

     }
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}

}
