/*
 * Eindhoven University of Technology
 * 
 * Copyright (C) 2016-2016 by Eindhoven University of Technology and the contributors
 * 
 * Complete list of developers available at our web site:
 * 
 * www.rapidprom.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package application.prom;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.processmining.framework.packages.PackageDescriptor;
import org.processmining.framework.plugin.PluginManager;

/**
 * This class provides hooks for initialization and its methods are called via
 * reflection by RapidMiner Studio. Without this class and its predefined
 * methods, an extension will not be loaded.
 *
 * @author REPLACEME
 */
public final class PluginInitRapidProM {

	static ClassLoader classLoader;
	static URL url;

	public PluginInitRapidProM() {
		// Utility class constructor
		classLoader = this.getClass().getClassLoader();
		try {
			url = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method will be called directly after the extension is initialized.
	 * This is the first hook during start up. No initialization of the
	 * operators or renderers has taken place when this is called.
	 */
	public void initPlugin() {
		PluginManager promPluginManager = new RapidProMPluginManager(RapidProMPluginContext.class);
		PackageDescriptor packageDescriptor = new RapidProMPackageDescriptor();
		promPluginManager.register(url, packageDescriptor, classLoader);
		RapidProMGlobalContext.initialize(promPluginManager);
	}

}
