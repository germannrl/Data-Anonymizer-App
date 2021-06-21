package src.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

import Jama.Matrix;
import src.data.Attribute;
import src.data.Dataset;
import src.data.Distances;
import src.data.Record;
import src.data.SOperators;
import src.data.SemanticMethods;
import src.data.Tuple;
import src.data.WordNetAccess;
import src.gui.IfrGenerateDistances;

/**
 * Pruebas para los métodos de la clase SOperators
 */
public class SOperatorsTest
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		IfrGenerateDistances.ontology = new WordNetAccess();
	}
	
	
	@Test
	public void computeDistancesTest()
	{
		Distances distances = SOperators.computeDistances("./PRUEBAS/pruebas_computeDistancesTest.txt");
		/*Comprobamos algunas distancias entre conceptos*/
		Long offset1 = new Long(13935956);
		Long offset2 = new Long(13969175);
		float dist1 = IfrGenerateDistances.ontology.distance_WP(offset1, offset2);
		float zero = 0;
		assertEquals(dist1, distances.getDistanceBetween(offset1, offset2)); //en un sentido
		assertEquals(dist1, distances.getDistanceBetween(offset2, offset1)); //y en otro
		assertEquals(zero, distances.getDistanceBetween(offset1, offset1)); //y a sí mismo
		
		/*Pruebas con carga de dominio ofreciendo una categoría de dominio*/
		
		Distances distances2 = SOperators.computeDistances(new Long(13934383)); //flu
		assertEquals(3, distances2.getDistances().size());
	}
	
	
	@Test
	public void semanticMeanTest()
	{
		Dataset dataset = new Dataset("./PRUEBAS/pruebas_semanticMeanTest.txt");
		Attribute attribute = dataset.getAttribute(1);
		Distances domain = SOperators.computeDistances("./PRUEBAS/pruebas_dominioSOperators.txt");
		
		Long mean = SOperators.semanticMean(attribute, domain);
		System.out.println("Media: " + WordNetAccess.getSynset(mean));
		
		ArrayList<Record> aData = attribute.get();
		
		for(HashMap.Entry<Long, HashMap<Long, Float>> d : domain.getDistances().entrySet())
		{
			float sumDistances = 0;
			System.out.print(d.getKey() + ": ");
			for(Record registro : aData)
			{
				float distance = domain.getDistanceBetween(d.getKey(), registro.getSemanticValue());
				System.out.print(distance + ", ");
				sumDistances += distance;
			}
			System.out.println(" = " + sumDistances);
		}
	}
	
	
	@Test
	public void semanticVarianceTest()
	{
		Dataset dataset = new Dataset("./PRUEBAS/pruebas_semanticMeanTest.txt");
		Attribute attribute = dataset.getAttribute(1);
		Distances domain = SOperators.computeDistances("./PRUEBAS/pruebas_dominioSOperators.txt");
		
		float variance = SOperators.semanticVariance(attribute, domain, new Long(2110707));
		System.out.println("Varianza: " + variance);
	}
	
	
	@Test
	public void mostDistancesValueTest()
	{
		Dataset dataset = new Dataset("./PRUEBAS/pruebas_semanticMeanTest.txt");
		Attribute attribute = dataset.getAttribute(1);
		Distances domain = SOperators.computeDistances("./PRUEBAS/pruebas_dominioSOperators.txt");
		
		ArrayList<Record> aData = attribute.get();
		Long mdv = SOperators.mostDistantValue(aData, domain);
		System.out.println("Most Distant Value: " + WordNetAccess.getSynset(mdv));
		
		for(Record r1 : aData)
		{
			float sumDistances = 0;
			System.out.print(r1.getSemanticValue() + ": ");
			for(Record registro : aData)
			{
				float distance = domain.getDistanceBetween(r1.getSemanticValue(), registro.getSemanticValue());
				System.out.print(distance + ", ");
				sumDistances += distance;
			}
			System.out.println(" = " + sumDistances);
		}
	}
	
	
	/* TEST DE LOS MÉTODOS DE LA COVARIANZA SEMÁNTICA */
	
	@Test
	public void semanticCovarianceTest()
	{
		Dataset dataset = new Dataset("./PRUEBAS/pruebas_semanticCovarianceTest.txt");
		Attribute a1 = dataset.getAttribute(1);
		Attribute a2 = dataset.getAttribute(2);
		Distances distances = SOperators.computeDistances("./PRUEBAS/pruebas_dominioSOperators.txt");
		
		System.out.println("Covarianza: " + SOperators.semanticCovariance(a1, a2, distances));
		System.out.println("Correlación: " + SOperators.semanticCorrelation(a1, a2, distances));
		
		assertEquals(1.0, SOperators.semanticCorrelation(a1, a1, distances));
	}
	
	
	@Test
	public void constructAttributeMatrixTest()
	{
		Dataset dataset = new Dataset("./PRUEBAS/pruebas_semanticCovarianceTest.txt");
		Attribute a1 = dataset.getAttribute(1);
		Distances distances = SOperators.computeDistances("./PRUEBAS/pruebas_dominioSOperators.txt");
		
		double[][] m = SOperators.constructAttributeMatrix(a1, distances);
		System.out.println("Matriz del atributo:");
		
		for(int i = 0; i < m.length; i++)
		{
			for(int j = 0; j < m[i].length; j++)
				System.out.print(m[i][j] + " ");
			
			System.out.println("");
		}
	}

	
	@Test
	public void constructDoubleCenterMatrixTest()
	{
		Dataset dataset = new Dataset("./PRUEBAS/pruebas_semanticCovarianceTest.txt");
		Attribute a1 = dataset.getAttribute(1);
		Distances distances = SOperators.computeDistances("./PRUEBAS/pruebas_dominioSOperators.txt");
		
		double[][] m = SOperators.constructAttributeMatrix(a1, distances);
		double[][] DCM = SOperators.constructDoubleCenterMatrix(m);
		System.out.println("Matriz doblemente centrada:");
		
		for(int i = 0; i < DCM.length; i++)
		{
			for(int j = 0; j < DCM[i].length; j++)
				System.out.print(DCM[i][j] + " ");
			
			System.out.println("");
		}
	}
	
	
	/* TEST DE MÉTODOS MULTIVARIADOS */
	
	/*@Test
	public void semanticAddNoiseMultivariateTest()
	{
		Dataset dataset = new Dataset("./DATASETS/zoo_dataset_offset.txt");
		Distances distances = new Distances("./DOMINIOS/mammaldomain.txt");
		int[] atts = {1,3};
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		Attribute a1 = dataset.getAttribute(1);
		Attribute a2 = dataset.getAttribute(3);
		
		attributes.add(dataset.getDataset().get(0));
		attributes.add(dataset.getDataset().get(2));
		Float alpha = new Float(0.02);
		
		System.out.println("Covarianza antes: " +SOperators.semanticCovariance(a1, a2, distances));
		System.out.println("Media (a) antes: " + SOperators.semanticMean(a1, distances));
		System.out.println("Media (b) antes: " + SOperators.semanticMean(a2, distances));
		
		attributes = SemanticMethods.semanticAddNoiseMultivariate(dataset, atts, alpha, distances);
		
		System.out.println("Covarianza después: " +SOperators.semanticCovariance(attributes.get(0), attributes.get(1), distances));
	
		System.out.println("Media (a) después: " + SOperators.semanticMean(attributes.get(0), distances));
		System.out.println("Media (b) después: " + SOperators.semanticMean(attributes.get(1), distances));
		
		for(int i = 0; i < attributes.get(0).get().size(); i++)
			System.out.println(attributes.get(0).get().get(i).getSemanticValue());
	}*/
	
	
	@Test
	public void mostDistanceTupleTest()
	{
		Dataset dataset = new Dataset("./PRUEBAS/pruebas_semanticMeanTest.txt");
		Distances domain = SOperators.computeDistances("./PRUEBAS/pruebas_dominioSOperators.txt");
		int[] atts = {1,3};
		
		ArrayList<Tuple> aData = Tuple.toArrayOfTuples(dataset, atts);
		
		Tuple mdt = SOperators.mostDistantTuple(aData, domain);
		System.out.println("Most Distant Tuple: " + mdt.getIndex());
		
		for(Tuple t1 : aData)
		{
			float sumDistances = 0;
			System.out.print(t1.getIndex() + ": ");
			for(Tuple t2 : aData)
			{
				float distance = SOperators.semanticDistanceBetweenTuples(t1, t2, domain);
				System.out.print(distance + ", ");
				sumDistances += distance;
			}
			System.out.println(" = " + sumDistances);
		}	
	}
	
	/*
	@Test
	public void semanticRankSwappingMultivariateTest()
	{
		Dataset dataset = new Dataset("./DATASETS/zoo_dataset_offset.txt");
		Distances distances = new Distances("./DOMINIOS/mammaldomain.txt");
		int k = 3;
		int[] atts = {1,3};
		
		Attribute a1 = dataset.getAttribute(1);
		Attribute a2 = dataset.getAttribute(3);
		
		System.out.println("Covarianza antes: " +SOperators.semanticCovariance(a1, a2, distances));
		System.out.println("Media (a) antes: " + SOperators.semanticMean(a1, distances));
		System.out.println("Media (b) antes: " + SOperators.semanticMean(a2, distances));
		
		ArrayList<Attribute> attributes = SemanticMethods.semanticRankSwappingMultivariate(dataset, atts, k, distances);
		
		System.out.println("Covarianza después: " +SOperators.semanticCovariance(attributes.get(0), attributes.get(2), distances));
		
		System.out.println("Media (a) después: " + SOperators.semanticMean(attributes.get(0), distances));
		System.out.println("Media (b) después: " + SOperators.semanticMean(attributes.get(2), distances));
		
		for(Attribute a : attributes)
		{
			for(Record r : a.get())
			{
				if(a.getTipo() == 0)
					System.out.print(r.getNumericValue() + " ");
				else
					System.out.print(r.getSemanticValue() + " ");
			}
			System.out.println("");
		}
	}
	
	
	@Test
	public void semanticIndividualRankingMultivariateTest()
	{
		Dataset dataset = new Dataset("./DATASETS/zoo_dataset_offset.txt");
		Distances distances = new Distances("./DOMINIOS/mammaldomain.txt");
		int k = 6;
		int[] atts = {1,3};
		
		Attribute a1 = dataset.getAttribute(1);
		Attribute a2 = dataset.getAttribute(3);
		
		System.out.println("Covarianza antes: " +SOperators.semanticCovariance(a1, a2, distances));
		System.out.println("Media (a) antes: " + SOperators.semanticMean(a1, distances));
		System.out.println("Media (b) antes: " + SOperators.semanticMean(a2, distances));
		
		ArrayList<Attribute> attributes = SemanticMethods.semanticIndividualRankingMultivariate(dataset, atts, k, distances);
		
		System.out.println("Covarianza después: " +SOperators.semanticCovariance(attributes.get(0), attributes.get(2), distances));
		
		System.out.println("Media (a) después: " + SOperators.semanticMean(attributes.get(0), distances));
		System.out.println("Media (b) después: " + SOperators.semanticMean(attributes.get(2), distances));
		
		for(Attribute a : attributes)
		{
			for(Record r : a.get())
			{
				if(a.getTipo() == 0)
					System.out.print(r.getNumericValue() + " ");
				else
					System.out.print(r.getSemanticValue() + " ");
			}
			System.out.println("");
		}
		
	}
	*/
}
