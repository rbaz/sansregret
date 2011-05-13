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
		ArrayList<Point2D.Double> dataset = getDataset(readData("dataset.txt"));
		ArrayList<Point2D.Double> exemples = getExemples(dataset,0,50);
		
		Matrix m = createMatriceVandermonde(exemples,3);
		Matrix y = createY(exemples);
		Matrix poids = calculerPoids(m,y);
		poids.print(5, 5);
		
		
		double erreur = getErreur(poids, dataset);
		System.out.println("Erreur: " + erreur);

	}
	

	/**
	 * Créer la matrice de Vandermonde
	 * @param dataset
	 * @param nbExemples
	 * @param p : Complexité
	 */
	public static Matrix createMatriceVandermonde(ArrayList<Point2D.Double> exemples, int p){
		Matrix m = new Matrix(exemples.size(),p+1);
		
		for(int i=0; i<exemples.size(); i++){
			m.set(i, 0, 1);
			for(int j=1; j<=p; j++){
				double x = exemples.get(i).x;
				m.set(i, j, Math.pow(x,j));
			}
		}
		return m;
	}
	public static Matrix createY(ArrayList<Point2D.Double> exemples){
		Matrix m = new Matrix(exemples.size(), 1);
		for(int i=0; i<exemples.size();i++){
			m.set(i, 0, exemples.get(i).y);
		}
		return m;
	}
	
	public static Matrix calculerPoids(Matrix m, Matrix y){
		Matrix mT = m.transpose();
		Matrix w = mT.times(m).inverse().times(mT).times(y);
		
		return w;
		
	}
	
	public static ArrayList<Point2D.Double> getExemples(ArrayList<Point2D.Double> dataset, int startIndex, int nbExemples){
		ArrayList<Point2D.Double> exemples=new ArrayList<Point2D.Double>();
		for(int i=0; i< nbExemples; i++){
			exemples.add(new Point2D.Double(dataset.get(startIndex+i).x,
					dataset.get(startIndex+i).y));
		}
		return exemples;
	}

	public static ArrayList<Point2D.Double> getDataset(String data){
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
		final int degreMax = poids.getRowDimension();
		
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
