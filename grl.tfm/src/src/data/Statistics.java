package src.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Statistics
{
	public String lemaOriginal, lemaMasked;
	public float distancia;
	public Double numericMeanOrig, numericMeanMask;
	public Long semanticMeanOrig, semanticMeanMask;
	public Double varianceOrig, varianceMask;
	public Double stdDevOrig, stdDevMask;
	public Double mae;
	public Double rmse;
	public ArrayList<Double> covariancesOrig, covariancesMask, correlationOrig, correlationMask;
	public int _modo; //1 si es semántico y comparar; 2 si es numérico y comparar; 3 si es semántico individual;
					  //4 si es numérico individual
	
	public Statistics(int modo) 
	{
		_modo = modo;
		covariancesOrig = new ArrayList<Double>();
		covariancesMask = new ArrayList<Double>();
		correlationOrig = new ArrayList<Double>();
		correlationMask = new ArrayList<Double>();
	}
	
	public Statistics(Statistics estadisticas)
	{
		numericMeanOrig = estadisticas.numericMeanOrig;
		numericMeanMask = estadisticas.numericMeanMask;
		semanticMeanOrig = estadisticas.semanticMeanOrig;
		semanticMeanMask = estadisticas.semanticMeanMask;
		varianceOrig = estadisticas.varianceOrig;
		varianceMask = estadisticas.varianceMask;
		stdDevOrig = estadisticas.stdDevOrig;
		stdDevMask = estadisticas.stdDevMask;
		mae = estadisticas.mae;
		rmse = estadisticas.rmse;
		covariancesOrig = estadisticas.covariancesOrig;
		covariancesMask = estadisticas.covariancesMask;
	}
	
	public void save(String fileName)
	{
		File file = new File(fileName);
		
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			
			switch(_modo)
			{
				case 1:
					if(semanticMeanOrig != null)
					{
						bw.write("Media original: " + semanticMeanOrig + " (" + lemaOriginal + ")");
						bw.newLine();
					}
					
					if(semanticMeanMask != null)
					{
						bw.write("Media enmascarada: " + semanticMeanMask + " (" + lemaMasked + ")");
						bw.newLine();
						
						bw.write("Distancia semántica entre las medias: " + distancia);
						bw.newLine();
					}
					
					if(varianceOrig != null)
					{
						bw.write("Varianza original: " + varianceOrig);
						bw.newLine();
					}
					
					if(varianceMask != null)
					{
						bw.write("Varianza enmascarada: " + varianceMask);
						bw.newLine();
					}
					
					if(stdDevOrig != null)
					{
						bw.write("Desviación típica original: " + stdDevOrig);
						bw.newLine();
					}
					
					if(stdDevMask != null)
					{
						bw.write("Desviación típica enmascarada: " + stdDevMask);
						bw.newLine();
					}
					
					if(mae != null)
					{
						bw.write("MAE: " + mae);
						bw.newLine();
					}
					
					if(rmse != null)
					{
						bw.write("RMSE: " + rmse);
						bw.newLine();
					}
					
					if(covariancesOrig.size() != 0)
					{
						bw.write("Covarianza original con respecto a todos los atributos semánticos: ");
						for(Double d : covariancesOrig)
							bw.write(d+ " ");
						
						bw.newLine();
					}
					
					if(covariancesMask.size() != 0)
					{
						bw.write("Covarianza enmascarada con respecto a todos los atributos semánticos: ");
						for(Double d : covariancesMask)
							bw.write(d+ " ");
						
						bw.newLine();
					}
					
					if(correlationOrig.size() != 0)
					{
						bw.write("Correlación original con respecto a todos los atributos semánticos: ");
						for(Double d : correlationOrig)
							bw.write(d+ " ");
						
						bw.newLine();
					}
					
					if(correlationMask.size() != 0)
					{
						bw.write("Correlación enmascarada con respecto a todos los atributos semánticos: ");
						for(Double d : correlationMask)
							bw.write(d+ " ");
						
						bw.newLine();
					}
					
					break;
					
				case 2:
					if(numericMeanOrig != null)
					{
						bw.write("Media original: " + numericMeanOrig);
						bw.newLine();
					}
					
					if(numericMeanMask != null)
					{
						bw.write("Media enmascarada: " + numericMeanMask);
						bw.newLine();
					}
					
					if(varianceOrig != null)
					{
						bw.write("Varianza original: " + varianceOrig);
						bw.newLine();
					}
					
					if(varianceMask != null)
					{
						bw.write("Varianza enmascarada: " + varianceMask);
						bw.newLine();
					}
					
					if(stdDevOrig != null)
					{
						bw.write("Desviación típica original: " + stdDevOrig);
						bw.newLine();
					}
					
					if(stdDevMask != null)
					{
						bw.write("Desviación típica enmascarada: " + stdDevMask);
						bw.newLine();
					}
					
					if(mae != null)
					{
						bw.write("MAE: " + mae);
						bw.newLine();
					}
					
					if(rmse != null)
					{
						bw.write("RMSE: " + rmse);
						bw.newLine();
					}
					
					if(covariancesOrig.size() != 0)
					{
						bw.write("Covarianza original con respecto a todos los atributos numéricos: ");
						for(Double d : covariancesOrig)
							bw.write(d+ " ");
						
						bw.newLine();
					}
					
					if(covariancesMask.size() != 0)
					{
						bw.write("Covarianza enmascarada con respecto a todos los atributos numéricos: ");
						for(Double d : covariancesMask)
							bw.write(d+ " ");
						
						bw.newLine();
					}
					
					if(correlationOrig.size() != 0)
					{
						bw.write("Correlación original con respecto a todos los atributos numéricos: ");
						for(Double d : correlationOrig)
							bw.write(d+ " ");
						
						bw.newLine();
					}
					
					if(correlationMask.size() != 0)
					{
						bw.write("Correlación enmascarada con respecto a todos los atributos numéricos: ");
						for(Double d : correlationMask)
							bw.write(d+ " ");
						
						bw.newLine();
					}
					
					break;
					
				case 3:
					if(semanticMeanOrig != null)
					{
						bw.write("Media: " + semanticMeanOrig + " (" + lemaOriginal + ")");
						bw.newLine();
					}
					
					if(varianceOrig != null)
					{
						bw.write("Varianza: " + varianceOrig);
						bw.newLine();
					}
					
					if(stdDevOrig != null)
					{
						bw.write("Desviación típica: " + stdDevOrig);
						bw.newLine();
					}
					
					if(covariancesOrig.size() != 0)
					{
						bw.write("Covarianza con respecto a todos los atributos semánticos: ");
						for(Double d : covariancesOrig)
							bw.write(d+ " ");
						
						bw.newLine();
					}
					
					if(correlationOrig.size() != 0)
					{
						bw.write("Correlación con respecto a todos los atributos semánticos: ");
						for(Double d : correlationOrig)
							bw.write(d+ " ");
						
						bw.newLine();
					}
					
					break;
					
				case 4:
					if(numericMeanOrig != null)
					{
						bw.write("Media: " + numericMeanOrig);
						bw.newLine();
					}
					
					if(varianceOrig != null)
					{
						bw.write("Varianza: " + varianceOrig);
						bw.newLine();
					}
					
					if(stdDevOrig != null)
					{
						bw.write("Desviación típica: " + stdDevOrig);
						bw.newLine();
					}
					
					if(covariancesOrig.size() != 0)
					{
						bw.write("Covarianza con respecto a todos los atributos numéricos: ");
						for(Double d : covariancesOrig)
							bw.write(d+ " ");
						
						bw.newLine();
					}
					
					if(correlationOrig.size() != 0)
					{
						bw.write("Correlación con respecto a todos los atributos numéricos: ");
						for(Double d : correlationOrig)
							bw.write(d+ " ");
						
						bw.newLine();
					}
					
					break;
			}
			bw.close();
		}
		catch (IOException e) {e.printStackTrace();}
		
	}
}
