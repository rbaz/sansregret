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
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Jama.Matrix;

/****
 * 		�cole de Technologie Sup�rieure
 * 
 * 		@author Francis Par�  	& 	Jonathan Aubuchon
 *		   		PARF21038708	&	AUBJ18038704
 *
 *		Laboratoire #2 du cours LOG770	
 *
 *		Influence du nombre de donn�es d'entra�nement
 *		&
 *		Influence de l'ordre de r�gression
 */


public class main {

	private final static String FILE_CHART1 = "chart1";
	private final static String FILE_CHART2 = "chart2";
	private final static String TITLE_CHART1 = "Erreurs en fonction du nombre de donn�es d'entra�nement";
	private final static String TITLE_CHART2 = "Erreurs en fonction de l'ordre de r�gression";
	private final static String AXE_X_CHART1 = "Nombre de donn�es d'entra�nement";
	private final static String AXE_X_CHART2 = "Ordre du polyn�me de r�gression";


	/**
	 * main()
	 * @param args: none
	 */
	public static void main(String[] args) {
		//R�cup�rer les points � utiliser
		ArrayList<Point2D.Double> dataset = getDataset(readData("dataset.txt"));
		
		//Calculer les erreurs empiriques et de g�n�ralisation pour la Partie 3
		ArrayList<Point2D.Double>[] erreursNombre = testerNombreDataTest(dataset);
		generateChart(FILE_CHART1, 
					TITLE_CHART1, 
					AXE_X_CHART1,
					erreursNombre[0], 
					erreursNombre[1]);
		
		//Calculer les erreurs empiriques et de g�n�ralisation pour la Partie 4
		ArrayList<Point2D.Double>[] erreursRegression = testerOrdreRegression(dataset);
		generateChart(FILE_CHART2, 
					TITLE_CHART2, 
					AXE_X_CHART2,
					erreursRegression[0], 
					erreursRegression[1]);

		// Simplement g�n�rer la courbe de r�gression d'ordre 6 
		// et l'afficher avec les donn�es utilis�es (train/valid/test)
		generateModel(dataset);
	}
	


