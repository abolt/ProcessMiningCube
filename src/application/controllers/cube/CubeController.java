package application.controllers.cube;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.model.XAttribute;

import application.controllers.AbstractTabController;
import application.models.dimension.Attribute;
import application.models.dimension.Dimension;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class CubeController extends AbstractTabController {

	public static final String EVENTS = "events", TRACES_FIRST = "traces (first event)",
			TRACES_ANY = "traces (any event)";
	@FXML
	private Tab tabCube;

	@FXML
	private ImageView image;

	@FXML
	private TextField sliceName;

	@FXML
	private ComboBox<String> operation;

	@FXML
	private TreeView<XAttribute> valueSetItems;
	@FXML
	private ComboBox<Attribute> selectedAttribute, granularity;
	@FXML
	private ComboBox<String> distributionSelection;

	@FXML
	private ListView<Dimension> availableDimensions, cubeDimensions;
	private ObservableList<Dimension> availableDimensionList, cubeDimensionList;
	private Map<Attribute, ObservableList<CheckBoxTreeItem<XAttribute>>> selectedValues;

	@FXML
	protected void initialize() {
		name = "cubeController";
	}

	@Override
	public void initializeTab(Tab input) {
		tabCube = input;

	}

	@Override
	protected void enableTab(boolean value) {
		tabCube.setDisable(!value);
		if (value) {

			availableDimensionList = FXCollections.observableArrayList();
			for (Dimension d : mainController.getDimensions())
				availableDimensionList.add(d);
			availableDimensions.setItems(availableDimensionList);
			cubeDimensionList = FXCollections.observableArrayList();
			cubeDimensions.setItems(cubeDimensionList);

			selectedValues = new HashMap<Attribute, ObservableList<CheckBoxTreeItem<XAttribute>>>();
			for (Dimension dimension : mainController.getDimensions())
				for (Attribute att : dimension.getAttributes()) {
					ObservableList<CheckBoxTreeItem<XAttribute>> values = FXCollections.observableArrayList();
					for (XAttribute a : att.getValueSet()) {
						CheckBoxTreeItem<XAttribute> aux = new CheckBoxTreeItem<XAttribute>(a);
						aux.selectedProperty().set(att.isSelected(a.toString()));
						values.add(aux);
					}
					selectedValues.put(att, values);
				}
			ObservableList<String> options = FXCollections.observableArrayList();
			options.addAll(EVENTS, TRACES_FIRST, TRACES_ANY);
			distributionSelection.setItems(options);
			distributionSelection.getSelectionModel().select(EVENTS);

			initializeListeners();
		}

		// initialize the contents of the gui elements

	}

	private void initializeListeners() {

		enableDragDrop();

		cubeDimensions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Dimension>() {
			@Override
			public void changed(ObservableValue<? extends Dimension> observable, Dimension oldValue,
					Dimension newValue) {
				if (newValue != null) {
					sliceName.setText(newValue.getNameProperty().getValue());
					selectedAttribute.setItems(newValue.getAttributes());
					if (newValue.getAttributes() != null)
						selectedAttribute.getSelectionModel().select(newValue.getAttributes().get(0));
					granularity.setItems(newValue.getAttributes());
					granularity.getSelectionModel().select(newValue.getGranularity());
					if (operation.getSelectionModel().getSelectedItem() != null)
						if (operation.getSelectionModel().getSelectedItem().equals("Dice")) {
							oldValue.setDiced(true);
							oldValue.setSliced(false);
						} else {
							oldValue.setDiced(false);
							oldValue.setSliced(true);
						}

					ObservableList<String> operationOptions = FXCollections.observableArrayList();
					operationOptions.addAll("Slice", "Dice");
					operation.setItems(operationOptions);
					if (newValue.isDiced())
						operation.getSelectionModel().select("Dice");
					else if (newValue.isSliced())
						operation.getSelectionModel().select("Slice");
				} else {
					sliceName.setText("");
					selectedAttribute.setItems(null);
				}
			}
		});
		granularity.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Attribute>() {
			@Override
			public void changed(ObservableValue<? extends Attribute> observable, Attribute oldValue,
					Attribute newValue) {
				if (newValue != null && cubeDimensions.getSelectionModel().getSelectedItem() != null) {
					cubeDimensions.getSelectionModel().getSelectedItem().setGranularity(newValue);
				}
			}
		});

		valueSetItems.setCellFactory(CheckBoxTreeCell.forTreeView());
		selectedAttribute.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Attribute>() {
			@Override
			public void changed(ObservableValue<? extends Attribute> observable, Attribute oldValue,
					Attribute newValue) {

				CheckBoxTreeItem<XAttribute> dummyRoot = new CheckBoxTreeItem<>();
				dummyRoot.getChildren().addAll(selectedValues.get(newValue));
				valueSetItems.setRoot(null);
				valueSetItems.refresh();
				valueSetItems.setRoot(dummyRoot);
				valueSetItems.setShowRoot(false);
			}
		});

	}

	@Override
	public void updateImage() {
		if (isEnabled() && !isCompleted())
			image.setImage(new Image("images/cube_red.png"));
		else if (isEnabled() && isCompleted())
			image.setImage(new Image("images/cube_green.png"));
		else
			image.setImage(new Image("images/cube_black.png"));
	}

	@FXML
	protected void handleCreateCubeButton(ActionEvent event) {

		for (Dimension d : cubeDimensionList)
			for (Attribute a : d.getAttributes())
				for (CheckBoxTreeItem<XAttribute> item : selectedValues.get(a))
					a.setSelected(item.getValue().toString(), item.selectedProperty().get());

		ObservableList<Dimension> allDimensions = mainController.getDimensions();
		for (Dimension dimension : allDimensions)
			if (cubeDimensionList.contains(dimension))
				dimension.setVisible(true);
			else
				dimension.setVisible(false);
		mainController.setSelectedValues(selectedValues);
		mainController.setDistributionModel(distributionSelection.getSelectionModel().getSelectedItem());
		setCompleted(true);
	}

	public void enableDragDrop() {

		availableDimensions.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (availableDimensions.getSelectionModel().getSelectedItem() == null) {
					return;
				}

				Dragboard dragBoard = availableDimensions.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.putString(
						availableDimensions.getSelectionModel().getSelectedItem().getNameProperty().getValue());
				dragBoard.setContent(content);
			}
		});

		cubeDimensions.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (cubeDimensions.getSelectionModel().getSelectedItem() == null) {
					return;
				}

				Dragboard dragBoard = cubeDimensions.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.putString(cubeDimensions.getSelectionModel().getSelectedItem().getNameProperty().getValue());
				dragBoard.setContent(content);
			}
		});

		availableDimensions.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				dragEvent.acceptTransferModes(TransferMode.MOVE);
			}
		});

		cubeDimensions.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				dragEvent.acceptTransferModes(TransferMode.MOVE);
			}
		});

		availableDimensions.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				if (!dragEvent.getSource().equals(availableDimensionList)) {
					availableDimensionList.add(cubeDimensions.getSelectionModel().getSelectedItem());
					cubeDimensionList.remove(cubeDimensions.getSelectionModel().getSelectedItem());

					cubeDimensions.refresh();
					availableDimensions.refresh();
				}
				dragEvent.consume();
			}
		});

		cubeDimensions.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				if (!dragEvent.getSource().equals(cubeDimensionList)) {
					cubeDimensionList.add(availableDimensions.getSelectionModel().getSelectedItem());
					availableDimensionList.remove(availableDimensions.getSelectionModel().getSelectedItem());

					cubeDimensions.refresh();
					availableDimensions.refresh();
				}
				dragEvent.consume();
			}
		});
	}
}
