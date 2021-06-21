package src.data;
import java.util.*;

import Jama.Matrix;

public class Operators
{
	public static double Mean(List<Record> attribute)
	{
		double mean = 0;
		for(Record record : attribute)
			mean += record.getNumericValue();
		return mean/attribute.size();
	}
	
	
	public static double Variance(List<Record> attribute)
	{
		double variance = 0;
		double mean = Mean(attribute);
		for(Record record : attribute)
			variance += Math.pow(record.getNumericValue() - mean, 2);
		return variance/attribute.size();
	}
	
	
	public static double StandardDeviation(List<Record> attribute)
	{
		return Math.sqrt(Variance(attribute));
	}
	
	
	public static double MAE(List<Record> a1, List<Record> a2)
	{
		double mae = 0;
		int n = a1.size();
		for(int i = 0; i < n; i++)
		{
			Record r1 = a1.get(i);
			Record r2 = a2.get(i);
			
			double v1 = r1.getNumericValue(); //original;
			double v2 = r2.getNumericValue(); //enmascarado;
			
			mae += Math.abs(v1 - v2);
		}
		return mae/n;
	}
	
	
	public static double MSE(List<Record> a1, List<Record> a2)
	{
		double mse = 0;
		int n = a1.size();
		for(int i = 0; i < n; i++)
		{
			Record r1 = a1.get(i);
			Record r2 = a2.get(i);
			
			double v1 = r1.getNumericValue(); //original;
			double v2 = r2.getNumericValue(); //enmascarado;
			
			mse += Math.pow(v1 - v2, 2);
		}
		return mse/n;
	}
	
	
	public static double RMSE(List<Record> a1, List<Record> a2)
	{
		return Math.sqrt(MSE(a1, a2));
	}
	
	
	/* MÉTODOS DE LA COVARIANZA NUMÉRICA */
	
	
	public static double covariance(List<Record> a1, List<Record> a2)
	{
		double sum = 0;
		int n = a1.size();
		double mean1 = Mean(a1);
		double mean2 = Mean(a2);
		
		for(int i = 0; i < n; i++)
			sum += ((a1.get(i).getNumericValue() - mean1) * (a2.get(i).getNumericValue() - mean2));
		
		return sum/n;
	}
	
	
	public static double[][] createCovarianceMatrix(ArrayList<Attribute> attributes)
	{
		int n = attributes.size(); //nº de atributos para la matriz de covarianza
		double[][] matrix = new double[n][n];
		
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
				matrix[i][j] = covariance(attributes.get(i).get(), attributes.get(j).get());
		
		return matrix;
	}
	
	
	/**
	 * Versión del método para multiplicar el valor de alfa automáticamente (en Noise Addition)
	 * @param attributes
	 * @param alpha
	 * @return
	 */
	public static double[][] createCovarianceMatrix(ArrayList<Attribute> attributes, float alpha)
	{
		int n = attributes.size(); //nº de atributos para la matriz de covarianza
		double[][] matrix = new double[n][n];
		
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
				matrix[i][j] = covariance(attributes.get(i).get(), attributes.get(j).get()) * alpha;
		
		return matrix;
	}
	
	
	public static double correlation(List<Record> a1, List<Record> a2)
	{
		return covariance(a1,a2)/(StandardDeviation(a1) * StandardDeviation(a2));
	}
	
	
}
