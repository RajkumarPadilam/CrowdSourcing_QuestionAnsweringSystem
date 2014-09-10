/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.lang.Object;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;


//import static questionanswering.QuestionAnswering.QuestionAnswering;

/**
 *
 * @author mxf7595xx
 */

public class MainPart {
   
    public static String Actula_list[][]=new String[1714][6];
    public static ArrayList<String>[][] preprocessed_list= (ArrayList<String>[][]) new ArrayList [1714][6];
    public static Hashtable<String, Float> WFQ= new Hashtable<String, Float>();
    Hashtable<String, Float> QWeight= new Hashtable<String, Float>();
    public static String Path;
    
    public static void IDF()
    {
        for(int i =0;i<1714;i++)
        {
            Hashtable<String, Integer> WFQ_temp= new Hashtable<String, Integer>();
         
            
            for(String word:preprocessed_list[i][1])
            {
             if(WFQ_temp.get(word)==null)
             {
                 WFQ_temp.put(word, 1);
                 if(WFQ.get(word)==null)
                 {
                    WFQ.put(word, (float)1);
                 }
                 else
                 {
                     WFQ.put(word, WFQ.get(word)+1);
                 }
             }
            }
        }
        Iterator it = WFQ.entrySet().iterator();
         while (it.hasNext()) {
                 Map.Entry pairs = (Map.Entry)it.next();
                 String Key=(String)pairs.getKey();
                 float Value=(float)pairs.getValue();
                 
                 WFQ.put(Key, (float)Math.log10((double)1714/Value));
               
         }
    }
    
    public static int calculateWeight(String Question)
    {
       ArrayList<String> Question_word=parsingAndStemmingHani.words(Question);
       Hashtable<String,Double> Qti = new Hashtable<String,Double>();
       double sum_Q=0;    
       double max_value=-100000.0;
       int max_index=-1;
       for(String word:Question_word)
           {
               if(Qti.get(word)==null)
               {
                   Qti.put(word,1.0);
                                   
               }
               else
               {
                   Qti.put(word, Qti.get(word)+1);
               }
           }
               Iterator it = Qti.entrySet().iterator();
           
               while (it.hasNext()) {
                 Map.Entry pairs = (Map.Entry)it.next();
                 String Key=(String)pairs.getKey();
                 double Value=(double)pairs.getValue();
                 Value=1+ Math.log10(Value);
                if(WFQ.get(Key)!=null)
                    Value*=WFQ.get(Key);
                else
                    Value*=Math.log10(1714);
                 Qti.put(Key,Value);
                 sum_Q+=Math.pow(Value, 2);
                 
             }
               sum_Q=Math.sqrt(sum_Q);
               
           
       for(int i=0;i<1714;i++)
       {
           Hashtable<String,Double> Wti = new Hashtable<String,Double>();
           for(String word:preprocessed_list[i][1])
           {
               if(Wti.get(word)==null)
               {
                   Wti.put(word,1.0);
                                   
               }
               else
               {
                   Wti.put(word, Wti.get(word)+1);
               }
           }
               Iterator it1 = Wti.entrySet().iterator();
               double sum_W=0;
               while (it1.hasNext()) {
                 Map.Entry pairs = (Map.Entry)it1.next();
                 String Key=(String)pairs.getKey();
                 double Value=(double)pairs.getValue();
                 Value=1+ Math.log10(Value);
                 Value*=WFQ.get(Key);
                 Wti.put(Key,Value);
                 sum_W+=Math.pow(Value, 2);
                 
             }
             sum_W=Math.sqrt(sum_W);
             double sum_total=0;
           Iterator it2 = Qti.entrySet().iterator();
           
               while (it2.hasNext()) {
                 Map.Entry pairs = (Map.Entry)it2.next();
                 String Key=(String)pairs.getKey();
                 double Value=(double)pairs.getValue();
                 if(Wti.get(Key)!=null)
                 {
                     sum_total+=Value*Wti.get(Key);
                 }
               }
               sum_total/=(sum_Q*sum_W);
               if(max_value<sum_total)
               {
                   max_value=sum_total;
                   max_index=i;
               }
           
       }
       return max_index;
    }
        

    
    public static void parsingDataSet()
    {
        InputStream in = null;
        int line_no=0;
        try{
            in = new FileInputStream(Path+"/input.txt");
            BufferedReader reader =  new BufferedReader(new InputStreamReader(in));
            String templine;
            while( (templine = reader.readLine())!=null) {
                String[] line;
                line=templine.split("\t");
                Actula_list[line_no]=line;
                preprocessed_list[line_no][0]=parsingAndStemmingHani.words(Actula_list[line_no][0]);
                preprocessed_list[line_no][1]=parsingAndStemmingHani.words(Actula_list[line_no][1]);
                preprocessed_list[line_no][2]=parsingAndStemmingHani.words(Actula_list[line_no][2]);
                        
                line_no++;
                
            }
        }
        catch(IOException e){
          
        }
    }
    
   public static String[] HaniMethod(String Question,String path)
   {
       Path=path+"HaniData/";
       parsingDataSet();
       IDF();
       int max=calculateWeight(Question);
       String answer[]= new String[2];
       if(max==-1)
       {
           answer[0]="No Question";
           answer[1]="No Answer";
       }
       else
       {
           answer[0]= Actula_list[max][1];
           answer[1]=Actula_list[max][2];
       }
       return answer;
   }/*
// public static 
    public static void main(String[] args) {
        
    Scanner input = new Scanner(System.in);
    String inp=input.nextLine();
    String Queston = "Was Lorenzo Romano Amedeo Carlo Avogadro an Italian savant?";
    String answer[]= MainPart.HaniMethod(Queston,"C:/courses/Chengkai/QuestionAnswering/hanidata");
    System.out.println(answer[0]);
    System.out.println(answer[1]);
    
    
    }*/
    
}
