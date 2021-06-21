package src.data;
import java.util.*;
import java.io.*;


public class Attribute
{
	private ArrayList<Record> _attribute;
	private int _tipo; //semántico o continuo
	private boolean _masked; //indica si ha sido o no enmascarado
	private int columna; //columna que ocupa el atributo en el dataset (entre 1 y el numTotal de columnas)
	private String _nombre; //nombre del atributo
	
	/**
	 * @param column
	 * Constructor de la clase Attribute.
	 * 
	 * Descripción: almacena la columna dada (atributo) que se va a enmascarar.
	 * 
	 * Precondiciones: los valores del atributo nominal se expresan mediante offsets.
	 * "column" es un valor entre 1 y "p". "modo" indica si es un atributo nominal o numérico.
	 */
	public Attribute(BufferedReader br, int column, int modo, String nombre)
	{
		_attribute = new ArrayList<Record>();
		_tipo = modo;
		_masked = false;
		columna = column;
		_nombre = nombre; 

		String line;
		long offset;
		float value;
		int row;
		
		try
		{	
			row = 1; //nº de tupla en la que se encuentra el registro
			
			while((line = br.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(line, ",");
				for(int i = 1; i < column; i++)
					st.nextToken();
				
				Record r = null;
				
				if(modo == 1) //nominal
				{
					offset = Long.parseLong(st.nextToken()); //obtiene el offset de la fila "row"
					r = new Record(row, offset); //crea un nuevo registro
				}
				else //numérico
				{
					value = Float.parseFloat(st.nextToken());
					r = new Record(row, value);
				}
				_attribute.add(r); //almacena el registro en la lista del atributo
				row++; //pasa a la siguiente fila
			}
		}
		catch(IOException ioe){ ioe.printStackTrace(); }
	}
	
	
	/**
	 * @return _attribute
	 * Descripción: método observador, devuelve la columna del dataset
	 */
	public ArrayList<Record> get(){return _attribute;}
	
	/**
	 * @param attribute
	 */
	public void set(ArrayList<Record> attribute) {_attribute = attribute;}
	
	public void setMasked(boolean s) {_masked = s;}
	
	public boolean isMasked() {return _masked;}
	
	/**
	 * @return
	 * Descripción: método observador, devuelve el tipo de dato
	 */
	public int getTipo() {return _tipo;}
	
	public int getColumn() {return columna;}
	
	public String getNombre() {return _nombre;}
	
}
