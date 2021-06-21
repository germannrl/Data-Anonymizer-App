package src.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import src.data.Attribute;
import src.data.Dataset;
import src.data.Record;
import src.data.SnomedBD;
import src.data.WordNetAccess;
import src.gui.IfrGenerateDistances;

public class SnomedBDTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		IfrGenerateDistances.ontology = new SnomedBD();
	}

	@Test
	public void snomedTest()
	{		
		Long snomed_ct = new Long(138875005);
		Long disease = new Long(64572001);
		Long disorder_body_site = new Long(123946008);
		Long chromosomal_disorder = new Long(409709004);
		Long disorder_body_cavity = new Long(399902003);
		
		//Caso 1: SnomedCT y SnomedCT
		assertEquals(snomed_ct, SnomedBD.getLCS(snomed_ct, snomed_ct));
		assertEquals(0, SnomedBD.numPathLinks(snomed_ct, snomed_ct));
		
		//Caso 2: SnomedCT y Disease
		assertEquals(snomed_ct, SnomedBD.getLCS(snomed_ct, disease));
		assertEquals(2, SnomedBD.numPathLinks(snomed_ct, disease));
		
		//Caso 3: Disease y Disease
		assertEquals(disease, SnomedBD.getLCS(disease, disease));
		assertEquals(0, SnomedBD.numPathLinks(disease, disease));

		//Caso 4: Body_Cavity y Disease
		assertEquals(disease, SnomedBD.getLCS(disorder_body_cavity, disease));
		assertEquals(2, SnomedBD.numPathLinks(disorder_body_cavity, disease));
		
		//Caso 5: Body Cavity y Chromosomal
		assertEquals(disease, SnomedBD.getLCS(disorder_body_cavity, chromosomal_disorder));
		assertEquals(3, SnomedBD.numPathLinks(disorder_body_cavity, chromosomal_disorder));
		
		
		Long flu_id = new Long(6142004);
		Long coronavirus_id = new Long(415360003);
		assertEquals(15, SnomedBD.numPathLinks(flu_id, coronavirus_id));
		
		assertEquals(0.88235295, IfrGenerateDistances.ontology.distance_WP(flu_id, coronavirus_id), 0.001);
		assertEquals(1-0.88235295, SnomedBD.similarity_WP(flu_id, coronavirus_id), 0.001);
		
		assertEquals(18, IfrGenerateDistances.ontology.getHyponymTree(flu_id).size());
		
		assertEquals(16, SnomedBD.encontrarAncestrosConDistanciaMinima(flu_id).size());
		
		assertEquals(9, SnomedBD.conceptDepth(flu_id));
		
		
		/*List<Long> offsetKeys = new ArrayList<Long>();
		File f = null;
		FileReader fr = null;
		BufferedReader br = null;
		
		try
		{	
			f = new File("./DOMINIOS/DOMINIO1000.txt");
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			String line;
			
			while((line = br.readLine()) != null)
			{
				Long v = Long.parseLong(line); //obtiene el offset de cada fila
				offsetKeys.add(v);
			}
			fr.close();	
		}
		catch(IOException ioe){ ioe.printStackTrace(); }	
		
		System.out.println("Tamaño total: " + offsetKeys.size());
		List<Long> hijosDesease = SnomedBD.obtenerHijos(new Long(399902003));
		
		System.out.println(SnomedBD.obtainConcept(new Long(399902003)));
		
		for(Long hijo : hijosDesease)
		{
			List<Long> descendientes = SnomedBD.obtenerDescendientes(hijo);
			int contador = 0;
			
			for(Long offset : offsetKeys)
			{
				if(descendientes.contains(offset))
					contador++;
			}
			
			if(contador >= 300)
				System.out.println(hijo + ": " + SnomedBD.obtainConcept(hijo) + " : " + contador + "/ Dominio size: " + descendientes.size());
		}*/
		
		
		/*List<Long> offsetKeys = new ArrayList<Long>();
		File f = null;
		FileReader fr = null;
		BufferedReader br = null;
		
		try
		{	
			f = new File("./DOMINIOS/DOMINIO4000.txt");
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			String line;
			
			while((line = br.readLine()) != null)
			{
				Long v = Long.parseLong(line); //obtiene el offset de cada fila
				offsetKeys.add(v);
			}
			fr.close();	
		}
		catch(IOException ioe){ ioe.printStackTrace(); }	
		
		Long lcs = SnomedBD.getLCS(offsetKeys);
		System.out.println("LCS: " + lcs + ", " + SnomedBD.obtainConcept(lcs));*/
		
		/*Dataset dataset = new Dataset("./DATASETS/DATASET981_LIMPIO.txt");
		Attribute a1 = dataset.getAttribute(1);
		Attribute a2 = dataset.getAttribute(2);
		List<Integer> aBorrar = new ArrayList<Integer>();
		int size = a1.get().size();
		
		//49483002
		//90708001
		//19829001
		//280128006
		//81308009
		//79787007
		//41427001
		List<Long> disease = SnomedBD.obtenerDescendientes(new Long(49483002));
		disease.addAll(SnomedBD.obtenerDescendientes(new Long(90708001)));
		disease.addAll(SnomedBD.obtenerDescendientes(new Long(19829001)));
		disease.addAll(SnomedBD.obtenerDescendientes(new Long(280128006)));
		disease.addAll(SnomedBD.obtenerDescendientes(new Long(81308009)));
		disease.addAll(SnomedBD.obtenerDescendientes(new Long(79787007)));
		disease.addAll(SnomedBD.obtenerDescendientes(new Long(41427001)));
		
		int q = 0;
		int tot = disease.size()-1;
		
		for(int i = tot; i >= 0;i--)
		{
			Long d = disease.get(i);
			if(!a1.get().contains(d) && !a2.get().contains(d) && q < 9000)
			{
				disease.remove(i);
				q++;
			}
		}
		
		System.out.println("Tamano: " + disease.size());
		
		 try {
		      File myObj = new File("./DOMINIOS/dominioFinal8000.txt");
		      if (myObj.createNewFile()) {
		        
		    	  FileWriter myWriter = new FileWriter("./DOMINIOS/dominioFinal8000.txt");
		    	  BufferedWriter writer = new BufferedWriter(myWriter);
		    	  
		    	  for(Long d : disease)
		    	  {
		    		  writer.write(d.toString());
		    		  writer.newLine();
		    	  }
		    	  myWriter.close();
		    	  
		      } else {
		        System.out.println("File already exists.");
		      }
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		}
		*/
		
		/*System.out.println("Dominio size: " + disease.size());
		
		for(int i = 0; i < size; i++)
		{
			Record r1 = a1.get().get(i);
			Record r2 = a2.get().get(i);
			
			if(!disease.contains(r1.getSemanticValue()) || !disease.contains(r2.getSemanticValue()))
				aBorrar.add(i);
		}
		
		System.out.println("hola: " + aBorrar.size());
		
		int aBSize = aBorrar.size()-1;
		int j = 1;
		
		for(Attribute a : dataset.getDataset())
		{
			for(int i = aBSize; i >= 0; i--)
			{
				int x = aBorrar.get(i);
				a.get().remove(x);
			}
			dataset.setAttribute(a, j);
			j++;
		}
		
		System.out.println(dataset.getAttribute(1).get().size());
		System.out.println(dataset.getAttribute(2).get().size());
		
		//dataset.save("./DATASETS/DATASET981_LIMPIO.txt");*/
	}

}
