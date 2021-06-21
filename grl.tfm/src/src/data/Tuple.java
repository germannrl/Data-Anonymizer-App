package src.data;

import java.util.ArrayList;

public class Tuple
{
	private ArrayList<Record> t; //tupla
	int[] pos; //columnas de los atributos en el dataset
	int _index; //posici�n de la tupla dentro del dataset (de 0 a dataset-1)
	double standardScoreSum; //suma de las unidades tipificadas 
	
	
	/**
	 * Descripci�n: obtiene la tupla index-�sima del dataset dado, formada por los atributos indicados en el vector
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
			
			int numAtt = atts[i]; //obtiene la posici�n del atributo de donde se obtendr� un elemento de la tupla (de 1 a n)
			Record r = new Record(dataset.getAttribute(numAtt).get().get(index)); //obtiene el registro de la tupla
			standardScoreSum += r.getStandardScore();
			t.add(r);
		}
	}
	
	
	/**
	 * Descripci�n: transforma un dataset en un array de tuplas (s�lo con los atributos indicados)
	 * @param atts
	 * @return
	 */
	public static ArrayList<Tuple> toArrayOfTuples(Dataset dataset, int[] atts)
	{
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		int numFilas = dataset.getAttribute(1).get().size(); //n�mero de filas
		
		for(int i = 0; i < numFilas; i++)
		{
			Tuple t = new Tuple(i, atts, dataset);
			tuples.add(t);
		}
		return tuples;
	}
	
	
	/**
	 * Descripci�n: determina si una tupla est� completamente anonimizada (todos sus registros lo est�n)
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
