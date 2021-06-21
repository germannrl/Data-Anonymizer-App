package src.data;

import java.util.ArrayList;

public class Tuple
{
	private ArrayList<Record> t; //tupla
	int[] pos; //columnas de los atributos en el dataset
	int _index; //posición de la tupla dentro del dataset (de 0 a dataset-1)
	double standardScoreSum; //suma de las unidades tipificadas 
	
	
	/**
	 * Descripción: obtiene la tupla index-ésima del dataset dado, formada por los atributos indicados en el vector
	 * @param index
	 * @param atts (debe estar ordenado de menor a mayor)
	 * @param dataset
	 */
	public Tuple(int index, int[] atts, Dataset dataset)
	{
		_index = index;
		t = new ArrayList<Record>();
		pos = new int[atts.length];
		standardScoreSum = 0;
		
		for(int i = 0; i < atts.length; i++)
		{
			pos[i] = atts[i];
			
			int numAtt = atts[i]; //obtiene la posición del atributo de donde se obtendrá un elemento de la tupla (de 1 a n)
			Record r = new Record(dataset.getAttribute(numAtt).get().get(index)); //obtiene el registro de la tupla
			standardScoreSum += r.getStandardScore();
			t.add(r);
		}
	}
	
	
	/**
	 * Descripción: transforma un dataset en un array de tuplas (sólo con los atributos indicados)
	 * @param atts
	 * @return
	 */
	public static ArrayList<Tuple> toArrayOfTuples(Dataset dataset, int[] atts)
	{
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		int numFilas = dataset.getAttribute(1).get().size(); //número de filas
		
		for(int i = 0; i < numFilas; i++)
		{
			Tuple t = new Tuple(i, atts, dataset);
			tuples.add(t);
		}
		return tuples;
	}
	
	
	/**
	 * Descripción: determina si una tupla está completamente anonimizada (todos sus registros lo están)
	 * @return
	 */
	public boolean isAnonymized()
	{	
		for(Record r : t)
		{
			if(!r.isAnonymized())
				return false;
		}
		return true;
	}
	
	
	
	public ArrayList<Record> getTuple() {return t;}
	
	public void setTuple(ArrayList<Record> tuple) {t = tuple;}
	
	public int[] getPositions() {return pos;}
	
	public int getIndex() {return _index;}
	
	public double getStandardScoreSum() {return standardScoreSum;}
	
}
