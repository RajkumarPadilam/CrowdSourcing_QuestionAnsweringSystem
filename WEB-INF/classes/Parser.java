import java.util.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.ResultSet;

public class Parser {
	
	private String dbURL = "jdbc:mysql://dbinstance.c8kmfn2haf9k.us-east-1.rds.amazonaws.com";
	private String username = "developer";
	private String password = "KEVaFeF7HA";
	
	public String parseQuestionAnswer(char type, int id, String text, String path){
		String returnValue = "Initial value from parseQA";
		String questionVectorsPath = path + "questionVectors/";
		String userVectorsPath = path + "userVectors/";
		String documentVectorFilePath = "";
		File documentVector = null;
		String stopwordsFileLocation = path;
	
		try{
			String filePath = "";
			if(type == 'q'){
				filePath = questionVectorsPath + "Question" + Integer.toString(id)+".csv";
			}
			if(type == 'a' || type == 'd'){
				filePath = userVectorsPath + "User" + Integer.toString(id)+".csv";
			}
	    	
			File file = new File(filePath);
	    	String[] strWords = new String[5000];
	    	double[] wordCount = new double[5000];
	    	double tfValue = 0.0;
	    	double idfValue = 0.0;
	    	double[] tfidfValue = new double[5000];
	    	String existingWord = "";
	    	StringBuilder contentToWrite = new StringBuilder();
	    	StringBuilder dfcontentToWrite = new StringBuilder();

	    	
	    	documentVectorFilePath = path + "documentFrequency.csv";
			documentVector = new File(documentVectorFilePath);
			if(!documentVector.exists()){
				documentVector.createNewFile();
				returnValue = returnValue + " Created new document frequency vector file.";
    		}
    		BufferedReader dfReader = new BufferedReader(new FileReader(documentVector));
    		int d = 0;
    		String dfexistingWord = "";
    		String[] dfstrWords = new String[50000];
    		double[] dfCount = new double[50000];
    		int dfwordFound = 0;
    		while((dfexistingWord = dfReader.readLine()) != null){
     			dfstrWords[d] = dfexistingWord.split(",")[0];
    			dfCount[d] = Double.parseDouble(dfexistingWord.split(",")[1]);
    			d++;
    		}	
    		dfReader.close();

    		
			
    		if(!file.exists()){
    			boolean bool = file.createNewFile();
				returnValue = returnValue + " Created new file at " + filePath + " : " + bool;
    		}

    		BufferedReader reader = new BufferedReader(new FileReader(filePath));
    		int i = 0;
    		while((existingWord = reader.readLine()) != null){
     			strWords[i] = existingWord.split(",")[0];
    			wordCount[i] = Double.parseDouble(existingWord.split(",")[1]);
    			i++;
    		}
    		reader.close();
    		
    		FileWriter writer = new FileWriter(filePath); 
    		FileWriter dfwriter = new FileWriter(documentVectorFilePath);
    		
	    	for(String word : words(text, stopwordsFileLocation)){
		    	int wordFound = 0;
		    	for(i = 0; i < 5000 && strWords[i] != null; i++){
		    		if(word.equals(strWords[i])){
		    			if(type == 'q' || type == 'a'){				//If this is new question or answer, increase term frequency.
		    				wordCount[i] += 1;
		    			}
		    			if(type == 'd'){							//If this is downvote, decrease term frequency.
		    				wordCount[i] -= 1;
		    			}
		    			wordFound = 1;
		    		}
		    	}
		    	
		    	if(wordFound == 0){									//This case will never occur for downvote
		    		for(i = 0; i < 5000; i++){
			    		if(strWords[i] == null){
			    			strWords[i] = word;
			    			wordCount[i] = 1;
			    			break;
			    		}
			    	}
		    	}
		    	
		    	tfValue = wordCount[i];
		    	
		    	if(type == 'a' || type == 'd'){
		    		for(d = 0; d < 50000 && dfstrWords[d] != null; d++){
			    		if(word.equals(dfstrWords[d])){
			    			dfCount[d] += 1;
			    			dfwordFound = 1;
			    			break;
			    		}
			    	}
			    	if(dfwordFound == 0){
			    		for(d = 0; d < 50000; d++){
				    		if(dfstrWords[d] == null){
				    			dfstrWords[d] = word;
				    			dfCount[d] = 1;
				    			break;
				    		}
				    	}
			    	}
			    	
			    	if(dfstrWords[d] != null)
			    		idfValue = 1 / dfCount[d];
			    	else
			    		idfValue = 1;
		    	}
		    	
		    	if(type == 'q'){
		    		for(i = 0; i < 5000; i++){
		    			tfidfValue[i] = wordCount[i];
		    		}
		    	}
		    	if(type == 'a' || type == 'd'){
		    		for(i = 0; i < 5000; i++){
		    			tfidfValue[i] = wordCount[i] * idfValue;
		    		}
		    	}
    		}

	    	for(i = 0; i < 5000 && strWords[i] != null; i++){
	    		contentToWrite.append(strWords[i]);
		    	contentToWrite.append(",");
		    	contentToWrite.append(tfidfValue[i]);
		    	contentToWrite.append("\n");
	    	}
	    	writer.append(contentToWrite.toString());
	    	writer.flush();
	    	writer.close();
	    	
	    	for(d = 0; d < 50000 && dfstrWords[d] != null; d++){
	    		dfcontentToWrite.append(dfstrWords[d]);
		    	dfcontentToWrite.append(",");
		    	dfcontentToWrite.append(dfCount[d]);
		    	dfcontentToWrite.append("\n");
	    	}	    	
	    	dfwriter.append(dfcontentToWrite.toString());
	    	dfwriter.flush();	
	    	dfwriter.close();
	    }
	    catch (IOException e) {
			e.printStackTrace();
		}
		
		if(type == 'q'){
			returnValue = this.matchVectors(id, questionVectorsPath + "Question" + id + ".csv", userVectorsPath);
		}
		return returnValue;
	}
	
	
	public void modifyRank(int change, int answerid, int id, String path){
		String answer = "";
		try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
			String sql = "SELECT answer FROM UTA.ANSWERS WHERE answerid = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, answerid);
			ResultSet rs = statement.executeQuery(sql);
			while(rs.next()){
				answer = rs.getString("answer");
			}
			statement.close();
			conn.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(change == 1){				//Upvote
			this.parseQuestionAnswer('a', id, answer, path);
		}
		else{
			this.parseQuestionAnswer('d', id, answer, path);
		}
	}
	
	
	static HashSet<String> stopwords = new HashSet<String>();
	
	
	private void addStopwords(String stopwordsFileLocation){
		try{
			BufferedReader br = new BufferedReader(new FileReader(stopwordsFileLocation + "stopwords.txt"));
			while(br.ready()){
				stopwords.add(br.readLine());
			}
			br.close();
		}
		catch(Exception e){System.out.println(e);}
	}
	
	
	private ArrayList<String> words(String line, String stopwordsFileLocation){
		if(stopwords.size() == 0)
			addStopwords(stopwordsFileLocation);
		ArrayList<String> result = new ArrayList<String>();
 
		String[] words = line.split("[ \t\n,\\.\"!?$~()\\[\\]\\{\\}:;/\\\\<>+=%*]");
		for(int i=0; i < words.length; i++){
			if(words[i] != null && !words[i].equals("")){
				String word = words[i].toLowerCase();
				if(!stopwords.contains(word)){
					result.add(Stemmer.stem(word));
				}
			}
		}
		return result;
	}
	

