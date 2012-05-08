package pedagogicComponent;

import utils.Enums.EchoesActivity;
import utils.Enums.EchoesObjectType;

/**
 * Activities the practitioner can choose. A learning activity consists of an
 * EchoesActivity and an EchoesObjectType.
 * 
 * @author Elaine Farrow
 */
public enum Activity
{
    BUBBLES("bubble play"),
    EXPLORE("explore without Andy"),
    CLOUD_RAIN(EchoesObjectType.Cloud, EchoesActivity.CloudRain),
    FLOWER_GROW(EchoesObjectType.Cloud, EchoesActivity.FlowerGrow),
    FLOWER_PICK(EchoesObjectType.Flower, EchoesActivity.FlowerPickToBasket),
    POT_STACK(EchoesObjectType.Pot, EchoesActivity.PotStackRetrieveObject),
    FLOWER_BALL(EchoesObjectType.Flower, EchoesActivity.FlowerTurnToBall),
    BALL_THROW(EchoesObjectType.Ball, EchoesActivity.BallThrowing),
    BALL_SORT(EchoesObjectType.Ball, EchoesActivity.BallSorting),
    FLOWER_BALL_ANDY(EchoesObjectType.Flower,
                     EchoesActivity.FlowerTurnToBall,
                     true),
    BALL_THROW_ANDY(EchoesObjectType.Ball, EchoesActivity.BallThrowing, true),
    TICKLE_AND_TREE(null, EchoesActivity.TickleAndTree),
    EXPLORE_WITH_ANDY(null, EchoesActivity.ExploreWithAgent);

    public final EchoesObjectType object;
    public final EchoesActivity activity;
    public final boolean isContingent;
    public String label;

    private Activity(String label)
    {
        this(null, null, false);

        this.label = label;
    }

    private Activity(EchoesObjectType object, EchoesActivity activity)
    {
        this(object, activity, false);
    }

    private Activity(EchoesObjectType object,
                     EchoesActivity activity,
                     boolean isContingent)
    {
        this.object = object;
        this.activity = activity;
        this.isContingent = isContingent;
    }

    public void startActivity(PractitionerServer server)
    {
        server.activityStarted();

        switch (this)
        {
            case EXPLORE:
                server.startGardenScene();
                break;
            case BUBBLES:
                server.startBubbleScene();
                break;
            default:
                server.setLearningActivity(activity, object, isContingent);
                break;
        }
    }

    public String toString()
    {
        if (label == null)
        {
            label = Utilities.toString(activity);

            if (isContingent)
            {
                label += " with Andy";
            }
        }

        return label;
    }
}
