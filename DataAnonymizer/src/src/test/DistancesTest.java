package src.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import java.util.*;
import src.data.Distances;
import src.data.WordNetAccess;
import src.gui.IfrGenerateDistances;

/**
 * Pruebas para los métodos de la clase Distances
 */
public class DistancesTest
{
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		IfrGenerateDistances.ontology = new WordNetAccess();
	}

	
	/**
	 * Test de los métodos de la clase Distances, y de los métodos
	 * computeDistances de la clase SOperators (deberían cambiarse a Distances)
	 */
	@Test
	public void distancesTest()
	{	
		/*Pruebas con carga de dominio desde fichero*/
		
		Distances distances = new Distances("./PRUEBAS/pruebas_distancesTest.txt");
		
		/*Comprobamos algunas distancias entre conceptos*/
		Long offset1 = new Long(877455);
		Long offset2 = new Long(605313);
		float dist1 = IfrGenerateDistances.ontology.distance_WP(offset1, offset2);
		float zero = 0;
		assertEquals(dist1, distances.getDistanceBetween(offset1, offset2)); //en un sentido
		assertEquals(dist1, distances.getDistanceBetween(offset2, offset1)); //y en otro
		assertEquals(zero, distances.getDistanceBetween(offset1, offset1)); //y a sí mismo
		
		Long offset3 = new Long(875734);
		Long offset4 = new Long(936470);
		float dist2 = IfrGenerateDistances.ontology.distance_WP(offset3, offset4);
		assertEquals(dist2, distances.getDistanceBetween(offset3, offset4)); //en un sentido
		assertEquals(dist2, distances.getDistanceBetween(offset4, offset3)); //y en otro
		assertEquals(zero, distances.getDistanceBetween(offset3, offset3)); //y a sí mismo
		
		/*Pruebas con carga de dominio ofreciendo un listado de offsets (árbol de hipónimos)*/
		
		List<Long> listado = Arrays.asList(new Long(936062), new Long(936184), new Long(604528));
		Distances distances2 = new Distances(listado);
		assertEquals(3, distances2.getDistances().size());
	}
}
