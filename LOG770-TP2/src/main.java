import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Jama.Matrix;



public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<Point2D.Double> dataset = getDataset(readData("dataset.txt"));
		
		ArrayList<Point2D.Double>[] erreursNombre = testerNombreDataTest(dataset);
		generateChart("chart1", 
					"Erreurs en fonction du nombre de données d'entraînement", 
					"Nombre de données d'entraînement",
					erreursNombre[0], 
					erreursNombre[1]);
		
		ArrayList<Point2D.Double>[] erreursRegression = testerOrdreRegression(dataset);
		generateChart("chart2", 
					"Erreurs en fonction de l'ordre de régression", 
					"Ordre du polynôme de régression",
					erreursRegression[0], 
					erreursRegression[1]);
		
		 
	        
	}
	
	public static void generateChart(String fileName, String chartTitle, String Xaxis, 
			ArrayList<Point2D.Double> erreurEmpiriqueData, ArrayList<Point2D.Double> erreurGeneralisationData){
		
		
		final XYSeries empiriqueSeries = new XYSeries("Erreur empirique");
		for(int i=0; i<erreurEmpiriqueData.size(); i++){
			empiriqueSeries.add(
					new XYDataItem(erreurEmpiriqueData.get(i).getX(),erreurEmpiriqueData.get(i).getY()));
		}
		
		final XYSeries generalisationSeries = new XYSeries("Erreur généralisation");
		for(int i=0; i<erreurEmpiriqueData.size(); i++){
			generalisationSeries.add(erreurGeneralisationData.get(i).getX(),erreurGeneralisationData.get(i).getY());
		}
        
		final XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(generalisationSeries);
		data.addSeries(empiriqueSeries);
		
        final JFreeChart chart = ChartFactory.createXYLineChart(
            chartTitle,
            Xaxis, 
            "Erreur (RMS)", 
            data,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        
        
        
        try {
			ChartUtilities.saveChartAsJPEG(new File("C:charts/"+fileName+".jpg"), chart, 500, 300);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/***
	 *  3. Influence du nombre de données d'entraînement
	 * @param dataset: Les 100 données lues du fichier
	 */
	public static ArrayList[] testerNombreDataTest(ArrayList<Point2D.Double> dataset){

		System.out.println(" -------------------------------------------------------");
		System.out.println(" PARTIE 3. Influence du nombre de données d'entraînement");
		System.out.println(" -------------------------------------------------------");
		
		ArrayList<Point2D.Double> dataTest = getExemples(dataset,80,20);
		
		ArrayList[] erreurs = {
				new ArrayList<Point2D.Double>(),
				new ArrayList<Point2D.Double>()
		};
		
		for(int i=10; i<=80;i=i+5){
			ArrayList<Point2D.Double> dataTrain = getExemples(dataset,0,i);
			Matrix m = createMatriceVandermonde(dataTrain,3);
			Matrix y = createY(dataTrain);
			Matrix poids = calculerPoids(m,y);
			
			double erreurE = getErreur(poids, dataTrain);			
			System.out.println("Erreur Empirique: (" + i + " exemples d'entraînement) : "+ erreurE);
			
			double erreurG = getErreur(poids, dataTest);
			System.out.println("Erreur  de Généralisation: (" + i + " exemples d'entraînement) : "+ erreurG);
			
			erreurs[0].add(new Point2D.Double(i,erreurE));
			erreurs[1].add(new Point2D.Double(i,erreurG));
	
		}
		return erreurs;
	}
	
	/***
	 * 4. Influence de l'ordre de régression
	 * @param dataset: Les 100 données lues du fichier
	 */
	public static ArrayList[] testerOrdreRegression(ArrayList<Point2D.Double> dataset){

		System.out.println(" --------------------------------------------");
		System.out.println(" PARTIE 4. Influence de l'ordre de régression");
		System.out.println(" --------------------------------------------");
		
		ArrayList<Point2D.Double> dataTrain = getExemples(dataset,0,60);
		ArrayList<Point2D.Double> dataValid = getExemples(dataset,60,20);
		ArrayList<Point2D.Double> dataTest = getExemples(dataset,80,20);
		
		ArrayList[] erreurs = {
				new ArrayList<Point2D.Double>(),
				new ArrayList<Point2D.Double>()
		};
		
		double minErreur = 100;
		int bestOrdre = 0;
		for(int degre=1;degre<=19;degre++){
			
			Matrix m = createMatriceVandermonde(dataTrain,degre);
			Matrix y = createY(dataTrain);
			Matrix poids = calculerPoids(m,y);
			double erreurE = getErreur(poids, dataTrain);
			System.out.println("Erreur Empirique: (Polynome de degré " + degre + ") : "+ erreurE);
			
			double erreurG = getErreur(poids, dataValid);
			System.out.println("Erreur  de Généralisation: (Polynome de degré " + degre + ") : "+ erreurG);
			if(erreurG<minErreur)
			{
				minErreur = erreurG;
				bestOrdre = degre+1;
			}
			
			erreurs[0].add(new Point2D.Double(degre+1,erreurE));
			erreurs[1].add(new Point2D.Double(degre+1,erreurG));
		}
		
		System.out.println("Le meilleur degre: "+ bestOrdre);
		System.out.println(" Avec une erreur de généralisation de: "+ minErreur + "  (sur dataValid)");
		
		// Ici on refait la regression sur Train avec bestDegre
		// On évalue erreur de généralisation sur Test
		// On compare ErreurG sur Test avec erreurG sur Valid
		Matrix m = createMatriceVandermonde(dataTrain,bestOrdre);
		Matrix y = createY(dataTrain);
		Matrix poids = calculerPoids(m,y);

		double erreurG = getErreur(poids, dataTest);
		System.out.println(" Avec une erreur de généralisation de: "+ erreurG + "  (sur dataTest)");
		

		return erreurs;
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
