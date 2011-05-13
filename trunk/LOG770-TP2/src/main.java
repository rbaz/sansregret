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
		ArrayList<Point2D.Double> dataset = getexemples(readData("dataset.txt"));
		
		Matrix m = createMatriceVandermonde(dataset,10,3);
		
		m.print(5,5);
		
		//System.out.println(exemples.toString());
		


	}
	
	public static  void partie1(ArrayList<Point2D.Double> exemples){
		for(int nbExemples=10; nbExemples <= 80; nbExemples=nbExemples+5){
			
			Matrix m = new Matrix(4,nbExemples);
			
		
		}
	}
	

	/**
	 * Créer la matrice de Vandermonde
	 * @param dataset
	 * @param nbExemples
	 * @param p : Complexité
	 */
	public static Matrix createMatriceVandermonde(ArrayList<Point2D.Double> dataset, int nbExemples, int p){
		Matrix m = new Matrix(nbExemples,p+1);
		
		for(int i=0; i<nbExemples; i++){
			m.set(i, 0, 1);
			for(int j=1; j<=p; j++){
				double x = dataset.get(i).x;
				m.set(i, j, Math.pow(x,j));
			}
		}
		return m;
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

	
	/***
	 *  Calculer l'erreur empirique ou l'erreur de généralisation
	 * @param poids : Un vecteur de poids calculés selon w=(X' * X)^-1 * X' * y
	 * @param dataSet: Les données d'entraînement ou les données de test
	 * @return 	L'erreur empirique si on fournit les données d'entraînement;
	 * 			L'erreur de généralisation si on fournit les données de test.
	 */
	public static double getErreur(Matrix poids, ArrayList<Point2D.Double> dataSet)
	{
		double erreur = 0;
		final int size = dataSet.size();
		final int degreMax = poids.length;
		
		double sum = 0;
		//index du x dans le dataset
		for(int i=0;i<size;i++){
			
			double x = dataSet.get(i).getX();
			double y = dataSet.get(i).getY();
			double val = 0;
			//degré du polynome
			for(int j=0;j<degreMax;j++){
				val += poids.get(j,0) * Math.pow(x,j);
			}
			sum += Math.pow((val - y),2);
		}
		
		erreur = Math.sqrt(sum/size);
		
		return erreur;
	}
}
