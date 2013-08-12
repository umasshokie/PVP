package sim.app.pvp;

import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

public class BehaviorProcessor {

	private int foodRewards = 0;
	private int otherRewards = 0;
	private int unpleasant = 0;
	private SparseGrid2D world;
	private int direct;

	//Sends in arguments of a Bag of items, and probability of movement, returns array of new probabilites
	BehaviorProcessor(SparseGrid2D grid){
		world = grid;
	}
	
	// Updates the probabilities of a Predator based on its vision.
	public int[] updateProbPred(Bag locs, Bag seen, int[] oldProb, Predator predator, SimState state){
		
		this.checkDisease(predator, state);

		direct = predator.direction;
		
		int[] newProb = oldProb;
		
		Bag locations = new Bag();
		//System.out.println("Seen.size(): " + seen.size());
		//System.out.println("Locs.size(): " + locs.size());
		assert (seen.size() == locs.size());
		
		for(int s = 0; s < seen.size(); s++){
			//System.out.println("I saw " + seen.get(s) + "at " + locs.get(s));
			if(seen.get(s).getClass().equals(Prey.class)){
				
				// If hungry, 100% chance of moving onto food
				if(predator.lastMeal > 56){
					
					Int2D rewardLoc = (Int2D)locs.get(s);
					for(int i = 0; i < newProb.length; i++){
						newProb[i] = 0;
					}
					
					int index = this.probIndex(world.getObjectLocation(predator), rewardLoc);
					newProb[index] = 100;
					return newProb;	
				}
				
			foodRewards++;
			locations.add(locs.get(s));
			}
			else if (seen.get(s).getClass().equals(Predator.class)){
				otherRewards++;
				locations.add(locs.get(s));
			}
		}
	
		//opposite is reduced by half, goes to location of reward
		//two sides reduced by one fourth, goes to location around reward
		
		//Reward Probability
		if(foodRewards >= 1 || foodRewards == 0 && otherRewards >= 1){
			int directOpposite = 0;
			int adjOpposite1 = 0;
			int adjOpposite2 = 0;
			Int2D pLoc = world.getObjectLocation(predator);
			
			
			//Reward locations
			Int2D rewardLoc = (Int2D) locations.get(0);
			Bag adj = this.findAdjSquares(rewardLoc, pLoc);
			Int2D adjLoc1 = (Int2D) adj.get(0);
			Int2D adjLoc2 = (Int2D) adj.get(1);
		
			
			Bag opp = this.getOpposite(rewardLoc, world.getObjectLocation(predator), predator.direction);
			Int2D oppositeCell = (Int2D) opp.get(0);
			Int2D adjCell1 = (Int2D) opp.get(1);
			Int2D adjCell2 = (Int2D) opp.get(2);
			
			//Indexes
			int directOppositeIndex = this.probIndex(pLoc, oppositeCell);
			int adjOpposite1Index = this.probIndex(pLoc, adjCell1);
			int adjOpposite2Index = this.probIndex(pLoc, adjCell2);
			int rewardIndex = this.probIndex(pLoc, rewardLoc);
			int adj1Index = this.probIndex(pLoc, adjLoc1);
			int adj2Index = this.probIndex(pLoc, adjLoc2);
			
			//variables for redistribution
			directOpposite = oldProb[directOppositeIndex]/2;
			//System.out.println("directOpposite before: " + oldProb[directOppositeIndex] + " directopp after: " + oldProb[directOppositeIndex]/2);
			adjOpposite1 = oldProb[adjOpposite1]/4;
			adjOpposite2 = oldProb[adjOpposite2]/4;
			
			//updating probability reduction
			newProb[directOppositeIndex] = directOpposite;
			newProb[adjOpposite1Index] = adjOpposite1;
			newProb[adjOpposite2Index] = adjOpposite2;
			
			System.out.println(newProb[rewardIndex]);
			newProb[rewardIndex] += directOpposite;
			System.out.println(newProb[rewardIndex]);
			newProb[adj1Index] += adjOpposite1;
			newProb[adj2Index] += adjOpposite2;
			
			for(int i = 0; i < newProb.length; i++)
				System.out.println("Prob, Index:" + i + " = " + newProb[i]);
			return newProb;
		}			
		
		return newProb;
		}
	
