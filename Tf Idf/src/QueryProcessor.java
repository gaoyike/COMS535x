

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import He.IndexBuilder.InvertedIndex;
import He.IndexBuilder.PostingsPart;
//import He.PageRank.ValueForPage;

/**
 * Created by chenguanghe on 4/15/15.
 */
public class QueryProcessor {
	private String folderPath = "D:\\cs_class\\CS535x\\Homework\\project4\\pa4\\pa4";
	private File[] fileList; // to store all the filename in this folder
	private int bitPerbiword = 8;
	private BiWordDocumentFilter[] bf;
	private IndexBuilder indexB;
	
	public QueryProcessor() throws FileNotFoundException{
		indexB = new IndexBuilder(folderPath);
		indexB.buildIndex();
		fileList = indexB.getFileses();
		bf = new BiWordDocumentFilter[fileList.length];
		for(int i= 0; i<fileList.length; i++){
		bf[i] = new BiWordDocumentFilter(bitPerbiword, fileList[i].getName(), folderPath);
		bf[i].addDocument();
		}	
		
	}
	
	public void query(ArrayList<String> query, int k){
          //get top 2k cosine similar documents
          Score[] topDocs = cosinSimilar(query, 2*k);
          RankOfDoc[] rank = new RankOfDoc[2*k];
          
          //
          for(int i = 0; i < topDocs.length; i++){
        	  int r = 0;
        	  for(int j = 0; j < query.size()-1; j++){
        		  String tocheck = query.get(j)+" " + query.get(j+1);
        		   if(bf[topDocs[i].getNum()].appears(tocheck)){
        			   r++;
             	  }
        	  }
        	  rank[i] = new RankOfDoc(topDocs[i], r);
          }	
          Arrays.sort(rank, new RankOfDocComparator());
          RankOfDoc[] sortedRank = new RankOfDoc[k];
          System.out.println("The top "+ k + " document with its cosine similarity to q is :");
          for(int i = 0; i < k; i++){
        	  int temp = i+1;
        	  sortedRank[i] = rank[rank.length - 1 - i];
        	  System.out.println("the top " + temp+ " rank document is " + sortedRank[i].getScore().getName() + " , the cosine similarity is " + sortedRank[i].getScore().getScore());
          }
	}
	
	
	// this method return the top k document as a set 
	public Score[] cosinSimilar(ArrayList<String> query, int k){
		double lengthOfqSquare = 0.0;
		double[] vectorOfq = new double[query.size()];
		double[] lengthOfDoc = indexB.lengthDoc();
		
		for(int i = 0; i < query.size(); i++){
			String term = query.get(i);
			vectorOfq[i] = Math.log10((double) (indexB.numTerm()/ indexB.DFti(term)));
			lengthOfqSquare += Math.log10((double) (indexB.numTerm()/ indexB.DFti(term)))*Math.log10((double) (indexB.numTerm()/ indexB.DFti(term)));
		}
		lengthOfqSquare = Math.sqrt(lengthOfqSquare);
        Score[] score = new Score[fileList.length];
        
        
        for(int j = 0; j< fileList.length; j++){
        	double s = 0.0;
		     for(int i = 0; i<query.size(); i++){
		    	
				s += vectorOfq[i] * indexB.weigh(query.get(i), j+"")/(lengthOfqSquare*lengthOfDoc[j]);
			}
//		    System.out.println("The length of doc " + fileList[j].getName() + " is " + lengthOfDoc[j]);
		    score[j] = new Score(fileList[j].getName(), s, j); 
		}

		Arrays.sort(score, new ScoreComparator());
		Score[] res = new Score[k];
		for(int i = 0; i < k; i++){
			res[i] = score[score.length-1-i];
//			System.out.println("The top " +(i+1) + "-th score is "+ res[i].getName() + " the score is "+ res[i].getScore());
		}
		return res;
		
	}
	
	class Score{
		private String name;
		private double score;
		private int fileNum;
		Score(String fileName, double s, int num){
			name = fileName;
			score = s;
			fileNum = num;
		}
		
		public String getName(){
			return name;
		}
		public double getScore(){
			return score;
		}
		public int getNum(){
			return fileNum;
		}

		
	}
	
	class ScoreComparator implements Comparator<Score>{
		@Override
		public int compare(Score s1, Score s2){
			if(s1.score-s2.score<0)
				return -1;
			else if(s1.score == s2.score){
				return 0;
				
			}
			else return 1;
		}
	}
	
	class RankOfDoc{
		Score s;
		int rank;
		RankOfDoc(Score s1, int r){
			s = s1;
			rank = r;
		}		
		
		public Score getScore(){
			return s;
		}
	}
	
	class RankOfDocComparator implements Comparator<RankOfDoc>{
		@Override 
		public int compare(RankOfDoc s1, RankOfDoc s2){
			if(s1.rank-s2.rank <0)
				return -1;
			else if(s1.rank - s2.rank ==0){
				if(s1.s.getScore() - s2.s.getScore()<0)
					return -1;
				else if(s1.s.getScore() - s2.s.getScore()==0)
					return 0;
				else return 1;
			}
			else
				return 1;
		}
	}
	
    private String removeSymbol(String s) {
        String res = s;
        res = res.replaceAll("[^\\p{L}^\\d]", "");
        return res;
    }
    
    public String modifyString(String s) {
        s = removeSymbol(s);
        String str = s.toLowerCase();
        String[] stop = new String[]{"the"};
        for (int i = 0; i < stop.length; i++) {
            if (stop[i].equals(str))
            return null;
        }
        if (str.length() < 3)
            return null;
        return str;
    }
	
	public static void main(String[] args) throws FileNotFoundException{
		QueryProcessor qp = new QueryProcessor();
        while(true){
        	try{
        	System.out.println("please give a query:");
        	ArrayList<String> q = new ArrayList<String>();
        	Scanner scanner = new Scanner(System.in);
        	String[] strs = scanner.nextLine().split(" ");
        	for(String s : strs){
        		s = qp.modifyString(s);
        		if(s != null){
        			System.out.println(s);
        			q.add(s);
        		}
        	}
           	System.out.println("please give an integer k:");
           	int k = scanner.nextInt();
        	qp.query(q, k);
        	}catch(Exception e){
        		
        	}
        }	
	}
}
