package sim.app.pvp;

import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

public class Predator extends Animal implements Steppable{

private static int oldAge;
private static double defaultDeathRate;
private double actualDeathRate;
private static int deathRandNum;
private static double agingDeathMod;
private static double hungerDeathMod;
private static int lastMealLow;
private static int lastMealMed;
private static int lastMealHigh;
private static int repAge;
private static int repRandNum;
private static double actualRepRate;
private static double defaultRepRate;
private Bag seen;



	
	Predator(SimState state, SparseGrid2D grid, int num){
		
		int directionNum= state.random.nextInt(3);
		if(directionNum == 0)
			direction = 0;
		else if(directionNum == 1)
			direction = 1;
		else if (directionNum == 2)
			direction = 2;
		else
			direction = 3;
		vP = new VisualProcessor(state);
		map = new ExpectationMap(grid.getWidth(), grid.getHeight(), expectMapDecayRate);
		maxHunger = 30;
		maxSocial = 50;
		actualRepRate = defaultRepRate;
		actualDeathRate = defaultDeathRate;
		ID = "F" + num;
	}
	
	protected final static void initializePred(int maxH, int old,
			double dR, int dRN, double agDM, double hDM, int lmL, int lmM, int lmH, int rA, double dRR, int rRN ){
	
		maxHunger = maxH;
		oldAge = old;
		defaultDeathRate = dR;
		deathRandNum = dRN;
		agingDeathMod = agDM;
		hungerDeathMod = hDM;
		lastMealLow = lmL;
		lastMealMed = lmM;
		lastMealHigh = lmH;
		repAge = rA;
		defaultRepRate = dRR;
		repRandNum = rRN;
		
	}
	
	public void makeStoppable(Stoppable stopper){
		stop = stopper;
	}
	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
		super.step(state);
		
		// Timesteps since last social interaction
		//System.out.println("Last Meal: " + lastMeal + " timesteps");
		
		//Death Calculations
		if(this.iDie(state)){
			System.out.print(", " + ID);
			map.printMaps();
			System.out.print(", " + lastMeal);
			System.out.print(", " + actualDeathRate);
			System.out.print(", " + lastSocial);
			System.out.print(", " + directChangeTotal + "\n");
			return;
		}
		
		//Reproduction Calculations
		else if(this.iReproduce(state)){
			System.out.print(", " + ID);
			map.printMaps();
			System.out.print(", " + lastMeal);
			System.out.print(", " + actualDeathRate);
			System.out.print(", " + lastSocial);
			System.out.print(", " + directChangeTotal + "\n");
			return;
		}
		
		//Will I eat?
		else if(this.willEat(grid, state)){
			System.out.print(", " + ID);
			map.printMaps();
			System.out.print(", " + lastMeal);
			System.out.print(", " + actualDeathRate);
			System.out.print(", " + lastSocial);
			System.out.print(", " + directChangeTotal);
			System.out.print(", " + "Predator Ate" + "\n");
			return;
		}
		
