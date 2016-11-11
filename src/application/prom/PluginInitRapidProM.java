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

import org.processmining.framework.packages.PackageDescriptor;
import org.processmining.framework.plugin.PluginManager;
import org.rapidprom.external.connectors.prom.RapidProMGlobalContext;
import org.rapidprom.external.connectors.prom.RapidProMPackageDescriptor;
import org.rapidprom.external.connectors.prom.RapidProMPluginContext;
import org.rapidprom.external.connectors.prom.RapidProMPluginManager;
import org.rapidprom.util.RapidMinerUtils;

import com.rapidminer.gui.MainFrame;
import com.rapidminer.tools.plugin.Plugin;

/**
 * This class provides hooks for initialization and its methods are called via
 * reflection by RapidMiner Studio. Without this class and its predefined
 * methods, an extension will not be loaded.
 *
 * @author REPLACEME
 */
public final class PluginInitRapidProM {

	private PluginInitRapidProM() {
		// Utility class constructor
	}

	/**
	 * This method will be called directly after the extension is initialized.
	 * This is the first hook during start up. No initialization of the
	 * operators or renderers has taken place when this is called.
	 */
	public static void initPlugin() {
		PluginManager promPluginManager = new RapidProMPluginManager(RapidProMPluginContext.class);
		PackageDescriptor packageDescriptor = new RapidProMPackageDescriptor();
		Plugin rapidMinerPluginEntry = RapidMinerUtils.getRapidProMPlugin();
		try {
			promPluginManager.register(rapidMinerPluginEntry.getFile().toURI().toURL(), packageDescriptor,
					rapidMinerPluginEntry.getClassLoader());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		RapidProMGlobalContext.initialize(promPluginManager);
	}

	/**
	 * This method is called during start up as the second hook. It is called
	 * before the gui of the mainframe is created. The Mainframe is given to
	 * adapt the gui. The operators and renderers have been registered in the
	 * meanwhile.
	 *
	 * @param mainframe
	 *            the RapidMiner Studio {@link MainFrame}.
	 */
	public static void initGui(MainFrame mainframe) {
	}

	/**
	 * The last hook before the splash screen is closed. Third in the row.
	 */
	public static void initFinalChecks() {
	}

	/**
	 * Will be called as fourth method, directly before the UpdateManager is
	 * used for checking updates. Location for exchanging the UpdateManager. The
	 * name of this method unfortunately is a result of a historical typo, so
	 * it's a little bit misleading.
	 */
	public static void initPluginManager() {
	}
}
