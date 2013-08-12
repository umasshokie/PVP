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
	
	
	Prey(SimState state){
	
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
	// if(this.willEat(grid, state))
		// return;
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
		
		Prey p = new Prey(state);
		
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
			stop.stop();
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
	
	public double getRepRate(){
		
		return actualRepRate;
	}
	
	public void setRepRate(double repRate){
		actualRepRate = repRate;
	}
}