		//Visual Processor
		else
			this.vision(state, grid);
			
		
	
		
		//End of Step, print out tests
		System.out.print(", " + ID);
		map.printMaps();
		System.out.print(", " + lastMeal);
		System.out.print(", " + actualDeathRate);
		System.out.print(", " + lastSocial);
		System.out.print(", " + directChangeTotal + "\n");
		

	}

	//Method that allows Predator to kill its Prey
	public void eat(Object p){
		assert (p != null);
		if(p.getClass().equals(Prey.class)){
		
			Prey prey = (Prey) p;
			assert(prey != null);
			if(prey.isDiseased())
				this.setDisease(true);
			lastMeal = 0;
			prey.stop.stop();
			numPrey--;
			grid.remove(prey);
			//System.out.println("Prey was eaten by Predator");

	
			
		}
			
	}
	
	
	//Method that "kills" the Predator by removing it from the grid
	public boolean iDie(SimState state){
		
		 //older = more likely to die
		 //if(age>oldAge)
		 	//actualDeathRate = actualDeathRate * agingDeathMod;
		 	
		 //Last meal, more likely to die
		 if(lastMeal > lastMealMed)
			actualDeathRate = actualDeathRate * hungerDeathMod;
		/*//System.out.println("deathRate: " + deathRate);
		 if(lastMeal > lastMealHigh){
			 stop.stop();
			 numPredator--;
			 grid.remove(this);
			 return true;
		 }*/
		 // Death Rate
		double d = state.random.nextInt(deathRandNum);
		double death = d/deathRandNum;
		
		assert(d >= 0 && death >=0);
		
		//System.out.println("d: " + d + " death: " + death);
		if(death < actualDeathRate){
			System.out.println(this + " Died from Death Chance");
			stop.stop();
			numPredator--;
			grid.remove(this);
			
				
			return true;
		}
		
		
		return false;
	}
	
	public boolean iReproduce(SimState state){
		// Reproduction Rate
		double r = state.random.nextInt(repRandNum);
		double repo = r/repRandNum;
				
		assert (r >= 0 && repo >= 0);
				
		if(repo <= actualRepRate && age >= repAge){
			this.reproduce(state);
			return true;
		}
		return false;
	}
	
	public boolean willEat(SparseGrid2D grid, SimState state){
		
		if(lastMeal < lastMealLow)
			actualRepRate = actualRepRate * 1.5;
		/*
		else if(lastMeal < lastMealMed){
			if(state.schedule.getTime()%1 == 0)
				return false;
		}*/
		
		//Eating Prey on the same location
		assert(grid.getObjectsAtLocationOfObject(this) !=null);
		
		
		//System.out.println(grid.getObjectsAtLocationOfObject(this).size());
		int gridNum = grid.getObjectsAtLocationOfObject(this).size();
			
		assert(gridNum != 0);
			
			for(int i = 0; i < gridNum; i++){
				Object obj = (grid.getObjectsAtLocationOfObject(this)).get(i);
				if(obj.getClass().equals(Prey.class)){
					//System.out.println("Predator Ate");
					this.eat(obj);
					return true;
				}// end of if
			}// end of for loop
		
		
		return false;
	}
	//Method that allows Predator to duplicate
	public void reproduce(SimState state){
		
		//System.out.println("Predator Reproduced");
		
		Predator p = new Predator(state, grid, numPredator + 1);
		numPredator++;
		grid.setObjectLocation(p, grid.getObjectLocation(this));
		Stoppable stop = state.schedule.scheduleRepeating(p);
		p.makeStoppable(stop);
	}
	public void vision(SimState state, SparseGrid2D grid){
		
		Int2D cord = grid.getObjectLocation(this);
		assert(cord != null);

		seen = vP.sight(cord.x, cord.y, state, direction);
		Bag locations = new Bag();
		if(state.schedule.getTime()%2 != 0)
			map.updateMapsPred(seen, grid);
		
		//map.printMaps();
		for(int s = 0; s < seen.size(); s++){
			
			Int2D obLoc = grid.getObjectLocation(seen.get(s));
	
			locations.add(obLoc);
			//System.out.println(" at location:" + obLoc);
			//if(j.equals(Prey.class))
				//System.out.println("****" + seen.get(s));
			
		}
			
		this.behaviorProb(locations, seen, state);
		
		//Move every timestep
		super.move(grid, state);
		
		//System.out.println("Predator Moved");
	}// end of vision
	
	public void behaviorProb(Bag locs, Bag seen, SimState state){
	
		behavior = new BehaviorProcessor(grid);
		double[] newProb = behavior.updateProbPred(locs, seen, defaultProb, this, state, maxHunger);
		
		actualProb = newProb;
	}
	
	public double getRepRate(){
		
		return actualRepRate;
	}
	
	public void setRepRate(double repRate){
		actualRepRate = repRate;
	}
}
