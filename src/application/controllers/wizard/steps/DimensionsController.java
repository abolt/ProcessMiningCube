package application.controllers.wizard.steps;

import java.net.URL;
import java.util.ResourceBundle;

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
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class DimensionsController extends AbstractWizardStepController{

	private static final String viewLocation = "/application/views/wizard/Dimensions.fxml";
	private ObservableList<MappingRow> attributeObjects;
	
	@FXML
	private ListView<Dimension> dimensionsList;
	
	@FXML
	private ListView<Attribute> attributeList, unusedAttributes;
	
	public DimensionsController(CubeWizardController controller) {
		super(controller, viewLocation);
		attributeObjects = ((MappingController) controller.getNode(1)).getMappingRows();
		initializeView();
	}
	
	private void initializeView(){
		
	}
	
	private void initializeDimensions() {

		ObservableList<Dimension> dimensions = FXCollections.observableArrayList();
		ObservableList<Attribute> attributes = FXCollections.observableArrayList();
		ObservableList<MappingRow> mappingRows = ((MappingController) mainController.getNode(1)).getMappingRows();
		
		/**
		 * Initialize all atributes that are not ignored
		 */
		for (MappingRow m : mappingRows) {
			attributes.add(new Attribute(null, null));
		}
		
		for (MappingRow m : mappingRows) {
			if (m.createDimensionProperty().getValue() && !m.getUseAs().equals(Attribute.IGNORE)) {
				Dimension newDimension = new Dimension(m.getAttributeName());
				newDimension.addAttribute(attributes.get(m.getAttributeName()));
				dimensions.add(newDimension);
			}
		}
		mainController.setDimensions(dimensions);

		dimensionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Dimension>() {
			@Override
			public void changed(ObservableValue<? extends Dimension> observable, Dimension oldValue,
					Dimension newValue) {
				if(newValue != null)
					dimensionAttributes.setItems(newValue.getAttributes());
				else
					dimensionAttributes.setItems(null);
			}
		});
		dimensionList.setItems(mainController.getDimensions());

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

		attributeList.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				dragEvent.acceptTransferModes(TransferMode.MOVE);
			}
		});

		attributeList.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				attributeList.getItems().add(unusedAttributes.getSelectionModel().getSelectedItem());
				unusedAttributes.getItems().remove(unusedAttributes.getSelectionModel().getSelectedItem());
				attributeList.refresh();
				unusedAttributes.refresh();
			}
		});
	}

	@FXML
	protected void addDimensionButton(){
		
	}
	
	@FXML
	protected void removeDimensionButton(){
		
	}
	
	@FXML
	protected void addAttributeButton(){
		
	}
	
	@FXML
	protected void removeAttributeButton(){
		
	}
	
	@FXML
	protected void backButton(){
		
	}
	
	@FXML
	protected void nextButton(){
		
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
