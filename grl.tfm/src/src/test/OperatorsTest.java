package src.test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import Jama.Matrix;
import src.data.Attribute;
import src.data.Dataset;
import src.data.NumericMethods;
import src.data.Operators;
import src.data.Record;
import src.data.SOperators;

/**
 * Pruebas para los métodos de la clase Operators
 */
public class OperatorsTest
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void operatorsTest()
	{
		Dataset dataset = new Dataset("./PRUEBAS/pruebas_OperatorsTest.txt"); //datos para el test
		ArrayList<Record> atributo = dataset.getAttribute(1).get();
		
		assertEquals(212.264, Operators.Mean(atributo), 0.0001);
		assertEquals(11889.415, Operators.Variance(atributo), 0.0001);
		assertEquals(109.0385, Operators.StandardDeviation(atributo), 0.0001);
		
		ArrayList<Record> atributo2 = dataset.getAttribute(2).get();
		
		assertEquals(4092.536, Operators.MSE(atributo, atributo2), 0.001);
		assertEquals(63.9729, Operators.RMSE(atributo, atributo2), 0.001);
		
		/* TESTS DE COVARIANZA */
		
		System.out.println("Covarianza: " + Operators.covariance(atributo, atributo2));
		System.out.println("Correlación: " + Operators.correlation(atributo, atributo2));
		
		assertEquals(1.0, Operators.correlation(atributo, atributo), 0.001);
		
		double[][] covMatrix = Operators.createCovarianceMatrix(dataset.getDataset());
		
		for(int i = 0; i < covMatrix.length; i++)
		{
			for(int j = 0; j < covMatrix.length; j++)
				System.out.print(covMatrix[i][j] + " ");
			System.out.println("");
		}
	}
	
	
