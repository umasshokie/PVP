package sim.app.pvp;

import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

public class Prey extends Animal implements Steppable{

/**
	 * 
	 */
private static final long serialVersionUID = 1L;
private int oldAge = 1000;
private double deathRate = .0001;
private double actualRepRate = .000001;
private double agingDeathMod = 1.0001;
private int randomRepoNumb = 1000;
private double hungerDeathMod = 1.01;
private double repAge = 56;
private int deathRandNum = 10000;
private int daysLM = 28;
private Bag seen;
	
	
	Prey(SimState state, SparseGrid2D grid){
	
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
		map = new ExpectationMap(grid.getWidth(), grid.getHeight());
	}
	
	public void makeStoppable(Stoppable stopper){
		stop = stopper;
	}
	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
	 super.step(state);
	
	 //Death Chance
	 if(this.iDie(state))
		 return;
		
	 //Reproduction Chance
	 if(this.iReproduce(state))
		 return;
	
	 //Chance of Eating
	 if(this.willEat(grid, state))
		return;
	/*//Eating Food on the same location
		if(grid.getObjectsAtLocationOfObject(this) != null && this != null){
			int gridNum = grid.getObjectsAtLocationOfObject(this).numObjs;
			for(int i = 0; i < gridNum; i++){
				Object obj = (grid.getObjectsAtLocationOfObject(this)).get(i);
				if(obj.getClass().equals(Food.class) && obj != null){
					this.eat(obj);
					break;
				}// end of if
			} // end of for loop
		}// end of if statement*/
		
		//Moving every time
		super.move(grid, state);
			

	 
	}
public boolean willEat(SparseGrid2D grid, SimState state){
		
		//Eating Prey on the same location
		assert(grid.getObjectsAtLocationOfObject(this).size() != 0);
		assert(grid.getObjectsAtLocationOfObject(this) !=null);
		assert(this != null);
		assert(grid !=null);
		
		System.out.println(grid.getObjectsAtLocationOfObject(this).size());
			int gridNum = grid.getObjectsAtLocationOfObject(this).size();
			
			assert(gridNum != 0);
			
			for(int i = 0; i < gridNum; i++){
				Object obj = (grid.getObjectsAtLocationOfObject(this)).get(i);
				if(obj.getClass().equals(Food.class) && obj != null){
					this.eat(obj);
					return true;
				}// end of if
			}// end of for loop
		
		
		return false;
	}

	public void eat(Object p){
		
		//System.out.println(p);
			Food food = (Food) p;
			if(food.isDiseased())
				this.setDisease(true);
			//System.out.println(this + " ate " + p);
			lastMeal = 0;
			grid.remove(p);
			//System.out.println("Food is removed");
		
	}
	
	public void setDiseased(boolean dis){
		isDiseased = dis;
	}
	
	
	public void reproduce(SimState state){
		
		Prey p = new Prey(state, grid);
		
		grid.setObjectLocation(p, grid.getObjectLocation(this));
		state.schedule.scheduleRepeating(p);
		
	}
	
	public boolean isDiseased(){
		return isDiseased;
	}

	public boolean iDie(SimState state){
		//older = more likely to die
	 	if(age>oldAge)
	 		deathRate = deathRate * agingDeathMod;
	 	
	 	//Last meal, more likely to die
	 	if(lastMeal > daysLM)
			deathRate = deathRate * hungerDeathMod;
		//System.out.println("deathRate: " + deathRate);
		
	 // Death Rate
		double d = state.random.nextInt(deathRandNum);
		double death = d/deathRandNum;
		
		//System.out.println("d: " + d + " death: " + death);
		if(death < deathRate && death != 0){
			this.stop.stop();
			grid.remove(this);
			return true;
		}
		return false;
	}
	
	public boolean iReproduce(SimState state){
	// Reproduction Rate
		double r = state.random.nextInt(randomRepoNumb);
		double repo = r/randomRepoNumb;
		if(repo <= actualRepRate && age >= repAge){
			this.reproduce(state);
			return true;
			}
		return false;
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
		double[] newProb = behavior.updateProbPrey(locs, seen, defaultProb, this, state);
		
		actualProb = newProb;
	}
	public double getRepRate(){
		
		return actualRepRate;
	}
	
	public void setRepRate(double repRate){
		actualRepRate = repRate;
	}
	
}
