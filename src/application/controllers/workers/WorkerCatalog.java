package application.controllers.workers;

public class WorkerCatalog {
	/**
	 * Singletons for the workers
	 */
	static RapidMinerWorker rapidMiner;
	static DBWorker dbWorker;

	public static RapidMinerWorker getRapidMinerWorker() {
		if (rapidMiner == null)
			rapidMiner = new RapidMinerWorker();
		return rapidMiner;
	}
	
	public static DBWorker getDBWorker(){
		if(dbWorker == null)
			dbWorker = new DBWorker();
		return dbWorker;
	}

}
