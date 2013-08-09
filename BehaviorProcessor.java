package sim.app.pvp;

import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;

public class BehaviorProcessor {

	private int rewards = 0;
	private int unpleasant = 0;
	private SparseGrid2D world;
	private int direct;
	//Sends in arguments of a Bag of items, and probability of movement, returns array of new probabilites
	BehaviorProcessor(SparseGrid2D grid){
		world = grid;
	}
	
	// Updates the probabilities of a Predator based on its vision.
	public int[] updateProbPred(Bag locs, Bag seen, int[] oldProb, Predator predator){
		

		direct = predator.direction;
		
		int[] newProb = new int[8];
		
		Bag locations = new Bag();
		for(int s = 0; s < seen.size(); s++){
			System.out.println("I saw " + seen.get(s) + "at " + locs.get(s));
			if(seen.get(s).getClass().equals(Prey.class)){
				
				// If hungry, 100% chance of moving onto food
				if(predator.lastMeal > 56){
					
					Int2D rewardLoc = (Int2D)locs.get(s);
					for(int i = 0; i < oldProb.length; i++){
						newProb[i] = 0;
					}
					
					int index = this.probIndex(world.getObjectLocation(predator), rewardLoc);
					newProb[index] = 100;
					return newProb;
					
				}
				
				rewards++;
				locations.add(locs.get(s));
				}
				//Save Prey location, calculate opposite
			}
	
		
			
			
			//opposite is reduced by half, goes to location of reward
			//two sides reduced by one fourth, goes to location around reward
				
	
			
			//Need to calculate opposite position of Prey
			//Increase and redistribute
		
					
		
		return oldProb;
		}
	//Working on predator first	
	public int[] updateProbPrey(Bag locs, Bag seen, int[] oldProb){
		
		for(int s = 0; s < seen.size(); s++){
			if(seen.get(s).equals(Predator.class))
				System.out.println("I SAW A PREDATOR!!!");
			if(seen.get(s).equals(Food.class))
				System.out.println("I SAW FOOD");
		}
		
		return oldProb;
		//}
	}
	// This method gets the opposing cell (and soon to be side cells)
	// of a particular item's location in relation to the animal 
	// Helps with editing opposing probabilities
	private Int2D getOpposite(Int2D location, Int2D pLoc){
		
		int x = location.getX();
		int y = location.getY();
		//UH OH DIRECTION!!!!
		int sideX1;
		int sideX2;
		int sideY1;
		int sideY2;
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
		if(direct == 2){
			
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
		if(direct == 2){
			
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
		return new Int2D(x, y);
		
	}// end of opposite method
	
	
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
