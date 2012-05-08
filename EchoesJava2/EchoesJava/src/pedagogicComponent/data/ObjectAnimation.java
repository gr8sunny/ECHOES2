package pedagogicComponent.data;

import java.util.HashMap;
import java.util.Map;

public class ObjectAnimation {

    static {
	OBJECT_ANIMATION_MAP = new HashMap<String, ObjectAnimation>();
    }

    /**
     * The list of object animations known to the Pedagogic Component.
     * 
     * This includes all object animations, such as object appearance, disappearance, etc.
     * 
     */

    // bubbles
    public static final ObjectAnimation bubble_entrance = new ObjectAnimation("bubble_entrance");

    public static final ObjectAnimation bubble_exit = new ObjectAnimation("bubble_exit");

    public static final ObjectAnimation bubble_pop = new ObjectAnimation("bubble_pop");

    public static final ObjectAnimation bubble_merge = new ObjectAnimation("bubble_merge");

    // flowers
    public static final ObjectAnimation flower_appear = new ObjectAnimation("flower_appear");

    public static final ObjectAnimation flower_turnToBubble = new ObjectAnimation("flower_turnToBubble");

    public static final ObjectAnimation flower_turnToBall = new ObjectAnimation("flower_turnToBall");

    public static final ObjectAnimation flower_grow = new ObjectAnimation("flower_grow");

    // ball
    public static final ObjectAnimation ball_bounce = new ObjectAnimation("ball_bounce");

    public static final ObjectAnimation ball_exit = new ObjectAnimation("ball_exit");

    public static final ObjectAnimation ball_entrance = new ObjectAnimation("ball_entrance");

    // cloud
    public static final ObjectAnimation cloud_entrance = new ObjectAnimation("cloud_entrance");

    public static final ObjectAnimation cloud_exit = new ObjectAnimation("cloud_exit");

    public static final ObjectAnimation cloud_move = new ObjectAnimation("cloud_move");

    public static final ObjectAnimation cloud_rain = new ObjectAnimation("cloud_rain");

    // tree leaves
    public static final ObjectAnimation leaves_fly = new ObjectAnimation("leaves_fly");

    public static final ObjectAnimation leaves_grow = new ObjectAnimation("leaves_grow");

    // flowerpot
    public static final ObjectAnimation flower_potScale = new ObjectAnimation("flower_potScale");

    private static Map<String, ObjectAnimation> OBJECT_ANIMATION_MAP;

    private String name;

    /**
     * Create a new instance with the given name.
     * 
     * Note that the constructor is private so that only the above static
     * instances can be created.
     * 
     * @param name
     *            the name of this instance.
     * 
     */
    private ObjectAnimation(String name) {
	this.name = name;
	OBJECT_ANIMATION_MAP.put(name, this);
    }

    public static ObjectAnimation getObjectAnimation(String animationName)
	    throws StringLabelException {
    	ObjectAnimation animation = OBJECT_ANIMATION_MAP.get(animationName);
	if (animation == null)
	    throw new StringLabelException("Animation label does not match" + animationName);
	return animation;
    }

    public String getName() {
	return name;
    }

}
