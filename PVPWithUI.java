package sim.app.pvp;

import javax.swing.JFrame;

import sim.app.*;
import sim.display.*;
import sim.engine.*;
import sim.portrayal.*;
import sim.portrayal.grid.*;
import sim.portrayal.simple.*;

import java.awt.*;



public class PVPWithUI extends GUIState {

	public Display2D display;
	public JFrame displayFrame;
	public int worldWidth = 500;
	public int worldHeight = 500;
	
	SparseGridPortrayal2D worldPortrayal = new SparseGridPortrayal2D();
	
	public PVPWithUI(){ super(new PVP(System.currentTimeMillis())); }
	
	public PVPWithUI(SimState state) {super(state);}
	
	// returns name
	public static String getName() { return "Predator vs. Prey: Agent-Based Simulation";}
	
	
	public static void main(String[] args)
	{
	
		// creates graphical console which allows to 
		// us to stop start, etc.
		PVPWithUI vid = new PVPWithUI();
		Console c = new Console(vid);
		c.setVisible(true);
	}

	public void start(){
		super.start();
		setupPortrayals();
	}


	
	public void load(SimState state)
	{
		super.load(state);
		setupPortrayals();
	}
	
	public void setupPortrayals()
	{
		PVP sims = (PVP) state;
		
		//tell the portrayals what to portray and how to portray them
		worldPortrayal.setField(sims.world);
		OvalPortrayal2D oval = new OvalPortrayal2D(Color.green);
		RectanglePortrayal2D prey = new RectanglePortrayal2D(Color.white);
		RectanglePortrayal2D predator = new RectanglePortrayal2D(Color.black);
		worldPortrayal.setPortrayalForClass(Food.class, oval);
		worldPortrayal.setPortrayalForClass(Prey.class, prey);
		worldPortrayal.setPortrayalForClass(Predator.class, predator);
		
		//reschedule the displayer
		display.reset();
		
		//redraw the display
		display.repaint();
	
        
		}
	
	   public void init(Controller c)
       {
       super.init(c);
       
       display = new Display2D(worldWidth,worldHeight,this);
       display.setClipping(false);
       
       displayFrame = display.createFrame();
       displayFrame.setTitle("Predator vs. Prey");
       c.registerFrame(displayFrame);
       displayFrame.setVisible(true);
       display.setBackdrop(Color.gray);
       display.attach(worldPortrayal,"Grid");
   
       
       }
	   
	    public Object getSimulationInspectedObject()
        {
        return state;
        }

	    
	    public Inspector getInspector()
        {
        Inspector i = super.getInspector();
        i.setVolatile(true);
        return i;
        }
    
    public void quit()
	{
		super.quit();
		
		if(displayFrame!=null) displayFrame.dispose();
		displayFrame=null;
		display = null;
	}
}
