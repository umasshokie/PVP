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
	private int maxFoodIncrease;
	private int maxHunger = 30;
	private double mainDivider = 2.0;
	private double adjDivider = 4.0;

	//Sends in arguments of a Bag of items, and probability of movement, returns array of new probabilites
	BehaviorProcessor(SparseGrid2D grid){
		world = grid;
	}
	
	// Updates the probabilities of a Predator based on its vision.
	public double[] updateProbPred(Bag locs, Bag seen, double[] oldProb, Predator predator, SimState state){
		
		this.checkDisease(predator, state);

		direct = predator.direction;
		double[] newProb = new double[8];
		
		for(int i = 0; i < oldProb.length; i++){
			System.out.println("oldProb[i]: " + oldProb[i]);
			newProb[i] = oldProb[i];
		}
	
		
		
		Bag sLocations = new Bag();
		Bag fLocations = new Bag();
		//System.out.println("Seen.size(): " + seen.size());
		//System.out.println("Locs.size(): " + locs.size());
		assert (seen.size() == locs.size());
		
		System.out.println("Predator Location: " + predator.grid.getObjectLocation(predator));
		System.out.println("Predator direction: " + predator.direction);
		System.out.println();
		
		for(int s = 0; s < seen.size(); s++){
			System.out.println("I saw " + seen.get(s) + "at " + locs.get(s));
			if(seen.get(s).getClass().equals(Prey.class)){
				
				foodRewards++;
				fLocations.add(locs.get(s));
			}
			else if (seen.get(s).getClass().equals(Predator.class)){
				otherRewards++;
				sLocations.add(locs.get(s));
			}
		}

		//opposite is reduced by half, goes to location of reward
		//two sides reduced by one fourth, goes to location around reward
		
		Bag oldRewardLoc = new Bag();
	
		
		//Reward Probability
		for(int f = 0; f< fLocations.size(); f++){
			Int2D pLoc = world.getObjectLocation(predator);
			
			//Set of conditions. If hungry
			//Reward locations
			Int2D rewardLoc = (Int2D) fLocations.get(0);
			
	
			//System.out.println("RewardLoc: " + rewardLoc);
			Bag adj = this.findAdjSquares(rewardLoc, pLoc, predator.direction);
			Int2D adjLoc1 = (Int2D) adj.get(1);
			Int2D adjLoc2 = (Int2D) adj.get(2);
			System.out.println("Adjacent Loc1: " + adjLoc1);
			System.out.println("Adjacent Loc2: " + adjLoc2);
		
			
			Bag opp = this.getOpposite(rewardLoc, world.getObjectLocation(predator), predator.direction);
			Int2D oppositeCell = (Int2D) opp.get(0);
			Int2D adjCell1 = (Int2D) opp.get(1);
			Int2D adjCell2 = (Int2D) opp.get(2);
			
			System.out.println("Opposite Cell: " + oppositeCell);
			System.out.println("Opp Adj Cell 1:" + adjCell1);
			System.out.println("Opp Adj Cell 2:" + adjCell2);
			
			//Indexes
			int directOppositeIndex = this.probIndex(pLoc, oppositeCell, predator.direction);
			int adjOpposite1Index = this.probIndex(pLoc, adjCell1, predator.direction);
			int adjOpposite2Index = this.probIndex(pLoc, adjCell2, predator.direction);
			int rewardIndex = this.probIndex(pLoc, rewardLoc, predator.direction);
			int adj1Index = this.probIndex(pLoc, adjLoc1, predator.direction);
			int adj2Index = this.probIndex(pLoc, adjLoc2, predator.direction);
			
			System.out.println("DirectOppositeIndex: " + directOppositeIndex);
			System.out.println("AdjacentOppositeIndex1: " + adjOpposite1Index);
			System.out.println("AdjacentOppositeIndex2: " + adjOpposite2Index);
			System.out.println("RewardIndex: " + rewardIndex);
			System.out.println("Adjacent 1 Index: " + adj1Index);
			System.out.println("Adjacent 2 Index: " + adj2Index);
			
			System.out.println("Adding for largest:" + (newProb[directOppositeIndex]/ mainDivider));
			System.out.println("Adding for adjacent1Opposite: " + (newProb[adjOpposite1Index]/adjDivider));
			System.out.println("Adding for adjacent2Opposite: " + (newProb[adjOpposite2Index]/adjDivider));
			
			if(oldRewardLoc.contains(rewardLoc)){
				mainDivider = 1.5;
				adjDivider = 2.0;
				
			}
			double opposite = newProb[directOppositeIndex];
			double directOppositeReduction = opposite/mainDivider;
			
			
			double adjOpp1 = newProb[adjOpposite1Index];
			double adjOpp1Reduction = adjOpp1/adjDivider;
			
			double adjOpp2 = newProb[adjOpposite2Index];
			double adjOpp2Reduction = adjOpp2/adjDivider;
			
			//variables for redistribution
			newProb[directOppositeIndex] = oldProb[directOppositeIndex] - (directOppositeReduction);
			newProb[adjOpposite1Index] = oldProb[adjOpposite1Index] - (adjOpp1Reduction);
			newProb[adjOpposite2Index] = oldProb[adjOpposite2Index] - (adjOpp2Reduction);
			//updating probability reduction
			
			newProb[rewardIndex] = oldProb[rewardIndex] + directOppositeReduction;
			newProb[adj1Index] = oldProb[adj1Index] + (adjOpp1Reduction);
			newProb[adj2Index] = oldProb[adj2Index] + (adjOpp2Reduction);
			
			oldRewardLoc.add(rewardLoc);
			
		}
		
		
	
		for(int i = 0; i < newProb.length; i++){
			System.out.println("newProb[i]: " + newProb[i]);
		}
		
		//Social rewards
		for(int s = 0; s< sLocations.size(); s++){
			Int2D pLoc = world.getObjectLocation(predator);
			
			//Set of conditions. If hungry
			//Reward locations
			Int2D rewardLoc = (Int2D) sLocations.get(0);
			
			//if(oldRewardLoc.contains(rewardLoc)){
				
			//}
			//System.out.println("RewardLoc: " + rewardLoc);
			Bag adj = this.findAdjSquares(rewardLoc, pLoc, predator.direction);
			Int2D adjLoc1 = (Int2D) adj.get(1);
			Int2D adjLoc2 = (Int2D) adj.get(2);
			System.out.println("Adjacent Loc1: " + adjLoc1);
			System.out.println("Adjacent Loc2: " + adjLoc2);
		
			
			Bag opp = this.getOpposite(rewardLoc, world.getObjectLocation(predator), predator.direction);
			Int2D oppositeCell = (Int2D) opp.get(0);
			Int2D adjCell1 = (Int2D) opp.get(1);
			Int2D adjCell2 = (Int2D) opp.get(2);
			
			System.out.println("Opposite Cell: " + oppositeCell);
			System.out.println("Opp Adj Cell 1:" + adjCell1);
			System.out.println("Opp Adj Cell 2:" + adjCell2);
			
			//Indexes
			int directOppositeIndex = this.probIndex(pLoc, oppositeCell, predator.direction);
			int adjOpposite1Index = this.probIndex(pLoc, adjCell1, predator.direction);
			int adjOpposite2Index = this.probIndex(pLoc, adjCell2, predator.direction);
			int rewardIndex = this.probIndex(pLoc, rewardLoc, predator.direction);
			int adj1Index = this.probIndex(pLoc, adjLoc1, predator.direction);
			int adj2Index = this.probIndex(pLoc, adjLoc2, predator.direction);
			
			System.out.println("DirectOppositeIndex: " + directOppositeIndex);
			System.out.println("AdjacentOppositeIndex1: " + adjOpposite1Index);
			System.out.println("AdjacentOppositeIndex2: " + adjOpposite2Index);
			System.out.println("RewardIndex: " + rewardIndex);
			System.out.println("Adjacent 1 Index: " + adj1Index);
			System.out.println("Adjacent 2 Index: " + adj2Index);
			
			System.out.println("Adding for largest:" + (newProb[directOppositeIndex]/ 4.0));
			System.out.println("Adding for adjacent1Opposite: " + (newProb[adjOpposite1Index]/8.0));
			System.out.println("Adding for adjacent2Opposite: " + (newProb[adjOpposite2Index]/8.0));
			
			double opposite = newProb[directOppositeIndex];
			double directOppositeReduction = opposite/2.0;
			
			double adjOpp1 = newProb[adjOpposite1Index];
			double adjOpp1Reduction = adjOpp1/4.0;
			
			double adjOpp2 = newProb[adjOpposite2Index];
			double adjOpp2Reduction = adjOpp2/4.0;
			
			//variables for redistribution
			newProb[directOppositeIndex] = oldProb[directOppositeIndex] - (directOppositeReduction);
			newProb[adjOpposite1Index] = oldProb[adjOpposite1Index] - (adjOpp1Reduction);
			newProb[adjOpposite2Index] = oldProb[adjOpposite2Index] - (adjOpp2Reduction);
			//updating probability reduction
			
			newProb[rewardIndex] = oldProb[rewardIndex] + directOppositeReduction;
			newProb[adj1Index] = oldProb[adj1Index] + (adjOpp1Reduction);
			newProb[adj2Index] = oldProb[adj2Index] + (adjOpp2Reduction);
			
			oldRewardLoc.add(rewardLoc);
			
		}

	
		/*for(int i = 0; i < newProb.length; i++){
			System.out.println("Social-newProb[i]: " + newProb[i]);
		}*/
		
		return newProb;
		
		
		}
	
	
	/***************************************PREY***********************************************/
	//Working on predator first	
	public double[] updateProbPrey(Bag locs, Bag seen, double[] oldProb, Prey prey, SimState state){
		
		this.checkDisease(prey, state);

		direct = prey.direction;
		
		double[] newProb = oldProb;
		
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
					
					int index = this.probIndex(world.getObjectLocation(prey), rewardLoc, prey.direction);
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
		/*
		System.out.println("location: " + location);
		System.out.println("pLoc: " + pLoc);
		System.out.println("direct: " + direct);
		System.out.println("x: "+ x);
		System.out.println("y: " + y);*/
/*****************************************NORTH************************************/		
		//Facing North
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
			//Right Peripheral
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
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
			else {
				System.out.println("Error - North");
			}
		}// end of South
		
