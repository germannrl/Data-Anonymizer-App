package src.data;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.HashMap;

import Jama.Matrix;
import src.gui.IfrGenerateDistances;


public class SOperators
{
	/**
	 * @param domainFile
	 * @return distancesMatrix
	 * Descripción: calcula y devuelve la matriz de distancias entre los distintos
	 * conceptos del fichero "domainFile".
	 * 
	 * Precondiciones: el fichero "domainFile" contiene una columna con todos los offsets
	 * del dominio.
	 */
	public static Distances computeDistances(String domainFile)
	{
		List<Long> offsetKeys = new ArrayList<Long>();
		File f = null;
		FileReader fr = null;
		BufferedReader br = null;
		
		try
		{	
			f = new File(domainFile);
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			String line;
			
			while((line = br.readLine()) != null)
			{
				Long v = Long.parseLong(line); //obtiene el offset de cada fila
				offsetKeys.add(v);
			}
			fr.close();	
		}
		catch(IOException ioe){ ioe.printStackTrace(); }
		
		return new Distances(offsetKeys);
	}
	
	
	/**
	 * @param domainCategory
	 * @return distancesMatrix
	 * Descripción: dado una categoría de dominio, devuelve las distancias entre los
	 * conceptos hipónimos bajo dicho dominio.
	 */
	public static Distances computeDistances(Long domainCategory)
	{	
		List<Long> offsetKeys = IfrGenerateDistances.ontology.getHyponymTree(domainCategory);	
		return new Distances(offsetKeys);
	}


