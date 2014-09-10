import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class ProcessTag 
{

	//  Database credentials
	static final String JDBC_DRIVER="com.mysql.jdbc.Driver";  
    static final String DB_URL="jdbc:mysql://dbinstance.c8kmfn2haf9k.us-east-1.rds.amazonaws.com/UTA";   
    static final String USER = "developer";
    static final String PASS = "KEVaFeF7HA";
    static final String SUCCESS="success";
    static final String MESSAGE="msg";
	
    Connection conn;
    ResultSet rs,rs1;
    Statement stmt;
    JSONObject json;
    
	public JSONObject processLoginTag(String email,String password)
	{
		try {
			
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection(DB_URL,USER,PASS);
		
			stmt=conn.createStatement();
		
				String sql="Select * from LOGIN_INFO where EMAIL='"+email+"'";
				rs=stmt.executeQuery(sql);
				json=new JSONObject();
				
				if(rs.isBeforeFirst())
				{
					while(rs.next())
					{
						String paswd=rs.getString("PASSWORD");
					
						if(paswd.equals(password))
						{
							json.put(SUCCESS, 1);
							json.put(MESSAGE, "Login Successful");
							json.put("USER_ID", rs.getInt("USER_ID"));
							
							rs1=stmt.executeQuery("Select * from QUESTIONS");
							if(rs1.isBeforeFirst())
							{
								JSONArray questions=new JSONArray();
								JSONObject object;
								while(rs1.next())
								{
									object=new JSONObject();
									object.put("QUESTION_ID", rs1.getInt("QUESTION_ID"));
									object.put("QUESTION_ASKED", rs1.getString("QUESTION_ASKED"));
									questions.add(object);
								}
								json.put("QUESTIONS", questions);
								
							}else
							{
								json.put(SUCCESS, 1);
								json.put(MESSAGE, "NoQuestions");
								json.put("USER_ID", rs.getInt("USER_ID"));
							}
							
							return(json);
						}
						else
						{
							json.put(SUCCESS, 0);
							json.put(MESSAGE,"Invalid Password, Please try Again" );
							return(json);
						}
					}
				}
				else
				{
					json.put(SUCCESS, 0);
					json.put(MESSAGE, "Invalid Email, Please try Again");
					return(json);
				}
				
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				finally
				{
			      try{
			        	rs.close();
			        	stmt.close();
			            if(conn!=null)
			            conn.close();
			         }catch(SQLException se){
			            se.printStackTrace();
			         }
			    }
				//json.put(SUCCESS, 0);
				//json.put(MESSAGE,"COULD NOT PROCESS REQUEST, PLEASE TRY AFTER SOMETIME" );
				return(json);
		}
	
	public JSONObject processRegisterTag(String USER_NAME,String EMAIL,String Password)
	{
		try {
			
					Class.forName(JDBC_DRIVER);
					conn=DriverManager.getConnection(DB_URL,USER,PASS);
					
					stmt=conn.createStatement();
					json=new JSONObject();
					
					//Check if USer Alrady exists
					rs1=stmt.executeQuery("Select * from LOGIN_INFO where EMAIL='"+EMAIL+"'");
					if(rs1.isBeforeFirst())
					{
						json.put(SUCCESS, 0);
						json.put(MESSAGE, "Email already exists");
						return json;
					}
					
									
					String sql="insert into LOGIN_INFO(USER_NAME,EMAIL,PASSWORD) values('"+USER_NAME+"','"+EMAIL
									+"','"+Password+"')";
						
					int result=stmt.executeUpdate(sql);				
					if(result>0)
					{
						json.put(SUCCESS, 1);
						json.put(MESSAGE, "Registration was successful");
						
						ResultSet rs2=stmt.executeQuery("Select * from LOGIN_INFO where EMAIL='"+EMAIL+"'");
						rs2.next();
						json.put("USER_ID",rs2.getInt("USER_ID"));
						rs2.close();
						
						//Send the Questions present in Database
						ResultSet rs3=stmt.executeQuery("Select * from QUESTIONS");
						if(rs3.isBeforeFirst())
						{
							JSONArray questions=new JSONArray();
							JSONObject object;
							while(rs3.next())
							{
								object=new JSONObject();
								object.put("QUESTION_ID", rs3.getInt("QUESTION_ID"));
								object.put("QUESTION_ASKED", rs3.getString("QUESTION_ASKED"));
								questions.add(object);
							}
							json.put("QUESTIONS", questions);
						}else
						{
							json.put(MESSAGE, "NoQuestions");
						}
						rs3.close();
						return(json);
					}
					
					rs1.close();
		        	stmt.close();		
					
				
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				finally
				{
			      try{
			        	
			            if(conn!=null)
			            conn.close();
			         }catch(SQLException se){
			            se.printStackTrace();
			         }
			    }
				
				//json.put(SUCCESS, 0);
				//json.put(MESSAGE, "Sorry, couldn't Process Request");
				return(json);
		}
	

	public JSONObject getAnswersTag(int USER_ID,int QUESTION_ID)
	{
		try {
			
					Class.forName(JDBC_DRIVER);
					conn=DriverManager.getConnection(DB_URL,USER,PASS);
				
					stmt=conn.createStatement();
					json=new JSONObject();
					
					
					String SQL="Select * from ANSWERS where QUESTION_ID="+QUESTION_ID;
					rs=stmt.executeQuery(SQL);					
					if(rs.isBeforeFirst())
					{
						json.put(SUCCESS, 1);
						json.put(MESSAGE, "Answers Fetched");
						JSONArray Answers=new JSONArray();
						JSONObject answer;
						while(rs.next())
						{
							answer=new JSONObject();
							answer.put("ANSWER", rs.getString("ANSWER"));
							answer.put("ANSWER_ID", rs.getString("ANSWER_ID"));
							answer.put("RANK", rs.getInt("RANK"));
							Answers.add(answer);
						}
						json.put("ANSWERS", Answers);
					}
					else
					{
						json.put(SUCCESS, 0);
						json.put(MESSAGE, "This Question doesn't has any Answer yet");
						return(json);
					}
					
					rs.close();
		        	stmt.close();	
		        	
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				finally
				{
			      try{
			        	
			            if(conn!=null)
			            conn.close();
			         }catch(SQLException se){
			            se.printStackTrace();
			         }
			    }
				
				//json.put(SUCCESS, 0);
				//json.put(MESSAGE, "Sorry, couldn't Process Request");
				return(json);
		}
	
	
	public JSONObject postAnswerTag(int USER_ID,int QUESTION_ID,String ANSWER)
	{
		try {
			
					Class.forName(JDBC_DRIVER);
					conn=DriverManager.getConnection(DB_URL,USER,PASS);
				
					stmt=conn.createStatement();
					json=new JSONObject();
					
					
					//String SQL="insert into ANSWERS(USER_ID,QUESTION_ID,ANSWER) values('"+USER_ID+"','"+QUESTION_ID+"','"+ANSWER+"')";
					//int result=stmt.executeUpdate(SQL);
					
					//if(result>0)
					//{
						
						//Calling the LocationBasedHandler's NewResponse method to notify() the uers.
						LocationBasedHandler object=new LocationBasedHandler();
						int result=object.NewResponse(""+USER_ID,""+QUESTION_ID, ANSWER);
						object=null;
						
						if(result==1)
					{
						json.put(SUCCESS, 1);
						json.put(MESSAGE, "Insertion Successful");
					
						String SQL="Select * from ANSWERS where QUESTION_ID="+QUESTION_ID;
						rs=stmt.executeQuery(SQL);
						
						if(rs.isBeforeFirst())
						{
							JSONArray Answers=new JSONArray();
							JSONObject answer;
							while(rs.next())
							{
								answer=new JSONObject();
								answer.put("ANSWER", rs.getString("ANSWER"));
								answer.put("ANSWER_ID", rs.getString("ANSWER_ID"));
								answer.put("RANK", rs.getInt("RANK"));
								Answers.add(answer);
							}
							json.put("ANSWERS", Answers);
						}
					}else
					{
						json.put(SUCCESS, 0);
					}
					rs.close();
		        	stmt.close();
		        	
		        	
		        	//Calling Mahesh Method
		        	Parser parseobject=new Parser();
		        	parseobject.parseQuestionAnswer('a', USER_ID, ANSWER, Constants.PATH);
		        	
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				finally
				{
			      try{
			        	
			            if(conn!=null)
			            conn.close();
			         }catch(SQLException se){
			            se.printStackTrace();
			         }
			    }
				
				//json.put(SUCCESS, 0);
				//json.put(MESSAGE, "Sorry, couldn't Process Request");
				return(json);
		}
		
	public JSONObject postQuestionTag(int USER_ID, String QUESTION_ASKED)
	{
		
		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection(DB_URL,USER,PASS);
			
			int QidToMahesh=0;
			
			stmt=conn.createStatement();
			json=new JSONObject();
			
			String SQL="insert into QUESTIONS(USER_ID,QUESTION_ASKED) values('"+USER_ID+"','"+QUESTION_ASKED
					+"')";
			
			int result=stmt.executeUpdate(SQL);
			
			if(result>0)
			{
				json.put(SUCCESS, 1);
				json.put(MESSAGE, "Question Inserted successfully");
				
				//Send the Questions present in Database
				rs=stmt.executeQuery("Select * from QUESTIONS");
				if(rs.isBeforeFirst())
				{
					JSONArray questions=new JSONArray();
					JSONObject object;
					while(rs.next())
					{
						object=new JSONObject();
						object.put("QUESTION_ID", rs.getInt("QUESTION_ID"));
						object.put("QUESTION_ASKED", rs.getString("QUESTION_ASKED"));
						
						if(rs.getString("QUESTION_ASKED").equalsIgnoreCase(QUESTION_ASKED))
							QidToMahesh=rs.getInt("QUESTION_ID");
						
						questions.add(object);
					}
					json.put("QUESTIONS", questions);
				}
				rs.close();
				//return(json);
			}
			else
			{
				json.put(SUCCESS, 0);
				json.put(MESSAGE, "Error Occured while Posting the Question");
				//return json;
			}
			
			
			//Calling Mahesh Method
        	Parser parseobject=new Parser();
        	String maheshResult=parseobject.parseQuestionAnswer('q', QidToMahesh, QUESTION_ASKED, Constants.PATH);
        	
        	//Calling Hani Method
        	MainPart haniObject=new MainPart();
        	String Answers[]=haniObject.HaniMethod(QUESTION_ASKED,Constants.PATH);
        	if(!Answers[0].equals("No Question"))
        	{
        		Statement stmt2=conn.createStatement();
        		int result2=stmt2.executeUpdate("insert into HANI_ANSWERS(QUESTION_ID,USER_ID,ANSWER) values ('"+QidToMahesh+"','"+USER_ID+"','"+Answers[1]+"'");
        		stmt2.close();
        	}
        	
        	//Calling Prathu's Method
        	
        	SubGraphsRetrieval prathuObject=new SubGraphsRetrieval();
        	String PrathuAnswers[]=prathuObject.retrievingSubGraphs(QUESTION_ASKED);
        	if(!PrathuAnswers[0].equals("No Answer"))
        	{
        		Statement stmt2=conn.createStatement();
        		int result3=stmt2.executeUpdate("insert into PRATHU_ANSWERS(QUESTION_ID,ANSWER) values ('"+QidToMahesh+"','"+PrathuAnswers[0]+"'");
        		stmt2.close();
        	}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
		      try{
		        	
		            if(conn!=null)
		            conn.close();
		         }catch(SQLException se){
		            se.printStackTrace();
		         }
		    }
		
		//json.put(SUCCESS, 0);
		//json.put(MESSAGE, "Error Occured while Posting the Question");
		return json;
	}
	
	public JSONObject postQuestionTag(int USER_ID, String QUESTION_ASKED, double Longitude, double Latitude,int Deadline, int Radius)
	{
	
		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection(DB_URL,USER,PASS);
			
			//stmt=conn.createStatement();
			json=new JSONObject();
			
			/*
			String SQL="insert into QUESTIONS(USER_ID,QUESTION_ASKED,LONGITUDE,LATITUDE,DEADLINE) values('"+USER_ID+"','"+QUESTION_ASKED
					+"','"+Longitude+"','"+Latitude+"','"+Deadline+"')";
			
			int result=stmt.executeUpdate(SQL);
			json.put(SUCCESS, -1);
			json.put("error", SQL);
			stmt.close();
			
			if(result>0)
			{
				
				SQL = "SELECT QUESTION_ID FROM UTA.QUESTIONS WHERE USER_ID="+USER_ID+" AND Question_Asked='"+QUESTION_ASKED+"'";
				Statement stmt4=conn.createStatement();
				ResultSet rsTemp = stmt4.executeQuery(SQL);
				rsTemp.next(); 
				SQL = rsTemp.getString("QUESTION_ID");	
				stmt4.close();
				*/
			
				LocationBasedHandler handler=new LocationBasedHandler();
				handler.NewQuestion(USER_ID+"",QUESTION_ASKED, Longitude, Latitude, Radius, Deadline);
				handler=null;
				
				
				json.put(SUCCESS, 1);
				json.put(MESSAGE, "Question Inserted successfully");
				
				Statement stmt2=conn.createStatement();
				ResultSet rsTemp2 = stmt2.executeQuery("Select * from QUESTIONS");
				
				if(rsTemp2.isBeforeFirst())
				{
					JSONArray questions=new JSONArray();
					JSONObject object;
					while(rsTemp2.next())
					{
						object=new JSONObject();
						object.put("QUESTION_ID", rsTemp2.getInt("QUESTION_ID"));
						object.put("QUESTION_ASKED", rsTemp2.getString("QUESTION_ASKED"));
						questions.add(object);
					}
					json.put("QUESTIONS", questions);
				}
				stmt2.close();
			/*}
			else
			{
				json.put(SUCCESS, 0);
				json.put(MESSAGE, "Error Occured while Posting the Question");
				return json;
			}
			
			*/
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			json.put("error1", e.getMessage());
		}finally
		{
		      try{
		        	
		            if(conn!=null)
		            conn.close();
		         }catch(SQLException se){
		            se.printStackTrace();
		         }
		    }
		
		//json.put(SUCCESS, 0);
		//json.put(MESSAGE, "Error Occured while Posting the Question");
		return json;
	}
	
	public JSONObject getQuestionsTag(int USER_ID)
	{	
		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection(DB_URL,USER,PASS);
			
		
			Statement stmt2=conn.createStatement();
					  stmt=conn.createStatement();
					  
			json=new JSONObject();
			json.put(SUCCESS, 1);

			ResultSet rstemp=stmt2.executeQuery("select * from LOGIN_INFO where USER_ID="+USER_ID);
			rs=stmt.executeQuery("Select * from QUESTIONS");
		
		if(rstemp.isBeforeFirst())
		{	
			if(rs.isBeforeFirst())
				{
					JSONArray questions=new JSONArray();
					JSONObject object;
					while(rs.next())
					{
						object=new JSONObject();
						object.put("QUESTION_ASKED", rs.getString("QUESTION_ASKED"));
						object.put("QUESTION_ID", rs.getInt("QUESTION_ID"));
						questions.add(object);
					}
					
					json.put("QUESTIONS", questions);
					rs.close();
				}
			else
			{
				json.put(SUCCESS, 0);
				json.put(MESSAGE, "No Questions have been posted yet");
				
			}
			
			rstemp.close();
			stmt2.close();
			stmt.close();
		}else
		{
			json.put(SUCCESS, 0);
			json.put(MESSAGE, "Not a valid User");
		}
		   
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
		      try{
		            if(conn!=null)
		            conn.close();
		         }catch(SQLException se){
		            se.printStackTrace();
		         }
		}
		return json;
	}
	
	public JSONObject getNotificationsTag(int USER_ID)
	{	
		ResultSet rstemp = null;
		ResultSet rstemp1=null;
		Statement stmt1=null;
	
		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection(DB_URL,USER,PASS);
			
			stmt=conn.createStatement();
			stmt1=conn.createStatement();
					  
			json=new JSONObject();
			json.put(SUCCESS, 0);		

			rstemp1=stmt.executeQuery("select * from NOTIFICATION where USER_ID="+USER_ID);
			
		
			String QUESTION_ID="";
			
			if(rstemp1.isBeforeFirst())
				{
					json.put(SUCCESS, 1);
					JSONArray notifications=new JSONArray();
					JSONObject object;
					while(rstemp1.next())
					{
						if(true)//rs.getInt("userNotified")!=0)
						{
						object=new JSONObject();
						object.put("QUESTION_ID", rstemp1.getInt("QUESTION_ID"));
						object.put("postedByTo", rstemp1.getInt("postedByTo"));
						object.put("userNotified", rstemp1.getInt("userNotified"));
						
						QUESTION_ID=rstemp1.getString("QUESTION_ID");						
						rstemp=stmt1.executeQuery("Select QUESTION_ASKED FROM QUESTIONS WHERE QUESTION_ID='"+QUESTION_ID+"'");
						if(rstemp.next())
						object.put("QUESTION_ASKED", rstemp.getString(Constants.QUESTION_ASKED));
						
						notifications.add(object);
						}
					}
					json.put("NOTIFICATIONS", notifications);
				}
			else
			{
				json.put(SUCCESS, 0);
				json.put(MESSAGE, "NoNotifications");
				
			}

			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			try{
		            if(conn!=null)
		            conn.close();
		            rstemp.close();
		            rstemp1.close();
					stmt.close();
		         }catch(SQLException se){
		            se.printStackTrace();
		         }
		}
		return json;
	}
	
	public JSONObject updateNotificationTag(int QUESTION_ID, int USER_ID)
	{	
		try {
			Class.forName(JDBC_DRIVER);
			conn=DriverManager.getConnection(DB_URL,USER,PASS);
			
			stmt=conn.createStatement();
					  
			json=new JSONObject();
			json.put(SUCCESS, 0);		

			int result=stmt.executeUpdate("update NOTIFICATION SET userNotified=1 where USER_ID='"+USER_ID+"' and QUESTION_ID='"+QUESTION_ID+"'");
			
			if(result>0)
				{
					json.put(SUCCESS, 1);
					json.put(MESSAGE, "updated");
				}
			else
			{
				json.put(SUCCESS, 0);
				json.put(MESSAGE, "Error occured");
				
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			try{
		            if(conn!=null)
		            conn.close();
					stmt.close();
		         }catch(SQLException se){
		            se.printStackTrace();
		         }
		}
		return json;
	}
	
	public JSONObject updateRANKTag(int USER_ID,int QUESTION_ID,int ANSWER_ID, int RANK, int increased)
	{
		try {
			
					Class.forName(JDBC_DRIVER);
					conn=DriverManager.getConnection(DB_URL,USER,PASS);
				
					stmt=conn.createStatement();
					json=new JSONObject();
					
					
					String SQL="update ANSWERS set RANK ='"+RANK+"'where ANSWER_ID='"+ANSWER_ID+"'";
					//int result=stmt.executeUpdate(SQL);
					
					LocationBasedHandler object=new LocationBasedHandler();
					object.A_Vote(ANSWER_ID+"",USER_ID+"", increased);
					object=null;
					
					//if(result>0)
					//{
						
						json.put(SUCCESS, 1);
						json.put(MESSAGE, "Insertion Successful");
					
						SQL="Select * from ANSWERS where QUESTION_ID="+QUESTION_ID;
						rs=stmt.executeQuery(SQL);
						
						if(rs.isBeforeFirst())
						{
							JSONArray Answers=new JSONArray();
							JSONObject answer;
							while(rs.next())
							{
								answer=new JSONObject();
								answer.put("ANSWER", rs.getString("ANSWER"));
								answer.put("ANSWER_ID", rs.getString("ANSWER_ID"));
								answer.put("RANK", rs.getInt("RANK"));
								Answers.add(answer);
							}
							json.put("ANSWERS", Answers);
						}
					//}
					rs.close();
		        	stmt.close();	
		        	
		        	
		        	//Calling Mahesh Method
		        	Parser parseobject=new Parser();		        	
		        	parseobject.modifyRank(increased, ANSWER_ID, USER_ID, Constants.PATH);
		        	
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				finally
				{
			      try{
			        	
			            if(conn!=null)
			            conn.close();
			         }catch(SQLException se){
			            se.printStackTrace();
			         }
			    }
				
				//json.put(SUCCESS, 0);
				//json.put(MESSAGE, "Sorry, couldn't Process Request");
				return(json);
	}
	
	public JSONObject getURLSTag()
	{
		try {
			
					Class.forName(JDBC_DRIVER);
					conn=DriverManager.getConnection(DB_URL,USER,PASS);
				
					stmt=conn.createStatement();
					json=new JSONObject();
					
					
					String SQL="select * from images LIMIT 15";
					
					rs=stmt.executeQuery(SQL);
					
					
				
					if(rs.isBeforeFirst())
					{
						json.put(SUCCESS, 1);
						StringBuffer urls=new StringBuffer();
						StringBuffer ids=new StringBuffer();
						while(rs.next())
							{
								urls.append(rs.getString("url")+",");
								ids.append(rs.getString("id")+",");
							}
							json.put("URLS", urls.toString());
							json.put("IDS", ids.toString());
					}else
					{
						json.put(SUCCESS, 0);
					}
					rs.close();
		        	stmt.close();	
		        	
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				finally
				{
			      try{
			        	
			            if(conn!=null)
			            conn.close();
			         }catch(SQLException se){
			            se.printStackTrace();
			         }
			    }
				
				return(json);
	}
	
	public JSONObject getURLSForCategoriesTag(int cat_id)
	{
		try {
			
					Class.forName(JDBC_DRIVER);
					conn=DriverManager.getConnection(DB_URL,USER,PASS);
				
					stmt=conn.createStatement();
					json=new JSONObject();
					
					ResultSet rsTemp = null;
					Statement stmt1=conn.createStatement();
					
					
					String SQL="select image_id from image_cats where cat_id='"+cat_id+"' LIMIT 5";
					
					rs=stmt.executeQuery(SQL);
					
					
				
					if(rs.isBeforeFirst())
					{
						json.put(SUCCESS, 1);
						StringBuffer urls=new StringBuffer();
						while(rs.next())
							{
								rsTemp=stmt1.executeQuery("select url from images where id='"+rs.getString("image_id")+"'");
								rsTemp.next();
								urls.append(rsTemp.getString("url")+",");
							}
							json.put("URLS", urls.toString());
					}else
					{
						json.put(SUCCESS, 0);
					}
					rs.close();
		        	stmt.close();	
		        	
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				finally
				{
			      try{
			        	
			            if(conn!=null)
			            conn.close();
			         }catch(SQLException se){
			            se.printStackTrace();
			         }
			    }
				
				return(json);
	}
	
	public JSONObject getImageCategoriesTag(int ImageID)
	{
		try {
			
					Class.forName(JDBC_DRIVER);
					conn=DriverManager.getConnection(DB_URL,USER,PASS);
				
					stmt=conn.createStatement();
					json=new JSONObject();
					
					
					String SQL="select cat_id from image_cats where image_id='"+ImageID+"'";
					
					rs=stmt.executeQuery(SQL);
					
					ResultSet rsTemp = null;
					Statement stmt1=conn.createStatement();
					
					if(rs.isBeforeFirst())
					{
						json.put(SUCCESS, 1);
						StringBuffer ids=new StringBuffer();
						StringBuffer names=new StringBuffer();
						String temp;
						while(rs.next())
							{
								temp=rs.getString("cat_id");
								ids.append(rs.getString("cat_id")+",");
								
								rsTemp=stmt1.executeQuery("select name from categories where id='"+temp+"'");
								rsTemp.next();
								
								names.append(rsTemp.getString("name")+",");
							}
								json.put("IDS", ids.toString());
								json.put("NAMES", names.toString());
					}else
					{
						json.put(SUCCESS, 0);
					}
					rsTemp.close();
					stmt1.close();
					rs.close();
		        	stmt.close();	
		        	
		        	
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				finally
				{
			      try{
			        	
			            if(conn!=null)
			            conn.close();
			         }catch(SQLException se){
			            se.printStackTrace();
			         }
			    }
				
				return(json);
	}

	public JSONObject addCategoryAction(String UserID, String action, String ImageId, String TargetId, String targetValue)
	{
		try {
					Class.forName(JDBC_DRIVER);
					conn=DriverManager.getConnection(DB_URL,USER,PASS);
				
					stmt=conn.createStatement();
					json=new JSONObject();
					
					
					String SQL="insert into category_actions(user_id,action,image_id,target_id,target_value) values"
							+ "('"+UserID+"','"+action+"','"+ImageId+"','"+TargetId+"','"+targetValue+"')";
					
					json.put(SUCCESS, -1);
					json.put("error", SQL);
					
					int result=stmt.executeUpdate(SQL);
					
					if(result>0)
					{
						json.put(SUCCESS, 1);
						json.put("msg", "Successfully inserted data");
					}else
					{
						json.put(SUCCESS, -1);
						json.put("error", SQL);
					}
					stmt.close();	
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					json.put("error1", e.getMessage());
					e.printStackTrace();
				}
				finally
				{
			      try{			        	
			            if(conn!=null)
			            conn.close();
			         }catch(SQLException se){
			            se.printStackTrace();
			         }
			    }
				
				return(json);
	}
	
	public void updateLocationTag(String UID, double Longitude, double Latitude)
	{
		LocationBasedHandler object=new LocationBasedHandler();
		object.UpdateLocation(UID, Longitude, Latitude);
		object=null;
	}
	
	public void isFollowingTag(String UID, String QID, String isFollowing)
	{
		LocationBasedHandler handler=new LocationBasedHandler();
		if(isFollowing.equalsIgnoreCase("true"))
		{
			handler.FollowQuestion(UID, QID);
		}else
		{
			handler.unFollowQuestion(UID, QID);
		}
	}
}
