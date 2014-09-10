// Abolfazl Asudeh: a.asudeh@gmail.com
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

public class LocationBasedHandler {
	private String dbURL = "jdbc:mysql://dbinstance.c8kmfn2haf9k.us-east-1.rds.amazonaws.com";
	private String username = "developer";
	private String password = "KEVaFeF7HA";
	private Double EarthRad = 6378.137;
	
	private Double dist(Double lon1,Double lat1, Double lon2, Double lat2)
	{
		Double dLat = (lat2 - lat1) * Math.PI / 180;
	    Double dLon = (lon2 - lon1) * Math.PI / 180;
	    Double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
	    Math.sin(dLon/2) * Math.sin(dLon/2);
	    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    Double d = EarthRad * c;
	    return d * 1000;
	}
	
	private ArrayList<String> FindResponders (Double longitude, Double latitude, int Radius/*in meter*/)
	{
		//convert radius to points
		Double Rad = Radius*180/(Math.PI*EarthRad*1000);
		ArrayList<String> output = new ArrayList<String>();
		java.util.Date date2 = new java.util.Date();
		date2.setMinutes(date2.getMinutes()-5);
		date2.setSeconds(0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(date2);
		//establish the connection
		try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
			
			String sql = "SELECT USER_ID,Longitude,Latitude FROM UTA.LOCATION where Longitude < "+ Double.toString(longitude+Rad)+" and Longitude> "+ Double.toString(longitude-Rad)+" and Latitude< "+Double.toString(latitude+Rad)+" and Latitude> "+Double.toString(latitude-Rad)+" and LocUpdateDate > '"+ date+"'";
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String userid = rs.getString("USER_ID");
				Double lon2 = rs.getDouble("Longitude");
				Double lat2 = rs.getDouble("Latitude");
				Double dist2 = dist(longitude, latitude, lon2, lat2);
				if(dist2<Radius)
					output.add(userid);
			}
			statement.close();
			conn.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return output;
		
	}
	
	private void notify(String UID, String QID,int type)
	{
		try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
			String sql = "INSERT INTO UTA.NOTIFICATION(USER_ID,QUESTION_ID,postedByTo) Values("+UID+","+QID+","+Integer.toString(type)+")";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			statement.close();
			if(type==0) // notify the question followers
			{
				sql = "SELECT UID FROM UTA.QFOLLOWERS WHERE QID="+QID;
				statement = conn.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();
				statement.close();
				
				sql = "INSERT INTO UTA.NOTIFICATION(USER_ID,QUESTION_ID,postedByTo) Values";
				while(rs.next())
					sql+="("+rs.getString("UID")+","+QID+",0),";
				
				if(sql!="INSERT INTO UTA.NOTIFICATION(USER_ID,QUESTION_ID,postedByTo) Values")
				{
					sql = sql.substring(0, sql.length()-1); // remove the last ',' from the query
					statement = conn.prepareStatement(sql);
					statement.executeUpdate();
					statement.close();
				}
			}
			conn.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private String InsertNewQuestion(String UID, String Question, Double longitude, Double latitude, int deadline)
	{
		String output = "Error";
		try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
			String sql = "insert into UTA.QUESTIONS (USER_ID,Question_Asked,Longitude,Latitude,Deadline) values("+UID+",'"+Question+"',"+Double.toString(longitude)+","+ Double.toString(latitude)+","+Integer.toString(deadline)+")";
			PreparedStatement statement = conn.prepareStatement(sql);
			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				//System.out.println("A new user was inserted successfully!");
				statement.close();
				sql = "SELECT QUESTION_ID FROM UTA.QUESTIONS WHERE USER_ID="+UID+" AND Question_Asked='"+Question+"' AND Longitude="+Double.toString(longitude)+" AND Latitude="+ Double.toString(latitude)+" AND Deadline="+Integer.toString(deadline);
				statement = conn.prepareStatement(sql);
				ResultSet rs = statement.executeQuery();
				while (rs.next()) 
					output = rs.getString("QUESTION_ID");
			}
			statement.close();
			conn.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return output;
	}
	
	public void NewQuestion(String UID, String Question, Double longitude, Double latitude, int Radius/*in meter*/, int deadline/*in minutes*/)
	{
		String QID = InsertNewQuestion(UID, Question, longitude, latitude, deadline);
		ArrayList<String> responders = FindResponders (longitude, latitude, Radius);
		for(int i=0;i<responders.size();i++)
		notify(responders.get(i),QID,1);
	}
	
