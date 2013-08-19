package sim.app.pvp;

import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;

public class ExpectationMap {
	
	protected double decayRate;
	protected double[][] foodLocationMap;
	protected double[][] predatorLocationMap;
	protected double[][] conspecificLocationMap;
	protected double[][] poisonLocationMap;
	protected int gWidth;
	protected int gHeight;
	
	public ExpectationMap(int gridWidth, int gridHeight, double decay){
		foodLocationMap = new double[gridWidth][gridHeight];
		predatorLocationMap = new double[gridWidth][gridHeight];
		conspecificLocationMap = new double[gridWidth][gridHeight];
		poisonLocationMap = new double[gridWidth][gridHeight];
		
		gWidth = gridWidth;
		gHeight = gridHeight;
		
		decayRate = decay;
	}

	protected void updateMapsPred (Bag objects, SparseGrid2D grid){
		
		this.decayMaps();
		
		for(int i = 0; i < objects.size(); i++){
			Object o = objects.get(i);
			if(o.getClass().equals(Prey.class)){
				Prey prey = (Prey)o;
				int x = grid.getObjectLocation(prey).x;
				int y = grid.getObjectLocation(prey).y;
				
				foodLocationMap[x][y] = 1.0;
				
				if(prey.isDiseased)
					poisonLocationMap[x][y] = 1.0;
					
			}
			
			else if(o.getClass().equals(Predator.class)){
				Predator pred = (Predator)o;
				int x = grid.getObjectLocation(pred).x;
				int y = grid.getObjectLocation(pred).y;
				
				conspecificLocationMap[x][y] = 1.0;
			}
			
		}
	}// end of update maps Predator
	
	protected void printMaps(){
		for(int w = 0; w < gWidth; w++){
			for(int h = 0; h <gHeight; h++){
				System.out.println("Food Map: " + foodLocationMap[w][h]);
				System.out.println("Predator Map: " + predatorLocationMap[w][h]);
				System.out.println("Conspecific Map: " + conspecificLocationMap[w][h]);
				System.out.println("Poison Location Map: " + poisonLocationMap[w][h])
				;
			}
		}
	}
	
	
	protected void updateMapsPrey (Bag objects, SparseGrid2D grid){
		this.decayMaps();
		
		for(int i = 0; i < objects.size(); i++){
			Object o = objects.get(i);
			if(o.getClass().equals(Food.class)){
				Food food = (Food)o;
				int x = grid.getObjectLocation(food).x;
				int y = grid.getObjectLocation(food).y;
				
				foodLocationMap[x][y] = 1.0;
				
				if(food.isDiseased())
					poisonLocationMap[x][y] = 1.0;
					
			}
			
			else if(o.getClass().equals(Predator.class)){
				Predator pred = (Predator)o;
				int x = grid.getObjectLocation(pred).x;
				int y = grid.getObjectLocation(pred).y;
				
				predatorLocationMap[x][y] = 1.0;
			}
			
			else if (o.getClass().equals(Prey.class)){
				Prey prey = (Prey)o;
				int x = grid.getObjectLocation(prey).x;
				int y = grid.getObjectLocation(prey).y;
				
				conspecificLocationMap[x][y] = 1.0;
			}
			
		}
	}// end of update maps Prey
	
	public void decayMaps(){
		for(int w = 0; w < gWidth; w++){
			for(int h = 0; h < gHeight; h++){
				
				foodLocationMap[w][h] = foodLocationMap[w][h] - decayRate;
				predatorLocationMap[w][h] = predatorLocationMap[w][h] - decayRate;
				conspecificLocationMap[w][h] = conspecificLocationMap[w][h] - decayRate;
				poisonLocationMap[w][h] = poisonLocationMap[w][h] - decayRate;
				
				if(foodLocationMap[w][h] < 0)
					foodLocationMap[w][h] = 0;
				if(predatorLocationMap[w][h] < 0)
					predatorLocationMap[w][h] = 0;
				if(conspecificLocationMap[w][h] < 0)
					conspecificLocationMap[w][h] = 0;
				if(poisonLocationMap[w][h] < 0)
					poisonLocationMap[w][h] = 0;
				
			}
		}
	}
}
