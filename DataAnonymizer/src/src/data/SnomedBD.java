package src.data;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mays.snomed.ConceptBasic;
import com.mays.snomed.SnomedIsa;
import com.mays.util.Sql;

public class SnomedBD implements Ontologia
{
	private static SnomedIsa database;
	
	/**
	 * Constructor de la clase SnomedBD.
	 */
	public SnomedBD()
	{
		try
		{
			Connection conn = Sql.getConnection("./ddb", "snomed-test");	
			this.database = new SnomedIsa();
			this.database.init(conn);
		}
		catch (Exception e) {e.printStackTrace();}
	}
	
	
	public static ArrayList<ConceptBasic> obtainConcept(String name)
	{
		name = name.toLowerCase();
		ArrayList<ConceptBasic> conceptos = database.getConcepts();
		ArrayList<ConceptBasic> cb = new ArrayList<ConceptBasic>();
		for(int i = 0; i < conceptos.size(); i++)
		{
			ConceptBasic c = conceptos.get(i);
			String nameC = c.getFullySpecifiedName().toLowerCase();
			if(nameC.contains(name))
				cb.add(c);
		}
		return cb;
	}
	
	
	public static String obtainConcept(Long id)
	{	
		return database.getConcept(id).getFullySpecifiedName();
	}
	
