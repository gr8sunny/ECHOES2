package echoesEngine;

import java.util.ArrayList;
import java.util.List;

import FAtiMA.knowledgeBase.KnowledgeBase;
import FAtiMA.wellFormedNames.Name;
import FAtiMA.wellFormedNames.SubstitutionSet;

/**
 * @author Elaine Farrow
 */
class KbUtilities {

    private KbUtilities() {
        // no instances
    }

    /**
     * Get a list of bindings for the given property.
     * 
     * @param property
     * the property.
     * 
     * @return the list (may be empty but never <code>null</code>).
     */
    @SuppressWarnings("unchecked")
    public static List <SubstitutionSet> kbGetBindings(String property) {
        List <SubstitutionSet> bindings = new ArrayList <SubstitutionSet>();

        String name = "[x]" + property;
        List<SubstitutionSet> objects = KnowledgeBase.GetInstance().GetPossibleBindings(Name.ParseName(name));
        if (objects != null) {
            bindings.addAll(objects);
        }

        return bindings;
    }

    /**
     * Does the given property have the value "True"?
     * 
     * @param property
     * the property.
     * 
     * @return <code>true</code> if the property value is "True";
     * <code>false</code> otherwise.
     */
    public static boolean kbAskTrue(String property) {
        return kbAskEqual(property, "True");
    }

    /**
     * Does the given property have the given value?
     * 
     * @param property
     * the property.
     * 
     * @param value
     * the value.
     * 
     * @return <code>true</code> if the property value matches the given value;
     * <code>false</code> otherwise.
     */
    public static boolean kbAskEqual(String property, String value) {
        return value.equals(kbAsk(property));
    }

    /**
     * Get the value of the given property.
     * 
     * @param property
     * the property.
     * 
     * @return the property value.
     */
    public static Object kbAsk(String property) {
        return KnowledgeBase.GetInstance().AskProperty(Name.ParseName(property));
    }

    /**
     * Set the value of the given property to either "True" or "False".
     * 
     * @param property
     * the property.
     * 
     * @param val
     * <code>true</code> to set the value to "True"; <code>false</code> to set
     * the value to "False".
     */
    public static void kbTell(String property, boolean val) {
        kbTell(property, val ? "True" : "False");
    }

    /**
     * Set the value of the given property to the given value.
     * 
     * @param property
     * the property.
     * 
     * @param val
     * the value.
     */
    public static void kbTell(String property, String val) {
        KnowledgeBase.GetInstance().Tell(Name.ParseName(property), val);
    }
}
