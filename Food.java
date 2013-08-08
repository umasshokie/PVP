package sim.app.pvp;

import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;

public class Food implements Steppable {

	private double amount = 1.0;
	private double diseasePr = .00005;
	private int diseaseRandNum = 10000;
	private double repPr = .001;
	private int repRandNum = 10000;
	private boolean diseased = false;
	public SparseGrid2D grid;

	
	@Override
	public void step(SimState state) {
		// TODO Auto-generated method stub
		PVP pvp = (PVP)state;
		
		grid = pvp.world;
		
		//Chance of disease
		this.diseaseChance(pvp);
		
		//Chance of spread
		this.reproductionChance(pvp);
	}

	public void diseaseChance(PVP pvp){
		
		double d = pvp.random.nextInt(diseaseRandNum);
		double disease = d/diseaseRandNum;
		
		assert (disease > 0);
		
		if(disease < diseasePr)
			diseased = true;
		
	}
	
	public void reproductionChance(PVP pvp){
		
		// reproduction rate
		double rep = pvp.random.nextInt(repRandNum);
		//System.out.println("rep: " + rep);
		double repro = rep/repRandNum;
		//System.out.println("Repro: " + repro);
		assert (repro > 0);
		
		if(repro < repPr)
			this.spread(grid, pvp);
	}
	
	public boolean isDiseased(){
		return diseased;
	}

	public void spread(SparseGrid2D grid, SimState state){
		
	
		Int2D cord = grid.getObjectLocation(this);
		
		if(cord != null){
		
			Food p = new Food();
		
			int direction = state.random.nextInt(7);
	
			
			if (direction == 0){
				grid.setObjectLocation(p, cord.x, cord.y + 1);		
			}
			else if (direction == 1){
				grid.setObjectLocation(p, cord.x, cord.y - 1);	
			}
			else if (direction == 2){
				grid.setObjectLocation(p, cord.x + 1, cord.y);			
			}
			else if (direction == 3){
				grid.setObjectLocation(p, cord.x + 1, cord.y + 1);		
			}
			else if (direction == 4){
				grid.setObjectLocation(p, cord.x + 1, cord.y - 1);		
			}
			else if (direction == 5){
				grid.setObjectLocation(p, cord.x - 1, cord.y + 1);
			}
			else if (direction == 6){
				grid.setObjectLocation(p, cord.x - 1, cord.y - 1);
			}
		}// end of if
	}// end of spread
}// end of class
