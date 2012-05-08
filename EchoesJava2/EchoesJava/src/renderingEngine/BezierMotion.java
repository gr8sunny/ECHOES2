package renderingEngine.src;

import java.util.List;
import java.util.Vector;

public class BezierMotion
{
  public float speed = (float)(0.01);
  public float bezierIndex = 0;
  public List<float[]> ctrlpoints = new Vector<float[]>();
  public float pos[] = new float[3];
  public float targetPos[] = null;
  public float boundingBox[][] = null;
  public float boundingBoxFactor = (float)(0.8 );  
  public float overshooting = (float)(0.3); // potentially producing the first control point outside the bounding box to smooth transition between two beziers
  public boolean showCtrlPoints = false;

  public void initBezierVars()
  {  
    // replacement (multiple inheritance stuff...)
    // NOTE Must be used in combination with either an EchoesObject or EchoesAgent (needsapp!)
  
   //boundingBox = screenCanvas.theCanvas.getRegionCoords("all");     // bounding box in which the object should move
  }

        
  public float[] nextBezierPos(boolean flat)
  {
    float retval[] = new float[3];
    retval[0] = (float)(Math.pow(1-bezierIndex, 3) *ctrlpoints.get(0)[0] + 3 * Math.pow(1-bezierIndex, 2) *bezierIndex *ctrlpoints.get(1)[0] + 3 * (1-bezierIndex) * Math.pow(bezierIndex,2) * ctrlpoints.get(2)[0] + Math.pow(bezierIndex, 3) *ctrlpoints.get(3)[0]);
    retval[1] = (float)(Math.pow(1-bezierIndex, 3) *ctrlpoints.get(0)[1] + 3 * Math.pow(1-bezierIndex, 2) *bezierIndex *ctrlpoints.get(1)[1] + 3 * (1-bezierIndex) * Math.pow(bezierIndex,2) * ctrlpoints.get(2)[1] + Math.pow(bezierIndex, 3) *ctrlpoints.get(3)[1]);
    if (!flat)
      retval[2] = (float)(Math.pow(1-bezierIndex, 3) *ctrlpoints.get(0)[2] + 3 * Math.pow(1-bezierIndex, 2) *bezierIndex *ctrlpoints.get(1)[2] + 3 * (1-bezierIndex) * Math.pow(bezierIndex,2) * ctrlpoints.get(2)[2] + Math.pow(bezierIndex, 3) *ctrlpoints.get(3)[2]);
    else
      retval[2] = pos[2];
    bezierIndex +=speed;
    if (bezierIndex > 1.0)
      newCtrlPoints(retval);
    return retval;
  }
  
  public void setStartPos(float pos[])
  {
     this.pos = pos;
     ctrlpoints.clear();
     newCtrlPoints(null);
  }
  
  public void setTargetPos(float pos[])
  {
     targetPos = pos;
     newCtrlPoints(null);
  }

  public void newCtrlPoints(float latestpos[])
  {
    bezierIndex = 0;
    if (latestpos == null)
       ctrlpoints.set(0, pos);
    else 
       ctrlpoints.set(0, latestpos);

    if (ctrlpoints.size() >=2)
       ctrlpoints.set(1, new float[] {pos[0] + (pos[0]-ctrlpoints.get(2)[0])* overshooting,
                                       pos[1] + (pos[1]-ctrlpoints.get(2)[1])*overshooting, 
                                       pos[2] + (pos[2]-ctrlpoints.get(2)[2])*overshooting});
    else
       ctrlpoints.set(1, randompoint());
    
    ctrlpoints.set(2, randompoint());
    if (targetPos != null)
    {
       ctrlpoints.set(3, targetPos);
       targetPos = null;
    }
    else
       ctrlpoints.set(3, randompoint());
  }
  
  public float[] randompoint()    
  {
    float retval[] = new float[3];
    retval[0] = (float)(boundingBox[0][0] + (boundingBox[1][0] -boundingBox[0][0]) * ((1.0-boundingBoxFactor)/2 +boundingBoxFactor*Math.random())); 
    retval[1] = (float)(boundingBox[0][1] + (boundingBox[1][1] -boundingBox[0][1]) * ((1.0-boundingBoxFactor)/2 +boundingBoxFactor*Math.random()));          
    retval[2] = (float)(boundingBox[0][2] + (boundingBox[1][2] -boundingBox[0][2]) * ((1.0-boundingBoxFactor)/2 +boundingBoxFactor*Math.random()));        
    return retval;
  }
}
