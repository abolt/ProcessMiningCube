package application.controllers.workers;

public class WorkerCatalog {
	/**
	 * Singletons for the workers
	 */
	static RapidMinerWorker rapidMiner;

	public static RapidMinerWorker getRapidMinerWorker() {
		if (rapidMiner == null)
			rapidMiner = new RapidMinerWorker();
		return rapidMiner;
	}

}