	/*public void NewQuestion(String QID, Double longitude, Double latitude, int Radiusin meter)
	{
		//String QID = InsertNewQuestion(UID, Question, longitude, latitude, Radius);
		ArrayList<String> responders = FindResponders (longitude, latitude, Radius);
		for(int i=0;i<responders.size();i++)
			notify(responders.get(i),QID,1);
	}*/
	
	public void UpdateLocation(String UID, Double Longitude, Double Latitude)
	{
		try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(new java.util.Date());
			String sql = "SELECT User_ID FROM UTA.Location where User_ID="+UID;
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			statement.close();
			if(!rs.next())
			{
				//INSERT
				sql="INSERT INTO UTA.LOCATION(USER_ID,LONGITUDE,LATUTUDE,DATE) VALUES("+UID+","+Double.toString(Longitude)+","+Double.toString(Latitude)+",'"+date+"')";
			}
			else
			{
				// UPDATE
				sql="UPDATE UTA.LOCATION SET LONGITUDE="+Double.toString(Longitude)+",LATUTUDE="+Double.toString(Latitude)+",DATE='"+date+"' WHERE  USER_ID="+UID;
			}
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			statement.close();
			conn.close();
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public int NewResponse(String UID,String QID,String Response)
	{		
		try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
			// check the question deadline
			String  sql = "SELECT POSTDATE,DEADLINE FROM UTA.QUESTIONS WHERE QUESTION_ID="+QID;
			PreparedStatement  statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			if(!rs.next()) return 0; // there is no such question 
			String qdate= rs.getString("POSTDATE");
			int deadline = rs.getInt("DEADLINE");
			statement.close();
			java.util.Date date2 = new java.util.Date();
			date2.setMinutes(date2.getMinutes()-deadline);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sdf.format(date2);
			
			//COMMENTED CODE..NEED TO VERIFY WITH ABOL-----------------------------------------------------------
//			if(deadline!=0)
	//			if(date.compareTo(qdate) < 0) return 0; //the answer is posted after the deadline			
				
				//Update the DB
				sql = "INSERT INTO UTA.ANSWERS(USER_ID,QUESTION_ID,ANSWER) VALUES("+UID+","+QID+",'"+Response+"')";
				statement = conn.prepareStatement(sql);
				statement.executeUpdate();
				statement.close();

			//Add the id of questioner
			sql = "SELECT USER_ID FROM UTA.QUESTIONS WHERE QUESTION_ID="+QID;
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			String questioner="";
			while (rs.next()) 
				questioner = rs.getString("USER_ID");
			statement.close();
			conn.close();
			//Add the notification
			notify(questioner,QID,0);			
			
		} catch (SQLException ex) {
			ex.printStackTrace();
			return 0;
		}
		return 1;
		
	}

