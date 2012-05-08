package stateManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import utils.Enums.FacialExpression;
import utils.Enums.ScreenRegion;

public class State {
    
    public class StateDetails<T> {
        AtomicReference<T> value;
        double confidence;
        long timestamp;

        public StateDetails(T t) {
            value = new AtomicReference<T>(t);
            timestamp = System.currentTimeMillis();
        }
        
        public boolean setNewValue(T newValue, double confidence, long timestamp) {
            if(value.getAndSet(newValue) != newValue) {
                this.confidence = confidence;
                this.timestamp = timestamp;
                return true;
            } else {
                return false;
            }
        }
    }
    
    public StateDetails<ScreenRegion> gazeRegion;
    public StateDetails<FacialExpression> expression;
    public ConcurrentMap<String, ScreenRegion> objectRegions;

    private State() {
        gazeRegion = new StateDetails<ScreenRegion>(ScreenRegion.ScreenUnknown);
        expression = new StateDetails<FacialExpression>(
                FacialExpression.ExpressionUnknown);
        objectRegions = new ConcurrentHashMap<String, ScreenRegion>();
    }
    
    private static final State _inst = new State();
    
    public static State getInstance() {
        return _inst;
    }

}
