package application.controllers.workers;

import org.deckfour.xes.model.XLog;

/**
 * This abstract class handles tasks that involve heavy processing and, thus,
 * require a progress indicator
 * 
 * @author abolt
 *
 */
public abstract class AbstrWorker {

	protected AbstrWorker() {
		initialize();
	}

	/**
	 * Loads classes, sets objects, etc.
	 */
	protected abstract void initialize();

	/**
	 * Does the work in a separate thread
	 */
	protected abstract void run(XLog log, Object... objects);

}
