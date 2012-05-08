package pedagogicComponent;

import utils.Interfaces.IDramaManager;
import utils.Interfaces.IActionEngine;

/*
 * A class that has access to the components of the Pedagogic Component
 * 
 * @author katerina avramides
 *
 */
public class PCcomponentHandler 
{
  protected IDramaManager dmPrx;
  protected IActionEngine aePrx;
  private PCcomponents pCc;

  public PCcomponentHandler() 
  {
  	this.pCc = null;
  	this.dmPrx = null;
  	this.aePrx = null;
	}
    
	public PCcomponentHandler(PCcomponents pCc, IDramaManager dmPrx, IActionEngine aePrx) 
	{
		this.pCc = pCc;
		this.dmPrx = dmPrx;
		this.aePrx = aePrx;
	}
	
	public PCcomponents getPCcs() 
	{
		return pCc;
	}
}