	private String matchVectors(int qid, String questionVectorPath, String userVectorsPath) {
		String returnValue = "Initial value form matchVectors";
		int numberOfUsers = new File(userVectorsPath).list().length;
		if(numberOfUsers == 0){
			return "No user vectors yet at the location " + userVectorsPath;
		}
		double[] cosineSimilarities = new double[5000];
		String queDmsn = "";
		String[] queWord = new String[5000];
		double[] queWordCount = new double[5000];
		int i, j;
		String userDmsn = "";
		String userWord = "";
		double userWordCount = 0;
		int howManyUsers = 3;
		
		try{
			BufferedReader reader = new BufferedReader(new FileReader(questionVectorPath));
			for(i = 0; (queDmsn = reader.readLine()) != null; i++){
     			queWord[i] = queDmsn.split(",")[0];
     			queWordCount[i] = Double.parseDouble(queDmsn.split(",")[1]);
    		}
    		reader.close();
  
    		File[] userVectorFiles = new File(userVectorsPath).listFiles();
    		if(userVectorFiles != null){
    			for(File userVector : userVectorFiles){
    				BufferedReader userReader = new BufferedReader(new FileReader(userVector));
    				while((userDmsn = userReader.readLine()) != null){
             			userWord = userDmsn.split(",")[0];
             			userWordCount = Double.parseDouble(userDmsn.split(",")[1]);
             			String fileName = userVector.getName();
             			j = Integer.parseInt(fileName.substring(4, fileName.indexOf('.')));
             			for(i = 0;  i < 5000 && queWord[i] != null; i++){
             				if(userWord.equals(queWord[i])){
             					cosineSimilarities[j] += userWordCount * queWordCount[i];
             				}
             			}
            		}
        			userReader.close();
    			}
    		}
    		
    		for(j = 1; j <= howManyUsers; j++){
	    		int maxIndex = 0;
	    		for (i = 0; i < cosineSimilarities.length; i++){
	    			if(cosineSimilarities[i] != 0){
		    			double newnumber = cosineSimilarities[i];
		    			if ((newnumber > cosineSimilarities[maxIndex])){
		    			   maxIndex = i;
		    			}
	    			}
	    		}

	    		if(cosineSimilarities[maxIndex] == 0){			//If similarity score is 0, don't notify any more users.
	    			break;
	    		}
	    		else{
	    			returnValue = notify(Integer.toString(maxIndex), Integer.toString(qid), 1);
	    			cosineSimilarities[maxIndex] = -1;
	    		}
    		}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return returnValue;
	}

	
	private String notify(String UID, String QID,int type)
	{
		String returnValue = "";
		returnValue = "Failed to notify users!";
		try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
			String sql = "INSERT INTO UTA.NOTIFICATION(USER_ID,QUESTION_ID,postedByTo) Values("+UID+","+QID+","+Integer.toString(type)+")";
			PreparedStatement statement = conn.prepareStatement(sql);
			if(statement.executeUpdate() > 0){
				returnValue = returnValue + "    Notified user " + UID + " successfully.";
			}
			statement.close();
			conn.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		//System.out.println("Notified user " + UID + " for question " + QID + " successfully.");
		return returnValue;
	}
	
}
