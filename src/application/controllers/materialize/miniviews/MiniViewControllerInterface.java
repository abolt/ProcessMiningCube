package application.controllers.materialize.miniviews;

import application.models.cube.Cell;
import application.models.cube.Cell.Metrics;

public interface MiniViewControllerInterface {
	
	public void setSelected(boolean state);
	
	public void changeState();
	
	public void initializeValues();
	
	public Cell getCell();
	
}
