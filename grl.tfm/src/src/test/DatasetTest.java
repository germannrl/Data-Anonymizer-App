package src.test;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.BeforeClass;
import org.junit.Test;
import src.data.Dataset;
import src.data.Record;
import src.data.Tuple;
import src.data.Attribute;

/**
 * Pruebas para los métodos de las clases Record, Attribute y Dataset
 */
public class DatasetTest
{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception{}

	
	@Test
	public void recordTest()
	{
		Record record = new Record(1, 13934930);
		assertEquals(1, record.getId());
		assertEquals(new Long(13934930), record.getSemanticValue());
		
		record.setDistance(new Float(0.5));
		assertEquals(new Float(0.5), record.getDistance());
		
		record.setPrivacyValue(new Double(0.2));
		assertEquals(new Double(0.2), record.getPrivacyValue());	
	}
	
	
	@Test
	public void datasetTest()
	{
		Dataset dataset = new Dataset("./PRUEBAS/pruebas_datasetTest.txt");
	
		ArrayList<Record> attribute = dataset.getAttribute(2).get(); //tomamos el segundo atributo
		int i = 1;
		
		for(Record record : attribute) //Comprobamos que el Id esté correctamente asignado
		{
			assertEquals(i, record.getId());
			i++;
		}
		//Comprobamos que el primer y último offsets guardados del atributo indicado sean los esperados:
		assertEquals(new Long(5935672), attribute.get(0).getSemanticValue());
		assertEquals(new Long(6066346), attribute.get(attribute.size()-1).getSemanticValue());
		
		//Intercambiamos los atributos de fila para probar los set:
		Attribute aux = dataset.getAttribute(1); //guardamos el primer atributo en una variable auxiliar
		dataset.setAttribute(dataset.getAttribute(2), 1); //guardamos el segundo atributo en la primera columna
		dataset.setAttribute(aux, 2); //guardamos el primer atributo en la segunda columna
		dataset.save("./PRUEBAS/save_datasetTest.txt"); //guardamos el nuevo dataset en un fichero
		
		Dataset newDataset = new Dataset("./PRUEBAS/save_datasetTest.txt"); //cargamos el dataset que hemos creado
		ArrayList<Record> newAttribute = newDataset.getAttribute(1).get(); //tomamos el primer atributo del nuevo dataset
		
		for(int c = 1 ; c < attribute.size(); c++) //Comprobamos que el intercambio ha sido correcto
			assertEquals(attribute.get(c).getSemanticValue(), newAttribute.get(c).getSemanticValue());
	}
	
	
	@Test
	public void tupleTest()
	{
		Dataset dataset = new Dataset("./PRUEBAS/pruebas_datasetTest.txt");
		int i = 0;
		int[] atts = {1,2};
		
		Tuple tuple = new Tuple(i, atts, dataset);
		
		assertEquals(0, tuple.getIndex());
		
		ArrayList<Record> t = tuple.getTuple();
		int[] pos = tuple.getPositions();
		
		for(int x = 0; x < pos.length; x++)
			assertEquals(x+1, pos[x]);
		
		assertEquals(dataset.getAttribute(1).get().get(0).getSemanticValue(),t.get(0).getSemanticValue());
		assertEquals(dataset.getAttribute(2).get().get(0).getSemanticValue(),t.get(1).getSemanticValue());
	}
	
	
	@Test
	public void conversionTuplesDataset()
	{
		Dataset dataset = new Dataset("./PRUEBAS/pruebas_datasetTest.txt");
		int[] atts = {1,2};
		
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
				
		tuples = Tuple.toArrayOfTuples(dataset, atts);
		
		for(Tuple t : tuples)
		{
			for(Record r: t.getTuple())
				r.setSemanticValue(r.getSemanticValue() + 2);
		}
		
		dataset.updateTuples(tuples, atts);
		
		Dataset origData = new Dataset("./PRUEBAS/pruebas_datasetTest.txt");
		int i = 1, j = 0;
		ArrayList<Attribute> d = dataset.getDataset();
		for(Attribute a : d)
		{
			Attribute aOrig = origData.getAttribute(i);
			for(Record ra : a.get())
			{
				Record rOrig = aOrig.get().get(j);
				if(a.getTipo() == 1)
					assertEquals(rOrig.getSemanticValue()+2, ra.getSemanticValue());
				
				j++;
			}
			i++;
			j = 0;
		}	
	}
	
	
	
}
