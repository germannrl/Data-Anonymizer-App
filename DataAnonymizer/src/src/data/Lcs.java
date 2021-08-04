package src.data;

import java.util.ArrayList;
import net.didion.jwnl.data.Synset;


public class Lcs
{
	private Synset _synset1; //primer synset
	private Synset _synset2; //segundo synset
	private Synset _lcs; //synset LCS entre los dos synsets
	private int _depthLcs; //profundidad del synset LCS
	
	/**
	 * @param synset1
	 * @param synset2
	 * @param wordNet
	 * Descripción: construye un synset LCS dados dos synsets.
	 */
	public Lcs(Synset synset1, Synset synset2)
	{
		_synset1 = synset1;
		_synset2 = synset2;
		_lcs = computeLcs(synset1,synset2);
		_depthLcs = computeLcsDepth(_lcs);
	}
	
	
	/**
	 * @param s1
	 * @param s2
	 * @return
	 * Descripción: a partir de los dos synsets dados, determina el synset LCS.
	 */
	private Synset computeLcs(Synset s1, Synset s2)
	{
		ArrayList<Synset> path;
		int depth, minDepth;
		Synset lcs = null;
		
		path = WordNetAccess.getPath(_synset1, _synset2);
		minDepth = 10000;
		for (Synset s : path)
		{
			depth = WordNetAccess.getNumNodesToEntity(s);
			if(depth < minDepth)
			{
				minDepth = depth;
				lcs = s;
			}
		}
		return lcs;
	}
	
	
	/**
	 * @param lcs
	 * @return
	 * Descripción: determina la profundidad del synset LCS.
	 */
	private int computeLcsDepth(Synset lcs)
	{
		int depth = WordNetAccess.getNumNodesToEntity(lcs);
		return depth;
	}
	
	/*Métodos observadores*/
	
	public Synset getSynset1() {return _synset1;}
	
	public Synset getSynset2() {return _synset2;}
	
	public Synset getLcs() {return _lcs;}
	
	public int getDepthLcs() {return _depthLcs;}
}
