package application.controllers.synchronization;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import application.controllers.AbstractController;

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

	private static List<AbstractController> controllerList = new Vector<AbstractController>();

	public static void addController(AbstractController newController) {

		if (controllerList.isEmpty())
			controllerList.add(newController);

		else {
			boolean added = false;
			for (AbstractController controller : controllerList) {
				if (added)
					controller.setUnstable();
				if (newController.getClass().isInstance(controller)) {
					added = true;
					controller = newController;
				}
			}
			if (!added)
				controllerList.add(newController);
		}
	}

	public static void synchronize(AbstractController trigger) {

	}

}
