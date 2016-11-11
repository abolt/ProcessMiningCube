package application.utils;



public class RapidMinerUtils {

	private static Plugin plugin = null;

	public static Plugin getRapidProMPlugin() {
		if (plugin == null) {
			for (Plugin plugin : Plugin.getAllPlugins()) {
				if (plugin.getName().equals(RapidProMProperties.instance().getExtensionName())) {
					return plugin;
				}
			}
		}
		return plugin;
	}

}