	public void FollowQuestion(String UID, String QID)
	{
		try (Connection conn = DriverManager.getConnection(dbURL, username, password))
		{
			//Update the DB
			String sql = "INSERT INTO UTA.QFOLLOWERS(UID,QID) VALUES("+UID+","+QID+")";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			statement.close();
			//Add the id of questioner
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void unFollowQuestion(String UID, String QID)
	{
		try (Connection conn = DriverManager.getConnection(dbURL, username, password))
		{
			//Update the DB
			String sql = "DELETE UTA.QFOLLOWERS WHERE UID="+UID+" AND QID="+QID+")";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			statement.close();
			//Add the id of questioner
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public ArrayList<String> similarQuestions(Double longitude, Double latitude)
	{
		int Radius = 1;//meter
		Double Rad = Radius*180/(Math.PI*EarthRad*1000);
		ArrayList<String> output = new ArrayList<String>();
		java.util.Date date2 = new java.util.Date();
		date2.setMinutes(date2.getMinutes()-5);
		date2.setSeconds(0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(date2);
		//establish the connection
		try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
			
			String sql = "SELECT QUESTION_ID,QUESTION_ASKED from UTA.QUESTIONS where Longitude < "+ Double.toString(longitude+Rad)+" and Longitude> "+ Double.toString(longitude-Rad)+" and Latitude< "+Double.toString(longitude+Rad)+" and Latitude> "+Double.toString(longitude-Rad)+" and PostDate > '"+ date+"'";
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String st = rs.getString("QUESTION_ID") + "$"+rs.getString("QUESTION_ASKED");
				output.add(st);
			}
			statement.close();
			conn.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return output;
		
	}

	public void Q_Vote(String QID, String UID, int vote) // vote can be 0 or 1
	{
		//check if this user have previously voted for this question
		try (Connection conn = DriverManager.getConnection(dbURL, username, password))
		{
			//Update the DB
			String sql = "SELECT ID FROM UTA.QUESTION_VOTE WHERE UID="+UID+" AND QID="+QID;
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			
			if(rs.next())
			{
				statement.close();
				sql = "UPDATE UTA.QUESTION_VOTE SET VOTE="+Integer.toString(vote)+" WHERE ID="+rs.getString("ID");
				statement = conn.prepareStatement(sql);
				statement.executeUpdate();
			}
			else
			{
				statement.close();
				sql = "INSERT INTO UTA.QUESTION_VOTE(UID,QID,VOTE) VALUES("+UID+","+QID+","+Integer.toString(vote)+")";
				statement = conn.prepareStatement(sql);
				statement.executeUpdate();
			}
			statement.close();
			conn.close();
			//Add the id of questioner
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
	}
	
	public void A_Vote(String AID, String UID, int vote) // vote can be 0 or 1
	{
		//check if this user have previously voted for this question
		int __c__=0;
		int currentVote;
		try (Connection conn = DriverManager.getConnection(dbURL, username, password))
		{
			//Update the DB
			String sql = "SELECT ID,VOTE FROM UTA.ANSWER_VOTE WHERE UID="+UID+" AND AID="+AID;
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			
			if(rs.next())
			{
				currentVote = rs.getInt("VOTE");
				__c__ = 1;
				if(vote!=currentVote)
				{
					
					__c__=2;
					sql = "UPDATE UTA.ANSWER_VOTE SET VOTE="+Integer.toString(vote)+" WHERE ID="+rs.getString("ID");
					statement.close();
					statement = conn.prepareStatement(sql);
					statement.executeUpdate();
				}
			}
			else
			{
				
				sql = "INSERT INTO UTA.ANSWER_VOTE(UID,AID,VOTE) VALUES("+UID+","+AID+","+Integer.toString(vote)+")";
				statement.close();
				statement = conn.prepareStatement(sql);
				statement.executeUpdate();
			}
			statement.close();
			
			// Find the user_id of the person who asked the question
			String sql2="SELECT USER_ID FROM UTA.ANSWERS WHERE ANSWER_ID="+AID;
			statement = conn.prepareStatement(sql2);
			rs = statement.executeQuery();
			if(!rs.next()) return;			
			String UID2 = rs.getString("USER_ID");
			statement.close();
			
			// update the answer rank and user credit
			if(__c__ != 1) // the rank has changed
			{
				if(vote ==1) // increment the rank
				{
					if(__c__ == 2)
					{
						sql="UPDATE UTA.ANSWERS SET RANK=RANK+2 WHERE ANSWER_ID="+AID;	
						sql2 = "UPDATE UTA.USERProfile SET CREDIT=CREDIT+2 WHERE USER_ID="+UID2;
					}
					else
					{
						sql="UPDATE UTA.ANSWERS SET RANK=RANK+1 WHERE ANSWER_ID="+AID;	
						sql2 = "UPDATE UTA.USERProfile SET CREDIT=CREDIT+1 WHERE USER_ID="+UID2;
					}
				}
				else // decrement the rank
				{
					if(__c__ == 2)
					{
						sql="UPDATE UTA.ANSWERS SET RANK=RANK-2 WHERE ANSWER_ID="+AID;	
						sql2 = "UPDATE UTA.USERProfile SET CREDIT=CREDIT-2 WHERE USER_ID="+UID2;
					}
					else
					{
						sql="UPDATE UTA.ANSWERS SET RANK=RANK-1 WHERE ANSWER_ID="+AID;	
						sql2 = "UPDATE UTA.USER_CREDIT SET CREDIT=CREDIT-1 WHERE USER_ID="+UID2;
					}
				}
				statement = conn.prepareStatement(sql);
				statement.executeUpdate();
				statement.close();
				
				//statement = conn.prepareStatement(sql2);
				//statement.executeUpdate();
				//statement.close();
			}
			conn.close();
			//Add the id of questioner
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
	}
}
