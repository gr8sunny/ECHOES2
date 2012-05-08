package FAtiMA.socialRelations;

import java.util.ArrayList;
import java.util.ListIterator;

import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.util.enumerables.RelationType;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.Substitution;
import FAtiMA.wellFormedNames.SubstitutionSet;

public class RespectRelation extends Relation{

	public RespectRelation(String sub1, String sub2)
	{
		this._subj1 = sub1;
		this._subj2 = sub2;
	}
	
	public String increment( float intensity)
	{
		float respect = getValue();
		respect+= intensity/2;
		if(respect > 10)
		{
			respect = 10;
		}
		setValue(respect);
		return this._subj2 + ": " + respect;
	}
	
	public String decrement(float intensity)
	{
		float respect = getValue();
		respect-= intensity/2;
		if(respect < -10)
		{
			respect = -10;
		}
		setValue(respect);
		return this._subj2 + ": " + respect;
	}
	
	
	public float getValue()
	{
		Name respectProperty = Name.ParseName("Respect(" + this._subj1 + "," + this._subj2 + ")");
		Float result = (Float) KnowledgeBase.GetInstance().AskProperty(respectProperty);
		//If relation doesn't exists, create it in a neutral state
		if(result == null)
		{
			KnowledgeBase.GetInstance().Tell(respectProperty, new Float(0));
			return 0;
		}
		return result.floatValue();
	}
	
	public void setValue(float like)
	{
		Name respectProperty = Name.ParseName("Respect(" + this._subj1 + "," + this._subj2 + ")");
		KnowledgeBase.GetInstance().Tell(respectProperty, new Float(like));
	}
	
	public String getHashKey() {
		return RelationType.RESPECT + "-" + this._subj1 + this._subj2;
	}
	
	public String getTarget() {
		return _subj2;
	}
	
	public String getSubject() {
		return _subj1;
	}

	public static Relation getRelation(String subject1, String subject2) {
		return new RespectRelation(subject1, subject2);
	}

	public static ArrayList getAllRelations(String subject1) {
		ArrayList relations = new ArrayList();

		Name relationProperty = Name.ParseName("Respect(" + subject1 + ",[X])");
		ArrayList bindingSets = KnowledgeBase.GetInstance()
				.GetPossibleBindings(relationProperty);

		if (bindingSets != null) {
			for (ListIterator li = bindingSets.listIterator(); li.hasNext();) {
				SubstitutionSet subSet = (SubstitutionSet) li.next();
				Substitution sub = (Substitution) subSet.GetSubstitutions()
						.get(0);
				String target = sub.getValue().toString();
				relations.add(new RespectRelation(subject1, target));
			}
		}
		return relations;
	}
}
