package src.test;

import static org.junit.Assert.*;

import java.text.DecimalFormat;

import org.junit.BeforeClass;
import org.junit.Test;

import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.Synset;
import src.data.WordNetAccess;
import src.gui.IfrGenerateDistances;

public class WordNetAccessTest
{	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		IfrGenerateDistances.ontology = new WordNetAccess();
	}

	/**
	 * Pruebas unitarias para los métodos de la clase WordNetAccess.
	 */
	@Test
	public void WordNetTest()
	{			
		String test_1 = "[Synset: [Offset: 2064081] [POS: noun] Words: dog, domestic_dog, "
				+ "Canis_familiaris -- (a member of the genus Canis (probably descended from"
				+ " the common wolf) that has been domesticated by man since prehistoric times;"
				+ " occurs in many breeds; \"the dog barked all night\")]";
		
		assertEquals(test_1, WordNetAccess.getSynset(2064081).toString());
		
		Synset synset_test = WordNetAccess.getSynset(2064081); //synset "dog"
		assertEquals(185, WordNetAccess.getHyponymTree(synset_test).size());
		assertEquals(185, IfrGenerateDistances.ontology.getHyponymTree(new Long(2064081)).size());
		
		/*Comprobamos que el camino desde entity hasta entity solo se tiene a sí mismo*/
		Synset synTest_entity = WordNetAccess.getSynset(1740);
		assertEquals(1, WordNetAccess.getPathToEntity(synTest_entity).size());
		
		/*Comprobamos que cualquier otro camino hasta entity es correcto*/
		Synset synTest_dog = WordNetAccess.getSynset(2064081);
		assertEquals(13, WordNetAccess.getPathToEntity(synTest_dog).size());
		assertEquals(13, WordNetAccess.getNumNodesToEntity(synTest_dog));
		
		Synset synTest_cat = WordNetAccess.getSynset(2100898);
		assertEquals(5, WordNetAccess.getNumNodesBetween(synTest_dog, synTest_cat));
		assertEquals(13, WordNetAccess.getNumNodesBetween(synTest_dog, synTest_entity));
		assertEquals(13, WordNetAccess.getNumNodesBetween(synTest_entity, synTest_dog));
		
		assertEquals(4, WordNetAccess.getNumLinksBetween(synTest_dog, synTest_cat));
		assertEquals(0.846, WordNetAccess.similarity_WP(synTest_dog, synTest_cat), 0.001);
		assertEquals(0.154, WordNetAccess.distance_WP(synTest_dog, synTest_cat), 0.001);
		assertEquals(0.154, IfrGenerateDistances.ontology.distance_WP(new Long(2064081), new Long(2100898)), 0.001);
	}
}
