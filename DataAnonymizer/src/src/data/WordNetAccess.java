package src.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.list.PointerTargetNode;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.data.list.PointerTargetTree;
import net.didion.jwnl.data.relationship.Relationship;
import net.didion.jwnl.data.relationship.RelationshipFinder;
import net.didion.jwnl.data.relationship.RelationshipList;
import net.didion.jwnl.dictionary.Dictionary;

public class WordNetAccess implements Ontologia
{
	private static Dictionary _dictionary;
	
	/**
	 * Constructor de la clase WordNetAccess.
	 * Descripci�n: Establece la conexi�n a la ontolog�a de WordNet.
	 */
	public WordNetAccess()
	{
		FileInputStream f;
		try 
		{
			f = new FileInputStream("file_properties.xml"); //Abre el fichero de propiedades.
			JWNL.initialize(f); //Establece la conexi�n.
			_dictionary = Dictionary.getInstance(); //Obtiene la ontolog�a.
		}
		catch (FileNotFoundException e){e.printStackTrace();}
		catch (JWNLException e){e.printStackTrace();}
	}
	
	
	/**
	 * @param word
	 * @return
	 * Descripci�n: devuelve el offset u offsets correspondientes al
	 * concepto que se le pasa como par�metro.
	 */
	public static IndexWord getIndexWord(String word)
	{ 
		IndexWord iw = null;
		try
		{
			iw = _dictionary.lookupIndexWord(POS.NOUN, word); //Busca el concepto sustantivo
			if(iw == null) //Si no lo encuentra, busca el concepto verbo
				iw = _dictionary.lookupIndexWord(POS.VERB, word);
		}
		catch (JWNLException e) {e.printStackTrace();}
		return iw;
	}
	
