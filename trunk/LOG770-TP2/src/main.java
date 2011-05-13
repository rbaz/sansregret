import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import Jama.Matrix;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<Point2D.Double> exemples = getexemples(readData("dataset.txt"));
		
		System.out.println(exemples.toString());
		

	}
	
	public static  void partie1(ArrayList<Point2D.Double> exemples){
		for(int nbExemples=10; nbExemples <= 80; nbExemples=nbExemples+5){
			
			Matrix m = new Matrix(4,80);
		}
	}
	public static ArrayList<Point2D.Double> getexemples(String data){
		ArrayList<Point2D.Double> exemplesDouble= new ArrayList<Point2D.Double>();
		String[] exemplesString = data.split(" ");
		for(int i=0; (i+1) < exemplesString.length; i=i+2){
			Point2D.Double p = new Point2D.Double(Double.valueOf(exemplesString[i]), 
					Double.valueOf(exemplesString[i+1]));
			exemplesDouble.add(p);
		}
		return exemplesDouble;
	}
	
	public static String readData(String fileName){
		StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = null;
		try 
		{
			reader = new BufferedReader(new FileReader(fileName));
			char[] buf = new char[1024];
			int numRead=0;
			while((numRead=reader.read(buf)) != -1){
			    String readData = String.valueOf(buf, 0, numRead);
			    fileData.append(readData);
			    buf = new char[1024];
			}
			reader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileData.toString();
	}

}
