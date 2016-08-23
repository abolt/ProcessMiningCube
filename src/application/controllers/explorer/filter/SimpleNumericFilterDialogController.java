package application.controllers.explorer.filter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.RangeSlider;

import application.models.attribute.ContinuousAttribute;
import application.models.attribute.DiscreteAttribute;
import application.models.attribute.abstr.AbstrNumericalAttribute;
import application.models.attribute.abstr.Attribute;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

public class SimpleNumericFilterDialogController extends BorderPane implements Initializable {

	@FXML
	RangeSlider slider;

	@FXML
	TextField textField, from, to;

	@FXML
	HBox box;

	public SimpleNumericFilterDialogController(Attribute<?> attribute) {

		FXMLLoader fxmlLoader = new FXMLLoader(
				this.getClass().getResource("/application/views/filter/SimpleNumericFilterDialog.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		initializeList((AbstrNumericalAttribute<?>) attribute);

	}

	private void initializeList(AbstrNumericalAttribute<?> inputAttribute) {

		box.getChildren().clear();
		// initialize attributeName
		textField.setText(inputAttribute.getLabel());
		textField.setEditable(false);

		StringConverter<Number> converter = null;

		if (inputAttribute instanceof ContinuousAttribute) {
			ContinuousAttribute attribute = (ContinuousAttribute) inputAttribute;
			slider = new RangeSlider(attribute.getMin(), attribute.getMax(), attribute.getSelectedMin(),
					attribute.getSelectedMax());
			slider.adjustLowValue(attribute.getSelectedMin());

			converter = new NumberStringConverter();
		} else {
			DiscreteAttribute attribute = (DiscreteAttribute) inputAttribute;
			slider = new RangeSlider((double) attribute.getMin(), (double) attribute.getMax(),
					(double) attribute.getSelectedMin(), (double) attribute.getSelectedMax());
			slider.adjustLowValue(attribute.getSelectedMin());
			converter = new NumberStringConverter("###");
		}

		Bindings.bindBidirectional(from.textProperty(), slider.lowValueProperty(), converter);
		Bindings.bindBidirectional(to.textProperty(), slider.highValueProperty(), converter);

		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);

		slider.setPrefWidth(300);

		from.setText(converter.toString(slider.getLowValue()));
		to.setText(converter.toString(slider.getHighValue()));
		// reset the box
		box.getChildren().add(from);
		box.getChildren().add(slider);
		box.getChildren().add(to);

		this.layout();

	}

	public double getSelectedMin() {
		return slider.getLowValue();
	}

	public double getSelectedMax() {
		return slider.getHighValue();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}
}
