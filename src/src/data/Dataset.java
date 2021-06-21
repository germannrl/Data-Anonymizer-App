package src.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Dataset
{
	private ArrayList<Attribute> _dataset;
	
	/**
	 * @param datasetFile
	 * Descripci�n: constructor de la clase Dataset, a partir de un nombre de fichero
	 * que almacena el dataset
	 */
	public Dataset(String datasetFile)
	{
		_dataset = new ArrayList<Attribute>();
		
		File f = null;
		FileReader fr = null;
		BufferedReader br = null;
		
		try
		{
			f = new File(datasetFile); //lee el fichero
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			
			//La primera l�nea de cada fichero contendr� los nombres de los atributos:
			StringTokenizer nombresAtributos = new StringTokenizer(br.readLine(), ",");
			
			//La segunda l�nea de cada fichero contendr� si es nominal o num�rico:
			StringTokenizer tiposAtributos = new StringTokenizer(br.readLine(), ",");
			int columnas = tiposAtributos.countTokens(); //n� de columnas del dataset
			
			for(int i = 1; i <= columnas; i++)
			{
				String nombreAtt = nombresAtributos.nextToken(); //nombre de atributo por cada columna
				int modo = Integer.parseInt(tiposAtributos.nextToken()); //tipo por cada columna
				_dataset.add(new Attribute(br, i, modo, nombreAtt));
				br = new BufferedReader(new FileReader(f));
				br.readLine();
				br.readLine();
			}
			
			fr.close();
		}
		catch(IOException ioe){ ioe.printStackTrace(); }
	}
	
	/**
	 * @param ficheroAnonim
	 * Descripci�n: almacena el dataset en el fichero especificado como par�metro 
	 */
	public void save(String ficheroAnonim)
	{
		File file = new File(ficheroAnonim);
		
		int nFilas = _dataset.get(0).get().size();
		int nColumnas = _dataset.size();
		
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			
			int j = 1;
			
			for(Attribute a : _dataset) //primero los nombres de los atributos
			{
				String nombre = a.getNombre();
				bw.write(nombre);
				if(j != nColumnas)
					bw.write(",");
				j++;
			}
			bw.newLine();
			
			j = 1;
			
			for(Attribute a : _dataset) //despu�s los tipos de datos
			{
				Integer type = a.getTipo();
				bw.write(type.toString());
				if(j != nColumnas)
					bw.write(",");
				j++;
			}
			bw.newLine();
			
			for(int i = 0; i < nFilas; i++)
			{
				j = 1;
				
				for(Attribute a : _dataset)
				{
					Record r = a.get().get(i);
					
					if(a.getTipo() == 1) //si es nominal
					{
							Long offset = r.getSemanticValue();
							bw.write(offset.toString());
					}
					else //o es numeral
					{
						Double value = r.getNumericValue();
						bw.write(value.toString());
					}
					
					if(j != nColumnas)
						bw.write(",");
					
					j++;
				}
				bw.newLine();
			}
			bw.close();
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	
	/**
	 * Descripci�n: actualiza las tuplas del dataset
	 * @param tuples
	 * @param atts
	 */
	public void updateTuples(ArrayList<Tuple> tuples, int[] atts)
	{
		int ntuplas = _dataset.get(0).get().size(); //longitud del dataset (n� de tuplas)
		int natts = atts.length; //n� de atributos considerados para anonimizar
		
		for(int i = 0; i < ntuplas; i++)
		{
			for(int j = 0; j < natts; j++)
			{
				int pos = atts[j]; //posici�n concreta de un atributo
				Record r = tuples.get(i).getTuple().get(j);
				_dataset.get(pos-1).get().set(i, r);
			}
		}
	}
	
	
	public void updateNumericTuples(ArrayList<Tuple> tuples, int[] atts)
	{
		int ntuplas = _dataset.get(0).get().size(); //longitud del dataset (n� de tuplas)
		int natts = atts.length; //n� de atributos considerados para anonimizar
		
		for(int i = 0; i < ntuplas; i++)
		{
			for(int j = 0; j < natts; j++)
			{
				int pos = atts[j]; //posici�n concreta de un atributo
				Record r = tuples.get(i).getTuple().get(j);
				_dataset.get(pos-1).get().set(r.getId() - 1, r);
			}
		}
	}
	
	
	/**
	 * @return _dataset
	 * Descripci�n: m�todo observador, devuelve el dataset completo
	 */
	public ArrayList<Attribute> getDataset() {return _dataset;}
	
	
	/**
	 * @param i
	 * @return attribute
	 * Descripci�n: m�todo observador, devuelve la columna i-�sima del dataset
	 */
	public Attribute getAttribute(int i) {return _dataset.get(i-1);}
	
	
	public void setAttribute(Attribute attribute, int columna)
	{
		_dataset.set(columna-1, attribute);
	}
	
}
