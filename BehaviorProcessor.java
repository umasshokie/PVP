package sim.app.pvp;

import sim.util.Bag;
import sim.util.Int2D;

public class BehaviorProcessor {

	//Sends in arguments of a Bag of items, and probability of movement, returns array of new probabilites
	BehaviorProcessor(){
		
	}
	
	public int[] updateProbPred(Bag locs, Bag seen, int[] oldProb){
		
		
		for(int s = 0; s < seen.size(); s++){
			System.out.println("I saw " + seen.get(s) + "at " + locs.get(s));
			if(seen.get(s).getClass().equals(Prey.class));
				//Save Prey location, calculate opposite
			}
	
			
	
			
			//Need to calculate opposite position of Prey
			//Increase and redistribute
		
					
		
		return oldProb;
		}
		
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
}