	/**
	 * @param dataset
	 * @param distances
	 * @return mean
	 * Descripción: calcula la media semántica (offset promedio) en Dominio x Atributo
	 * Precondiciones: "distances" contiene distancias de domino por dominio.
	 */	
	public static Long semanticMean(Attribute dataset, Distances distances)
	{
		ArrayList<Record> attribute = dataset.get(); //obtiene la columna del atributo
		return semanticMean(attribute, distances);
	}
	
	
	public static Long semanticMean(ArrayList<Record> attribute, Distances distances)
	{
		Long mean = new Long(0);
		float minValue = Float.MAX_VALUE;
		
		//Recorre todos los conceptos del dominio:
		for(HashMap.Entry<Long, HashMap<Long, Float>> domain : distances.getDistances().entrySet())
		{
			//Para cada concepto del dominio,
			Long domainOffset = domain.getKey(); //obtiene su offset
			float sumDistances = 0;
					
			for(Record record : attribute) //y recorre la columna del atributo 
			{
				//sumando las distancias desde el concepto del dominio a toda la columna.
				Long attributeOffset = record.getSemanticValue();
				sumDistances += distances.getDistanceBetween(domainOffset, attributeOffset);
			}
			if(sumDistances < minValue) //se queda con el concepto cuya suma sea mínima.
			{
				minValue = sumDistances;
				mean = domainOffset;
			}
		}
		return mean;
	}
	
	
	/**
	 * @param dataset
	 * @param mean
	 * @return variance
	 * Descripción: calcula la varianza semántica de un atributo
	 * Precondición: "distances" contiene distancias de dominio por dominio,
	 * y "mean" es el concepto medio de "dataset".
	 */
	public static float semanticVariance(Attribute dataset, Distances distances, Long mean)
	{
		float variance = 0;
		ArrayList<Record> attribute = dataset.get();
		
		for(Record record : attribute)
			variance += Math.pow(distances.getDistanceBetween(record.getSemanticValue(), mean), 2);
		
		return variance/attribute.size();
	}
	
	
	/**
	 * @param dataset
	 * @param distances
	 * @param mean
	 * @return stdDev
	 * Descripción: calcula la desviación estándar semántica de un atributo
	 * Precondición: "distances" contiene distancias de dominio por dominio, y "mean" es el concepto
	 * medio de "dataset".
	 */
	public static float semanticStdDeviation(Attribute dataset, Distances distances, Long mean)
	{
		double v = semanticVariance(dataset, distances, mean);
		float dev = (float) Math.sqrt(v);
		return dev;
	}
	
	
	/**
	 * @param d1
	 * @param d2
	 * @param distances
	 * @return MAE
	 * Descripción: calcula el MAE entre dos atributos.
	 * Precondición: "distances" contiene distancias de dominio por dominio.
	 */
	public static float semanticMAE(Attribute d1, Attribute d2, Distances distances)
	{
		ArrayList<Record> a1 = d1.get();
		ArrayList<Record> a2 = d2.get();
		float mae = 0;
		int n = a1.size();
		
		for(int i = 0; i < n; i++)
		{
			Record r1 = a1.get(i);
			Record r2 = a2.get(i);
			
			Long offset1 = r1.getSemanticValue(); //original
			Long offset2 = r2.getSemanticValue(); //enmascarado
			
			mae += distances.getDistanceBetween(offset1, offset2);
		}
		return mae/n;
	}
	
	
	/**
	 * @param d1
	 * @param d2
	 * @param distances
	 * @return MSE
	 * Descripción: calcula el MSE entre dos atributos.
	 * Precondición: "distances" contiene distancias de dominio por dominio.
	 */
	public static float semanticMSE(Attribute d1, Attribute d2, Distances distances)
	{
		ArrayList<Record> a1 = d1.get();
		ArrayList<Record> a2 = d2.get();
		float mse = 0;
		int n = a1.size();
		
		for(int i = 0; i < n; i++)
		{
			Record r1 = a1.get(i);
			Record r2 = a2.get(i);
			
			Long offset1 = r1.getSemanticValue(); //original
			Long offset2 = r2.getSemanticValue(); //enmascarado
			
			mse += Math.pow(distances.getDistanceBetween(offset1, offset2), 2);
		}
		return mse/n;
	}
	
	
	public static float semanticRMSE(Attribute d1, Attribute d2, Distances distances)
	{
		double mse = semanticMSE(d1, d2, distances);
		float rmse = (float) Math.sqrt(mse);
		return rmse;
	}
	
	
	/**
	 * @param dataset
	 * @param distances
	 * @return mdv
	 * Descripción: devuelve aquel concepto del atributo que maximiza la suma de
	 * las distancias semánticas con respecto al resto de conceptos.
	 */
	public static Long mostDistantValue(ArrayList<Record> attribute, Distances distances)
	{
		Long mdv = new Long(-1); //Most Distant Value del atributo dado
		float maxValue = Float.MIN_VALUE;
		
		//Recorre todos los conceptos del atributo:
		for(Record r1 : attribute)
		{
			//Para cada concepto del atributo, si no ha sido anonimizado aún:
			if(!r1.isAnonymized())
			{	
				Long r1Offset = r1.getSemanticValue(); //obtiene su offset
				float sumDistances = 0;
			
				for(Record r2 : attribute) //y recorre la columna del atributo
				{
					//sumando las distancias desde el concepto de referencia a todo el atributo.
					Long r2Offset = r2.getSemanticValue();
					sumDistances += distances.getDistanceBetween(r1Offset, r2Offset);
				}
				if(sumDistances > maxValue) //se queda con el concepto cuya suma sea mayor.
				{
					maxValue = sumDistances;
					mdv = r1Offset;
				}
			}
		}
		return mdv;
	}
	
	
	public static Record mostDistantValueRecord(ArrayList<Record> attribute, Distances distances)
	{
		Record mdv = null; //Most Distant Value del atributo dado
		float maxValue = Float.MIN_VALUE;
		
		//Recorre todos los conceptos del atributo:
		for(Record r1 : attribute)
		{
			//Para cada concepto del atributo, si no ha sido anonimizado aún:
			if(!r1.isAnonymized())
			{	
				Long r1Offset = r1.getSemanticValue(); //obtiene su offset
				float sumDistances = 0;
			
				for(Record r2 : attribute) //y recorre la columna del atributo
				{
					//sumando las distancias desde el concepto de referencia a todo el atributo.
					Long r2Offset = r2.getSemanticValue();
					sumDistances += distances.getDistanceBetween(r1Offset, r2Offset);
				}
				if(sumDistances > maxValue) //se queda con el concepto cuya suma sea mayor.
				{
					maxValue = sumDistances;
					mdv = r1;
				}
			}
		}
		return mdv;
	}

	
	/* MÉTODOS DE LA COVARIANZA */
	
	
	public static double semanticCovariance(Attribute a1, Attribute a2, Distances distances)
	{
		double[][] m1 = constructAttributeMatrix(a1, distances); //construimos la matriz de distancias del atributo 1
		double[][] m2 = constructAttributeMatrix(a2, distances); //construimos la matriz de distancias del atributo 2
		
		double[][] DMC1 = constructDoubleCenterMatrix(m1); //construimos la matriz doblemente centrada del atributo 1
		double[][] DMC2 = constructDoubleCenterMatrix(m2); //construimos la matriz doblemente centrada del atributo 2
		return sdCov(DMC1, DMC2);
	}
	
	
	public static double semanticDistanceVariance(Attribute a, Distances distances)
	{
		return semanticCovariance(a, a, distances);
	}
	
	
	public static double semanticCorrelation(Attribute a1, Attribute a2, Distances distances)
	{
		double sdCov = semanticCovariance(a1, a2, distances);
		double sdVar1 = semanticDistanceVariance(a1, distances);
		double sdVar2 = semanticDistanceVariance(a2, distances);
		double prodSdVar = sdVar1 * sdVar2;
		
		if(prodSdVar > 0)
			return sdCov/Math.sqrt(prodSdVar);
		else
			return 0;
	}
	
	
	/**
	 * @param a
	 * @param distances
	 * @return
	 * Descripción: construye la matriz de distancias del atributo
	 */
	public static double[][] constructAttributeMatrix(Attribute a, Distances distances)
	{
		int attSize = a.get().size(); //obtenemos el tamaño del atributo
		double[][] mA = new double[attSize + 1][attSize + 1]; //creamos la matriz de distancias del atributo
		ArrayList<Record> al = a.get(); //obtenemos el ArrayList de registros del atributo
		
		for(int i = 0; i < attSize; i++) //rellenamos matriz y calculamos media numérica de las filas
		{
			//System.out.println("Matriz atributo: " + i + "/" + attSize);
			
			double sumRow = 0;
			for(int j = 0; j < attSize; j++)
			{
				Record ri = al.get(i);
				Record rj = al.get(j);
				float dist = distances.getDistanceBetween(ri.getSemanticValue(), rj.getSemanticValue());
				mA[i][j] = dist;
				sumRow += dist;
			}
			mA[i][attSize] = sumRow/attSize;
		}
		
		for(int j = 0; j < attSize; j++) //asignamos media numérica de las columnas
			mA[attSize][j] = mA[j][attSize];
		
		double sumTotal = 0; //calculamos la media numérica de toda la matriz del atributo
		for(int i = 0; i < attSize; i++)
			for(int j = 0; j < attSize; j++)
				sumTotal += mA[i][j];
		mA[attSize][attSize] = sumTotal/(attSize * attSize);
		
		return mA;
	}
	
	
	/**
	 * @param m
	 * @return
	 * Descripción: construye la matriz doblemente centrada de un atributo
	 */
	public static double[][] constructDoubleCenterMatrix(double[][] m)
	{		
		int size = m.length;
		double[][] DCM = new double[size - 1][size - 1]; //creamos una matriz del tamaño del atributo
		
		for(int i = 0; i < DCM.length; i++)
		{	
			//System.out.println("Matriz doblemente centrada: " + i + "/" + DCM.length);
			
			for(int j = 0; j < DCM.length; j++)
			{
				double value = m[i][j] - m[i][size-1] - m[size-1][j] + m[size-1][size-1];
				DCM[i][j] = value;
			}
		}
			
		return DCM;
	}

	
	/**
	 * @param d1
	 * @param d2
	 * @return
	 * Descripción: calcula el valor de la covarianza semántica
	 */
	public static double sdCov(double[][] d1, double[][] d2)
	{
		int size = d1.length;
		double sdCov = 0;
		
		for(int i = 0; i < size; i++)
			for(int j = 0; j < size; j++)
				sdCov = sdCov + (d1[i][j] * d2[i][j]);
				
		sdCov = Math.sqrt(sdCov);
		return sdCov/size;
	}
	
	
	/**
	 * Descripción: construye la matriz de covarianza semántica (con alpha ya multiplicado)
	 * @param attribute
	 * @param alpha
	 * @param distances
	 * @return
	 */
	public static double[][] createSemanticCovarianceMatrix(ArrayList<Attribute> attributes, float alpha, Distances distances)
	{
		int n = attributes.size(); //nº de atributos para la matriz de covarianzas
		double[][] matrix = new double[n][n];
		
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
				matrix[i][j] = semanticCovariance(attributes.get(i), attributes.get(j), distances) * alpha;
		
		return matrix;
	}
	
	
	/**
	 * Descripción: calcula la distancia semántica entre dos tuplas de un dataset
	 * @param tuple1
	 * @param tuple2
	 * @param distances
	 * @return
	 */
	public static float semanticDistanceBetweenTuples(Tuple tuple1, Tuple tuple2, Distances distances)
	{
		float sd;
		float sum = 0;
		int m = tuple1.getTuple().size(); //número de atributos de cada tupla
		
		for(int i = 0; i < m; i++)
		{
			Record r1 = tuple1.getTuple().get(i);
			Record r2 = tuple2.getTuple().get(i);
			sum += distances.getDistanceBetween(r1.getSemanticValue(), r2.getSemanticValue());
		}
	
		sd = sum/m;
			
		return sd;
	}
	
	
	/**
	 * Descripción: obtiene la tupla más alejada a todas las demás
	 * @param tuples
	 * @param distances
	 * @return
	 */
	public static Tuple mostDistantTuple(ArrayList<Tuple> tuples, Distances distances)
	{
		Tuple mdt = null; //Most Distant Tuple del conjunto de tuplas dado
		float maxValue = Float.MIN_VALUE;
		
		//Recorre todas las tuplas del conjunto:
		for(Tuple t1: tuples)
		{
			//Para cada tupla, si no ha sido anonimizada:
			if(!t1.isAnonymized())
			{
				float sumDistances = 0;
				
				for(Tuple t2 : tuples) //recorremos todas las tuplas para calcular las distancias a la primera
					sumDistances += semanticDistanceBetweenTuples(t1, t2, distances);
				
				if(sumDistances > maxValue) //se queda con el concepto cuya suma sea mayor.
				{
					maxValue = sumDistances;
					mdt = t1;
				}
			}
		}	
		return mdt;
	}

}
