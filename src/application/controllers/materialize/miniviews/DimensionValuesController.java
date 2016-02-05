package application.controllers.materialize.miniviews;

import java.util.Map;

import org.deckfour.xes.model.XAttribute;

import application.PMCLauncher;
import application.models.cube.Cell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class DimensionValuesController extends AnchorPane {

	@FXML
	TableView<Triplet> table;

	@FXML
	TableColumn<Triplet, String> dimension, attribute, value;

	public DimensionValuesController() {

		FXMLLoader fxmlLoader = new FXMLLoader(PMCLauncher.class.getResource("views/miniview/DimensionValues.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initializeValues(Cell cell){
		dimension.setCellValueFactory(new PropertyValueFactory<Triplet, String>("dimension"));
		attribute.setCellValueFactory(new PropertyValueFactory<Triplet, String>("attribute"));
		value.setCellValueFactory(new PropertyValueFactory<Triplet, String>("value"));

		Map<String, XAttribute> map = cell.getDimensionalValues();
		ObservableList<Triplet> objectList = FXCollections.observableArrayList();
		for (String value : map.keySet()) {
			objectList.add(new Triplet(cell.getDimension(value), value, map.get(value).toString()));
		}
		table.setItems(objectList);
		table.refresh();
	}
	public class Triplet {
		String dimension, attribute, value;

		public Triplet(String dimension, String attribute, String value) {
			this.dimension = dimension;
			this.attribute = attribute;
			this.value = value;
		}

		public String getDimension() {
			return dimension;
		}

		public void setDimension(String dimension) {
			this.dimension = dimension;
		}

		public String getAttribute() {
			return attribute;
		}

		public void setAttribute(String attribute) {
			this.attribute = attribute;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
