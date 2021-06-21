package src.data;

public class Record
{
	private int _id; //identificador único de su fila en el dataset (de 1 a dataset.size)
	private long _original; //en caso semántico, offset original del registro
	private double _numericOriginal; //en caso numérico, valor original
	private double _privacyValue; //valor de privacidad asociado
	private float _distance; //distancia entre el concepto original y el enmascarado
	private boolean _anonymized; //indica si ha sido anonimizado en Swapping o Individual Ranking semántico
	private double _standardScore; //indica el valor tipificado del registro numérico
	
	/**
	 * @param id
	 * @param original
	 * Constructor semántico de la clase Record.
	 */
	public Record(int id, long sOriginal)
	{
		_id = id;
		_original = sOriginal;
		_numericOriginal = 0;
		_privacyValue = 0;
		_distance = 0;
		_anonymized = false;
	}
	
	public Record(int id, double nOriginal)
	{
		_id = id;
		_original = 0;
		_numericOriginal = nOriginal;
		_privacyValue = 0;
		_distance = 0;
		_anonymized = false;
	}
	
	public Record(Record r)
	{
		_id = r.getId();
		_original = r.getSemanticValue();
		_numericOriginal = r.getNumericValue();
		_privacyValue = r.getPrivacyValue();
		_distance = r.getDistance();
		_anonymized = r.isAnonymized();
		_standardScore = r.getStandardScore();
	}
	
	/*Métodos observadores y modificadores de los atributos*/
	
	public int getId() {return _id;}

	public long getSemanticValue() {return _original;}
	
	public void setSemanticValue(long masked) {_original = masked;}
	
	public double getNumericValue() {return _numericOriginal;}
	
	public void setNumericValue(double masked) {_numericOriginal = masked;}
	
	public double getPrivacyValue() {return _privacyValue;}

	public void setPrivacyValue(double pv) {_privacyValue = pv;}
	
	public float getDistance() {return _distance;}

	public void setDistance(float distance) {_distance = distance;}
	
	public boolean isAnonymized() {return _anonymized;}
	
	public void setAnonymized(boolean a) {_anonymized = a;}
	
	public double getStandardScore() {return _standardScore;}
	
	public void setStandardScore(double ss) {_standardScore = ss;}
	
	
	public String toString() {
		return _id + "," + _original + "," + "," + _privacyValue + "," + _distance;
	}
}
