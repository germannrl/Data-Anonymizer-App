package src.data;

import java.util.*;

public class NumericMethods
{
	/**
	 * @param dataset
	 * @param k
	 * @return anonimizeAttribute
	 * Descripción: algoritmo de microagregación Individual Ranking, en su versión
	 * para atributos numéricos.
	 */
	public static ArrayList<Record> individualRanking(Attribute dataset, int k)
	{
		boolean end = false;
		ArrayList<Record> attribute = dataset.get();
		ArrayList<Record> anonimizeAttribute = new ArrayList<Record>();
		
		for(int i = 0; i < attribute.size(); i++)
			anonimizeAttribute.add(new Record(0, 0));
		
		attribute.sort(porAtributo); //Ordena los registros en orden ascendente
		
		for(int i = 0; i < attribute.size() && !end; i += k)
		{
			int n;
			if(i + 2*k > attribute.size())
			{	
				n = attribute.size();
				end = true;
			}	
			else
				n = i + k;
			
			List<Record> subgrupo = attribute.subList(i, n);
			double media = Operators.Mean(subgrupo);
			
			for(Record r : subgrupo)
			{	
				r.setNumericValue(media);
				anonimizeAttribute.set(r.getId() - 1, r);
			}	
		}		
		return anonimizeAttribute;
	}
	
	
	/**
	 * Descripción: comparador para Record, por atributo, en modo numérico
	 */
	public static Comparator<Record> porAtributo = new Comparator<Record>()
	{
		@Override
		public int compare(Record r1, Record r2)
		{
			if(r1.getNumericValue() < r2.getNumericValue())
				return -1;
			else if(r1.getNumericValue() > r2.getNumericValue())
				return 1;
			else
				return 0;
		}
	};
	
	
	/**
	 * @param dataset
	 * @param alpha
	 * @return attribute
	 * Descripción: a partir de la columna de un atributo numérico y el coeficiente de ruido,
	 * enmascara el atributo mediante adición de ruido.
	 */
	public static ArrayList<Record> addNoise(Attribute dataset, float alpha)
	{
		ArrayList<Record> attribute = dataset.get();
		double variance = Operators.Variance(attribute);
		SemanticMethods.generateNoise(attribute, variance, alpha);
		
		for(Record record : attribute)
			record.setNumericValue(record.getNumericValue() + record.getPrivacyValue());
		
		return attribute;
	}
	
	
	/**
	 * @param dataset
	 * @param k
	 * @return attribute
	 * Descripción: a partir de la columna de un atributo numérico y un valor de k,
	 * enmascara el atributo mediante Rank Swapping.
	 */
	public static ArrayList<Record> rankSwapping(Attribute dataset, int k)
	{
		ArrayList<Record> attribute = dataset.get();
		attribute.sort(porAtributo); //ordena los registros en orden ascendente
		
		for(int i = 0; i < attribute.size(); i++) //recorre toda la columna
		{
			Record referencia = attribute.get(i); //obtenemos el registro de referencia a intercambiar
			
			if(!referencia.isAnonymized()) //si no ha sido intercambiado aún,
			{
				int n;
				if(i + k >= attribute.size())
					n = attribute.size();
				else
					n = i + k + 1;
				
				ArrayList<Integer> clusterIndexs = new ArrayList<Integer>();
				for(int index = i + 1; index < n; index++)
					clusterIndexs.add(index); //creamos un cluster con los índices de los registros
				
				attribute = swapValues(clusterIndexs, attribute, i);
			}
		}
		return attribute;
	}
	
	
	/**
	 * @param cluster
	 * @param attribute
	 * @param ref
	 * @return attribute
	 * Descripción: intercambia dos valores, el de referencia y uno aleatorio entre los k siguientes.
	 */
	public static ArrayList<Record> swapValues(ArrayList<Integer> cluster, ArrayList<Record> attribute, int ref)
	{
		ArrayList<Integer> unswapped = obtainUnswappedValues(cluster, attribute);
		double valueRef = attribute.get(ref).getNumericValue(); //se obtiene el valor de referencia
		attribute.get(ref).setNumericValue(valueRef); //si no hay valores en unswapped, será el mismo enmascarado
		attribute.get(ref).setAnonymized(true);
		
		if(!unswapped.isEmpty())
		{
			Random rnd = new Random();
			int index = rnd.nextInt(unswapped.size()); //elige aleatoriamente un elemento de unswapped
			int elected = unswapped.get(index); //obtiene su índice
			
			double valueExch = attribute.get(elected).getNumericValue();
			attribute.get(ref).setNumericValue(valueExch);
			attribute.get(elected).setNumericValue(valueRef);
			attribute.get(elected).setAnonymized(true);
		}
		return attribute;
	}
	
	
	/**
	 * @param cluster
	 * @param attribute
	 * @return unswappedValues
	 * Descripción: devuelve aquellos elementos del clúster que no han sido intercambiados aún
	 */
	public static ArrayList<Integer> obtainUnswappedValues(ArrayList<Integer> cluster, ArrayList<Record> attribute)
	{
		ArrayList<Integer> unswappedValues = new ArrayList<Integer>();
		
		for(Integer i : cluster)
		{
			if(!attribute.get(i).isAnonymized())
				unswappedValues.add(i);
		}
		return unswappedValues;
	}
	
	
	/***** MÉTODOS MULTIVARIADOS *****/
	
	
	public static ArrayList<Attribute> individualRankingMultivariate(Dataset dataset, int[] atts, int k)
	{
		boolean end = false;
		obtainStandardScore(dataset, atts); //calcular la suma de unidades tipificadas
		ArrayList<Tuple> tuples = Tuple.toArrayOfTuples(dataset, atts);
		tuples.sort(porTupla);
		
		for(int i = 0; i < tuples.size() && !end; i += k)
		{
			int n;
			if(i + 2*k > tuples.size())
			{	
				n = tuples.size();
				end = true;
			}	
			else
				n = i + k;
						
			tuples = anonymizeRank(tuples, i, n);
		}
		dataset.updateNumericTuples(tuples, atts); //actualiza el dataset con los atributos anonimizados
		return dataset.getDataset();
	}
	
	
	public static ArrayList<Tuple> anonymizeRank(ArrayList<Tuple> tuples, int inf, int sup)
	{
		int n = tuples.get(0).getTuple().size(); //tamaño de las tuplas
		
		for(int i = 0; i < n; i++)
		{
			List<Record> cluster = new ArrayList<Record>();
			for(int j = inf; j < sup; j++)
			{
				Tuple t = tuples.get(j);
				cluster.add(t.getTuple().get(i)); //añadir cada registro i-ésimo de las tuplas del clúster
			}
			double media = Operators.Mean(cluster);
			
			for(int j = inf; j < sup; j++)
				tuples.get(j).getTuple().get(i).setNumericValue(media);
			
		}
		return tuples;
	}
	
	
	public static ArrayList<Attribute> addNoiseMultivariate(Dataset dataset, int[] atts, float alpha)
	{
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(dataset.getAttribute(atts[0]));
		attributes.add(dataset.getAttribute(atts[1]));
		
		int n = attributes.size(); //nº de atributos a anonimizar
		double[] means = new double[n]; //array con media cero
		double[][] covMatrix = Operators.createCovarianceMatrix(attributes, alpha); //matriz de covarianzas, ya con el valor alfa
		
		SemanticMethods.generateMultivariateNormalNoise(attributes, means, covMatrix);
		
		for(int i = 0; i < n; i++)
		{
			for(Record r : attributes.get(i).get())
				r.setNumericValue(r.getNumericValue() + r.getPrivacyValue());
		}
		return attributes;
	}
	
	
	public static ArrayList<Attribute> rankSwappingMultivariate(Dataset dataset, int[] atts, int k)
	{
		obtainStandardScore(dataset, atts); //calcular la suma de unidades tipificadas
		ArrayList<Tuple> tuples = Tuple.toArrayOfTuples(dataset, atts);
		tuples.sort(porTupla);
		
		for(int i = 0; i < tuples.size(); i++)
		{
			Tuple ref = tuples.get(i);
			
			if(!ref.isAnonymized())
			{
				int n;
				if(i + k >= tuples.size())
					n = tuples.size();
				else
					n = i + k + 1;
			
				ArrayList<Integer> C = new ArrayList<Integer>();
				for(int index = i + 1; index < n; index++)
					C.add(index); //creamos un cluster con los índices de las tuplas
			
				tuples = swapValuesInTuples(tuples, C, i);				
			}
		}
		dataset.updateNumericTuples(tuples, atts); //actualiza el dataset con los atributos anonimizados
		return dataset.getDataset();
	}
	
	
	/**
	 * Descripción: comparador para Record, por atributo, en modo numérico
	 */
	public static Comparator<Tuple> porTupla = new Comparator<Tuple>()
	{
		@Override
		public int compare(Tuple t1, Tuple t2)
		{
			if(t1.getStandardScoreSum() < t2.getStandardScoreSum())
				return -1;
			else if(t1.getStandardScoreSum() > t2.getStandardScoreSum())
				return 1;
			else
				return 0;
		}
	};
	
	
	public static ArrayList<Tuple> swapValuesInTuples(ArrayList<Tuple> tuples, ArrayList<Integer> C, int ref)
	{
		ArrayList<Integer> unswapped = NumericMethods.obtainClearNumericTuples(tuples, C);
		
		//marcamos los registros de la tupla de referencia como anonimizados:
		for(Record r : tuples.get(ref).getTuple())
			r.setAnonymized(true);
		
		if(!unswapped.isEmpty()) //si todo el cluster está enmascarado, no se intercambia esta tupla
		{
			Random rnd = new Random();
			int i = 0; //indica el atributo que se intercambia en cada momento
			
			//Intercambiamos cada registro con un registro aleatorio para su atributo correspondiente en la tupla:
			for(Record r : tuples.get(ref).getTuple())
			{
				int indexSwap = rnd.nextInt(unswapped.size()); //elige aleatoriamente un elemento de unswapped
				int elected = unswapped.get(indexSwap); //obtiene el índice de la tupla elegida
				
				double electedValue = tuples.get(elected).getTuple().get(i).getNumericValue();
				double refValue = r.getNumericValue();
				
				//Si el registro no está anonimizado, hacemos el intercambio. En otro caso, nada:
				if(!tuples.get(elected).getTuple().get(i).isAnonymized())
				{
					tuples.get(elected).getTuple().get(i).setAnonymized(true);
					tuples.get(ref).getTuple().get(i).setNumericValue(electedValue);
					tuples.get(elected).getTuple().get(i).setNumericValue(refValue);
				}
				i++;
			}
		}
		return tuples;
	}
	
	
	public static ArrayList<Integer> obtainClearNumericTuples(ArrayList<Tuple> tuples, ArrayList<Integer> C)
	{
		ArrayList<Integer> clearTuples = new ArrayList<Integer>();
		
		for(Integer i : C)
		{
			if(!tuples.get(i).isAnonymized())
				clearTuples.add(i);
		}
		return clearTuples;
	}
	
	/**
	 * Descripción: calcula la unidad tipificada de los registros de un dataset
	 * @param dataset
	 * @param atts
	 * @return
	 */
	public static void obtainStandardScore(Dataset dataset, int[] atts)
	{
		for(int i = 0; i < atts.length; i++)
		{
			double mean = Operators.Mean(dataset.getAttribute(atts[i]).get());
			double stdev = Operators.StandardDeviation(dataset.getAttribute(atts[i]).get());
			
			for(Record r : dataset.getAttribute(atts[i]).get())
			{
				double ss = (r.getNumericValue() - mean) / stdev;
				r.setStandardScore(ss);
			}
		}
	}
	
	
}
