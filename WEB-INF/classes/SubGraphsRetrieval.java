
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.io.*;


public class SubGraphsRetrieval  {
	String[] answers;
	static boolean change=false;
	
	static ArrayList<Integer> top=new ArrayList<Integer>();
	
	public String[] retrievingSubGraphs(String query)
	{
		try
    	{
			
		CreatingInvertedIndex obj=null;
	
		
		String[] keywords=query.split(" ");
		FileInputStream fis = null;
		ObjectInputStream in = null;
		
		 try {
		     fis = new FileInputStream("D://Project/Serialize11.txt");
		     
		      in = new ObjectInputStream(fis);
		      obj = (CreatingInvertedIndex) in.readObject();
		     
		      
		      in.close();
		    } catch (Exception ex) {
		    	System.out.println("null");
		      ex.printStackTrace();
		    }
		
	   
				ArrayList<ArrayList> fullMappings=new ArrayList<ArrayList>();
				Set<String> keys= obj.mapping.keySet();
				
				
				
				for(int i=0;i<keywords.length;i++)
				{
					Iterator<String> itr=keys.iterator();
					ArrayList<ArrayList<Integer>> indexes=new ArrayList<ArrayList<Integer>>();
					ArrayList<String> mappingList=new ArrayList<String>();
					while(itr.hasNext())
					{
						String key=itr.next();
						if(key.contains(keywords[i]))
						{
							indexes.add(obj.mapping.get(key));
						}

					}
					
					for(int j=0;j<indexes.size();j++)
					{
						ArrayList<Integer> index=indexes.get(j);
						for(int k=0;k<index.size();k++)
						{
						String str=(obj.triples.get(index.get(k)).toString());
						mappingList.add(str);
						}
						
					}
					
					
					
					fullMappings.add(mappingList);
					indexes=null;
					mappingList=null;
					
					
					
			}
				
				
				 File file5 = new File("D://Project/Lists.doc");
				 
					
					if (!file5.exists()) {
						file5.createNewFile();
					}
		 
					FileWriter fw5 = new FileWriter(file5.getAbsoluteFile());
					BufferedWriter bw5 = new BufferedWriter(fw5);
				
				
				for(int i=0;i<fullMappings.size();i++)
				{
					ArrayList<String> mappings=fullMappings.get(i);
					bw5.newLine();
					bw5.write("List "+"< " +keywords[i]+">");
					bw5.newLine();
					
					
					
					
					for(int j=0;j<mappings.size();j++)
					{
					    String s=mappings.get(j);
					 
					    bw5.write(s);
					    bw5.newLine();
					    
					}
					mappings=null;
					
				}
				bw5.close();
				fw5.close();
				
				
				
				ArrayList<HashMap<ArrayList,String>>Edges=new ArrayList<HashMap<ArrayList,String>>();
				
				for(int i=0;i<fullMappings.size();i++)
				{
					if(i!=fullMappings.size()-1)
					{
					for(int j=i+1;j<fullMappings.size();j++)
					{
						ArrayList<String> mappings=fullMappings.get(i);
						ArrayList<String> toCompareMappings=fullMappings.get(j);
						
						for(int k=0;k<mappings.size();k++)
						{
							boolean flag=false;
							
							for(int l=0;l<toCompareMappings.size();l++)
							{
								HashMap<ArrayList,String>edge=new HashMap<ArrayList,String>();
							    ArrayList<Integer>listKeys=new ArrayList<Integer>();
								if(mappings.get(k).hashCode()==toCompareMappings.get(l).hashCode())
								{
									listKeys.add(i);
									listKeys.add(j);
									edge.put(listKeys, mappings.get(k));
									Edges.add(edge);
									toCompareMappings.remove(l);
									flag=true;
									break;
								}
								
								
								edge=null;
								listKeys=null;
							}
							
							if(flag==false)
							{
								HashMap<ArrayList,String>edge=new HashMap<ArrayList,String>();
							    ArrayList<Integer>listKeys=new ArrayList<Integer>();
							    listKeys.add(i);
								edge.put(listKeys, mappings.get(k));
								Edges.add(edge);
								edge=null;
								listKeys=null;
							}
							
							
							
						}
						
						fullMappings.add(j, toCompareMappings);
						fullMappings.remove(j+1);
						mappings=null;
						toCompareMappings=null;
						
					}
					}
					
					else
					{
						ArrayList<String> mappings=fullMappings.get(i);
						for(int k=0;k<mappings.size();k++)
						{
							 ArrayList<Integer>listKeys=new ArrayList<Integer>();
							 listKeys.add(i);
							HashMap<ArrayList,String> edge=new HashMap<ArrayList,String>();
							edge.put(listKeys,mappings.get(k));
							Edges.add(edge);
							listKeys=null;
							edge=null;
						}
					}
					
				}
				
				fullMappings=null;
				
			
				
				for(int i=0;i<Edges.size()-1;i++)
				{
					HashMap<ArrayList,String> edge=Edges.get(i);
					Set list=edge.keySet();
					
				     Iterator itr=list.iterator();
				     ArrayList<Integer> al=(ArrayList)itr.next();
				     
				    	 String comparing=edge.get(al);
				    	 
				    	 for(int j=i+1;j<Edges.size();j++)
				    	 {
				    		 HashMap<ArrayList,String> toCompareedge=Edges.get(j);
								Set list2=toCompareedge.keySet();
							     Iterator itr2=list2.iterator();
							     ArrayList<Integer> al2=(ArrayList)itr2.next();
							     
							     
							    	 String toComparing=toCompareedge.get(al2);
							    	 
							    	 if(comparing.equals(toComparing))
							    	 {
							    		 if(al.size()>=al2.size())
							    		 Edges.remove(j);
							    		 else
							    			 Edges.remove(i);
							    	 }
							    	 
							    	 al2=null;
							    	 
							    	 toCompareedge=null;
							    	 list2=null;
							    	 itr2=null;
				    	 }
				    	 al=null;
				    	 edge=null;
				    	 list=null;
				    	 itr=null;
				     
				}
				
				
				
				
				for(int i=0;i<Edges.size();i++)
				{
				    HashMap<ArrayList,String> edges=new HashMap<ArrayList,String>();
					HashMap<ArrayList, String> edge=Edges.get(i);
					Set list=edge.keySet();
					Iterator itr=list.iterator();
				 ArrayList<Integer> al=(ArrayList)itr.next();
				 String value=edge.get(al);
				 al.add(0, i);
				 edges.put(al, value);
				 Edges.add(i, edges);
				 Edges.remove(i+1);
				 edges=null;
				 edge=null;
				 
					
				}
				
				
		      ArrayList<ArrayList> newFullMappings=new ArrayList<ArrayList>();
				for(int i=0;i<keywords.length;i++)
				{
					ArrayList<HashMap> newMappings=new ArrayList<HashMap>();
					newFullMappings.add(newMappings);
					newMappings=null;
				}
				
				
				for(int i=0;i<Edges.size();i++)
				{
					HashMap<ArrayList,String> edge=Edges.get(i);
					Set entryList=edge.entrySet();
					Iterator itr1=entryList.iterator();
					Set keyList=edge.keySet();
					Iterator itr=keyList.iterator();
					ArrayList<Integer> al=(ArrayList)itr.next();
					for(int j=1;j<al.size();j++)
					{
						int listLoc=al.get(j);
						for(int k=0;k<keywords.length;k++)
						{
							if(listLoc==k)
							{
								newFullMappings.get(k).add(edge);
								break;
							}
						}
						
					}
					 
					
					
				}
				
				ArrayList<ArrayList>latestFullMappings=new ArrayList<ArrayList>();
								
				
				for(int i=0;i<newFullMappings.size();i++)
				{
					if(i!=newFullMappings.size()-1)
					{
					ArrayList<HashMap> comparingMappings=newFullMappings.get(i);
					
					ArrayList<ArrayList>latestListMappings=new ArrayList<ArrayList>();
					
					
					for(int k=0;k<comparingMappings.size();k++)
					{
						ArrayList<HashMap<ArrayList,String>>latestEdgeMappings=new ArrayList<HashMap<ArrayList,String>>();
						HashMap<ArrayList,String> comparingEdge=comparingMappings.get(k);
						Set comparingList=comparingEdge.keySet();
						Iterator comparingItr=comparingList.iterator();
					 ArrayList<Integer> comparingKeySet=(ArrayList)comparingItr.next();
					 String[] comparingEdgeTokens=comparingEdge.get(comparingKeySet).split(",");
					 
					 
					latestEdgeMappings.add(comparingEdge);
						
						
					
					for(int j=i+1;j<newFullMappings.size();j++)
					{
					

						ArrayList<HashMap> toCompareMappings=newFullMappings.get(j);
						
						
					       for(int l=0;l<toCompareMappings.size();l++)
							{
								HashMap<ArrayList,String> toCompareEdge=toCompareMappings.get(l);
								Set toCompareList=toCompareEdge.keySet();
								Iterator toCompareItr=toCompareList.iterator();
							    ArrayList<Integer> toCompareKeySet=(ArrayList)toCompareItr.next();
							    
							    if(comparingKeySet.get(0)<toCompareKeySet.get(0))
							    {
							      int counter=0;
							      boolean flag=false;
							    	for(int s=1;s<comparingKeySet.size();s++)
							    	{
							    		for(int p=1;p<toCompareKeySet.size();p++)
							    		{
							    			if(comparingKeySet.get(s)==toCompareKeySet.get(p))
							    			{
							    				counter++;
							    			}
							    		}
							    	}
							    	
							    	if(counter==2 && comparingKeySet.size()==3&& toCompareKeySet.size()==3)
							    	{
							    		continue;
							    	}
							    	
							    	if(counter==1 && comparingKeySet.size()==2&& toCompareKeySet.size()==2)
							    	{
							    		continue;
							    	}
							    	
							    	if(counter==3 && comparingKeySet.size()==4&& toCompareKeySet.size()==4)
							    	{
							    		continue;
							    	}
							    	if(counter==1 && comparingKeySet.size()==2&& toCompareKeySet.size()==3)
							    	{
							    		continue;
							    	}
							    	if(counter==1 && comparingKeySet.size()==3&& toCompareKeySet.size()==2)
							    	{
							    		continue;
							    	}
							    	
							    	
							    	
							       String[] toCompareEdgeTokens=toCompareEdge.get(toCompareKeySet).split(",");
							    	
							    	if(comparingEdgeTokens[0].equalsIgnoreCase(toCompareEdgeTokens[0]))
							    	{
							    		flag=true;
							    	}
							    	else if(comparingEdgeTokens[0].equalsIgnoreCase(toCompareEdgeTokens[2]))
							    	{
							    		flag=true;
							    	}
							    	else if(comparingEdgeTokens[2].equalsIgnoreCase(toCompareEdgeTokens[0]))
							    	{
							    		flag=true;
							    	}
							    	else if(comparingEdgeTokens[2].equalsIgnoreCase(toCompareEdgeTokens[2]))
							    	{
							    		flag=true;
							    	}
							    	
							    	if(flag==false)
							    	{
							    		continue;
							    	}
							    	
							    	else
							    	{
							    		latestEdgeMappings.add(toCompareEdge);
							    	}
							    	
							    	
					         }
							    
						}
							
						toCompareMappings=null;
							
							
							
							}
					latestListMappings.add(latestEdgeMappings);
					latestEdgeMappings=null;
					 comparingEdge=null;;
				 comparingList=null;
				 comparingItr=null;
				  comparingKeySet=null;
				  comparingEdgeTokens=null;
				  
				 
				}
					latestFullMappings.add(latestListMappings);
					
					latestListMappings=null;
					
					comparingMappings=null;
				}
					
					else
					{
						
					ArrayList<HashMap> lastMappings=newFullMappings.get(i);
						ArrayList<ArrayList> listMappings=new ArrayList<ArrayList>();
						for(int j=0;j<lastMappings.size();j++)
						{
							HashMap<ArrayList,String> edge=lastMappings.get(j);
							ArrayList<HashMap> edgeList=new ArrayList<HashMap>();
							edgeList.add(edge);
							listMappings.add(edgeList);
							edge=null;
							edgeList=null;
						}
						latestFullMappings.add(listMappings);
						listMappings=null;
						
					
					}
					
			}	
				
				newFullMappings=null;
				
			
				
				for(int i=0;i<latestFullMappings.size();i++)
				{
					ArrayList<ArrayList> latestListMappings=latestFullMappings.get(i);
					for(int j=0;j<latestListMappings.size();j++)
					{
						ArrayList<HashMap<ArrayList,String>>edgeList=latestListMappings.get(j);
						for(int k=0;k<edgeList.size()-1;k++)
						{
							HashMap<ArrayList,String> comparingEdge=edgeList.get(k);
							Set comparingList=comparingEdge.keySet();
							Iterator comparingItr=comparingList.iterator();
						 ArrayList<Integer> comparingKeySet=(ArrayList)comparingItr.next();
						 String comparingEdgeValue=comparingEdge.get(comparingKeySet);
							
							for(int l=k+1;l<edgeList.size();l++)
							{
								HashMap<ArrayList,String> toCompareEdge=edgeList.get(l);
								Set toCompareList=toCompareEdge.keySet();
								Iterator toCompareItr=toCompareList.iterator();
							 ArrayList<Integer> toCompareKeySet=(ArrayList)toCompareItr.next();
							 String toCompareEdgeValue=toCompareEdge.get(toCompareKeySet);
							 
							          if(comparingEdgeValue.equalsIgnoreCase(toCompareEdgeValue))
							          {
							        	  edgeList.remove(l);
							          }
							          
							       }
							
							 comparingEdge=null;
							 comparingList=null;
							 comparingItr=null;
							  comparingKeySet=null;
							  comparingEdgeValue=null;
							
						}
						edgeList=null;
						
					}
					latestListMappings=null;
				}
				
	         for(int i=0;i<latestFullMappings.size()-1;i++)
				{
					ArrayList<ArrayList> latestListMappings=latestFullMappings.get(i);
					
					for(int j=0;j<latestListMappings.size();j++)
					{
						ArrayList<HashMap<String,String>> edgeListMappings=latestListMappings.get(j);
						for(int k=i+1;k<latestFullMappings.size();k++)
						{
							ArrayList<ArrayList> toLatestListMappings=latestFullMappings.get(k);
							
							for(int l=0;l<toLatestListMappings.size();l++)
							{
								ArrayList<HashMap<String,String>> toEdgeListMappings=toLatestListMappings.get(l);
								
								if(edgeListMappings.hashCode()==toEdgeListMappings.hashCode())
								{
									toLatestListMappings.remove(l);
								}
							}
							toLatestListMappings=null;
							
						}
						edgeListMappings=null;
					}
					latestListMappings=null;
				}
				
			
				
				for(int i=0;i<latestFullMappings.size()-1;i++)
				{
					ArrayList<ArrayList> latestListMappings=latestFullMappings.get(i);
					
					for(int j=0;j<latestListMappings.size();j++)
					{
						ArrayList<HashMap<ArrayList,String>> edgeListMappings=latestListMappings.get(j);
						
						for(int j1=0;j1<edgeListMappings.size();j1++)
						{
							ArrayList<Integer> keySet=edgeListMappings.get(j1).keySet().iterator().next();
							
							for(int k=i+1;k<latestFullMappings.size();k++)
							{
								ArrayList<ArrayList>toLatestListMappings=latestFullMappings.get(k);
								for(int l=0;l<toLatestListMappings.size();l++)
								{
									ArrayList<HashMap<ArrayList,String>> toEdgeListMappings=toLatestListMappings.get(l);
									
									for(int l1=0;l1<toEdgeListMappings.size();l1++)
									{
										ArrayList<Integer> toKeySet=toEdgeListMappings.get(l1).keySet().iterator().next();
										
										if(keySet.hashCode()==toKeySet.hashCode() && toEdgeListMappings.size()!=edgeListMappings.size())
										{
											toEdgeListMappings.remove(l1);
										}
									}
									toEdgeListMappings=null;
								}
								toLatestListMappings=null;
							}
							keySet=null;
						}
						edgeListMappings=null;
					}
					latestListMappings=null;
				}
				
				
			ArrayList<ArrayList>Subgraphs=new ArrayList<ArrayList>();
			for(int i=0;i<latestFullMappings.size();i++)
			{
				ArrayList<ArrayList>mappings=latestFullMappings.get(i);
				for(int j=0;j<mappings.size();j++)
				{
				ArrayList<HashMap<ArrayList,String>>edgeList=mappings.get(j);
				ArrayList<HashMap<ArrayList,String>>SubGraph=new ArrayList<HashMap<ArrayList,String>>();
				
				if(edgeList.size()>0)
				{
				
				for(int k=0;k<edgeList.size();k++)
				{
					SubGraph.add(edgeList.get(k));
					
				}
				Subgraphs.add(SubGraph);
				
				}
				SubGraph=null;
				}
				
			}
			
			
			
			  File file11 = new File("D://Project/SubgraphsNew.doc");
				 
				
				if (!file11.exists()) {
					file11.createNewFile();
				}
	 
				FileWriter fw1 = new FileWriter(file11.getAbsoluteFile());
				BufferedWriter bw1 = new BufferedWriter(fw1);
				
				System.out.println("Debug");
			
			
			
			for(int i=0;i<Subgraphs.size()-1;i++)
			{
				ArrayList<HashMap<ArrayList,String>>subgraph=Subgraphs.get(i);
				
				
				   
				for(int j=i+1;j<Subgraphs.size(); j++)
				{
					ArrayList<HashMap<ArrayList,String>>toSubgraph=Subgraphs.get(j);
					int increment=0;
					int i1=10;
					String out=null;
					int whichIndex=1000;
					ArrayList<Integer> index=new ArrayList<Integer>();
					for(int k=0;k<subgraph.size();k++)
					{
					
					   HashMap<ArrayList,String> edgeHash=subgraph.get(k);
					Set list= edgeHash.keySet();
					Iterator itr=list.iterator();
					ArrayList a=(ArrayList)itr.next();
					  
					   
					   String edge=edgeHash.get(a);
					   String[] edges=edge.split(",");
					
					
					for(int l=0;l<toSubgraph.size();l++)
					{
						HashMap<ArrayList,String> toEdgeHash=toSubgraph.get(l);
						ArrayList<Integer> al1=(ArrayList)toEdgeHash.keySet().iterator().next();
						String toEdge=toEdgeHash.get(al1);
						String toEdges[]=toEdge.split(",");
						
						if(edge.equalsIgnoreCase(toEdge))
						{
							increment++;
							
							
							break;
							
						}
						String edge1=edges[0].trim();
						String edge2=edges[2].trim();
						String edge3=toEdges[0].trim();
						String edge4=toEdges[2].trim();
						String edge5=edges[1].trim();
						String edge6=toEdges[1].trim();
						String [] len=null;
						if(edges.length>toEdges.length)
						{
							len=toEdges;
						}
						else
						{
							len=edges;
						}
						
					
						
					 if(edge1.equalsIgnoreCase(edge4) && edge2.equalsIgnoreCase(edge3) && ((edge5.equalsIgnoreCase("position") && edge6.equals("player"))||(edge5.equalsIgnoreCase("player") && edge6.equals("position"))))
					 {
						for(int c=0;c<keywords.length;c++)
						{
							for(int co=0;co<len.length;co++)
							{
								//System.out.println(edges.length+" "+toEdges.length);
								String e=edges[co].trim().toLowerCase();
								String e1=toEdges[co].trim().toLowerCase();
							if(e.contains(keywords[c]))
							{
								i1=0;
								out="yes";
								whichIndex=l;
								
								break;
							}
							else if(e1.contains(keywords[c]))
							{
								i1=1;
								out="yes";
								whichIndex=k;
								break;
							}
							}
							
							if(out=="yes")
								break;
						}
					 }
					 if(out=="yes")
							break;
						
						
					}
					
					 if(out=="yes")
							break;
					
				}
					
					if(increment==subgraph.size())
					{
						
				       top.add(i);
				      
				        
						Subgraphs.remove(j);
					}
					 if(i1==0)
					{
						 for(int m=0;m<toSubgraph.size();m++)
						 {
							 if(m!=whichIndex)
							 {
								 subgraph.add(toSubgraph.get(m));
							 }
						 }
					    
						Subgraphs.remove(j);
					}
					 if(i1==1)
					{
						 for(int m=0;m<subgraph.size();m++)
						 {
							 if(m!=whichIndex)
							 {
								 toSubgraph.add(subgraph.get(m));
							 }
						 }
						Subgraphs.remove(i);
					}
				}
				
				
			}
			
			
		//Removing Unnecessary Subgraphs
			
			System.out.println(Subgraphs.size());
			
			
			for(int i=0;i<Subgraphs.size();i++)
			{
				ArrayList<HashMap<ArrayList,String>> subgraph=Subgraphs.get(i);
				if(subgraph.size()==1)
				{
					HashMap<ArrayList,String>triple=subgraph.get(0);
					if(triple.keySet().iterator().next().size()==2)
					{
						Subgraphs.remove(i);
						i=i-1;
					}
				}
				subgraph=null;
			}
			
			
			
			System.out.println(Subgraphs.size());
			
			latestFullMappings=null;
				
				
				int counter=0;
				
				
			 for(int i=0;i<Subgraphs.size();i++)
		       {
		    	   ArrayList<HashMap<ArrayList,String>>subgraph=Subgraphs.get(i);
		    	   ArrayList<HashMap<ArrayList,String>> newsubgraph=new ArrayList<HashMap<ArrayList,String>>();
		    	    
		    	   HashMap<ArrayList,String> edge=subgraph.get(0);
		    	   Set list=edge.keySet();
		    	  ArrayList al= (ArrayList)list.iterator().next();
		    	  String triple=edge.get(al).toLowerCase();
		    	  int incre=0;
		    	  for(int j=0;j<keywords.length;j++)
		    	  {
		    		  if(triple.contains(keywords[j]))
		    			  incre++;
		    	  }
		    	  al=null;
		    	  list=null;
		    	  triple=null;
		    	  subgraph=null;
		    	  
		    	  if(incre==keywords.length)
		    	  {
		    		 Subgraphs.remove(i);
		    		 newsubgraph.add(edge);
		    		 Subgraphs.add(i,newsubgraph);
		    		 
		    	  }
		    	  newsubgraph=null;
		       }
		       
		       
		       ArrayList<Integer>pos=new ArrayList<Integer>();
		       
		       
		       for(int i=0;i<Subgraphs.size();i++)
		       {
		    	   ArrayList<HashMap<ArrayList,String>>subgraph=Subgraphs.get(i);
		    	   if(subgraph.size()==1)
		    	   {
		    		   ArrayList<Integer> aln=new ArrayList<Integer>();
		    		  HashMap<ArrayList,String>edge=subgraph.get(0);
		    		  ArrayList<Integer> al=edge.keySet().iterator().next();
		    		  for(int p=1;p<al.size();p++)
		    		  {
		    			  aln.add(al.get(p));
		    		  }
		    		 for(int j=i+1;j<Subgraphs.size();j++)
		    		 {
		    			 ArrayList<HashMap<ArrayList,String>>subgraph2=Subgraphs.get(j);
		    			 if(subgraph2.size()==1)
		    			 {
		    				 HashMap<ArrayList,String>edge2=subgraph2.get(0);
				    		  ArrayList<Integer> al2=edge2.keySet().iterator().next();
				    		  ArrayList<Integer>al2n=new ArrayList();
				    		  
				    		  for(int p=1;p<al2.size();p++)
				    		  {
				    			  al2n.add(al2.get(p));
				    		  }
				    		  if(al.size()==3 && al2.size()==3 && aln.hashCode()!=al2n.hashCode())
				    		  {
				    			  subgraph.add(edge2);
				    			  if(pos.size()>0)
				    			  {
				    				  int index=0;
				    				  for(int x=0;x<pos.size();x++)
				    				  {
				    					  if(pos.get(x)==j)
				    					  {
				    						  index=1;
				    						  break;
				    					  }
				    				  }
				    				  if(index==0)
				    				  {
				    					  change=true;
				    					  pos.add(j);
				    				  }
				    			  }
				    			  pos.add(j);
				    		  }
				    		  edge2=null;
				    		  al2=null;
		    			 }
		    			 else
		    				 continue;
		    			 subgraph2=null;
		    		 }
		    		 
		    		 edge=null;
		    		 al=null;
		    		 
		    	   }
		    	   else
		    	   {
		    		   continue;
		    	   }
		    	   subgraph=null;
		       }
		       
		       System.out.println("Are");
		       int temp;
		       
		       System.out.println(Subgraphs.size());
		       if(change==true)
		       {
		       int f=pos.get(0);
		    	  Subgraphs.remove(f);
		       }
		       System.out.println(Subgraphs.size());
		       
		       pos=null;
		       
		       
		        int change=0; 
		       for(int i=0;i<Subgraphs.size();i++)
		       {
		    	   ArrayList<HashMap> subgraph=Subgraphs.get(i);
		    	   
		    	   for(int j=0;j<subgraph.size();j++)
		    	   {
		    		   HashMap<ArrayList,String>edge0=subgraph.get(j);
		    		   ArrayList ke0=edge0.keySet().iterator().next();
		    		   String e0=edge0.get(ke0).trim().toLowerCase();
		    		   String[] arrs=e0.split(",");
		    		   String ee1=arrs[0];
		    		   String ee2=arrs[1];
		    		   String ee3=arrs[2];
		    		   int counters=0;
		    		   for(int k=0;k<keywords.length;k++)
		    		   {
		    			   if(e0.contains(keywords[k]))
		    			   {
		    				  counters++; 
		    			   }
		    		   }
		    		   
		    		   if(counters==keywords.length && change==0)
		    		   {
		    			   for(int m=1;m<subgraph.size();m++)
		    			   {
		    				   subgraph.remove(m);
		    				   m=m-1;
		    				   change=1;
		    				   counters=0;
		    			   }
		    		   }
		    		   
		    		   if(counters==keywords.length && change==1)
		    		   {
		    			   for(int m=0;m<subgraph.size();m++)
		    			   {
		    				   subgraph.remove(m);
		    				   m=m-1;
		    				   
		    			   }
		    		   }
		    	   }
		       }
		       
		       
		       
		     	for(int i=0;i<Subgraphs.size()-1;i++)
			{
				ArrayList<HashMap<ArrayList,String>>subgraph=Subgraphs.get(i);
				
				
				   
				for(int j=i+1;j<Subgraphs.size(); j++)
				{
					ArrayList<HashMap<ArrayList,String>>toSubgraph=Subgraphs.get(j);
					if(subgraph.size()==1 && toSubgraph.size()==1)
					{
					
					int increment=0;
					int i1=10;
					String out=null;
					int whichIndex=1000;
					ArrayList<Integer> index=new ArrayList<Integer>();
					for(int k=0;k<subgraph.size();k++)
					{
					
					   HashMap<ArrayList,String> edgeHash=subgraph.get(k);
					Set list= edgeHash.keySet();
					Iterator itr=list.iterator();
					ArrayList a=(ArrayList)itr.next();
					  
					   
					   String edge=edgeHash.get(a);
					   String[] edges=edge.split(",");
					
					
					for(int l=0;l<toSubgraph.size();l++)
					{
						HashMap<ArrayList,String> toEdgeHash=toSubgraph.get(l);
						ArrayList<Integer> al1=(ArrayList)toEdgeHash.keySet().iterator().next();
						String toEdge=toEdgeHash.get(al1);
						String toEdges[]=toEdge.split(",");
						
						
						String edge1=edges[0].trim();
						String edge2=edges[2].trim();
						String edge3=toEdges[0].trim();
						String edge4=toEdges[2].trim();
						String edge5=edges[1].trim();
						String edge6=toEdges[1].trim();
						String [] len=null;
						if(edges.length>toEdges.length)
						{
							len=toEdges;
						}
						else
						{
							len=edges;
						}
						
					
						
					 if(edge1.equalsIgnoreCase(edge4) && edge2.equalsIgnoreCase(edge3) && ((edge5.equalsIgnoreCase("position") && edge6.equals("player"))||(edge5.equalsIgnoreCase("player") && edge6.equals("position"))))
					 {
						for(int c=0;c<keywords.length;c++)
						{
							for(int co=0;co<len.length;co++)
							{
								//System.out.println(edges.length+" "+toEdges.length);
								String e=edges[co].trim().toLowerCase();
								String e1=toEdges[co].trim().toLowerCase();
							if(e.contains(keywords[c]))
							{
								i1=0;
								out="yes";
								whichIndex=l;
								
								break;
							}
							else if(e1.contains(keywords[c]))
							{
								i1=1;
								out="yes";
								whichIndex=k;
								break;
							}
							}
							
							if(out=="yes")
								break;
						}
					 }
					 if(out=="yes")
							break;
						
						
					}
					
					 if(out=="yes")
							break;
					
				}
					
					
					
					
					 if(i1==0)
					{
						
					    
						Subgraphs.remove(j);
					}
					 if(i1==1)
					{
						
						Subgraphs.remove(i);
					}
				}
				}
				
				
			}
			
		       
		       
		         for(int i=0;i<Subgraphs.size();i++)
		       {
		    	   ArrayList<HashMap> subgraph=Subgraphs.get(i);
		    	   if(subgraph.size()>1)
		    	   {
		    		   
		    		  ArrayList<String>matches=new ArrayList<String>();
		    		   for(int s=1;s<subgraph.size();s++)
		    		   {
		    			   HashMap<ArrayList,String>edge0=subgraph.get(s);
			    		   ArrayList ke0=edge0.keySet().iterator().next();
			    		   String e0=edge0.get(ke0);
			    		   matches.add(e0);
		    			   
		    		   }
		    		   
		    		   
		    		 
		    		   
		    		   for(int j=i+1;j<Subgraphs.size();j++)
		    		   {
		    			   ArrayList<HashMap> tosubgraph=Subgraphs.get(j);
		    			   if(tosubgraph.size()>1)
		    				   
		    			   {
		    				   ArrayList<String>tomatches=new ArrayList<String>();
		    				   
		    				   for(int t=1;t<tosubgraph.size();t++)
		    				   {
		    					   HashMap<ArrayList,String>toedge0=tosubgraph.get(t);
					    		   ArrayList toke0=toedge0.keySet().iterator().next();
					    		   String toe0=toedge0.get(toke0);
					    		   tomatches.add(toe0);
		    					   
		    				   }
		    				   
		    				   if(matches.size()==tomatches.size())
		    				   {
		    					   int count=0;
		    					  
		    					   for(int r=0;r<matches.size();r++)
		    					   {
		    						   boolean flag=false;
		    						   String match=matches.get(r).trim();
		    						   for(int r1=0;r1<tomatches.size();r1++)
		    						   {
		    							   String tomatch=tomatches.get(r1).trim();
		    							   if(match.equalsIgnoreCase(tomatch))
		    							   {
		    								  count++;
		    								  flag=true;
		    								  
		    								  break;
		    							   }
		    						   }
		    						   if(flag==true)
		    						   {
		    							  flag=false;
		    							  continue;
		    						   }
		    							  
		    					   }
		    					   
		    					   if(count==matches.size())
		    					   {
		    						   Subgraphs.remove(j);
		    						   j=j-1;
		    					   }
		    				   }
				    		  
				    		 
				    		   
				    	 }
		    		   }
		    		   
		    		   
		    		   
		    	}
		       }
		       
		       
		       
		        for(int i=0;i<Subgraphs.size();i++)
		       {
		    	   ArrayList<HashMap<ArrayList,String>> subgraph=Subgraphs.get(i);
		    	   int cant=0;
		    	   
		    	   if(subgraph.size()>1)
		    	   {
		    		   for(int j=1;j<subgraph.size();j++)
		    		   {
		    			   HashMap<ArrayList,String> edge=subgraph.get(j);
		    			   ArrayList<Integer>al=edge.keySet().iterator().next();
		    			   String triple=edge.get(al);
		    			   String triples[]=triple.split(",");
		    			   
		    			   for(int k=i+1;k<Subgraphs.size();k++)
		    			   {
		    				   
		    				   ArrayList<HashMap<ArrayList,String>> tosubgraph=Subgraphs.get(k);
		    				   if(tosubgraph.size()>1 && subgraph.size()==tosubgraph.size())
		    				   {
		    					   for(int s=1;s<tosubgraph.size();s++)
		    					   {
		    						   HashMap<ArrayList,String>toedge=tosubgraph.get(s);
		    						   ArrayList<Integer>toal=toedge.keySet().iterator().next();
		    						   String totriple=toedge.get(toal);
		    						   String totriples[]=totriple.split(",");
		    						   
		    						   for(int l=0;l<triples.length;l++)
		    						   {
		    							   String t=triples[l].trim().toLowerCase();
		    							   for(int m=0;m<totriples.length;m++)
		    							   {
		    								  String tt= totriples[m].trim().toLowerCase();
		    								  if(t.equalsIgnoreCase(tt))
		    								  {
		    									  int ind=0;
		    									  
		    									  for(int m1=0;m1<keywords.length;m1++)
		    									  {
		    										  String[] tg=null;
		    										  if(triples.length>=totriples.length) tg=triples;
		    										  else tg=totriples;
		    										  for(int b=0;b<tg.length;b++)
		    										  {String c=triples[b].trim().toLowerCase();
		    										   String c1=totriples[b].trim().toLowerCase();
		    										   String key=keywords[m1].trim().toLowerCase();
		    											  if(c.contains(key))
		    												  {ind=1; 
		    												  break;
		    												  }
		    											  else if(c1.contains(key)) {
		    												  ind=2;break;
		    												  }
		    										  }
		    										  if(ind!=0) break;
		    									  }
		    									  if(ind==1)
		    									  {
		    									  Subgraphs.remove(tosubgraph);
		    									 i=i-1;
		    									 cant=2;
		    									  }
		    									  else if(ind==2)
		    									  {
		    										  Subgraphs.remove(subgraph);
		    										  k=k-1;
		    										  cant=1;
		    									  }
		    									  
		    								  }
		    								  m+=1;
		    								  if(cant!=0)
		    									  break;
		    							   }
		    							   
		    							   
		    							   l+=1;
		    							   if(cant!=0)
		    								   break;
		    						   }
		    						   
		    						   if(cant!=0)
		    							   break;
		    						   
		    					   }
		    				   }
		    				   
		    				   if(cant==2)
		    					   continue;
		    				   else if(cant==1)
		    					   break;
		    			   }
		    			   
		    			   
		    			   if(cant==1)
		    				   break;
		    			    }
		    	   }
		    	   
		    	   if(cant==1)
		    		   continue;
		       }
		       
		       
		     
		       
		       
		       
		       
		     
		       for(int i=0;i<Subgraphs.size();i++)
		       {
		    	   ArrayList<HashMap> subgraph=Subgraphs.get(i);
		    	   int index=0;
		    	   for(int j=0;j<subgraph.size();j++)
		    	   {
		    		   HashMap<ArrayList,String>edge=subgraph.get(j);
		    		   ArrayList<Integer>al=edge.keySet().iterator().next();
		    		   String triple=edge.get(al).trim().toLowerCase();
		    		   int incre=0;
		    		   for(int k=0;k<keywords.length;k++)
		    		   {
		    			   String k1=keywords[k].trim();
		    			   if(triple.contains(k1))
		    			   {
		    				incre++;   
		    			   }
		    		   }
		    		   if(incre==keywords.length)
		    		   {
		    			   index=j;
		    			   break;
		    			  
		    		   }
		    		  
		    	   }
		    	   
		    	   for(int k=0;k<subgraph.size();k++)
		    	   {
		    		   if(k!=index && index!=0)
		    		   {
		    			   subgraph.remove(k);
		    		   }
		    	   }
		       }
		       
		       
		       
		       
		         //Ranking again done   
		       
		       ArrayList<ArrayList>RankedGraphs1=new ArrayList<ArrayList>();
		       
		       
		       for(int i=0;i<Subgraphs.size();i++)
		       {
		    	   ArrayList<HashMap> subgraph=Subgraphs.get(i);
		    	   if(subgraph.size()==1)
		    	   {
		    		   HashMap<ArrayList,String> edge=subgraph.get(0);
		    		   ArrayList key=edge.keySet().iterator().next();
		    		   String triple=edge.get(key);
		    		   String[] triples=triple.split(",");
		    		   int incr=0;
		    		   ArrayList<String>gen=new ArrayList();
		    		   
		    		   for(int k=0;k<triples.length;k++)
		    		   {
		    			   String t=triples[k].trim().toLowerCase();
		    		   for(int j=0;j<keywords.length;j++)
		    		   {
		    			   String k1 =keywords[j].trim();
		    			   if(t.contains(k1))
		    			   {
		    				   incr++;
		    				   if(gen.size()==0)gen.add(k1);
		    				   else
		    				   {
		    					   int c=0;
		    					   for(int v=0;v<gen.size();v++)
		    					   {
		    						   if(gen.get(v)==k1)
		    						   { c=1;
		    							   break;
		    						   }
		    					   }
		    					   if(c==0) gen.add(k1);
		    				   }
		    			   }
		    		   }
		    		   }
		    		   if(gen.size()==keywords.length)
		    		   {
		    			   RankedGraphs1.add(subgraph);
		    			   Subgraphs.remove(subgraph);
				    	   i=i-1;
		    		   }
		    	   }
		    	   subgraph=null;
		    	   
		    	   
		    	   
		       }
		       
		       
		       
		   System.out.println("Prathu");	       
		       for(int i=0;i<Subgraphs.size();i++)
		       {
		    	   ArrayList<HashMap> subgraph=Subgraphs.get(i);
		    	   
		    	   if(subgraph.size()>1)
		    	   {
		    		   ArrayList<Integer>match=new ArrayList<Integer>();
		    		   for(int j=0;j<subgraph.size();j++)
		    		   {
		    			   HashMap<ArrayList,String>edge=subgraph.get(j);
		    			   ArrayList<Integer> key=edge.keySet().iterator().next();
		    			   for(int s=1;s<key.size();s++)
		    			   {
		    				  
		    				   for(int l=0;l<keywords.length;l++)
		    				   {
		    					   boolean val=false;
		    					   if(key.get(s)==l)
		    					   {
		    						   
		    						   if(match.size()==0)
		    							   {match.add(l);
		    							   break;
		    							   
		    							   }
		    						   else
		    						   {
		    							   for(int h=0;h<match.size();h++)
		    							   {
		    								   if(match.get(h)==l)
		    								   { 
		    									   val=true;
		    									   break;
		    								   }
		    							   }
		    							    
		    							    if(val==false)
		    							   match.add(l);
		    							   break;
		    						   }
		    						  
		    						  
		    					   }
		    				   }
		    				  
		    			   }
		    			   
		    			   
		    		}
		    		   if(match.size()==3)
		    			   {RankedGraphs1.add(subgraph);
		    		   Subgraphs.remove(subgraph);
		    		   i=i-1;
		    			   }
		    	   }
		       }
		       
		       
		       
		       for(int i=0;i<Subgraphs.size();i++)
		       {
		    	   ArrayList<HashMap<ArrayList,String>> subgraph=Subgraphs.get(i);
		    	   if(subgraph.size()==1)
		    	   {
		    		   HashMap<ArrayList,String>edge=subgraph.get(0);
		    		   ArrayList<Integer>al=edge.keySet().iterator().next();
		    		   if(al.size()==keywords.length)
		    		   {
		    			   RankedGraphs1.add(subgraph);
		    			   Subgraphs.remove(subgraph);
		    			   i=i-1;
		    		   }
		    	   }
		       }
		       
		       
		       for(int i=0;i<Subgraphs.size();i++)
		       {
		    	   RankedGraphs1.add(Subgraphs.get(i));
		       }
		      // Subgraphs=null;
		       
		       
		       
		       for(int i=0;i<RankedGraphs1.size();i++)
		       {
		    	   ArrayList<HashMap> subgraph=RankedGraphs1.get(i);
		    	   boolean flag=false;
		    	   
		    	   if(subgraph.size()>1)
		    	   {
		    		 
		    	   
		    	   for(int j=0;j<subgraph.size()-1;j++)
		    	   {
		    		   HashMap<ArrayList,String>edge0=subgraph.get(0);
		    		   HashMap<ArrayList,String>edge1=subgraph.get(1);
		    		   
		    		   ArrayList ke0=edge0.keySet().iterator().next();
		    		   String e0=edge0.get(ke0).trim().toLowerCase();
		    		   String[] arrs=e0.split(",");
		    		   String ee1=arrs[0].trim().toLowerCase();
		    		   String ee2=arrs[1].trim().toLowerCase();
		    		   String ee3=arrs[2].trim().toLowerCase();
		    		   
		    		   ArrayList ke1=edge1.keySet().iterator().next();
		    		   String e1=edge1.get(ke1).trim().toLowerCase();
		    		   String[] arrs1=e1.split(",");
		    		   String ee11=arrs1[0].trim().toLowerCase();
		    		   String ee21=arrs1[1].trim().toLowerCase();
		    		   String ee31=arrs1[2].trim().toLowerCase();
		    		   
		    		   if(ee1.equalsIgnoreCase(ee11))
		    		   {
		    			   flag=true;
		    			  break;
		    		   }
		    		   else if(ee1.equalsIgnoreCase(ee31))
		    		   {
		    			   flag=true;
			    			  break;
		    		   }
		    		   else if(ee3.equalsIgnoreCase(ee11))
		    		   {
		    			   flag=true;
			    			  break; 
		    		   }
		    		   else if(ee3.equalsIgnoreCase(ee31))
		    		   {
		    			   flag=true;
			    			  break;
		    		   }
		    		   
		    	   }
		    	   }
		    	   
		    	   if(flag==false && subgraph.size()>1)
		    	   {
		    	   
		    	   for(int m=1;m<subgraph.size();m++)
    			   {
    				   subgraph.remove(m);
    				   m=m-1;
    			   }
		    	   }
		       }
		       
		       
		       
		    	   ArrayList<HashMap> subgraphh=RankedGraphs1.get(0);
		    	   ArrayList<HashMap> subgraphh2=RankedGraphs1.get(1);
		    	  if(subgraphh2.size()==1)
		    	  {
		    		  HashMap<ArrayList,String>edge0=subgraphh2.get(0);
		    		   ArrayList ke0=edge0.keySet().iterator().next();
		    		   String e0=edge0.get(ke0).trim().toLowerCase();
		    		   
		    		  
		    		   HashMap<ArrayList,String>edge1=subgraphh.get(0);
		    		   HashMap<ArrayList,String>edge11=subgraphh.get(1);
		    		   
		    		   
		    		   ArrayList ke1=edge1.keySet().iterator().next();
		    		   String e1=edge1.get(ke1).trim().toLowerCase();
		    		   ArrayList ke11=edge11.keySet().iterator().next();
		    		   String e11=edge11.get(ke11).trim().toLowerCase();
		    		   
		    		   if(e0.equalsIgnoreCase(e1) || (e0.equalsIgnoreCase(e11)))
		    		   {
		    			   RankedGraphs1.remove(1);
		    		   }
		    	  }
		    		   
		    		  		    	  
		       int dd=0;
		       answers=null;
		       
		       
		       
		       
		       
		       
		       ArrayList<String>ans=new ArrayList<String>();
				 for(int i=0;i<RankedGraphs1.size();i++)
					{
						ArrayList<HashMap> subgraph=RankedGraphs1.get(i);
						
						if(subgraph.size()>0)
						{
							
						StringBuffer tt=new StringBuffer();
					     for(int j=0;j<subgraph.size();j++)
					     {
					    	 tt.append("|");
					    	 HashMap<ArrayList,String>edge=subgraph.get(j);
					    	
					    	 
					    	
					    		 Set list=edge.keySet();
					    		 Iterator itr=list.iterator();
					    		 ArrayList<Integer>al=(ArrayList)itr.next();
					    		
					    		
					    		 String triple=edge.get(al);
					    		 tt.append(triple);
					    		 edge=null;
					    	 
					     }
					     
					     
					     subgraph=null;
					   
					     dd++;
					    ans.add(tt.toString());
						}
						  
						}
				 
				 
				 for(int l=0;l<ans.size();l++)
				 {
					 answers[l]=ans.get(l);
				 }
				 
				 
				 if(answers.length==0)
				 {
					 answers[0]="No Answer";
				 }
				 
			 }
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return answers;		
	  }
	}
	
	