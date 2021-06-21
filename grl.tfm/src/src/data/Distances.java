package src.data;
import java.io.*;
import java.util.*;
import src.gui.IfrGenerateDistances;


public class Distances
{
	private HashMap<Long, HashMap<Long, Float>> _distances;
 	
	/**
	 * @param file
	 * Descripción: carga las distancias de Dominio X Dominio desde un fichero
	 * de texto plano.
	 */
	public Distances(String file)
	{
		File f = null;
		FileReader fr = null;
		BufferedReader br = null;
		String line;
		_distances = new HashMap<Long, HashMap<Long, Float>>();
		
		try
		{
			f = new File(file); //lee el fichero
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			
			line = br.readLine(); //Obtenemos la primera línea (el dominio)
			StringTokenizer st = new StringTokenizer(line, " ");
			List<Long> domain = new ArrayList<Long>();
			while(st.hasMoreElements())
			{
				Long concepto = Long.parseLong(st.nextToken());
				domain.add(concepto);
			}
			
			for(Long concept : domain)
			{
				//Distancias de un concepto a todos los demás del dominio:
				HashMap<Long, Float> conceptDistances = new HashMap<Long, Float>();
				
				line = br.readLine();
				st = new StringTokenizer(line, " ");
				int i = 0;
				while(st.hasMoreElements())
				{
					Long conceptKey = domain.get(i);
					float fDistance = Float.parseFloat(st.nextToken());
					conceptDistances.put(conceptKey, fDistance);
					i++;
				}
				_distances.put(concept, conceptDistances);
			}
			br.close();
		}
		catch (IOException ioe) {ioe.printStackTrace();}
	}
	
	
	/**
	 * 
	 */
	public void saveDistances(String sNombre)
	{
		File file = new File(sNombre);
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
			for(Map.Entry<Long, HashMap<Long, Float>> i : _distances.entrySet())
			{	
				bw.write(i.getKey() + " ");
			}
			bw.newLine();
		
			for(Map.Entry<Long, HashMap<Long, Float>> i : _distances.entrySet()) 
			{
				HashMap<Long, Float> dist2ref = i.getValue();
				for(Map.Entry<Long, Float> j : dist2ref.entrySet())
				{
					bw.write(j.getValue() + " ");
				}
				bw.newLine();
			}
			bw.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param domain
	 * Constructor de la clase Distances
	 * Descripción: calcula y almacena las distancias entre todos los conceptos
	 * del dominio dado.
	 */
	public Distances(List<Long> domain)
	{
		int i = 0;
		_distances = new HashMap<Long, HashMap<Long, Float>>();
		for(Long offset : domain)
		{
			System.out.println(i + "/" + domain.size());
			//Distancias de un concepto a todos los demás del dominio:
			HashMap<Long, Float> conceptDistances = new HashMap<Long, Float>();
			
			for(Long concept : domain)
			{
				float dist = IfrGenerateDistances.ontology.distance_WP(offset, concept);
				conceptDistances.put(concept, dist);
			}
			//Distancias de cada concepto:
			_distances.put(offset, conceptDistances);
			i++;
		}
	}
	
	public HashMap<Long, HashMap<Long,Float>> getDistances(){return _distances;}
	
	
	public HashMap<Long, Float> getDistancesToConcept(Long offset){return _distances.get(offset);}
	
	
	public List<Map.Entry<Record,Float>> getDistancesFromAttributeToConcept(ArrayList<Record> attribute,
																	Long offset)
	{
		HashMap<Long, Float> distances = _distances.get(offset);
		
		List<Map.Entry<Record,Float>> distToAttribute = new LinkedList<Map.Entry<Record, Float> >(); 
		
		for(Record r : attribute)
			distToAttribute.add(new AbstractMap.SimpleEntry(r, distances.get(r.getSemanticValue())));
		
		return distToAttribute;
	}
	
	
	public float getDistanceBetween(Long offset1, Long offset2)
	{
		return _distances.get(offset1).get(offset2);
	}
	
	public void print()
	{
		for(Map.Entry<Long, HashMap<Long, Float>> i : _distances.entrySet()) 
		{
			System.out.println("Distancias del concepto " + i.getKey() + ": ");
			HashMap<Long, Float> dist2ref = i.getValue();
			for(Map.Entry<Long, Float> j : dist2ref.entrySet())
			{
				System.out.println("     al concepto " + j.getKey() + ": " + j.getValue());
			}
			System.out.println("====================================");
		}		
	}
	
}
