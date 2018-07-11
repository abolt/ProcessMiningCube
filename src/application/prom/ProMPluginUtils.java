package application.prom;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.logprojection.LogProjectionPlugin;
import org.processmining.logprojection.LogView;
import org.processmining.logprojection.plugins.dottedchart.DottedChart.DottedChartException;
import org.processmining.logprojection.plugins.dottedchart.ui.DottedChartInspector;
import org.processmining.models.graphbased.directed.fuzzymodel.attenuation.Attenuation;
import org.processmining.models.graphbased.directed.fuzzymodel.attenuation.NRootAttenuation;
import org.processmining.models.graphbased.directed.fuzzymodel.metrics.MetricsRepository;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMf;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.fuzzymodel.FastTransformerVisualization;
import org.processmining.plugins.fuzzymodel.miner.FuzzyMinerPlugin;
import org.processmining.plugins.petrinet.PetriNetVisualization;

public class ProMPluginUtils {

	public static JComponent createDottedChart(PluginContext context, XLog log) {

		LogView result = null;
		DottedChartInspector panel = null;
		try {
			result = new LogView(log);
			panel = LogProjectionPlugin.visualize(context, result);
		} catch (DottedChartException e) {
			System.out.println(e);
		}
		return panel;
	}

	public static JComponent createPetriNet(PluginContext context, XLog log) {

		PetriNetVisualization visualizer = new PetriNetVisualization();
		return visualizer.visualize(context,
				(Petrinet) IMPetriNet.minePetriNet(context, log, new MiningParametersIMf())[0]);
	}

	public static JComponent createFuzzyModel(PluginContext context, XLog log) {

		//PluginContext pluginContext = RapidProMGlobalContext.instance().getFutureResultAwarePluginContext(FuzzyMinerPlugin.class);

		XLogInfo logInfo = null;
		logInfo = XLogInfoFactory.createLogInfo(log, new XEventNameClassifier());
		MetricsRepository metrics = MetricsRepository.createRepository(logInfo);
		try {
			metrics.getUnaryLogMetrics().get(0).setNormalizationMaximum(1);
			metrics.getUnaryDerivateMetrics().get(0).setNormalizationMaximum(1);
			metrics.getSignificanceBinaryLogMetrics().get(0).setNormalizationMaximum(1);
			metrics.getCorrelationBinaryLogMetrics().get(0).setNormalizationMaximum(1);
			metrics.getCorrelationBinaryLogMetrics().get(1).setNormalizationMaximum(1);
			metrics.getCorrelationBinaryLogMetrics().get(2).setNormalizationMaximum(1);
			metrics.getCorrelationBinaryLogMetrics().get(3).setNormalizationMaximum(1);
			metrics.getCorrelationBinaryLogMetrics().get(4).setNormalizationMaximum(1);
			metrics.getSignificanceBinaryMetrics().get(1).setNormalizationMaximum(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Attenuation attenuation = new NRootAttenuation(2.7, 5);
		int maxDistance = 1;

		FuzzyMinerPlugin executer = new FuzzyMinerPlugin();
		MetricsRepository result = executer.mineGeneric(context, log, metrics, attenuation, maxDistance);

		FastTransformerVisualization visualizer = new FastTransformerVisualization();
		return visualizer.visualize(context, result);
	}

}
