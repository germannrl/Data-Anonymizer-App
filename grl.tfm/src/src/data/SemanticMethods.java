package src.data;

import java.util.*;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.uncommons.maths.random.*;

public class SemanticMethods
{
	/**
	 * @param dataset
	 * @param k
	 * @param distances
	 * @return 
	 * Descripci�n: algoritmo de microagregaci�n Individual Ranking, en su versi�n
	 * para atributos nominales.
	 */
	public static ArrayList<Record> individualRanking(Attribute dataset, int k, Distances distances)
	{
		ArrayList<Record> attribute = dataset.get(); //Obtiene la columna del atributo
		Long mdv = SOperators.mostDistantValue(attribute, distances); //Obtener el concepto m�s alejado
		
		while(hasClearValues(attribute))
		{
			int cv = countClearValues(attribute);
			ArrayList<Record> C = null;
			if(cv - k >= k)
				C = obtainMicroCluster(attribute, mdv, k, distances);
			else
				C = obtainMicroCluster(attribute, mdv, cv, distances);

			attribute = microaggregate(C, attribute, distances);
			mdv = SOperators.mostDistantValue(attribute, distances);
		}
		return attribute;
	}
	
	
	public static int countClearValues(ArrayList<Record> a)
	{
		int total = 0;
		for(Record r : a)
		{
			if(!r.isAnonymized())
				total++;
		}
		return total;
	}

	
	/**
	 * @param attribute
	 * @param mdv
	 * @param k
	 * @param distances
	 * @return C
	 * Descripci�n: obtiene un cluster con los k offsets de los registros m�s cercanos al
	 * registro de referencia, incluyendo el registro de referencia
	 */
	public static ArrayList<Record> obtainMicroCluster(ArrayList<Record> attribute, Long mdv, int k,
														Distances distances)
	{
		ArrayList<Record> C = new ArrayList<Record>();
		//En primer lugar, obtenemos las distancias del atributo al concepto de referencia:
		List<Map.Entry<Record,Float>> dist = distances.getDistancesFromAttributeToConcept(attribute, mdv);
		//Ordenamos esas distancias en orden creciente:
		List<Map.Entry<Record,Float>> orderedRecords = sortDomain(dist);
		
		Iterator it_distances = orderedRecords.iterator();
		int i = 0;
		
		while(i < k)
		{
			Map.Entry<Record, Float> key_value = (Map.Entry<Record, Float>) it_distances.next();
			if(!key_value.getKey().isAnonymized())
			{
				C.add(key_value.getKey());
				i++;
			}	
		}
		return C;
	}
	
	
	/**
	 * @param C
	 * @param attribute
	 * @param distances
	 * @return
	 * Descripci�n: se anonimiza el cl�ster, obteniendo el concepto medio de este y asign�ndolo como valor
	 * enmascarado.
	 */
	public static ArrayList<Record> microaggregate(ArrayList<Record> C, ArrayList<Record> attribute, Distances distances)
	{
		ArrayList<Record> meanConcepts = new ArrayList<Record>();
		ArrayList<Integer> positionConcepts = new ArrayList<Integer>();
			
		for(Record offset : C)
		{
			positionConcepts.add(offset.getId() - 1); //lo a�adimos a la lista de posiciones
			meanConcepts.add(new Record(0, attribute.get(offset.getId()-1).getSemanticValue())); //obtener concepto original
		}
		Long mean = SOperators.semanticMean(meanConcepts, distances); //media del cl�ster
			
		for(Integer i : positionConcepts)
		{
			attribute.get(i).setSemanticValue(mean); //como anonimizado, ponemos la media
			attribute.get(i).setAnonymized(true); //y lo marcamos como anonimizado
		}
		return attribute;
	}
	
	
	/**
	 * @param dataset
	 * @param alpha
	 * @param distances
	 * @return attribute
	 * Descripci�n: a partir de la columna de un atributo sem�ntico, el coeficiente de ruido y las
	 * distancias entre los conceptos del dominio, enmascara el atributo mediante adici�n de ruido.
	 * Precondici�n: "dataset" debe ser un atributo sem�ntico.
	 */
	public static ArrayList<Record> semanticAddNoise(Attribute dataset, float alpha, Distances distances)
	{
		Long mean = SOperators.semanticMean(dataset, distances); //Se calcula el valor promedio.
		float variance = SOperators.semanticVariance(dataset, distances, mean); //Se calcula la varianza.	
		ArrayList<Record> attribute = dataset.get(); //Obtiene la columna del atributo.
		generateNoise(attribute, variance, alpha); //Ruido normal N(0, alpha*variance)
		
		for(Record record : attribute)
		{
			double error = record.getPrivacyValue(); //Obtiene el valor de error asociado.
			
			if(error == 0) //Si el error es 0, el concepto enmascarado es el mismo que el original.
			{
				record.setSemanticValue(record.getSemanticValue());
				record.setDistance(0);
				//fin caso 1
			}
			else //En otro caso,
			{	//obtiene ordenadas las distancias de cada concepto del dominio al concepto original:
				Long original = record.getSemanticValue();
				List<Map.Entry<Long,Float>> orderedRecords = 
						sortDomain(distances.getDistancesToConcept(original));
				
				//Se busca el primer concepto cuya distancia al concepto original
				//sea mayor o igual al valor absoluto del ruido:
				
				Iterator it_distances = orderedRecords.iterator(); //puntero a las distancias ordenadas
				Long candidateKey;
				Float candidateDistance;
				Map.Entry<Long, Float> key_value;
				
				do
				{	//Se toma cada par Concepto-Distancia:
					key_value = (Map.Entry<Long, Float>) it_distances.next();
					candidateKey = key_value.getKey();
					candidateDistance = key_value.getValue(); //y se consideran como candidatos
				} //hasta obtener el que cumple las condiciones
				while(candidateDistance < Math.abs(error) && it_distances.hasNext());
				
				if(candidateDistance < Math.abs(error)) //Si ninguno cumple la condici�n,
				{										//asignamos el m�s alejado.
					record.setSemanticValue(candidateKey);
					record.setDistance(candidateDistance);
					//fin caso 2
				}
				else if(original == mean) //Si el original es igual a la media, se asigna el concepto de D m�s cercano
				{						 
					record.setSemanticValue(candidateKey);
					record.setDistance(candidateDistance);
					//fin caso 3
				}
				else if(error > 0) //Si alguno cumple la condici�n y error positivo:
				{
					Float Dist_CandidateToMean = distances.getDistanceBetween(candidateKey, mean);
					Long CandidateB = candidateKey; //El candidato actual ser� la Opci�n B provisional.
					Float Dist_CandidateBToMean = Dist_CandidateToMean;					
					Float Dist_CandidateBToOriginal = candidateDistance;
					
					//Buscar otro concepto candidato: aquel cuya distancia al concepto medio
					//sea mayor que la distancia de la Opci�n B  al concepto medio.
					while(candidateDistance == Dist_CandidateBToOriginal && it_distances.hasNext())
					{
						if(Dist_CandidateToMean > Dist_CandidateBToMean)
						{
							CandidateB = candidateKey; //Se obtiene la Opci�n B definitiva.
							Dist_CandidateBToMean = Dist_CandidateToMean;							
						}
						key_value = (Map.Entry<Long, Float>) it_distances.next();
						candidateKey = key_value.getKey();
						candidateDistance = key_value.getValue();
						Dist_CandidateToMean = distances.getDistanceBetween(candidateKey, mean);
					}
					
					//Calculamos la distancia del concepto original a la media:
					Float Dist_OriginalToMean=distances.getDistanceBetween(original, mean);
					
					//Se busca el concepto cuya distancia al concepto medio sea mayor que
					//la distancia del concepto original al concepto medio:
					while(Dist_CandidateToMean <= Dist_OriginalToMean && it_distances.hasNext())
					{
						key_value = (Map.Entry<Long, Float>)it_distances.next();
						candidateKey = key_value.getKey();
						Dist_CandidateToMean = distances.getDistanceBetween(candidateKey, mean);
					}
					//Si se encuentra, se asigna como enmascarado:
					if(Dist_CandidateToMean > Dist_OriginalToMean)
					{
						record.setSemanticValue(candidateKey);
						record.setDistance(distances.getDistanceBetween(candidateKey, original));
						//fin caso 4
					}
					else //Si no, se asigna la Opci�n B como enmascarado:
					{
						record.setSemanticValue(CandidateB);
						record.setDistance(distances.getDistanceBetween(CandidateB, original));
						//fin caso 5
					}
				}
				else if(error<0) //Si alguno cumple la condici�n y error negativo:
				{
					Float Dist_CandidateToMean = distances.getDistanceBetween(candidateKey, mean);
					Long CandidateB = candidateKey; //El candidato actual ser� la Opci�n B provisional.
					Float Dist_CandidateBToMean = Dist_CandidateToMean;
					Float Dist_CandidateBToOriginal = candidateDistance;
					
					//Buscar otro concepto candidato: aquel cuya distancia al concepto medio
					//sea menor que la distancia de la Opci�n B  al concepto medio.
					while(candidateDistance == Dist_CandidateBToOriginal && it_distances.hasNext())
					{
						if(Dist_CandidateToMean < Dist_CandidateBToMean)
						{
							CandidateB = candidateKey; //Se obtiene la Opci�n B definitiva.
							Dist_CandidateBToMean = Dist_CandidateToMean;
						}
						key_value = (Map.Entry<Long, Float>)it_distances.next();
						candidateKey = key_value.getKey();
						candidateDistance = key_value.getValue();
						Dist_CandidateToMean = distances.getDistanceBetween(candidateKey, mean);
					}
					
					//Calculamos la distancia del concepto original a la media:
					Float Dist_OriginalToMean=distances.getDistanceBetween(original, mean);
					
					//Se busca el concepto cuya distancia al concepto medio sea menor que
					//la distancia del concepto original al concepto medio:
					while(Dist_CandidateToMean >= Dist_OriginalToMean && it_distances.hasNext())
					{
						key_value = (Map.Entry<Long, Float>)it_distances.next();
						candidateKey = key_value.getKey();
						Dist_CandidateToMean = distances.getDistanceBetween(candidateKey, mean);
					}
					//Si se encuentra, se asigna como enmascarado:
					if(Dist_CandidateToMean < Dist_OriginalToMean)
					{
						record.setSemanticValue(candidateKey);
						record.setDistance(distances.getDistanceBetween(candidateKey, original));
						//fin caso 4
					}
					else //Si no, se asigna la Opci�n B como enmascarado:
					{
						record.setSemanticValue(CandidateB);
						record.setDistance(distances.getDistanceBetween(CandidateB, original));
						//fin caso 5
					}
				}
			}
		}
		return attribute;
	}
	
	
	/**
	 * @param attribute
	 * @param variance
	 * @param alpha
	 * Descripci�n: genera ruido aleatorio de una distribuci�n N(0, variance*alpha)
	 * y lo asigna al atributo PrivacyValue de cada registro correspondiente.
	 */
	public static void generateNoise(ArrayList<Record> attribute, double variance, float alpha)
	{
		Random random = new MersenneTwisterRNG();
		GaussianGenerator generator = new GaussianGenerator(0, variance * alpha, random);
		for(Record record : attribute)
			record.setPrivacyValue(generator.nextValue());
	}
	
	
	public static void generateMultivariateNormalNoise(ArrayList<Attribute> attributes,
															double[] means, double[][] covmat)
	{
		int n = attributes.get(0).get().size(); //tama�o de un atributo
		//double error1 = 0, error2 = 0;
		
		MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(means, covmat);
		for(int i = 0; i < n; i++)
		{
			double[] noises = mnd.sample(); //obtenemos P valores de ruido, uno para cada atributo a anonimizar
			
			for(int j = 0; j < noises.length; j++)
				attributes.get(j).get().get(i).setPrivacyValue(noises[j]);
				
			//error1 += Math.abs(noises[0]);
			//error2 += Math.abs(noises[1]);
		}
		//System.out.println("Error abs. att 1: " + error1/n);
		//System.out.println("Error abs. att 2: " + error2/n);
	}
	
	
	/**
	 * @param domainValues
	 * @return
	 * Descripci�n: ordena en orden creciente las distancias del dominio al concepto original
	 */
	private static List<Map.Entry<Long,Float>> sortDomain(HashMap<Long,Float> domainValues)
	{	
		List<Map.Entry<Long, Float> > distancesList = 
		       new LinkedList<Map.Entry<Long, Float> >(domainValues.entrySet()); 
		  
		Collections.sort(distancesList, new Comparator<Map.Entry<Long, Float> >() { 
		public int compare(Map.Entry<Long, Float> dist1,  
		                   Map.Entry<Long, Float> dist2) 
		       { 
		       		return (dist1.getValue()).compareTo(dist2.getValue()); 
		       } 
		});
	    return distancesList;
	}
	
	
	private static List<Map.Entry<Record,Float>> sortDomain(List<Map.Entry<Record,Float>> distancesList)
	{
		Collections.sort(distancesList, new Comparator<Map.Entry<Record, Float> >() { 
			public int compare(Map.Entry<Record, Float> dist1,  
			                   Map.Entry<Record, Float> dist2) 
			       { 
			       		return (dist1.getValue()).compareTo(dist2.getValue()); 
			       } 
			});
		return distancesList;
	}
	
	
	/**
	 * Descripci�n:  a partir de la columna de un atributo sem�ntico, el valor de k y las
	 * distancias entre los conceptos del dominio, enmascara el atributo mediante Rank Swapping.
	 * Precondici�n: "dataset" debe ser un atributo sem�ntico.
	 * @param dataset
	 * @param k
	 * @param distances
	 * @return attribute
	 */
	public static ArrayList<Record> semanticRankSwapping(Attribute dataset, int k, Distances distances)
	{
		ArrayList<Record> attribute = dataset.get();
		Record mdv = SOperators.mostDistantValueRecord(attribute, distances);
		
		while(hasClearValues(attribute))
		{
			ArrayList<Record> C = obtainSwappingCluster(attribute, mdv, k, distances);
			attribute = swapValue(C, attribute, mdv);
			mdv = SOperators.mostDistantValueRecord(attribute, distances);
		}
		return attribute;
	}
	
	
	/**
	 * Descripci�n: para los m�todos de Rank Swapping y Microagregaci�n, determina si queda alg�n registro
	 * sin anonimizar.
	 * @return
	 */
	public static boolean hasClearValues(ArrayList<Record> attribute)
	{
		for(Record r : attribute)
		{
			if(!r.isAnonymized())
				return true;
		}
		return false;
	}
	
	
	/**
	 * Descripci�n: obtiene un cluster con los k offsets de los registros m�s cercanos al
	 * registro de referencia
	 * @param attribute
	 * @param mdv
	 * @param k
	 * @param distances
	 * @return C
	 */
	public static ArrayList<Record> obtainSwappingCluster(ArrayList<Record> attribute, Record mdv, 
									int k, Distances distances)
	{
		ArrayList<Record> C = new ArrayList<Record>();
		//En primer lugar, obtenemos las distancias del atributo al concepto de referencia:
		List<Map.Entry<Record,Float>> dist = distances.getDistancesFromAttributeToConcept(attribute, mdv.getSemanticValue());
		//Ordenamos esas distancias en orden creciente:
		List<Map.Entry<Record,Float>> orderedRecords = sortDomain(dist);
		
		Iterator it_distances = orderedRecords.iterator();
		it_distances.next(); //no introducimos el primero, que es �l mismo
		int i = 0;
		
		while(i < k)
		{
			Map.Entry<Record, Float> key_value = (Map.Entry<Record, Float>) it_distances.next();
			C.add(key_value.getKey());
			i++;
		}
		return C;
	}
	
	
	/**
	 * Descripci�n: intercambia el registro de referencia con uno de los m�s cercanos
	 * @param C
	 * @param attribute
	 * @param mdv
	 */
	public static ArrayList<Record> swapValue(ArrayList<Record> C, ArrayList<Record> attribute, Record mdv)
	{
		ArrayList<Record> unswapped = obtainClearValues(C, attribute);
		attribute.get(mdv.getId()-1).setAnonymized(true);
		attribute.get(mdv.getId()-1).setSemanticValue(mdv.getSemanticValue());
		
		if(!unswapped.isEmpty())
		{
			Random rnd = new Random();
			int index = rnd.nextInt(unswapped.size()); //elige aleatoriamente un elemento de unswapped
			Record elected = unswapped.get(index); //obtiene su offset
			
			long electedValue = elected.getSemanticValue();
			long mdvValue = mdv.getSemanticValue();
			
			attribute.get(mdv.getId()-1).setSemanticValue(electedValue);
			attribute.get(elected.getId()-1).setSemanticValue(mdvValue);
			attribute.get(elected.getId()-1).setAnonymized(true);
		}
		return attribute;
	}
	
	
	/**
	 * Descripci�n: devuelve aquellos registros que a�n no han sido anonimizados.
	 * @param C
	 * @param attribute
	 * @return unswappedValues
	 */
	public static ArrayList<Record> obtainClearValues(ArrayList<Record> C, ArrayList<Record> attribute)
	{
		ArrayList<Record> clearValues = new ArrayList<Record>();
		
		for(Record offset : C)
		{
			int position = offset.getId()-1;
			
			if(position != -1)
			{
				Record r = attribute.get(position);
				if(!r.isAnonymized())
					clearValues.add(r);
			}
		}
		return clearValues;
	}
	
	
	/************* M�TODOS MULTIVARIADOS ***********************************/
	
	
	public static ArrayList<Attribute> semanticIndividualRankingMultivariate(Dataset dataset, int[] atts, 
																		int k, Distances distances)
	{
		ArrayList<Tuple> tuples = Tuple.toArrayOfTuples(dataset, atts);
		Tuple mdt = SOperators.mostDistantTuple(tuples, distances);
		
		while(hasClearTuples(tuples)) //mientras haya tuplas sin anonimizar,
		{
			int cv = countClearTuples(tuples);
			
			System.out.println(cv + "/" + tuples.size());
			
			ArrayList<Tuple> C = null;
			if(cv - k >= k)
				C= obtainMicroClusterOfTuples(tuples, mdt, k, distances);
			else
				C= obtainMicroClusterOfTuples(tuples, mdt, cv, distances);
			
			tuples = microaggregateTuples(C, tuples, distances);
			mdt = SOperators.mostDistantTuple(tuples, distances);
		}
		dataset.updateTuples(tuples, atts); //actualiza el dataset con los atributos anonimizados
		return dataset.getDataset();
	}
	
	
	/**
	 * Descripci�n: determina si alguna tupla est� sin anonimizar
	 * @param tuples
	 * @return
	 */
	public static int countClearTuples(ArrayList<Tuple> tuples)
	{
		int total = 0;
		for(Tuple t : tuples)
		{
			if(!t.isAnonymized())
				total++;
		}
		return total;
	}
	
	
	/**
	 * Descripci�n: crea el cluster con las k tuplas m�s cercanas a la tupla de referencia, incluida esta
	 * @param tuples
	 * @param mdt
	 * @param k
	 * @param distances
	 * @return
	 */
	public static ArrayList<Tuple> obtainMicroClusterOfTuples(ArrayList<Tuple> tuples, Tuple mdt, int k, Distances distances)
	{
		ArrayList<Tuple> C = new ArrayList<Tuple>();
		//En primer lugar, obtenemos las distancias del conjunto de tuplas a la tupla de referencia:
		List<Map.Entry<Tuple,Float>> dist = getDistancesFromReferenceToTuples(tuples, mdt, distances);
		//A continuaci�n, ordenamos las distancias de menor a mayor:
		List<Map.Entry<Tuple,Float>> orderedTuples = sortTuplesDistances(dist);
		
		Iterator it_distances = orderedTuples.iterator();
		int i = 0;
		
		while(i < k)
		{
			Map.Entry<Tuple, Float> key_value = (Map.Entry<Tuple, Float>) it_distances.next();
			if(!key_value.getKey().isAnonymized())
			{
				C.add(key_value.getKey());
				i++;
			}
		}
		return C;
	}
	
	
	public static ArrayList<Tuple> microaggregateTuples(ArrayList<Tuple> C, ArrayList<Tuple> tuples, Distances distances)
	{
		int n = C.get(0).getTuple().size(); //n� de atributos a anonimizar
	
		for(int i = 0; i < n; i++)
		{
			ArrayList<Record> recordCluster = new ArrayList<Record>();
			for(Tuple c : C)
				recordCluster.add(c.getTuple().get(i));
		
			Long mean = SOperators.semanticMean(recordCluster, distances); //media del cl�ster
			
			for(Tuple c : C)
			{
				int rId = c.getTuple().get(i).getId() - 1;
				tuples.get(rId).getTuple().get(i).setSemanticValue(mean); //como anonimizado, ponemos la media
				tuples.get(rId).getTuple().get(i).setAnonymized(true); //y lo marcamos como anonimizado
			}
		}
		return tuples;
	}
	
	
	/**
	 * Descripci�n: Adici�n sem�ntica de ruido multivariado, en su versi�n para dos atributos
	 * @param attributes
	 * @param alpha
	 * @param distances
	 * @return
	 */
	public static ArrayList<Attribute> semanticAddNoiseMultivariate(Dataset dataset, int[] atts,
															 float alpha, Distances distances)
	{
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(dataset.getAttribute(atts[0]));
		attributes.add(dataset.getAttribute(atts[1]));
		
		int n = attributes.size(); //n� de atributos a anonimizar
		double[] means = new double[n]; //array con media cero
		double[][] covMatrix = SOperators.createSemanticCovarianceMatrix(attributes, alpha, distances);
		
		SemanticMethods.generateMultivariateNormalNoise(attributes, means, covMatrix); //asignamos ruidos
		
		ArrayList<Record> a = addNoise2attributesVersion(attributes.get(0), attributes.get(1), distances);
		ArrayList<Record> b = addNoise2attributesVersion(attributes.get(1), attributes.get(0), distances);
		
		attributes.get(0).set(a);
		attributes.get(1).set(b);
	
		dataset.setAttribute(attributes.get(0), atts[0]);
		dataset.setAttribute(attributes.get(1), atts[1]);
		
		return dataset.getDataset();
	}
	
	
	private static ArrayList<Record> addNoise2attributesVersion(Attribute a1, Attribute a2, Distances distances)
	{
		ArrayList<Record> a = a1.get(); //Obtiene la columna del atributo.
		ArrayList<Record> b = a2.get();
		int n = a.size(); //tama�o de los atributos
		int j = 0; //contador para el atributo "b"
		
		for(Record record : a)
		{
			Long reg_B = b.get(j).getSemanticValue(); //offset del registro i-�simo del atributo "b"
			j++;
			double error = record.getPrivacyValue(); //ruido del registro i-�simo del atributo "a"
			
			if(error == 0) //Si el error es 0, el concepto enmascarado es el mismo que el original.
			{
				record.setSemanticValue(record.getSemanticValue());
				record.setDistance(0);
			}
			else //En otro caso, obtiene ordenadas las distancias de cada concepto del dominio al concepto original:
			{
				Long original = record.getSemanticValue();
				List<Map.Entry<Long,Float>> orderedRecords = 
						sortDomain(distances.getDistancesToConcept(original));
				
				//Se busca el primer concepto cuya distancia al concepto original
				//sea mayor o igual al valor absoluto del ruido:
				
				Iterator it_distances = orderedRecords.iterator(); //puntero a las distancias ordenadas
				Long candidateKey;
				Float candidateDistance;
				Map.Entry<Long, Float> key_value;
				
				do
				{	//Se toma cada par Concepto-Distancia:
					key_value = (Map.Entry<Long, Float>) it_distances.next();
					candidateKey = key_value.getKey();
					candidateDistance = key_value.getValue(); //y se consideran como candidatos
				} //hasta obtener el que cumple las condiciones
				while(candidateDistance < Math.abs(error) && it_distances.hasNext());
				
				if(candidateDistance < Math.abs(error)) //Si ninguno cumple la condici�n,
				{										//asignamos el m�s alejado.
					record.setSemanticValue(candidateKey);
					record.setDistance(candidateDistance);
				}
				
				else if(error > 0) //Si alguno cumple la primera condici�n y el error es positivo:
				{	
					//Se busca el concepto cuya distancia al concepto de reg_B sea mayor que la distancia
					//del concepto original de A al concepto de reg_B
				
					Float distFromAtoB = distances.getDistanceBetween(original, reg_B);
					Float candidateDistanceToB = distances.getDistanceBetween(candidateKey, reg_B);
					
					while(candidateDistanceToB <= distFromAtoB && it_distances.hasNext())
					{
						key_value = (Map.Entry<Long, Float>)it_distances.next();
						candidateKey = key_value.getKey();
						candidateDistanceToB = distances.getDistanceBetween(candidateKey, reg_B);
					}
					
					record.setSemanticValue(candidateKey);
					record.setDistance(distances.getDistanceBetween(candidateKey, original));
				}
					
				else if(error < 0) //Si alguno cumple la primera condici�n y el error es negativo:
				{
					//Se busca el concepto cuya distancia al concepto de reg_B sea menor que la distancia
					//del concepto original de A al concepto de reg_B
					
					Float distFromAtoB = distances.getDistanceBetween(original, reg_B);
					Float candidateDistanceToB = distances.getDistanceBetween(candidateKey, reg_B);
					
					while(candidateDistanceToB >= distFromAtoB && it_distances.hasNext())
					{
						key_value = (Map.Entry<Long, Float>)it_distances.next();
						candidateKey = key_value.getKey();
						candidateDistanceToB = distances.getDistanceBetween(candidateKey, reg_B);
					}
					
					record.setSemanticValue(candidateKey);
					record.setDistance(distances.getDistanceBetween(candidateKey, original));
				}	
			}
		}
		return a;
	}
	
	
	/**
	 * Descripci�n: m�todo multivariado sem�ntico de intercambio por ranking
	 * @param dataset
	 * @param atts
	 * @param k
	 * @param distances
	 * @return
	 */
	public static ArrayList<Attribute> semanticRankSwappingMultivariate(Dataset dataset, int[] atts, int k, Distances distances)
	{
		ArrayList<Tuple> tuples = Tuple.toArrayOfTuples(dataset, atts);
		Tuple mdt = SOperators.mostDistantTuple(tuples, distances);
		
		while(hasClearTuples(tuples)) //mientras haya tuplas sin anonimizar,
		{
			ArrayList<Tuple> C = obtainSwappingClusterOfTuples(tuples, mdt, k, distances); //obtener cluster
			tuples = swapValuesInTuples(C, tuples, mdt); //intercambiar registros
			mdt = SOperators.mostDistantTuple(tuples, distances);
		}
		
		dataset.updateTuples(tuples, atts); //actualiza el dataset con los atributos anonimizados
		return dataset.getDataset();
	}
	
	
	/**
	 * Descripci�n: determina si alguna tupla est� sin anonimizar
	 * @param tuples
	 * @return
	 */
	public static boolean hasClearTuples(ArrayList<Tuple> tuples)
	{
		for(Tuple t : tuples)
		{
			if(!t.isAnonymized())
				return true; 
		}
		return false;
	}
	
	
	/**
	 * Descripci�n: crea el cluster con las k tuplas m�s cercanas a la tupla de referencia
	 * @param tuples
	 * @param mdt
	 * @param k
	 * @param distances
	 * @return
	 */
	public static ArrayList<Tuple> obtainSwappingClusterOfTuples(ArrayList<Tuple> tuples, Tuple mdt, int k, Distances distances)
	{
		ArrayList<Tuple> C = new ArrayList<Tuple>();
		//En primer lugar, obtenemos las distancias del conjunto de tuplas a la tupla de referencia:
		List<Map.Entry<Tuple,Float>> dist = getDistancesFromReferenceToTuples(tuples, mdt, distances);
		//A continuaci�n, ordenamos las distancias de menor a mayor:
		List<Map.Entry<Tuple,Float>> orderedTuples = sortTuplesDistances(dist);
		
		Iterator it_distances = orderedTuples.iterator();
		it_distances.next(); //no introducimos el primero, que es �l mismo
		int i = 0;
		
		while(i < k)
		{
			Map.Entry<Tuple, Float> key_value = (Map.Entry<Tuple, Float>) it_distances.next();
			C.add(key_value.getKey());
			i++;
		}
		return C;
	}
	
	
	/**
	 * Descripci�n: determina la distancia de cada tupla del dataset a la tupla de referencia mdt
	 * @param tuples
	 * @param mdt
	 * @param distances
	 * @return
	 */
	public static List<Map.Entry<Tuple,Float>> getDistancesFromReferenceToTuples(ArrayList<Tuple> tuples, Tuple mdt,
																				Distances distances)
	{
		List<Map.Entry<Tuple,Float>> distToTuple = new LinkedList<Map.Entry<Tuple, Float> >();
		
		for(Tuple t : tuples)
			distToTuple.add(new AbstractMap.SimpleEntry(t, SOperators.semanticDistanceBetweenTuples(mdt, t, distances)));
		
		return distToTuple;
	}
	
	
	/**
	 * Descripci�n: ordena en orden creciente las distancias de cada tupla a la tupla de referencia
	 * @param distancesList
	 * @return
	 */
	public static List<Map.Entry<Tuple,Float>> sortTuplesDistances(List<Map.Entry<Tuple,Float>> distancesList)
	{
		Collections.sort(distancesList, new Comparator<Map.Entry<Tuple, Float> >() { 
			public int compare(Map.Entry<Tuple, Float> dist1,  
			                   Map.Entry<Tuple, Float> dist2) 
			       { 
			       		return (dist1.getValue()).compareTo(dist2.getValue()); 
			       } 
			});
		return distancesList;
	}
	
	
	/**
	 * Descripci�n: intercambia los registros de la tupla de referencia con algunos de los del cluster
	 * @param C
	 * @param tuples
	 * @param mdt
	 * @return
	 */
	public static ArrayList<Tuple> swapValuesInTuples(ArrayList<Tuple> C, ArrayList<Tuple> tuples, Tuple mdt)
	{
		ArrayList<Tuple> unswapped = obtainClearTuples(C); //obtiene las tuplas del cluster que no est�n anonimizadas
		
		//marcamos los registros de la tupla de referencia como anonimizados:
		for(Record r : tuples.get(mdt.getIndex()).getTuple())
			r.setAnonymized(true);			
		
		if(!unswapped.isEmpty()) //si todo el cluster est� enmascarado, no se intercambia esta tupla
		{
			Random rnd = new Random();
			int i = 0; //indica el atributo que se intercambia en cada momento
			
			//Intercambiamos cada registro con un registro aleatorio para su atributo correspondiente en la tupla:
			for(Record r : tuples.get(mdt.getIndex()).getTuple())
			{
				int indexSwap = rnd.nextInt(unswapped.size()); //elige aleatoriamente un elemento de unswapped
				Tuple elected = unswapped.get(indexSwap); //obtiene la tupla elegida
				
				long electedValue = elected.getTuple().get(i).getSemanticValue(); //valor del registro i-�simo
				long mdtValue = r.getSemanticValue();
				
				//Si el registro no est� anonimizado, hacemos el intercambio. En otro caso, nada:
				if(!elected.getTuple().get(i).isAnonymized())
				{
					r.setSemanticValue(electedValue);
					tuples.get(elected.getIndex()).getTuple().get(i).setAnonymized(true);
					tuples.get(elected.getIndex()).getTuple().get(i).setSemanticValue(mdtValue);
				}
				i++;
			}	
		}
		return tuples;
	}
	
	
	/**
	 * Descripci�n: obtiene las tuplas del cluster que tienen alg�n registro sin anonimizar
	 * @param C
	 * @return
	 */
	public static ArrayList<Tuple> obtainClearTuples(ArrayList<Tuple> C)
	{
		ArrayList<Tuple> clearTuples = new ArrayList<Tuple>();
		
		for(Tuple t : C)
		{
			if(!t.isAnonymized())
				clearTuples.add(t);
		}
		return clearTuples;
	}
	
}