/*****************************************SOUTH************************************/				
	
		//Assuming facing south
		else if(direct == 1){
			
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
			
			//Right Front opposite OR TWO RIGHT FURTHER VISIONS
			else if((y == world.ty(pLoc.y+1) && x == world.tx(pLoc.x-1)) || 
					(y == world.ty(pLoc.y+2) && x == world.tx(pLoc.x-2) ||
					(y == world.ty(pLoc.y+2) && x == world.tx(pLoc.x-1)))){
				x = world.tx(pLoc.x + 1);
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y);
				
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
			
			//Left Front or TWO FURTHER LEFT VISIONS
			else if((y == world.ty(pLoc.y + 1) && x == world.tx(pLoc.x + 1)) ||
					(y == world.ty(pLoc.y + 2) && x == world.tx(pLoc.x + 1)) ||
					(y == world.ty(pLoc.y + 2) && x == world.tx(pLoc.x + 2))){
				x = world.tx(pLoc.x - 1);
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y - 1);
			}
			
			else {
				System.out.println("Error - South");
			}
		}// end of South
		/*****************************************EAST************************************/	
		//East
		else if(direct == 2){
			
		//Peripheral vision
			
			//Right Peripheral
			if(y == world.ty(pLoc.y + 1) && x == pLoc.x){
				y = world.ty(pLoc.y - 1);
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y - 1);
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y - 1);
			}
			// Left Peripheral
			else if(y == world.ty(pLoc.y - 1) && x == pLoc.x){
				y = world.ty(pLoc.y + 1);
				sideX1 = world.tx(pLoc.x - 1);
				sideY1 = world.ty(pLoc.y + 1);
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
		//Three Frontal Visions and FURTHER VISIONS
			
			//Left Front opposite OR TWO LEFT FURTHER VISIONS
			else if((y == world.ty(pLoc.y+1) && x == world.tx(pLoc.x+1)) || 
					(y == world.ty(pLoc.y+2) && x == world.tx(pLoc.x+2) ||
					(y == world.ty(pLoc.y+1) && x == world.tx(pLoc.x+2)))){
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
				x = world.tx(pLoc.x - 1);
				
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
				System.out.println("Error - East");
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
			
			//Right Front opposite OR TWO RIGHT FURTHER VISIONS
			else if((y == world.ty(pLoc.y+1) && x == world.tx(pLoc.x-1)) || 
					(y == world.ty(pLoc.y+1) && x == world.tx(pLoc.x-2)) ||
					(y == world.ty(pLoc.y+2) && x == world.tx(pLoc.x-2))){
			
				x = world.tx(pLoc.x + 1);
				y = world.ty(pLoc.y - 1);
				
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y - 1);
			}
			//Middle Front Opposite OR FURTHER MIDDLE
			else if((y == world.ty(pLoc.y) && x == world.tx(pLoc.x - 1)) || 
					(y == world.ty(pLoc.y) && x == world.tx(pLoc.x - 2))){
				
				x = world.ty(pLoc.x + 1);
				
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y - 1);
				
				sideX2 = world.tx(pLoc.x + 1);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
			//Left Front or TWO FURTHER LEFT VISIONS
			else if((y == world.ty(pLoc.y - 1) && x == world.tx(pLoc.x - 1)) ||
					(y == world.ty(pLoc.y - 1) && x == world.tx(pLoc.x - 2)) ||
					(y == world.ty(pLoc.y - 2) && x == world.tx(pLoc.x - 2))){
			
				
				x = world.tx(pLoc.x + 1);
				y = world.ty(pLoc.y + 1);
				
				sideX1 = world.tx(pLoc.x + 1);
				sideY1 = world.ty(pLoc.y);
				
				sideX2 = world.tx(pLoc.x);
				sideY2 = world.ty(pLoc.y + 1);
			}
			
			else {
				System.out.println("Error - West");
			}
		}// end of West
		//Adding locations to the bag
		opposites.add(new Int2D(x,y));
		
		assert(sideX1 >= 0 && sideX2 >=0 && sideY1 >=0 && sideY2 >=0);
		
		opposites.add(new Int2D(sideX1, sideY1));
		opposites.add(new Int2D(sideX2, sideY2));
		
		/*for(int o = 0; o < opposites.size(); o++){
			System.out.println("Opposites[o]: " + opposites.get(o));
		}*/
		
		return opposites;
		
	}// end of opposite method
	
	//Need adjacent Method that finds two adjacent sides
	// Takes argument of location, returns bag of two
	// Adjacent Int2D locations
	private Bag findAdjSquares(Int2D loc, Int2D pLoc, int direction){
		Bag adj = new Bag();
		//System.out.println("Location: " + loc);
		//System.out.println("PLOC: " + pLoc);
		int x = loc.x;
		int y = loc.y;
		
		//For further visual in north direction, reset adj squares to correspond to possible movement square
		//North
		if(direction == 0){
			if(world.ty(loc.y) == world.ty(pLoc.y - 2)){
				if(world.tx(loc.x) == world.tx(pLoc.x - 2) || world.tx(loc.x) == world.tx(pLoc.x - 1)){
					x = world.tx(pLoc.x - 1);
					y = world.ty(pLoc.y - 1);
				}
				else if (world.tx(loc.x) == world.tx(pLoc.x)){
					y = world.ty(pLoc.y - 1);
				}
				else if(world.tx(loc.x) == world.tx(pLoc.x + 1) || world.tx(loc.x) == world.tx(pLoc.x + 2)){
					x = world.tx(pLoc.x + 1);
					y = world.ty(pLoc.y - 1);
				}
			}
		}
		//South
		else if (direction == 1){
			if(world.ty(loc.y) == world.ty(pLoc.y + 2)){
				if(world.tx(loc.x) == world.tx(pLoc.x -2) || world.tx(loc.x) == world.tx(pLoc.x - 1)){
					x = world.tx(pLoc.x - 1);
					y = world.ty(pLoc.y + 1);
				}
				else if (world.tx(loc.x) == world.tx(pLoc.x)){
					y = world.ty(pLoc.y + 1);
				}
				else if (world.tx(loc.x) == world.tx(pLoc.x + 1) || world.tx(loc.x) == world.tx(pLoc.x + 2)){
					x = world.tx(pLoc.x + 1);
					y = world.ty(pLoc.y + 1);
				}
			}
		}
		//East
		else if (direction == 2){
			if (world.tx(loc.x) == world.tx(pLoc.x + 1)){
				x = world.tx(pLoc.x + 1);
			}
			else if(world.tx(loc.x) == world.tx(pLoc.x + 2)){
				if(world.ty(loc.y) == world.ty(pLoc.y - 2) || world.ty(loc.y) == world.ty(pLoc.y - 1)){
					x = world.tx(pLoc.x + 1);
					y = world.ty(pLoc.y - 1);
				}
				else if (world.ty(loc.y) == world.ty(pLoc.y)){
					x = world.tx(pLoc.x + 1);
				}
				else if (world.ty(loc.y) == world.ty(pLoc.y + 1) || world.ty(loc.y) == world.ty(pLoc.y + 2)){
					x = world.tx(pLoc.x + 1);
					y = world.ty(pLoc.y + 1);
				}
			}
			
		}
		//West
		else{
			if (world.tx(loc.x) == world.tx(pLoc.x - 2)){
				if(world.ty(loc.y) == world.ty(pLoc.y - 2) || world.ty(loc.y) == world.ty(pLoc.y - 1)){
					x = world.tx(pLoc.x - 1);
					y = pLoc.y - 1;
				}
				else if (world.ty(loc.y) == world.ty(pLoc.y)){
					x = world.tx(pLoc.x - 1);
				}
				else if (world.ty(loc.y) == world.ty(pLoc.y + 1) || world.ty(loc.y) == world.ty(pLoc.y + 2)){
					x = world.tx(pLoc.x - 1);
					y = world.ty(pLoc.y + 1);
				}
			}
		}
	
		//Adding first element to the bag to be location of reward
		adj.add(new Int2D(x, y));
		//System.out.println("X and Y: " + adj.get(0));
		
		//Left location probabilities
		if(world.tx(x) < world.tx(pLoc.x)){
			if(world.ty(y) > world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(pLoc.x), world.ty(pLoc.y + 1)));
				adj.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y)));
			}
			else if(world.ty(y)< world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y)));
				adj.add(new Int2D(world.tx(pLoc.x), world.ty(pLoc.y - 1)));
			}
			else{
				adj.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y + 1)));
				adj.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y - 1)));
			}
		}// end of left location possibilities
			
		//Right Location Possibilities
		else if (world.tx(x) > world.tx(pLoc.x)){
			if(world.ty(y) < world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(pLoc.x), world.ty(pLoc.y - 1)));
				adj.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y)));
			}
			else if (world.ty(y)>world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(pLoc.x), world.ty(pLoc.y + 1)));
				adj.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y)));
			}
			else{
				adj.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y + 1)));
				adj.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y - 1)));
			}
		} // end of right location possibilites
			
		//Middle possibilites
		else{
			if(world.ty(y) > world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y + 1)));
				adj.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y + 1)));			
			}
			else if (world.ty(y) < world.ty(pLoc.y)){
				adj.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y - 1)));
				adj.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y - 1)));
			}
			else {
				System.out.println("Adjacent Error");
				adj.add(new Int2D(world.tx(pLoc.x - 1), world.ty(pLoc.y)));
				adj.add(new Int2D(world.tx(pLoc.x + 1), world.ty(pLoc.y)));
			}
		}// end of middle possibilities
	
	/*	for(int a = 0; a < adj.size(); a++){
			System.out.println("Adjacent Bag:" + a + " " + adj.get(a));
		}*/
		
		
		return adj;
	}
	
	//This method returns the index of a probability cell in movement
	// Useful for editing probabilites
	public int probIndex(Int2D pLoc, Int2D itemLoc, int direct){
		
		//North
		if(direct == 0){
			if(world.tx(itemLoc.x) < world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 0;
				else if(world.ty(itemLoc.y)> world.ty(pLoc.y))
					return 5;
				else
					return 3;
			} // end of left location possibilities
			else if(world.tx(itemLoc.x) > world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) <world.ty(pLoc.y))
					return 2;
				else if (world.ty(itemLoc.y) > world.ty(pLoc.y))
					return 7;
				else
					return 4;
			}// end of right location possibilities
			else{
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 1;
				else
					return 6;
			}
		}//end of North
		//South
		else if (direct == 1){
			if(world.tx(itemLoc.x) < world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 7;
				else if(world.ty(itemLoc.y)> world.ty(pLoc.y))
					return 2;
				else
					return 4;
			} // end of left location possibilities
			else if(world.tx(itemLoc.x) > world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) <world.ty(pLoc.y))
					return 5;
				else if (world.ty(itemLoc.y) > world.ty(pLoc.y))
					return 0;
				else
					return 3;
			}// end of right location possibilities
			else{
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 6;
				else
					return 1;
			}
		}//end of South
		//East
		else if (direct == 2){
			if(world.tx(itemLoc.x) < world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 5;
				else if(world.ty(itemLoc.y)> world.ty(pLoc.y))
					return 7;
				else
					return 6;
			} // end of left location possibilities
			else if(world.tx(itemLoc.x) > world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) <world.ty(pLoc.y))
					return 0;
				else if (world.ty(itemLoc.y) > world.ty(pLoc.y))
					return 2;
				else
					return 1;
			}// end of right location possibilities
			else{
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 3;
				else
					return 4;
			}
		}//end of East
		else{
			if(world.tx(itemLoc.x) < world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 2;
				else if(world.ty(itemLoc.y)> world.ty(pLoc.y))
					return 0;
				else
					return 1;
			} // end of left location possibilities
			else if(world.tx(itemLoc.x) > world.tx(pLoc.x)){
				if(world.ty(itemLoc.y) <world.ty(pLoc.y))
					return 7;
				else if (world.ty(itemLoc.y) > world.ty(pLoc.y))
					return 5;
				else
					return 6;
			}// end of right location possibilities
			else{
				if(world.ty(itemLoc.y) < world.ty(pLoc.y))
					return 4;
				else
					return 3;
			}
		}// end of West
	}// end of method

	//Returns indexes of empty squares
	protected int[] findEmptySquares(Int2D pLoc, Bag usedSquares){
		
		int[] indexes = new int[8];
		
		return indexes;
	}
	protected void increaseTowardFood(int lastMeal, int numAdj, int numMov, int[]newProb, Int2D pLoc, Bag usedSquares){
		
		double hunger;
		double x = 0;
		int[] emptySq = findEmptySquares(pLoc, usedSquares);
		for(int i = 0; i <emptySq.length; i++)
			x += newProb[i];
		
		
		
		//int x = probability of unused squares
		
		hunger = (double)lastMeal/ maxHunger;
		double prob = x * hunger;
		double g = prob/ numAdj;
		int numLeft = 8 - numMov;
		double dec = prob/ numLeft;
		
	} // end of method
	
}