	/**
	 * @param id1
	 * @param id2
	 * @return distanciaLCS
	 */
	public static int numPathLinks(Long id1, Long id2)
	{
		//Calcula los ancestros desde id1 e id2 hasta la raíz:
		HashMap<Long, Integer> ancestrosId1 = encontrarAncestrosConDistanciaMinima(id1);
		HashMap<Long, Integer> ancestrosId2 = encontrarAncestrosConDistanciaMinima(id2);
		
		//Toma aquel que tiene menos profundidad:
		if(ancestrosId1.size() > ancestrosId2.size())
		{
			HashMap<Long, Integer> ancestrosTemporal = ancestrosId1;
			ancestrosId1 = ancestrosId2;
			ancestrosId2 = ancestrosTemporal;
		}
		
		int distanciaLCS = Integer.MAX_VALUE;
		for(Long id : ancestrosId1.keySet()) 
			if(ancestrosId2.containsKey(id) && (distanciaLCS > ancestrosId1.get(id) + ancestrosId2.get(id))) 
				distanciaLCS = ancestrosId1.get(id) + ancestrosId2.get(id);
		return distanciaLCS;
	}
	
	
	/**
	 * Descripción: determina la distancia Wu and Palmer entre los dos conceptos
	 * correspondientes a los offsets dados
	 */
	@Override
	public float distance_WP(Long offset1, Long offset2)
	{
		float distance = 1 - similarity_WP(offset1, offset2);
		return distance;
	}
	
	
	/**
	* Descripción: determina la similitud Wu and Palmer entre los dos
	* conceptos correspondientes a los offsets dados.
	*/
	public static float similarity_WP(Long offset1, Long offset2)
	{	
		Long idLCS = getLCS(offset1, offset2);
		
		//if(idLCS==-1) return 0;
		
		int depthLCS = conceptDepth(idLCS);
		int pathLinks1 = numPathLinks(offset1, idLCS);
		int pathLinks2 = numPathLinks(offset2, idLCS);
		
		return ( (float) 2 * depthLCS / (float) (2 * depthLCS + pathLinks1 + pathLinks2));
	}
	
	
	/**
	 * Descripción: dado un offset, devuelve el subárbol completo de hipónimos de dicho concepto.
	 */
	@Override
	public List<Long> getHyponymTree(Long offset)
	{
		return database.getDescendants(offset);
	}
	
	
	/**
	 * @param id
	 * @return ancestrosDistanciaMin
	 * Descripción: devuelve los hiperónimos de un concepto dado, y las distancias mínimas
	 * desde el concepto hasta dichos hiperónimos.
	 */
	public static HashMap<Long, Integer> encontrarAncestrosConDistanciaMinima(Long id)
	{
		int distancia = 0;
		HashMap<Long, Integer> ancestrosDistanciaMin = new HashMap<Long, Integer>();
		Set<Long> conjuntoPadres = new HashSet<Long>();
		Set<Long> conjuntoAbuelos = new HashSet<Long>();
		Set<Long> conjuntoTemporal;
		ancestrosDistanciaMin.put(id, distancia);
		conjuntoPadres.addAll(database.getParents(id));

		while(conjuntoPadres.size() > 0)
		{
			distancia++;
			for (Long idPadre : conjuntoPadres)
			{
				if(!ancestrosDistanciaMin.containsKey(idPadre))
					ancestrosDistanciaMin.put(idPadre, distancia);
				conjuntoAbuelos.addAll(database.getParents(idPadre));
			}
			conjuntoTemporal = conjuntoPadres;
			conjuntoPadres = conjuntoAbuelos;
			conjuntoAbuelos = conjuntoTemporal;
			conjuntoAbuelos.clear();	
		}
		return ancestrosDistanciaMin;
	}
	
	
	/**
	 * @param id
	 * @return distanciaMaxima
	 * Descripción: 
	 */
	public static int conceptDepth (Long id)
	{
		int distanciaMaxima=0;
		Set<Long> conjuntoPadres = new HashSet<Long>();
		Set<Long> conjuntoAbuelos = new HashSet<Long>();
		Set<Long> conjuntoTemporal;
		conjuntoPadres.addAll(database.getParents(id));	
		while(conjuntoPadres.size() > 0)
		{
			distanciaMaxima++;
			for (Long idPadre : conjuntoPadres)
				conjuntoAbuelos.addAll(database.getParents(idPadre));
			
			conjuntoTemporal = conjuntoPadres;
			conjuntoPadres = conjuntoAbuelos;
			conjuntoAbuelos = conjuntoTemporal;
			conjuntoAbuelos.clear();
		}
		return distanciaMaxima + 1;
	}
	
	
	public static Long getLCS(Long offset1, Long offset2)
	{
		HashMap<Long, Integer> ancestrosId1 = encontrarAncestrosConDistanciaMinima(offset1);
		HashMap<Long, Integer> ancestrosId2 = encontrarAncestrosConDistanciaMinima(offset2);
		
		if(ancestrosId1.size() > ancestrosId2.size())
		{
			HashMap<Long, Integer> ancestrosTemporal = ancestrosId1;
			ancestrosId1 = ancestrosId2;
			ancestrosId2 = ancestrosTemporal;
		}
		
		long idLCS = -1;
		int distanciaLCS = Integer.MAX_VALUE;
		for(Long id : ancestrosId1.keySet()) 
			if(ancestrosId2.containsKey(id) && 
					(distanciaLCS > ancestrosId1.get(id) + ancestrosId2.get(id)))
			{
				idLCS = id;
				distanciaLCS = ancestrosId1.get(id) + ancestrosId2.get(id);
			}
		return idLCS;
	}
	
	
	/*public static Long getLCS(List<Long> offsets)
	{
		Long LCS = offsets.get(0);
		int i = 0;
		
		for(Long offset : offsets)
		{
			System.out.println(i + "/" + offsets.size());
			
			if(!database.getDescendants(LCS).contains(offset) && LCS !=offset)
			{
				HashMap<Long,Integer> ancestrosLCS = encontrarAncestrosConDistanciaMinima(LCS);
				HashMap<Long,Integer> ancestrosOffset = encontrarAncestrosConDistanciaMinima(offset);
				
				if(ancestrosLCS.size() > ancestrosOffset.size())
				{
					HashMap<Long,Integer> ancestrosTemporal = ancestrosLCS;
					ancestrosLCS = ancestrosOffset;
					ancestrosOffset = ancestrosTemporal;
				}
				
				int distanciaLCS = Integer.MAX_VALUE;
				
				for(Long id : ancestrosLCS.keySet())
				{
					if(ancestrosOffset.containsKey(id) && 
							(distanciaLCS > ancestrosLCS.get(id) + ancestrosOffset.get(id)))
					{
						LCS = id;
						distanciaLCS = ancestrosLCS.get(id) + ancestrosOffset.get(id);
					}
				}
			}
			System.out.println(LCS + ":" + obtainConcept(LCS));
			i++;
		}
		return LCS;
	}*/
	
	
	public static List<Long> obtenerDescendientes(Long offset)
	{		
		return database.getDescendants(offset);
	}
	
	public static List<Long> obtenerHijos(Long offset)
	{
		return database.getChildren(offset);
	}
	
	
}