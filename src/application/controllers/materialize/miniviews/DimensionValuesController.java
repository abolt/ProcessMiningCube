//package application.controllers.materialize.miniviews;
//
//import java.util.Map;
//
//import org.deckfour.xes.model.XAttribute;
//
//import application.PMCLauncher;
//import application.controllers.materialize.MaterializeController;
//import application.models.cube.Cell;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.effect.DropShadow;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.paint.Color;
//
//public class DimensionValuesController extends AnchorPane implements MiniViewControllerInterface {
//
//	@FXML
//	TableView<Triplet> table;
//
//	@FXML
//	TableColumn<Triplet, String> dimension, attribute, value;
//
//	private Cell cell;
//	private MaterializeController materializeController;
//
//	public DimensionValuesController(Cell cell, MaterializeController materializeController) {
//
//		FXMLLoader fxmlLoader = new FXMLLoader(PMCLauncher.class.getResource("views/miniview/DimensionValues.fxml"));
//		fxmlLoader.setRoot(this);
//		fxmlLoader.setController(this);
//		try {
//			fxmlLoader.load();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		this.cell = cell;
//		this.materializeController = materializeController;
//	}
//	
//	public Cell getCell(){
//		return cell;
//	}
//
//	public void setSelected(boolean state) {
//		if (state) {
//			DropShadow shadowEffect = new DropShadow();
//			shadowEffect.setSpread(0.78);
//			shadowEffect.setColor(Color.valueOf("#0c7dee"));
//			this.setEffect(shadowEffect);
//		}
//		else
//			this.setEffect(null);
//		cell.setSelected(state); // change the selection boolean
//	}
//
//	public void changeState() {
//		setSelected(!cell.isSelected());
//		materializeController.updateSelectionCount();
//	}
//
//	public void initializeValues() {
//		dimension.setCellValueFactory(new PropertyValueFactory<Triplet, String>("dimension"));
//		attribute.setCellValueFactory(new PropertyValueFactory<Triplet, String>("attribute"));
//		value.setCellValueFactory(new PropertyValueFactory<Triplet, String>("value"));
//
//		Map<String, XAttribute> map = cell.getDimensionalValues();
//		ObservableList<Triplet> objectList = FXCollections.observableArrayList();
//		for (String value : map.keySet()) {
//			objectList.add(new Triplet(cell.getDimension(value), value, map.get(value).toString()));
//		}
//		table.setItems(objectList);
//		setSelected(cell.isSelected());
//		table.refresh();
//	}
//
//	public class Triplet {
//		String dimension, attribute, value;
//
//		public Triplet(String dimension, String attribute, String value) {
//			this.dimension = dimension;
//			this.attribute = attribute;
//			this.value = value;
//		}
//
//		public String getDimension() {
//			return dimension;
//		}
//
//		public void setDimension(String dimension) {
//			this.dimension = dimension;
//		}
//
//		public String getAttribute() {
//			return attribute;
//		}
//
//		public void setAttribute(String attribute) {
//			this.attribute = attribute;
//		}
//
//		public String getValue() {
//			return value;
//		}
//
//		public void setValue(String value) {
//			this.value = value;
//		}
//	}
//}
