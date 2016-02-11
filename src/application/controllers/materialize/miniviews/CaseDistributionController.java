package application.controllers.materialize.miniviews;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.model.XTrace;

import application.PMCLauncher;
import application.controllers.materialize.MaterializeController;
import application.models.cube.Cell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class CaseDistributionController extends AnchorPane implements MiniViewControllerInterface {

	@FXML
	AreaChart<Number, Number> chart;

	private Cell cell;
	private MaterializeController materializeController;

	double upperBound = 0;

	public CaseDistributionController(Cell cell, MaterializeController materializeController) {

		FXMLLoader fxmlLoader = new FXMLLoader(PMCLauncher.class.getResource("views/miniview/CaseDistribution.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.cell = cell;
		this.materializeController = materializeController;
	}

	@Override
	public void setSelected(boolean state) {
		if (state) {
			DropShadow shadowEffect = new DropShadow();
			shadowEffect.setSpread(0.78);
			shadowEffect.setColor(Color.valueOf("#0c7dee"));
			this.setEffect(shadowEffect);
		} else
			this.setEffect(null);
		cell.setSelected(state); // change the selection boolean
	}

	@Override
	public void changeState() {
		setSelected(!cell.isSelected());
		materializeController.updateSelectionCount();
	}

	@Override
	public void initializeValues() {

		Map<Integer, Integer> caseDistribution = new HashMap<Integer, Integer>(); // <length,
																					// #>
		for (XTrace trace : cell.getLog()) {
			if (caseDistribution.containsKey(trace.size()))
				caseDistribution.put(trace.size(), caseDistribution.get(trace.size()) + 1);
			else
				caseDistribution.put(trace.size(), 1); // the first case of this
														// length
		}
		XYChart.Series<Number, Number> values = new XYChart.Series<Number, Number>();
		for (int length : caseDistribution.keySet()) {
			values.getData().add(new XYChart.Data<Number, Number>(length, caseDistribution.get(length)));
			if (caseDistribution.get(length) > upperBound)
				upperBound = caseDistribution.get(length);
		}
		
		chart.getData().add(values);
		setSelected(cell.isSelected());
	}

	@Override
	public Cell getCell() {
		return cell;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public void setRange(double range) {
		((NumberAxis) chart.getYAxis()).setLowerBound(0);
		((NumberAxis) chart.getYAxis()).setUpperBound(range);
	}

}