	/**
	 * @param word
	 * @return
	 * Descripci�n: devuelve el synset de un concepto. Si dicho concepto posee
	 * m�s de un sentido, devuelve el primer synset.
	 */
	public static Synset getSynset(String word, int sense)
	{
		try
		{
			IndexWord iw = getIndexWord(word); //Busca los offsets del concepto.
			return iw.getSense(sense); //Devuelve el primer synset.
		}
		catch (JWNLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * @param offSet
	 * @return
	 * Descripci�n: devuelve el synset asociado al offset que se
	 * pasa como par�metro.
	 */
	public static Synset getSynset(long offSet)
	{
		Synset synset = null;		
		try
		{	//Busca el synset �nico correspondiente al offset, y lo devuelve.
			synset = _dictionary.getSynsetAt(POS.NOUN, offSet);
		}
		catch (JWNLException e) {e.printStackTrace();}
		return synset;
	}

	
	/**
	 * @param synset
	 * @return
	 * Descripci�n: dado un synset, obtiene el sub�rbol completo de
	 * sus hip�nimos, y los almacena en forma de diccionario, con offset
	 * como clave.
	 */
	public static HashMap<Long, Synset> getHyponymTree(Synset synset)
	{	
		HashMap<Long, Synset> hmSynset = new HashMap<Long, Synset>();
		try
		{	//Obtiene el �rbol de hip�nimos del synset:
			PointerTargetTree tree = PointerUtils.getInstance().getHyponymTree(synset);
			@SuppressWarnings("unchecked")
			List<PointerTargetNodeList> branchList = tree.toList(); //Lo transforma a lista.	
			for (PointerTargetNodeList nodeList : branchList)
			{	//Para cada nodo de la lista,
				for (int i = 0; i < nodeList.size(); i++)
				{	//obtiene el conjunto de lemas,
					 PointerTargetNode node = (PointerTargetNode) nodeList.get(i);
					 Synset s = node.getSynset(); //obtiene su synset,
					 if (hmSynset.get(s.getKey())==null) //y si no est� en el HashMap
						 hmSynset.put((Long)s.getKey(), s); //introduce su offset y el synset.
				 }
			}
		}
		catch (JWNLException e) {e.printStackTrace();}
		return hmSynset;
	}
	
	
	/**
	 * @param offset
	 * @return
	 * Descripci�n: dado el offset de un synset, obtiene el sub�rbol completo
	 * de los hip�nimos de dicho synset, y almacena el listado de offsets. 
	 */
	@Override
	public List<Long> getHyponymTree(Long offset)
	{
		HashMap<Long, Synset> hmSynset = new HashMap<Long, Synset>();
		Synset synset = getSynset(offset); //Obtiene el synset correspondiente al offset.
		hmSynset = getHyponymTree(synset); //Obtiene el sub�rbol de hip�nimos.
		List<Long> offsetKeys = new ArrayList<Long>(hmSynset.keySet()); //Obtiene los offsets
		return offsetKeys;
	}
	
	
	/**
	 * @param synset1
	 * @param synset2
	 * @return
	 * Descripci�n: dados dos synsets desambiguados, devuelve el camino con los synsets
	 * entre uno y otro concepto.
	 */
	public static ArrayList<Synset> getPath(Synset synset1, Synset synset2)
	{
		ArrayList<Synset> aSynsetPath = new ArrayList<Synset>();
		try
		{			
			if(synset1.getWord(0).getLemma().equalsIgnoreCase("entity") && 
					synset2.getWord(0).getLemma().equalsIgnoreCase("entity"))
			{	//Si ambos conceptos son la ra�z "entity",
				aSynsetPath.add(synset1); //ser� el �nico synset del camino
				return aSynsetPath;
			}
			if(synset1.getWord(0).getLemma().equalsIgnoreCase("entity"))
			{	//Si el primer concepto es "entity",
				aSynsetPath = getPathToEntity(synset2); //obtener camino del segundo a "entity".
				return aSynsetPath;
			}
			if(synset2.getWord(0).getLemma().equalsIgnoreCase("entity"))
			{	//Si el segundo concepto es "entity",
				aSynsetPath = getPathToEntity(synset1); //obtener camino del primero a "entity".
				return aSynsetPath;
			}
			//Se obtiene el hiper�nimo com�n entre ambos conceptos:
			RelationshipList list = RelationshipFinder.getInstance().
									findRelationships(synset1, synset2, PointerType.HYPERNYM);
			
			PointerTargetNodeList l = ((Relationship) list.get(0)).getNodeList();
			for(int i= 0; i<l.size(); i++)
			{	//Para los nodos del camino, se obtienen sus synsets:
				PointerTargetNode p = (PointerTargetNode)l.get(i);
				aSynsetPath.add(p.getSynset()); //y se a�aden al camino
			}
		}
		catch (JWNLException e) {e.printStackTrace();}
		return aSynsetPath;
	}
	
	
	/**
	 * @param synset
	 * @return
	 * Descripci�n: dado un synset desambiguado, devuelve una lista con los
	 * synsets del camino desde el indicado hasta el synset ra�z.
	 */
	public static ArrayList<Synset> getPathToEntity(Synset synset)
	{
		ArrayList<Synset> aSynsetPath = new ArrayList<Synset>();
		try
		{
			if(synset.getWord(0).toString().equalsIgnoreCase("entity"))
			{	//Si el synset pertenece al concepto "entity",
				aSynsetPath.add(synset); //este ser� el �nico nodo del camino.
				return aSynsetPath;
			}
			
			//Se obtiene el �rbol de hiper�nimos en forma de lista:
			PointerTargetTree pt = PointerUtils.getInstance().getHypernymTree(synset);
			PointerTargetNodeList l = (PointerTargetNodeList)pt.toList().get(0);
			for(int i= 0; i<l.size(); i++)
			{	//Se recorre la lista para obtener cada hiper�nimo hasta "entity":
				PointerTargetNode p = (PointerTargetNode)l.get(i); 
				aSynsetPath.add(p.getSynset()); //y se almacena en el camino.
			}
		}
		catch (JWNLException e) {e.printStackTrace();}
		return aSynsetPath;
	}
	
			
	/**
	 * @param synset
	 * @return
	 * Descripci�n: devuelve el n�mero de nodos en el camino desde el
	 * synset dado hasta "entity".
	 */
	public static int getNumNodesToEntity(Synset synset)
	{
		if(synset.getWord(0).toString().equalsIgnoreCase("entity"))
			return 1; //Si el synset corresponde a "entity", el n�mero de nodos es 1.
		try
		{	//En otro caso, obtiene el �rbol de hiper�nimos en forma de lista
			PointerTargetTree pt = PointerUtils.getInstance().getHypernymTree(synset);
			PointerTargetNodeList l = (PointerTargetNodeList)pt.toList().get(0);
			return l.size(); //y devuelve su longitud.
		}
		catch (JWNLException e) {e.printStackTrace();}
		return -1;
	}
	
	
	/**
	 * @param synset1
	 * @param synset2
	 * @return
	 * Descripci�n: devuelve el n�mero de nodos en el camino entre los dos synsets dados,
	 */
	public static int getNumNodesBetween(Synset synset1, Synset synset2)
	{
		//Si el primer synset es "entity":
		if(synset1.getWord(0).getLemma().toString().equalsIgnoreCase("entity"))
			return getNumNodesToEntity(synset2); //devuelve el n�mero de nodos desde el
												 //segundo a "entity".
		//Si el segundo synset es "entity":
		if(synset2.getWord(0).getLemma().toString().equalsIgnoreCase("entity"))
			return getNumNodesToEntity(synset1); //devuelve el n�mero de nodos desde el
												 //primero a "entity".
		try
		{	//En otro caso, obtiene el hiper�nimo com�n:
			RelationshipList list = RelationshipFinder.getInstance().
									findRelationships(synset1, synset2, PointerType.HYPERNYM);
			return ((Relationship) list.get(0)).getDepth()+1; //y devuelve el n�mero de nodos
															  //entre ellos.
		} 
		catch (JWNLException e) {e.printStackTrace();}
		return -1;
	}
	
	
	/**
	 * @param synset1
	 * @param synset2
	 * @return
	 * Descripci�n: devuelve el n�mero de enlaces entre los dos synsets dados. 
	 */
	public static int getNumLinksBetween(Synset synset1, Synset synset2)
	{
		return getNumNodesBetween(synset1, synset2)-1;
	}
	
	
	/**
	 * @param synset1
	 * @param synset2
	 * @return
	 * Descripci�n: determina el grado de similitud Wu and Palmer entre
	 * los dos synsets dados. El valor estar� comprendido entre 0 y 1.
	 */
	public static float similarity_WP(Synset synset1, Synset synset2)
	{
		float similarity, numerator, denominator; 
		float N3, N1, N2; //N1 y N2 indican n�mero de links y N3 indica n�mero de nodos.
		Lcs lcs=new Lcs(synset1,synset2);		
		N3 = lcs.getDepthLcs();
		N1 = getNumLinksBetween(synset1, lcs.getLcs());
		N2 = getNumLinksBetween(synset2, lcs.getLcs());		
		numerator = (2 * N3);
		denominator = ((2 * N3) + N1 + N2);
		similarity = numerator / denominator;
		return similarity;
	}
	
	
	/**
	 * @param synset1
	 * @param synset2
	 * @return
	 * Descripci�n: determina la distancia Wu and Palmer entre los dos
	 * synsets dados.
	 */
	public static float distance_WP(Synset synset1, Synset synset2)
	{
		float distance = 1 - similarity_WP(synset1, synset2);		
		return distance;
	}
	
	
	/**
	 * @param offset1
	 * @param offset2
	 * @return
	 * Descripci�n: determina la distancia Wu and Palmer entre los dos
	 * synsets correspondientes a los offsets dados.
	 */
	@Override
	public float distance_WP(Long offset1, Long offset2)
	{
		float distance;
		Synset synset1 = getSynset(offset1);
		Synset synset2 = getSynset(offset2);
		distance = distance_WP(synset1,synset2);
		return distance;
	}
}