	/**
	 * Permet de g�n�rer les graphiques comparant l'erreur empirique � l'erreur de g�n�ralisation
	 * @param fileName : Le nom du fichier image
	 * @param chartTitle: Le titre du graphique
	 * @param Xaxis: Le libel� pour l'axe des X
	 * @param erreurEmpiriqueData : Les points associ�s aux erreurs empiriques
	 * @param erreurGeneralisationData :Les points associ�s aux erreurs de g�n�ralisation
	 */
	public static void generateChart(String fileName, String chartTitle, String Xaxis, 
			ArrayList<Point2D.Double> erreurEmpiriqueData, ArrayList<Point2D.Double> erreurGeneralisationData){
		
		
		final XYSeries empiriqueSeries = new XYSeries("Erreur empirique");
		for(int i=0; i<erreurEmpiriqueData.size(); i++){
			empiriqueSeries.add(
					new XYDataItem(erreurEmpiriqueData.get(i).getX(),erreurEmpiriqueData.get(i).getY()));
		}
		
		final XYSeries generalisationSeries = new XYSeries("Erreur g�n�ralisation");
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
        
        //Ajuster l'axe des X pour le graphique #2
        if(chartTitle.equals(TITLE_CHART2)){
	        XYPlot plotte = chart.getXYPlot();
	        ValueAxis axis = plotte.getDomainAxis();
	        axis.setAutoTickUnitSelection(false);
        }
        
        try {
			ChartUtilities.saveChartAsJPEG(new File("C:charts/"+fileName+".jpg"), chart, 500, 300);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/***
	 *  3. Influence du nombre de donn�es d'entra�nement
	 * @param dataset: Les 100 donn�es lues du fichier
	 * @return : Un arrayList contenant les points associ�s � l'erreur empirique
	 * 				et � l'erreur de g�n�ralisation
	 */
	public static ArrayList[] testerNombreDataTest(ArrayList<Point2D.Double> dataset){

		System.out.println(" -------------------------------------------------------");
		System.out.println(" PARTIE 3. Influence du nombre de donn�es d'entra�nement");
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
			System.out.println("Erreur Empirique: (" + i + " exemples d'entra�nement) : "+ erreurE);
			
			double erreurG = getErreur(poids, dataTest);
			System.out.println("Erreur  de G�n�ralisation: (" + i + " exemples d'entra�nement) : "+ erreurG);
			
			erreurs[0].add(new Point2D.Double(i,erreurE));
			erreurs[1].add(new Point2D.Double(i,erreurG));
	
		}
		return erreurs;
	}
	
	/***
	 * 4. Influence de l'ordre de r�gression
	 * @param dataset: Les 100 donn�es lues du fichier
	 * @return : Un arrayList contenant les points associ�s � l'erreur empirique
	 * 				et � l'erreur de g�n�ralisation
	 */
	public static ArrayList[] testerOrdreRegression(ArrayList<Point2D.Double> dataset){

		final int MIN_DEGRE = 1;
		final int MAX_DEGRE = 19;
		
		System.out.println(" --------------------------------------------");
		System.out.println(" PARTIE 4. Influence de l'ordre de r�gression");
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
		for(int degre=MIN_DEGRE;degre<=MAX_DEGRE;degre++){
			
			Matrix m = createMatriceVandermonde(dataTrain,degre);
			Matrix y = createY(dataTrain);
			Matrix poids = calculerPoids(m,y);
			double erreurE = getErreur(poids, dataTrain);
			System.out.println("Erreur Empirique: (Polynome de degr� " + degre + ") : "+ erreurE);
			
			double erreurG = getErreur(poids, dataValid);
			System.out.println("Erreur  de G�n�ralisation: (Polynome de degr� " + degre + ") : "+ erreurG);
			if(erreurG<minErreur)
			{
				minErreur = erreurG;
				bestOrdre = degre+1;
			}
			
			erreurs[0].add(new Point2D.Double(degre+1,erreurE));
			erreurs[1].add(new Point2D.Double(degre+1,erreurG));
		}
		
		System.out.println("Le meilleur ordre de r�gression: "+ bestOrdre);
		System.out.println(" Avec une erreur de g�n�ralisation de: "+ minErreur + "  (sur dataValid)");
		
		// Ici on refait la regression sur Train avec bestDegre
		// On �value erreur de g�n�ralisation sur Test
		// On compare ErreurG sur Test avec erreurG sur Valid
		Matrix m = createMatriceVandermonde(dataTrain,bestOrdre);
		Matrix y = createY(dataTrain);
		Matrix poids = calculerPoids(m,y);

		double erreurG = getErreur(poids, dataTest);
		System.out.println(" Avec une erreur de g�n�ralisation de: "+ erreurG + "  (sur dataTest)");
		

		return erreurs;
	}
	

	/**
	 * Permet de calculer la matrice de Vandermonde
	 * @param exemples: Les points avec lesquels cr�er la matrice de Vandermonde
	 * @param p: Le degr� du polyn�me utilis� pour cr�er le mod�le
	 * @return: La matrice de Vandermonde
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
	
	
	/**
	 * Retourne une matrice contenant les coordonn�es Y des points en param.
	 * @param exemples: Les points utilis�s
	 * @return : Une matrice des coordonn�es Y
	 */
	public static Matrix createY(ArrayList<Point2D.Double> exemples){
		Matrix m = new Matrix(exemples.size(), 1);
		for(int i=0; i<exemples.size();i++){
			m.set(i, 0, exemples.get(i).y);
		}
		return m;
	}
	
	
	/***
	 * Permet de calculer les poids selon (Mt*M)^-1 * Mt * y
	 * @param m : La matrice de Vandermonde
	 * @param y : La matrice contenant la coordonn�e Y des points
	 * @return la matrice contenant les poids calcul�s
	 */
	public static Matrix calculerPoids(Matrix m, Matrix y){
		Matrix mT = m.transpose();
		Matrix w = mT.times(m).inverse().times(mT).times(y);
		
		return w;
		
	}
	
	
	/***
	 * Retourne un �chantillon de points contenus dans le dataset de 100 points
	 * @param dataset: Le dataset contenant tous les points
	 * @param startIndex: L'index de d�part
	 * @param nbExemples: Le nombre de points � retourner
	 * @return: Un ArrayList<Points2D.Double> des points demand�s
	 */
	public static ArrayList<Point2D.Double> getExemples(ArrayList<Point2D.Double> dataset, int startIndex, int nbExemples){
		ArrayList<Point2D.Double> exemples=new ArrayList<Point2D.Double>();
		for(int i=0; i< nbExemples; i++){
			exemples.add(new Point2D.Double(dataset.get(startIndex+i).x,
					dataset.get(startIndex+i).y));
		}
		return exemples;
	}

	
	/***
	 * Retourne le String lu dans le fichier sous forme d'un dataset
	 * @param data : Le String lu dans le fichier
	 * @return un ArrayList<Point2D.Double> contenant les points du fichier
	 */
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

	
	/***
	 * Lire le fichier contenant les points
	 * @param filename: nom du fichier
	 * @return String du fichier.
	 */
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
	 *  Calculer l'erreur empirique ou l'erreur de g�n�ralisation
	 * @param poids : Un vecteur de poids calcul�s selon w=(X' * X)^-1 * X' * y
	 * @param dataSet: Les donn�es d'entra�nement ou les donn�es de test
	 * @return 	L'erreur empirique si on fournit les donn�es d'entra�nement;
	 * 			L'erreur de g�n�ralisation si on fournit les donn�es de test.
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
			//degr� du polynome
			for(int j=0;j<degreMax;j++){
				val += poids.get(j,0) * Math.pow(x,j);
			}
			sum += Math.pow((val - y),2);
		}
		
		erreur = Math.sqrt(sum/size);
		
		return erreur;
	}
	
	
	/***
	 * Permet de g�n�rer des graphiques contenant les points utilis�s et 
	 * 	la courbe de r�gression
	 * @param dataset: La totalit� des donn�es lues.
	 */
	private static void generateModel(ArrayList<Point2D.Double> dataset) {

		Object[] train = getExemples(dataset, 0, 60).toArray();
		Object[] valid = getExemples(dataset, 60, 20).toArray();
		Object[] test = getExemples(dataset, 80, 20).toArray();
		
	    final XYSeries serie1 = new XYSeries("Donn�es d'entra�nement");
		for(int i=0; i<train.length; i++){
			serie1.add(
					new XYDataItem(((Point2D.Double)train[i]).getX(),((Point2D.Double)train[i]).getY()));
		}
		final XYSeries serie2 = new XYSeries("Donn�es de validation");
		for(int i=0; i<valid.length; i++){
			serie2.add(
					new XYDataItem(((Point2D.Double)valid[i]).getX(),((Point2D.Double)valid[i]).getY()));
		}
		final XYSeries serie3 = new XYSeries("Donn�es de test");
		for(int i=0; i<test.length; i++){
			serie3.add(
					new XYDataItem(((Point2D.Double)test[i]).getX(),((Point2D.Double)test[i]).getY()));
		}
		final XYSeries serie4 = new XYSeries("Courbe de r�gression");
		
		for(double x=-1;x<1;x=(x+0.001)){
			double y = 4.779558350110513 - 17.01696920646155*x + 4.759823460356643*x*x + 0.3072822559905986 * x *x*x -1.640863698114691*x*x*x*x+ 16.496157379590596*x*x*x*x*x;
			serie4.add(new XYDataItem(x,y));
		}
		
		final XYSeriesCollection data1 = new XYSeriesCollection();
		data1.addSeries(serie1);
		data1.addSeries(serie4);

	    
	    final JFreeChart chart = ChartFactory.createScatterPlot(
	            "Comparaison entre la fonction de r�gression retenue et les donn�es d'entra�nement",
	            "X", 
	            "Y", 
	            data1,
	            PlotOrientation.VERTICAL,
	            true,
	            true,
	            false
	        );
	    try {
			ChartUtilities.saveChartAsJPEG(new File("C:charts/"+"chartTrain"+".jpg"), chart, 500, 300);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

		final XYSeriesCollection data2 = new XYSeriesCollection();
		
		data2.addSeries(serie3);
		data2.addSeries(serie4);
		
		final JFreeChart chart2 = ChartFactory.createScatterPlot(
	            "Comparaison entre la fonction de r�gression retenue et les donn�es de test",
	            "X", 
	            "Y", 
	            data2,
	            PlotOrientation.VERTICAL,
	            true,
	            true,
	            false
	        );


	    try {
			ChartUtilities.saveChartAsJPEG(new File("C:charts/"+"chartTest"+".jpg"), chart2, 500, 300);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
}
