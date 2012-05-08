package FAtiMA.util.enumerables;

import FAtiMA.exceptions.InvalidEmotionTypeException;

public class RelationType {
	public static final short LIKE = 0;
	public static final short RESPECT = 1;
	public static final short POWER = 2;

	private static final String[] _relationTypes = { "Like", "Respect", "Power" };
	
	public static short parseType(String relationType) throws Exception 
	{
		short i;
		if(relationType == null) throw new InvalidEmotionTypeException(null);
		
		for(i=0; i < _relationTypes.length; i ++) {
			if(_relationTypes[i].equals(relationType)) return i;
		}
		
		throw new Exception(relationType);
	}
	
	public static String getName(short relationType) {
		if(relationType >= 0 && relationType <= 2) return _relationTypes[relationType];
		return null;
	}
}
