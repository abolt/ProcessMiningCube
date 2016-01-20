package application.controllers.synchronization;

import java.util.List;
import java.util.Vector;

import application.controllers.AbstractTabController;
import application.controllers.menu.MenuBarController;

/**
 * Synchronizes the different steps
 * 
 * @author abolt
 *
 */

public class Synchronizer {

	/**
	 * @author abolt
	 * 
	 *         If a controller triggers, all future steps are unstabilized.
	 *
	 */

	private MenuBarController mainController;
	private List<AbstractTabController> controllerList;

	public Synchronizer(MenuBarController controller) {
		mainController = controller;
		controllerList= new Vector<AbstractTabController>();
	}

	public void addController(AbstractTabController newController) {

		if (controllerList.isEmpty())
			controllerList.add(newController);

		else {
			boolean added = false;
			for (AbstractTabController controller : controllerList) {
				if (added)
					controller.setCompleted(false);
				if (newController.getClass().isInstance(controller)) {
					added = true;
					controller = newController;
				}
			}
			if (!added)
				controllerList.add(newController);
		}
		//update tabs
	}

	public List<AbstractTabController> getControllers() {
		return controllerList;
	}
}