	/***************************************PREY***********************************************/
	//Working on predator first	
	public int[] updateProbPrey(Bag locs, Bag seen, int[] oldProb, Prey prey, SimState state){
		
		this.checkDisease(prey, state);

		direct = prey.direction;
		
		int[] newProb = oldProb;
		
		Bag locations = new Bag();
		//System.out.println("Seen.size(): " + seen.size());
		//System.out.println("Locs.size(): " + locs.size());
		assert (seen.size() == locs.size());
		
		for(int s = 0; s < seen.size(); s++){
			//System.out.println("I saw " + seen.get(s) + "at " + locs.get(s));
			if(seen.get(s).getClass().equals(Food.class)){
				
				// If hungry, 100% chance of moving onto food
				if(prey.lastMeal > 56){
					
					Int2D rewardLoc = (Int2D)locs.get(s);
					for(int i = 0; i < newProb.length; i++){
						newProb[i] = 0;
					}
					
					int index = this.probIndex(world.getObjectLocation(prey), rewardLoc);
					newProb[index] = 100;
					return newProb;	
				}
				
			foodRewards++;
			locations.add(locs.get(s));
			}
			else if (seen.get(s).getClass().equals(Prey.class)){
				otherRewards++;
				locations.add(locs.get(s));
			}
			else if (seen.get(s).getClass().equals(Predator.class)){
				//Fight or Flight -- Run away
				prey.velocity = 2;
				//***INSERT AVERSION FORMULA**
			}
		}
		
		return newProb;
	
	}
	
	private void checkDisease(Predator predator, SimState state){
		
		//If diseased, will move half of the time
		if(predator.isDiseased){
			int rand = state.random.nextInt(2);
			if(rand == 1)
				predator.velocity = 0;
			else
				predator.velocity = 1;
			
			//Not reproducing
			predator.setRepRate(0);
		}
	}
	
	private void checkDisease(Prey prey, SimState state){
		
		//If diseased will move half of the time
		if(prey.isDiseased){
			int rand = state.random.nextInt(2);
			if(rand == 1)
				prey.velocity = 0;
			else
				prey.velocity = 1;
		}
		
		//Not reproducing
		
		prey.setRepRate(0);
	}
	
	
	// This method gets the opposing cell (and soon to be side cells)
	// of a particular item's location in relation to the animal 
	// Helps with editing opposing probabilities
	private Bag getOpposite(Int2D location, Int2D pLoc, int direct){
		Bag opposites = new Bag();
		
		int x = location.getX();
		int y = location.getY();
		//UH OH DIRECTION!!!!
		int sideX1 = -1;
		int sideX2 = -1;
		int sideY1 = -1;
		int sideY2 = -1;
		
		
/*****************************************NORTH************************************/		
		//Assuming facing north
		if(direct == 0){
			
		//Peripheral vision
			
			//Left Peripheral
			if(y == pLoc.y && x == world.tx(pLoc.x - 1)){
				x = world.tx(pLoc.x + 1);
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			// Right Peripheral
			else if(y == pLoc.y && x == world.tx(pLoc.x + 1)){
				x = world.tx(pLoc.x - 1);
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x - 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			
		//Three Frontal Visions and FURTHER VISIONS
			
			//Left Front opposite OR TWO LEFT FURTHER VISIONS
			else if((y == world.ty(pLoc.y+1) && x == world.tx(pLoc.x-1)) || 
					(y == world.ty(pLoc.y+2) && x == world.tx(pLoc.x-2) ||
					(y == world.ty(pLoc.y+2) && x == world.tx(pLoc.x-1)))){
				x = world.tx(pLoc.x + 1);
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y + 1);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y - 1);
			}
			//Middle Front Opposite OR FURTHER MIDDLE
			else if((y == world.ty(pLoc.y + 1) && x == world.tx(pLoc.x)) || 
					(y == world.ty(pLoc.y + 2) && x == world.tx(pLoc.x))){
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y - 1);
				
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			
			//Right Front or TWO FURTHER RIGHT VISIONS
			else if((y == world.ty(pLoc.y + 1) && x == world.tx(pLoc.x + 1)) ||
					(y == world.ty(pLoc.y + 2) && x == world.tx(pLoc.x + 1)) ||
					(y == world.ty(pLoc.y + 2) && x == world.tx(pLoc.x + 2))){
				x = world.tx(pLoc.x - 1);
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y + 1);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y - 1);
			}
			