/*	
	public void individualRankingMultivariateTest()
	{
		Dataset dataset = new Dataset("./PRUEBAS/pruebas_OperatorsTest.txt");
		ArrayList<Attribute> attributes = dataset.getDataset();
		int[] atts = {1,3};
		int k = 3;
		
		System.out.println("Media original del atributo 1: " + Operators.Mean(attributes.get(0).get()));
		System.out.println("Media original del atributo 3: " + Operators.Mean(attributes.get(2).get()));
		
		System.out.println("Varianza original del atributo 1: " + Operators.Variance(attributes.get(0).get()));
		System.out.println("Varianza original del atributo 3: " + Operators.Variance(attributes.get(2).get()));
		
		System.out.println("Covarianza original de 1 y 3: " + Operators.covariance
															(attributes.get(0).get(), attributes.get(2).get()));
		
		System.out.println("Correlación original de 1 y 3: " + Operators.correlation
				(attributes.get(0).get(), attributes.get(2).get()));	
		
		attributes = NumericMethods.individualRankingMultivariate(dataset, atts, k);
		
		for(int i = 0; i < attributes.size(); i++)
		{
			ArrayList<Record> att = attributes.get(i).get();
			
			for(int j = 0; j < att.size(); j++)
				System.out.println(att.get(j).getNumericValue() + " ");
			
			System.out.println("");
		}
		
		System.out.println("Media enmascarada del atributo 1: " + Operators.Mean(attributes.get(0).get()));
		System.out.println("Media enmascarada del atributo 3: " + Operators.Mean(attributes.get(2).get()));
		
		System.out.println("Varianza enmascarada del atributo 1: " + Operators.Variance(attributes.get(0).get()));
		System.out.println("Varianza enmascarada del atributo 3: " + Operators.Variance(attributes.get(2).get()));
		
		System.out.println("Covarianza enmascarada de 1 y 3: " + Operators.covariance
				(attributes.get(0).get(), attributes.get(2).get()));
		
		System.out.println("Correlación enmascarada de 1 y 3: " + Operators.correlation
															(attributes.get(0).get(), attributes.get(2).get()));
	}
	
	
	
	public void noiseAdditionMultivariateTest()
	{
		Dataset dataset = new Dataset("./PRUEBAS/pruebas_OperatorsTest.txt");
		int[] atts = {1,3};
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(dataset.getAttribute(1));
		attributes.add(dataset.getAttribute(3));
		
		Float alpha = Float.parseFloat("0.00001");
		
		System.out.println("Media original del atributo 1: " + Operators.Mean(attributes.get(0).get()));
		System.out.println("Media original del atributo 3: " + Operators.Mean(attributes.get(1).get()));
		
		System.out.println("Varianza original del atributo 1: " + Operators.Variance(attributes.get(0).get()));
		System.out.println("Varianza original del atributo 3: " + Operators.Variance(attributes.get(1).get()));
		
		System.out.println("Covarianza original de 1 y 3: " + Operators.covariance
															(attributes.get(0).get(), attributes.get(1).get()));
		
		System.out.println("Correlación original de 1 y 3: " + Operators.correlation
				(attributes.get(0).get(), attributes.get(1).get()));
		
		for(int i = 0; i < attributes.size(); i++)
		{
			ArrayList<Record> att = attributes.get(i).get();
			
			for(int j = 0; j < att.size(); j++)
				System.out.println(att.get(j).getNumericValue() + " ");
			
			System.out.println("");
		}
		
		attributes = NumericMethods.addNoiseMultivariate(dataset, atts, alpha);
		
		for(int i = 0; i < attributes.size(); i++)
		{
			ArrayList<Record> att = attributes.get(i).get();
			
			for(int j = 0; j < att.size(); j++)
				System.out.print(att.get(j).getNumericValue() + " ");
			
			System.out.println("");
		}
		
		System.out.println("Media enmascarada del atributo 1: " + Operators.Mean(attributes.get(0).get()));
		System.out.println("Media enmascarada del atributo 3: " + Operators.Mean(attributes.get(1).get()));
		
		System.out.println("Varianza enmascarada del atributo 1: " + Operators.Variance(attributes.get(0).get()));
		System.out.println("Varianza enmascarada del atributo 3: " + Operators.Variance(attributes.get(1).get()));
		
		System.out.println("Covarianza enmascarada de 1 y 3: " + Operators.covariance
				(attributes.get(0).get(), attributes.get(1).get()));
		
		System.out.println("Correlación enmascarada de 1 y 3: " + Operators.correlation
															(attributes.get(0).get(), attributes.get(1).get()));
	}
	
	
	@Test
	public void rankSwappingMultivariateTest()
	{
		Dataset dataset = new Dataset("./PRUEBAS/pruebas_OperatorsTest.txt");
		ArrayList<Attribute> attributes = dataset.getDataset();
		int[] atts = {1,3};
		int k = 3;
		
		System.out.println("Media original del atributo 1: " + Operators.Mean(attributes.get(0).get()));
		System.out.println("Media original del atributo 3: " + Operators.Mean(attributes.get(2).get()));
		
		System.out.println("Varianza original del atributo 1: " + Operators.Variance(attributes.get(0).get()));
		System.out.println("Varianza original del atributo 3: " + Operators.Variance(attributes.get(2).get()));
		
		System.out.println("Covarianza original de 1 y 3: " + Operators.covariance
															(attributes.get(0).get(), attributes.get(2).get()));
		
		System.out.println("Correlación original de 1 y 3: " + Operators.correlation
				(attributes.get(0).get(), attributes.get(2).get()));	
		
		attributes = NumericMethods.rankSwappingMultivariate(dataset, atts, k);
		
		for(int i = 0; i < attributes.size(); i++)
		{
			ArrayList<Record> att = attributes.get(i).get();
			
			for(int j = 0; j < att.size(); j++)
				System.out.println(att.get(j).getNumericValue() + " ");
			
			System.out.println("");
		}
		
		System.out.println("Media enmascarada del atributo 1: " + Operators.Mean(attributes.get(0).get()));
		System.out.println("Media enmascarada del atributo 3: " + Operators.Mean(attributes.get(2).get()));
		
		System.out.println("Varianza enmascarada del atributo 1: " + Operators.Variance(attributes.get(0).get()));
		System.out.println("Varianza enmascarada del atributo 3: " + Operators.Variance(attributes.get(2).get()));
		
		System.out.println("Covarianza enmascarada de 1 y 3: " + Operators.covariance
				(attributes.get(0).get(), attributes.get(2).get()));
		
		System.out.println("Correlación enmascarada de 1 y 3: " + Operators.correlation
															(attributes.get(0).get(), attributes.get(2).get()));
	}
*/	
	
}
