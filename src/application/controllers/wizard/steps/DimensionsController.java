package application.controllers.wizard.steps;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

import com.sun.javafx.scene.control.skin.LabeledText;

import application.controllers.wizard.CubeWizardController;
import application.controllers.wizard.abstr.AbstractWizardStepController;
import application.models.dimension.Attribute;
import application.models.dimension.Dimension;
import application.models.wizard.MappingRow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;

public class DimensionsController extends AbstractWizardStepController {

	private static final String viewLocation = "/application/views/wizard/Dimensions.fxml";
	private ObservableList<MappingRow> attributeObjects;

	@FXML
	private ListView<Dimension> dimensionsList;

	@FXML
	private ListView<Attribute> attributeList, unusedAttributes;

	private ObservableList<Dimension> dimensions;
	private ObservableList<Attribute> attributes, aux;
	private ObservableList<Attribute> unusedAttributesElements;

	public DimensionsController(CubeWizardController controller) {
		super(controller, viewLocation);
		attributeObjects = ((MappingController) controller.getNode(1)).getMappingRows();
		initializeView();
		enableDragDrop();
	}

	private void initializeView() {
		initializeDimensions();
	}

	private void initializeDimensions() {

		dimensions = FXCollections.observableArrayList();
		attributes = FXCollections.observableArrayList();
		aux = FXCollections.observableArrayList();
		aux.add(new Attribute("Select a dimension to visualize its attribute hierarchy.", Attribute.IGNORE));
		attributeList.setItems(aux);
		attributeList.setDisable(true);

		unusedAttributesElements = FXCollections.observableArrayList();
		/**
		 * Initialize all atributes that are not ignored
		 */
		for (MappingRow m : attributeObjects) {
			if (!m.getUseAs().equals(Attribute.IGNORE)) {
				Attribute newAtt = new Attribute(m.getAttributeName(), m.getUseAs());
				attributes.add(newAtt);
				if (m.createDimensionProperty().getValue()) {
					Dimension newDimension = new Dimension(m.getAttributeName());
					newDimension.addAttribute(newAtt);
					dimensions.add(newDimension);
				} else
					unusedAttributesElements.add(newAtt);
			}

		}
		dimensionsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Dimension>() {
			@Override
			public void changed(ObservableValue<? extends Dimension> observable, Dimension oldValue,
					Dimension newValue) {
				attributeList.setDisable(false);
				if (newValue != null)
					attributeList.setItems(newValue.getAttributes());
				else
					attributeList.setItems(null);
			}
		});
		dimensionsList.setItems(dimensions);
		unusedAttributes.setItems(unusedAttributesElements);
	}

	public void enableDragDrop() {

		unusedAttributes.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (unusedAttributes.getSelectionModel().getSelectedItem() == null) {
					return;
				}

				Dragboard dragBoard = unusedAttributes.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.putString(unusedAttributes.getSelectionModel().getSelectedItem().getAttributeName());
				dragBoard.setContent(content);
			}
		});
		attributeList.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (attributeList.getSelectionModel().getSelectedItem() == null) {
					return;
				}

				Dragboard dragBoard = attributeList.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.putString(attributeList.getSelectionModel().getSelectedItem().getAttributeName());
				dragBoard.setContent(content);
			}
		});

		attributeList.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				dragEvent.acceptTransferModes(TransferMode.MOVE);
			}
		});

		attributeList.setOnDragDropped(new EventHandler<DragEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(DragEvent dragEvent) {

				ListView<String> source = (ListView<String>) dragEvent.getGestureSource();

				if (source.getId().equals(unusedAttributes.getId())) {
					attributeList.getItems().add(unusedAttributes.getSelectionModel().getSelectedItem());
					unusedAttributes.getItems().remove(unusedAttributes.getSelectionModel().getSelectedItem());
					updateLists();
				}

				else if (source.getId().equals(attributeList.getId())) {
					if (dragEvent.getTarget() instanceof LabeledText) {
						Iterator<Attribute> iterator = attributeList.getItems().iterator();
						for (int indexTarget = 0; iterator.hasNext(); indexTarget++) {
							Attribute targetElement = iterator.next();
							if (targetElement.getAttributeName().equals(((Text) dragEvent.getTarget()).getText())) {
								// winner index
								Attribute sourceElement = attributeList.getSelectionModel().getSelectedItem();
								int indexSource = attributeList.getItems().indexOf(sourceElement);
								attributeList.getItems().set(indexSource, targetElement);
								attributeList.getItems().set(indexTarget, sourceElement);
								updateLists();
								break;
							}
						}
					} else {
						/**
						 * if target is not an attribute, throw it to the end of
						 * the list TO-DO: handle the case where the attribute
						 * is dropped between two other ones
						 */
						Attribute sourceElement = attributeList.getSelectionModel().getSelectedItem();
						attributeList.getItems().remove(sourceElement);
						attributeList.getItems().add(sourceElement);
						updateLists();
					}
				}
			}

		});
	}

	protected void updateLists() {
		unusedAttributesElements.clear();
		for (Attribute att : attributes) {
			boolean isUsed = false;
			for (Dimension dim : dimensions)
				if (dim.getAttributes().size() > 0)
					for (Attribute att_dim : dim.getAttributes())
						if (att.getAttributeName().equals(att_dim.getAttributeName()))
							isUsed = true;
			if (!isUsed)
				unusedAttributesElements.add(att);
		}
		unusedAttributes.refresh();
		dimensionsList.refresh();
		attributeList.refresh();
	}

	@FXML
	protected void addDimensionButton() {
		TextInputDialog dialog = new TextInputDialog("New Dimension name");
		dialog.setTitle("New Dimension");
		dialog.setHeaderText("Please enter the name of the new Dimension.");
		dialog.showAndWait();

		Dimension newDimension = new Dimension(dialog.getResult());
		dimensions.add(newDimension);
		dimensionsList.refresh();
	}

	@FXML
	protected void removeDimensionButton() {
		dimensions.remove(dimensionsList.getSelectionModel().getSelectedItem());
		updateLists();
	}

	@FXML
	protected void addAttributeButton() {
		Dimension selectedDim = dimensionsList.getSelectionModel().getSelectedItem();
		if (selectedDim == null)
			errorMessage("No Dimension Selected","Please select a dimension to add an attribute to.");
		else {
			ChoiceDialog<Attribute> dialog = new ChoiceDialog<Attribute>(attributes.get(0),attributes);
			dialog.setTitle("Add Attribute to Dimension");
			dialog.setHeaderText("Please select an attribute to be added to the Dimension\"" + selectedDim.getNameProperty().getValue() + "\"");
			dialog.showAndWait();
			
			if(dialog.getResult() != null)
				selectedDim.addAttribute(dialog.getResult());
			updateLists();
		}
	}

	@FXML
	protected void removeAttributeButton() {
		Dimension selectedDim = dimensionsList.getSelectionModel().getSelectedItem();
		if (selectedDim == null)
			errorMessage("Remove Attribute from Dimension", "Please select a dimension to access its attribute list.");
		else {
			Attribute selectedAtt = attributeList.getSelectionModel().getSelectedItem();
			if(selectedAtt == null)
				errorMessage("Remove Attribute from Dimension", "Please select the attribute to be removed from this dimension.");
			else
				selectedDim.removeAttribute(selectedAtt);
		updateLists();
		}
	}

	@FXML
	protected void backButton() {
		mainController.backStep();
	}

	@FXML
	protected void nextButton() {
		boolean isOK = false;
		if(!dimensions.isEmpty())
			for(Dimension dim : dimensions)
				if(!dim.getAttributes().isEmpty()){
					isOK = true;
					break;
				}
		if(isOK)
			mainController.nextStep();
		else
			errorMessage("No dimensions/attributes used!", "Please create at least one dimension that contains at least one attribute.");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	protected void errorMessage(String title, String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(title);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	public ObservableList<Dimension> getDimensions(){
		return dimensions;
	}

}