			else {
				System.out.println("Error");
			}
		}// end of North
		/*****************************************SOUTH************************************/				
		//Facing South
		if(direct == 1){
			
			//Peripheral vision
			
			//Right Peripheral
			if(y == pLoc.y && x == world.tx(pLoc.x - 1)){
				x = world.tx(pLoc.x + 1);
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			// Left Peripheral
			else if(y == pLoc.y && x == world.tx(pLoc.x + 1)){
				x = world.tx(pLoc.x - 1);
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x - 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			
		//Three Frontal Visions and FURTHER VISIONS
			
			//Left Front opposite OR TWO LEFT FURTHER VISIONS
			else if((y == world.ty(pLoc.y-1) && x == world.tx(pLoc.x-1)) || 
					(y == world.ty(pLoc.y-2) && x == world.tx(pLoc.x-2) ||
					(y == world.ty(pLoc.y-2) && x == world.tx(pLoc.x-1)))){
				x = world.tx(pLoc.x + 1);
				y = world.ty(pLoc.y + 1);
				
				sideX1 = world.tx(pLoc.x);
				sideY1 = world.ty(pLoc.y + 1);
				
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y);
			}
			//Middle Front Opposite OR FURTHER MIDDLE
			else if((y == world.ty(pLoc.y - 1) && x == world.tx(pLoc.x)) || 
					(y == world.ty(pLoc.y - 2) && x == world.tx(pLoc.x))){
				y = world.ty(pLoc.y + 1);
				
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y + 1);
				
				sideX2 = world.tx(pLoc.x - 1);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
			//Right Front or TWO FURTHER RIGHT VISIONS
			else if((y == world.ty(pLoc.y - 1) && x == world.tx(pLoc.x + 1)) ||
					(y == world.ty(pLoc.y - 2) && x == world.tx(pLoc.x + 1)) ||
					(y == world.ty(pLoc.y - 2) && x == world.tx(pLoc.x + 2))){
				x = world.tx(pLoc.x - 1);
				y = world.ty(pLoc.y + 1);
				
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y - 1);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
			else {
				System.out.println("Error");
			}
		}// end of South
		/*****************************************EAST************************************/	
		//East
		else if(direct == 2){
			
		//Peripheral vision
			
			//Right Peripheral
			if(y == world.ty(pLoc.y + 1) && x == pLoc.x){
				y = world.ty(pLoc.y - 1);
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			// Left Peripheral
			else if(y == world.ty(pLoc.y - 1) && x == pLoc.x){
				y = world.ty(pLoc.y + 1);
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x - 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			
		//Three Frontal Visions and FURTHER VISIONS
			
			//Left Front opposite OR TWO LEFT FURTHER VISIONS
			else if((y == world.ty(pLoc.y+1) && x == world.tx(pLoc.x+1)) || 
					(y == world.ty(pLoc.y+2) && x == world.tx(pLoc.x+2) ||
					(y == world.ty(pLoc.y+2) && x == world.tx(pLoc.x+1)))){
				x = world.tx(pLoc.x - 1);
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y - 1);
			}
			//Middle Front Opposite OR FURTHER MIDDLE
			else if((y == world.ty(pLoc.y) && x == world.tx(pLoc.x + 1)) || 
					(y == world.ty(pLoc.y) && x == world.tx(pLoc.x + 2))){
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y - 1);
				
				sideX2 = world.tx(pLoc.x - 1);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
			//Right Front or TWO FURTHER RIGHT VISIONS
			else if((y == world.ty(pLoc.y - 1) && x == world.tx(pLoc.x + 1)) ||
					(y == world.ty(pLoc.y - 1) && x == world.tx(pLoc.x + 2)) ||
					(y == world.ty(pLoc.y - 2) && x == world.tx(pLoc.x + 2))){
				x = world.tx(pLoc.x - 1);
				y = world.ty(pLoc.y + 1);
				
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
			else {
				System.out.println("Error");
			}
		}// end of East
		
		/*****************************************WEST************************************/	
		
		//WEST
		else {
			
		//Peripheral vision
			
			//Left Peripheral
			if(y == world.ty(pLoc.y + 1) && x == pLoc.x){
				y = world.ty(pLoc.y - 1);
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			// Right Peripheral
			else if(y == world.ty(pLoc.y - 1) && x == pLoc.x){
				y = world.ty(pLoc.y + 1);
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x - 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			
		//Three Front Visions and FURTHER VISIONS
			
			//Left Front opposite OR TWO LEFT FURTHER VISIONS
			else if((y == world.ty(pLoc.y-1) && x == world.tx(pLoc.x-1)) || 
					(y == world.ty(pLoc.y-2) && x == world.tx(pLoc.x-2) ||
					(y == world.ty(pLoc.y-2) && x == world.tx(pLoc.x-1)))){
				x = world.tx(pLoc.x + 1);
				y = world.ty(pLoc.y + 1);
				
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y + 1);
			}
			//Middle Front Opposite OR FURTHER MIDDLE
			else if((y == world.ty(pLoc.y) && x == world.tx(pLoc.x + 1)) || 
					(y == world.ty(pLoc.y) && x == world.tx(pLoc.x + 2))){
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y - 1);
				
				sideX2 = world.tx(pLoc.x - 1);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
			//Right Front or TWO FURTHER RIGHT VISIONS
			else if((y == world.ty(pLoc.y + 1) && x == world.tx(pLoc.x + 1)) ||
					(y == world.ty(pLoc.y + 1) && x == world.tx(pLoc.x + 2)) ||
					(y == world.ty(pLoc.y + 2) && x == world.tx(pLoc.x + 2))){
				x = world.tx(pLoc.x + 1);
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y - 1);
			}
			
			else {
				System.out.println("Error");
			}
		}// end of West
		//Adding locations to the bag
		opposites.add(new Int2D(x,y));
		
		assert(sideX1 >= 0 && sideX2 >=0 && sideY1 >=0 && sideY2 >=0);
		
		opposites.add(new Int2D(sideX1, sideY1));
		opposites.add(new Int2D(sideX2, sideY2));
		
		return opposites;
		
	}// end of opposite method
	
	//Need adjacent Method that finds two adjacent sides
	// Takes argument of location, returns bag of two
	// Adjacent Int2D locations
	private Bag findAdjSquares(Int2D loc, Int2D pLoc){
		Bag adj = new Bag();
		
		//Left location probabilities
		if(world.tx(loc.x) < world.tx(pLoc.x)){
			if(world.ty(loc.y) > world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(loc.x), world.ty(loc.y + 1)));
				adj.add(new Int2D(world.tx(loc.x - 1), world.ty(loc.y)));
			}
			else if(world.ty(loc.y)< world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(loc.x - 1), world.ty(loc.y)));
				adj.add(new Int2D(world.tx(loc.x), world.ty(loc.y - 1)));
			}
			else{
				adj.add(new Int2D(world.tx(loc.x - 1), world.ty(loc.y + 1)));
				adj.add(new Int2D(world.tx(loc.x - 1), world.ty(loc.y - 1)));
			}
		}// end of left location possibilities
			
		//Right Location Possibilities
		else if (world.tx(loc.x) > world.tx(pLoc.x)){
			if(world.ty(loc.y) > world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(loc.x), world.ty(loc.y + 1)));
				adj.add(new Int2D(world.tx(loc.x + 1), world.ty(loc.y)));
			}
			else if (world.ty(loc.y)<world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(loc.x), world.ty(loc.y - 1)));
				adj.add(new Int2D(world.tx(loc.x + 1), world.ty(loc.y)));
			}
			else{
				adj.add(new Int2D(world.tx(loc.x + 1), world.ty(loc.y + 1)));
				adj.add(new Int2D(world.tx(loc.x + 1), world.ty(loc.y - 1)));
			}
		} // end of right location possibilites
			
		//Middle possibilites
		else{
			if(world.ty(loc.y) > world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(loc.x - 1), world.ty(loc.y + 1)));
				adj.add(new Int2D(world.tx(loc.x + 1), world.ty(loc.y + 1)));			
			}
			else if (world.ty(loc.y) < world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(loc.x - 1), world.ty(loc.y - 1)));
				adj.add(new Int2D(world.tx(loc.x + 1), world.ty(loc.y + 1)));
			}
			else {
				adj.add(new Int2D(world.tx(loc.x - 1), world.ty(loc.y)));
				adj.add(new Int2D(world.tx(loc.x + 1), world.ty(loc.y)));
			}
		}// end of middle possibilities
	
	
		
		
		return adj;
	}
	
	//This method returns the index of a probability cell in movement
	// Useful for editing probabilites
	public int probIndex(Int2D pLoc, Int2D itemLoc){
		
		if(world.tx(itemLoc.x) < pLoc.x){
			if(world.ty(itemLoc.y) > pLoc.y)
				return 0;
			else if(world.ty(itemLoc.y)< pLoc.y)
				return 5;
			else
				return 3;
		} // end of left location possibilities
		else if(world.tx(itemLoc.x) > pLoc.x){
			if(world.ty(itemLoc.y) > pLoc.y)
				return 2;
			else if (world.ty(itemLoc.y) < pLoc.y)
				return 7;
			else
				return 4;
		}// end of right location possibilities
		else{
			if(world.ty(itemLoc.y) > pLoc.y)
				return 1;
			else
				return 6;
		}
	}
}
