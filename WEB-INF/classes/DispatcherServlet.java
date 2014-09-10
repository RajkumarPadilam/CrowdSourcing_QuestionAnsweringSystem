// Loading required libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.simple.JSONObject;
 
public class DispatcherServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	   String TAG="nothing";
	   static final String SUCCESS="success";
	   static final String MESSAGE="msg";
	   
	   JSONObject json=new JSONObject();
	   ProcessTag processTagObject=new ProcessTag();
	   PrintWriter out;
	   
  public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
      doPost(request, response);
	  
      
  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
  {
		 //Getting the PATH
	  ServletContext servletContext = getServletContext();
	  String contextPath = servletContext.getRealPath(File.separator);
	  Constants.PATH=contextPath+"extras"+File.separator;
	  
	  out=response.getWriter();
	  TAG=request.getParameter("TAG");
	  
	  if(TAG==null)
	  {
		  json.put(SUCCESS, 0);
		  json.put(MESSAGE, "The TAG field is not present in the request");
		  out.print(json);
	  }
	  else
	  {
		  json=dispatchTAG(TAG,request);
		  out.print(json);
	  }
  }
  
  public  JSONObject dispatchTAG(String TAG,HttpServletRequest request)
  {
	  	JSONObject object=new JSONObject();
	  	
	  	if(TAG.equals(Constants.LOGIN))
	  	{
	  		object= processTagObject.processLoginTag(request.getParameter(Constants.EMAIL), request.getParameter(Constants.PASSWORD));
	  		
	  	}
	  	else if(TAG.equals(Constants.REGISTER))
	  	{
	  		object= processTagObject.processRegisterTag(request.getParameter(Constants.USER_NAME), request.getParameter(Constants.EMAIL), request.getParameter(Constants.PASSWORD));
	  		
	  	}
	  	else if(TAG.equals(Constants.GETQUESTIONS))
	  	{
	  		int UID=Integer.parseInt(request.getParameter(Constants.USER_ID));
	  		object= processTagObject.getQuestionsTag(UID);
	  		
	  	}
	  	else if(TAG.equals(Constants.GET_CATEGORIES))
	  	{
	  		int ImageID=Integer.parseInt(request.getParameter(Constants.IMAGE_ID));
	  		object= processTagObject.getImageCategoriesTag(ImageID);
	  		
	  	}else if(TAG.equals(Constants.GET_URLFROMCATEGORIES))
	  	{
	  		int cat_id=Integer.parseInt(request.getParameter(Constants.CATEGORY_ID));
	  		object= processTagObject.getURLSForCategoriesTag(cat_id);
	  		
	  	}
	  	else if(TAG.equals(Constants.POSTANSWER))
	  	{
	  		int UID=Integer.parseInt(request.getParameter(Constants.USER_ID));
	  		int QID=Integer.parseInt(request.getParameter(Constants.QUESTION_ID));
	  		
	  		object= processTagObject.postAnswerTag(UID, QID, request.getParameter(Constants.ANSWER));  		
	  	}
	  	else if(TAG.equals(Constants.UPDATE_NOTIFICATIONS))
	  	{
	  		int UID=Integer.parseInt(request.getParameter(Constants.USER_ID));
	  		int QID=Integer.parseInt(request.getParameter(Constants.QUESTION_ID));
	  		
	  		object= processTagObject.updateNotificationTag(QID, UID);  		
	  	}
	  	else if(TAG.equals(Constants.POSTQUESTION))
	  	{
	  		int UID=Integer.parseInt(request.getParameter(Constants.USER_ID));
	  		
	  		if(request.getParameter(Constants.LONGITUDE)!=null)
	  		{
	  			float longitude=Float.parseFloat(request.getParameter(Constants.LONGITUDE));
	  			float latitude=Float.parseFloat(request.getParameter(Constants.LATITUDE));
	  			int deadline=Integer.parseInt(request.getParameter(Constants.DEADILNE));
	  			int radius=Integer.parseInt(request.getParameter(Constants.RADIUS));
	  			object=processTagObject.postQuestionTag(UID, request.getParameter(Constants.QUESTION_ASKED), longitude, latitude, deadline, radius);
	  		}	  		
	  		else
	  		object=processTagObject.postQuestionTag(UID, request.getParameter(Constants.QUESTION_ASKED));
	  	}
	  	else if(TAG.equals(Constants.GETANSWERS))
	  	{
	  		int UID=Integer.parseInt(request.getParameter(Constants.USER_ID));
	  		int QID=Integer.parseInt(request.getParameter(Constants.QUESTION_ID));
	  		
	  		object=processTagObject.getAnswersTag(UID, QID);
	  	}
	  	else if(TAG.equals(Constants.FOLLOWQUESTION))
	  	{
	  		String UID=request.getParameter(Constants.USER_ID);
	  		String QID=request.getParameter(Constants.QUESTION_ID);
	  		String isFollowing=request.getParameter(Constants.ISFOLLOWING);
	  		
	  		processTagObject.isFollowingTag(UID, QID, isFollowing);
	  		object.put(SUCCESS, 1);
	  		object.put(MESSAGE, "Location Updated");
	  	}
	  	else if(TAG.equals(Constants.GET_URLS))
	  	{
	  		object=processTagObject.getURLSTag();
	  	}
	  	else if(TAG.equals(Constants.UPDATE_RANK))
	  	{
	  		int UID=Integer.parseInt(request.getParameter(Constants.USER_ID));
	  		int QID=Integer.parseInt(request.getParameter(Constants.QUESTION_ID));
	  		int AID=Integer.parseInt(request.getParameter(Constants.ANSWER_ID));
	  		int RANK=Integer.parseInt(request.getParameter("RANK"));
	  		int state=Integer.parseInt(request.getParameter("STATE"));
	  		
	  		object=processTagObject.updateRANKTag(UID, QID, AID, RANK, state);
	  	}
	  	else if(TAG.equals(Constants.ADD_CATEGORY_ACTION))
	  	{
	  		String UID=request.getParameter(Constants.USER_ID);
	  		String ImageID=request.getParameter(Constants.IMAGE_ID);
	  		String TargetID=request.getParameter(Constants.TARGET_ID);
	  		String action=request.getParameter(Constants.ACTION);
	  		String TargetValue=request.getParameter(Constants.TARGET_VALUE);
	  		
	  		object=processTagObject.addCategoryAction(UID, action, ImageID, TargetID, TargetValue);
	  	}
	  	else if(TAG.equals(Constants.UPDATELOCATION))
	  	{
	  		double Longitude=Double.parseDouble(request.getParameter(Constants.LONGITUDE));
	  		double Latitude=Double.parseDouble(request.getParameter(Constants.LATITUDE));
	  		
	  		processTagObject.updateLocationTag(request.getParameter(Constants.USER_ID), Longitude, Latitude);
	  		object.put(SUCCESS, 1);
	  		object.put(MESSAGE, "Location Updated");
	  	}
	  	else if(TAG.equals(Constants.GET_NOTIFICATIONS))
	  	{
	  		int UID=Integer.parseInt(request.getParameter(Constants.USER_ID));
	  		object=processTagObject.getNotificationsTag(UID);
	  	}
	  	else
	  	{
	  		object.put(SUCCESS, 0);
			object.put(MESSAGE, "No Matching TAG Found");
	  	}
	  	return object;	
  	}
} 