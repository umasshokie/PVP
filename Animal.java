package sim.app.pvp;

import ec.util.MersenneTwisterFast;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

public abstract class Animal implements Steppable {

	protected SparseGrid2D grid;
	protected boolean isDiseased = false;
	protected int age = 0;
	protected int direction;
	protected int lastMeal = 0;
	protected final static double[] defaultProb = {25,25,25,8,8,3,3,3};
	protected double[] actualProb = new double[8];
	public final static int NORTH = 0;
	public final static int SOUTH = 1;
	public final static int EAST = 2;
	public final static int WEST = 3;
	public static int numPrey;
	public static int numPredator;
	protected static double expectMapDecayRate;
	protected int velocity = 1;
	protected BehaviorProcessor behavior;
	protected VisualProcessor vP;
	protected Stoppable stop;
	protected ExpectationMap map;
	protected static int maxHunger;
	protected static int maxSocial;
	protected int lastSocial = 0;
	protected int directChangeTotal = 0;
	protected String ID;
	protected Bag allObjects = new Bag();
	
	protected final static void initialize(int prey, int pred, double exMap){
		numPrey = prey;
		numPredator = pred;
		expectMapDecayRate = exMap;
	}
	
	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
		PVP pvp = (PVP)state;
		
		grid = pvp.world;
		age++;
		lastMeal++;
		lastSocial++;
		//Start of every step uses default movement
		actualProb = defaultProb;
		vP = new VisualProcessor(state);
		
		System.out.println("TimeStep: " + state.schedule.getTime() + " Prey: " + numPrey + " Predator: " + numPredator);
		
	}

	protected void move(SparseGrid2D grid, SimState pvp){
		
		
		// Biased Random Movement
		Int2D cord = grid.getObjectLocation(this);
		//assert ---Cord != null);
		if(cord != null){
		int xCord = cord.getX();
		int yCord = cord.getY();
		
		
		int choice = pvp.random.nextInt(100);
		//System.out.println(choice);
		
		//Each direction has biased defaultProbabilities
	
		int facing = direction;
		
		
		//Facing upward
		switch(facing){
		
			case NORTH:
				if (choice < actualProb[0]){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4]+ actualProb[5])){
					grid.setObjectLocation(this, grid.tx(xCord-velocity), grid.ty(yCord - velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4]+ actualProb[5] + actualProb[6])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord - velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6] + actualProb[7])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord -velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
			
			
			//Facing to the left
			case WEST: 
				if (choice < actualProb[0]){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord - velocity));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord + velocity));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord- velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord - velocity));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6] + actualProb[7])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord  + velocity));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
			
			//Facing Downwards
			case SOUTH:
				if (choice < actualProb[0]){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord - velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord - velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord - velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6] + actualProb[7])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord  + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
			
			
			//Facing to the right
			case EAST:
				if (choice < actualProb[0]){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord + velocity));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2])){
					grid.setObjectLocation(this, grid.tx(xCord + velocity), grid.ty(yCord - velocity));
					this.direction = EAST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord + velocity));
					this.direction = NORTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4])){
					grid.setObjectLocation(this, grid.tx(xCord), grid.ty(yCord - velocity));
					this.direction = SOUTH;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord + velocity));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				else if (choice < (actualProb[0] + actualProb[1] + actualProb[2] + actualProb[3] + actualProb[4] + actualProb[5] + actualProb[6] + actualProb[7])){
					grid.setObjectLocation(this, grid.tx(xCord - velocity), grid.ty(yCord  - velocity));
					this.direction = WEST;
					if(facing != direction)
						directChangeTotal++;
					break;
				}
				
		}}
		}
	
	protected abstract void eat(Object p);
	
	protected abstract void reproduce(SimState state);
	
	protected void setDisease(boolean diseased){
		isDiseased = diseased;
	}
	
	protected enum Direction{
		NORTH(0), SOUTH(1), EAST(2), WEST(3);
		
		private int value;
		
		private Direction(int value){
			this.value = value;
		}
	}
	

	}
