
package sim.app.pvp;

import sim.engine.*;
import sim.util.*;
import sim.field.grid.*;
import ec.util.*;


public class PVP extends SimState{

	//world
	public SparseGrid2D world;
	//dimensions of the world
	private final int gridWidth;
	private final int gridHeight;
	private final int gridArea;
	//Rates and Numbers
	private final double foodPopRate = .4;
	private short numPred;
	private short numPrey;
	private int numFood;
	//Number of Clusters
	private final int clusters;
	private final int [][] clust;
	
	//Sets up the parameters of the world
	public PVP(long seed)
	{
		super(seed);
		gridWidth = 10;
		gridHeight = 10;
		numPred = 1;
		numPrey = 7;
		clusters = 20;
		gridArea = (gridWidth*gridHeight);
		numFood = (int) (gridArea * foodPopRate);
		//System.out.println("Grid Area: " + gridArea + " numFood: " + numFood);
		clust = new int[clusters][2];
	}
	
	//Populates the world with food, prey and predators
	public void start()
	{
		super.start();
		world = new SparseGrid2D(gridWidth, gridHeight);
		//grid.clear();
		
		//ONLY RANDOM NUMBER GENERATOR
		MersenneTwisterFast twister = new MersenneTwisterFast();
		
		//System.out.println("Clusters: " + clusters);
		
		//Clustered Visual Food - FIRST SET
		for(int h = 0; h < clusters; h++){
			
			
			MutableInt2D loc = new MutableInt2D();
			loc.x = world.tx(twister.nextInt());
			loc.y = world.ty(twister.nextInt());
			
			clust[h][0] = loc.x;
			clust[h][1] = loc.y;
				
			Food p = new Food();
				
				
			world.setObjectLocation(p, loc.x, loc.y);
		}
			
			//Expanding on these sets
			for(int l = 0; l < clusters; l++){
				
			//System.out.println("Cluster: " + l);
				
			int xcord = clust[l][0];
			int ycord = clust[l][1];
			
			//System.out.println("NumFood/Clusters: " + numFood/clusters);
			
			for(int f = 0; f < (numFood/ clusters); f++){
				
				//Placing them at random places around the initial food
				Food p = new Food();
				int direction = twister.nextInt(7);
					
				//Placed N
				if (direction == 0){
					//System.out.println(p + " Placed N");
					if(world.getObjectsAtLocation(xcord, ycord + 1) == null){
						world.setObjectLocation(p, xcord, ycord + 1);
						schedule.scheduleRepeating(p);
						ycord = ycord + 1;
					}	
							
				} // end of if
				
				//Placed S
				else if (direction == 1){
					//System.out.println(p + " Placed S");
					if(world.getObjectsAtLocation(xcord, ycord - 1) == null){
						world.setObjectLocation(p, xcord, ycord - 1);
						schedule.scheduleRepeating(p);
						ycord = ycord - 1;
					}	
				}
				//Placed E		
				else if (direction == 2){
					//System.out.println(p + " Placed E");
					if(world.getObjectsAtLocation(xcord + 1, ycord) == null){
						world.setObjectLocation(p, xcord + 1, ycord);
						schedule.scheduleRepeating(p);
						xcord = xcord +1;
					}	
				}
				//Placed NE		
				else if (direction == 3){
					//System.out.println(p + " Placed NE");
					if(world.getObjectsAtLocation(xcord + 1, ycord + 1) == null){
						world.setObjectLocation(p, xcord + 1, ycord + 1);
						schedule.scheduleRepeating(p);
						xcord = xcord + 1;
						ycord = ycord + 1;
					}	
				}
				//Placed SE	
				else if (direction == 4){
					//System.out.println(p + " Placed SE");
					if(world.getObjectsAtLocation(xcord + 1, ycord - 1) == null){
						world.setObjectLocation(p, xcord + 1, ycord - 1);
						schedule.scheduleRepeating(p);
						xcord = xcord + 1;
						ycord = ycord - 1;
					}	
				}	
				//Placed NW
				else if (direction == 5){
					//System.out.println(p + " Placed NW");
					if(world.getObjectsAtLocation(xcord - 1, ycord + 1) == null){
						world.setObjectLocation(p, xcord - 1, ycord + 1);
						schedule.scheduleRepeating(p);
						xcord = xcord - 1;
						ycord = ycord + 1;
					}	
				}
				//Placed SW		
				else if (direction == 6){
					//System.out.println(p + " Placed SW");
					if(world.getObjectsAtLocation(xcord - 1, ycord - 1) == null){
						world.setObjectLocation(p, xcord - 1, ycord - 1);
						schedule.scheduleRepeating(p);
						xcord = xcord - 1;
						ycord = ycord - 1;
					}// end of if	
				}	// end of else if
			} // end of for
		} // end of clusters

		for(int i=0; i<numPred; i++)
		{
			Predator p = new Predator(this, world);
			
			//Torodial random locations
			MutableInt2D loc = new MutableInt2D();
			loc.x = world.tx(twister.nextInt());
			loc.y = world.ty(twister.nextInt());
			
			//System.out.println("loc x : " + loc.x + " loc.y: " + loc.y);
			world.setObjectLocation(p, new Int2D(loc.x,loc.y));
			Stoppable stop = schedule.scheduleRepeating(p);
			p.makeStoppable(stop);
			//System.out.println(world.getObjectLocation(p));
			
		}
		
		for(int j=0; j<numPrey; j++)
		{
			Prey prey = new Prey(this, world);
			
			//Torodial random locations
			MutableInt2D loc = new MutableInt2D();
			loc.x = world.tx(twister.nextInt());
			loc.y = world.ty(twister.nextInt());
			
			world.setObjectLocation(prey, new Int2D(loc.x, loc.y));
			Stoppable stop = schedule.scheduleRepeating(prey);
			prey.makeStoppable(stop);
			
			System.out.println(world.getObjectLocation(prey));
		}
	}
	
	/**
	 * Runs the simulation using the built in "doLoop" that steps through scheduled agents.
	 * @param args
	 */
	public static void main(String[] args)
	{
		
		doLoop(PVP.class, args);
		System.exit(0);
	}
}

