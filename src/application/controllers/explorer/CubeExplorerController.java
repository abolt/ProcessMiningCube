package application.controllers.explorer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import com.sun.javafx.scene.control.skin.LabeledText;

import application.controllers.explorer.filter.SimpleNumericFilterDialogController;
import application.controllers.explorer.filter.TextFilterDialogController;
import application.models.attribute.ContinuousAttribute;
import application.models.attribute.DiscreteAttribute;
import application.models.attribute.TextAttribute;
import application.models.attribute.abstr.AbstrNumericalAttribute;
import application.models.attribute.abstr.Attribute;
import application.models.cube.Cube;
import application.models.dimension.DimensionImpl;
import application.models.eventbase.AbstrEventBase;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CubeExplorerController extends BorderPane implements Initializable {

	@FXML
	private VBox cubeViewSettingsPanel, miniViewPanel;
	@FXML
	private TreeView<Object> dimensions;

	@FXML
	private ListView<Attribute<?>> rows, columns, filters;

	private CubeTableViewController contentController;

	private Cube cube;

	public CubeExplorerController(Cube cube) {

		this.cube = cube;
		FXMLLoader fxmlLoader = new FXMLLoader(
				this.getClass().getResource("/application/views/explorer/CubeExplorer.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		initializeContent();

	}

	private void initializeContent() {

		initializeCubeSettings();
		if (contentController == null)
			contentController = new CubeTableViewController(this);
		this.setRight(null);
		this.setBottom(null);
		this.setCenter(contentController);
		this.layout();

	}

	private void initializeCubeSettings() {
		TreeItem<Object> root = new TreeItem<Object>();
		for (DimensionImpl d : cube.getStructure().getDimensions()) {
			TreeItem<Object> dim = new TreeItem<Object>(d);
			dim.setExpanded(true);
			for (Attribute<?> a : d.getAttributes())
				dim.getChildren().add(new TreeItem<Object>(a));
			root.getChildren().add(dim);
		}
		dimensions.setRoot(root);
		dimensions.setShowRoot(false);
		enableDragDrop();
		dimensions.layout();
	}

	public void enableDragDrop() {

		/**
		 * Enable drag and drop from and to dimensions list
		 */
		dimensions.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (dimensions.getSelectionModel().getSelectedItem() == null
						|| !(dimensions.getSelectionModel().getSelectedItem().getValue() instanceof Attribute<?>))
					return;
				Dragboard dragBoard = dimensions.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.putString(
						((Attribute<?>) dimensions.getSelectionModel().getSelectedItem().getValue()).getLabel());
				dragBoard.setContent(content);
			}
		});

		dimensions.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				dragEvent.acceptTransferModes(TransferMode.MOVE);
			}
		});

		dimensions.setOnDragDropped(new EventHandler<DragEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(DragEvent dragEvent) {
				if (!(dragEvent.getGestureSource() instanceof TreeView)) {
					ListView<Attribute<?>> source = (ListView<Attribute<?>>) dragEvent.getGestureSource();
					if (source.getId().equals(rows.getId())) {
						rows.getItems().remove(rows.getSelectionModel().getSelectedIndex());
						rows.refresh();
					} else if (source.getId().equals(columns.getId())) {
						columns.getItems().remove(columns.getSelectionModel().getSelectedIndex());
						columns.refresh();
					} else if (source.getId().equals(filters.getId())) {
						filters.getItems().remove(filters.getSelectionModel().getSelectedIndex());
						filters.refresh();
					}
					contentController.requestTableUpdate();
				}
			}
		});

		/**
		 * Enable drag and drop from and to rows list
		 */
		rows.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (rows.getSelectionModel().getSelectedItem() == null)
					return;
				Dragboard dragBoard = rows.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.putString(rows.getSelectionModel().getSelectedItem().getLabel());
				dragBoard.setContent(content);
			}
		});

		rows.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				dragEvent.acceptTransferModes(TransferMode.MOVE);
			}
		});

		rows.setOnDragDropped(new EventHandler<DragEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(DragEvent dragEvent) {

				Attribute<?> newAtt = null;
				ListView<Attribute<?>> source = null;
				if ((dragEvent.getGestureSource() instanceof TreeView)) {
					newAtt = (Attribute<?>) dimensions.getSelectionModel().getSelectedItem().getValue();
				} else {
					source = (ListView<Attribute<?>>) dragEvent.getGestureSource();
					if (source.getId().equals(rows.getId())) {
						// source == target... resorting
						if (dragEvent.getTarget() instanceof LabeledText) {
							Iterator<Attribute<?>> iterator = rows.getItems().iterator();
							for (int indexTarget = 0; iterator.hasNext(); indexTarget++) {
								Attribute<?> targetElement = iterator.next();
								if (targetElement.getLabel().equals(((Text) dragEvent.getTarget()).getText())) {
									// winner index
									Attribute<?> sourceElement = rows.getSelectionModel().getSelectedItem();
									int indexSource = rows.getItems().indexOf(sourceElement);
									rows.getItems().set(indexSource, targetElement);
									rows.getItems().set(indexTarget, sourceElement);
									rows.refresh();
									break;
								}
							}
						} else {
							Attribute<?> sourceElement = rows.getSelectionModel().getSelectedItem();
							rows.getItems().remove(sourceElement);
							rows.getItems().add(sourceElement);
							rows.refresh();
						}
					} else if (source.getId().equals(columns.getId())) {
						newAtt = columns.getSelectionModel().getSelectedItem();
						columns.getItems().remove(columns.getSelectionModel().getSelectedIndex());
						columns.refresh();
					} else if (source.getId().equals(filters.getId())) {
						newAtt = filters.getSelectionModel().getSelectedItem();
						filters.getItems().remove(filters.getSelectionModel().getSelectedIndex());
						filters.refresh();
					}
				}

				if (newAtt != null && !rows.getItems().contains(newAtt)) {
					rows.getItems().add(newAtt);
					rows.refresh();
					contentController.requestTableUpdate();
				}
			}
		});

		/**
		 * Enable drag and drop from and to columns list
		 */
		columns.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (columns.getSelectionModel().getSelectedItem() == null)
					return;
				Dragboard dragBoard = columns.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.putString(columns.getSelectionModel().getSelectedItem().getLabel());
				dragBoard.setContent(content);
			}
		});

		columns.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				dragEvent.acceptTransferModes(TransferMode.MOVE);
			}
		});

		columns.setOnDragDropped(new EventHandler<DragEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(DragEvent dragEvent) {

				Attribute<?> newAtt = null;
				ListView<Attribute<?>> source = null;
				if ((dragEvent.getGestureSource() instanceof TreeView)) {
					newAtt = (Attribute<?>) dimensions.getSelectionModel().getSelectedItem().getValue();
				} else {
					source = (ListView<Attribute<?>>) dragEvent.getGestureSource();
					if (source.getId().equals(rows.getId())) {
						newAtt = rows.getSelectionModel().getSelectedItem();
						rows.getItems().remove(rows.getSelectionModel().getSelectedIndex());
						rows.refresh();
					} else if (source.getId().equals(columns.getId())) {
						// source == target... do nothing
						if (dragEvent.getTarget() instanceof LabeledText) {
							Iterator<Attribute<?>> iterator = columns.getItems().iterator();
							for (int indexTarget = 0; iterator.hasNext(); indexTarget++) {
								Attribute<?> targetElement = iterator.next();
								if (targetElement.getLabel().equals(((Text) dragEvent.getTarget()).getText())) {
									// winner index
									Attribute<?> sourceElement = columns.getSelectionModel().getSelectedItem();
									int indexSource = columns.getItems().indexOf(sourceElement);
									columns.getItems().set(indexSource, targetElement);
									columns.getItems().set(indexTarget, sourceElement);
									columns.refresh();
									break;
								}
							}
						} else {
							Attribute<?> sourceElement = columns.getSelectionModel().getSelectedItem();
							columns.getItems().remove(sourceElement);
							columns.getItems().add(sourceElement);
							columns.refresh();
						}
					} else if (source.getId().equals(filters.getId())) {
						newAtt = filters.getSelectionModel().getSelectedItem();
						filters.getItems().remove(filters.getSelectionModel().getSelectedIndex());
						filters.refresh();
					}
				}
				if (newAtt != null && !columns.getItems().contains(newAtt)) {
					columns.getItems().add(newAtt);
					columns.refresh();
					contentController.requestTableUpdate();
				}
			}
		});

		/**
		 * Enable drag and drop from and to filters list
		 */
		filters.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (filters.getSelectionModel().getSelectedItem() == null)
					return;
				Dragboard dragBoard = filters.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.putString(filters.getSelectionModel().getSelectedItem().getLabel());
				dragBoard.setContent(content);
			}
		});

		filters.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				dragEvent.acceptTransferModes(TransferMode.MOVE);
			}
		});

		filters.setOnDragDropped(new EventHandler<DragEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public void handle(DragEvent dragEvent) {

				Attribute<?> newAtt = null;
				ListView<Attribute<?>> source = null;
				if ((dragEvent.getGestureSource() instanceof TreeView)) {
					newAtt = (Attribute<?>) dimensions.getSelectionModel().getSelectedItem().getValue();
				} else {
					source = (ListView<Attribute<?>>) dragEvent.getGestureSource();
					if (source.getId().equals(rows.getId())) {
						newAtt = rows.getSelectionModel().getSelectedItem();
						rows.getItems().remove(rows.getSelectionModel().getSelectedIndex());
						rows.refresh();
					} else if (source.getId().equals(columns.getId())) {
						newAtt = columns.getSelectionModel().getSelectedItem();
						columns.getItems().remove(columns.getSelectionModel().getSelectedIndex());
						columns.refresh();
					} else if (source.getId().equals(filters.getId())) {
						// source == target... do nothing
						if (dragEvent.getTarget() instanceof LabeledText) {
							Iterator<Attribute<?>> iterator = filters.getItems().iterator();
							for (int indexTarget = 0; iterator.hasNext(); indexTarget++) {
								Attribute<?> targetElement = iterator.next();
								if (targetElement.getLabel().equals(((Text) dragEvent.getTarget()).getText())) {
									// winner index
									Attribute<?> sourceElement = filters.getSelectionModel().getSelectedItem();
									int indexSource = filters.getItems().indexOf(sourceElement);
									filters.getItems().set(indexSource, targetElement);
									filters.getItems().set(indexTarget, sourceElement);
									filters.refresh();
									break;
								}
							}
						} else {
							Attribute<?> sourceElement = filters.getSelectionModel().getSelectedItem();
							filters.getItems().remove(sourceElement);
							filters.getItems().add(sourceElement);
							filters.refresh();
						}
					}
				}

				if (newAtt != null && !filters.getItems().contains(newAtt)) {
					filters.getItems().add(newAtt);
					filters.refresh();
					contentController.requestTableUpdate();
				}
			}
		});

		filters.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
					if (mouseEvent.getClickCount() == 2) {
						final Stage wizard = new Stage();
						if (filters.getSelectionModel().getSelectedItem() instanceof TextAttribute) {
							TextAttribute attribute = (TextAttribute) filters.getSelectionModel().getSelectedItem();
							TextFilterDialogController controller = new TextFilterDialogController(attribute);
							Scene dialogScene = new Scene(controller, 400, 400);
							wizard.setScene(dialogScene);
							wizard.getIcons().add(new Image(getClass().getResourceAsStream("/images/cube_black.png")));
							wizard.setTitle("Filter Text Attribute");
							wizard.showAndWait();

							attribute.resetValueSet();

							ObservableList<String> selected = controller.getSelectedValues();
							for (String r : attribute.getValueSet()) {
								if (!selected.contains(r))
									attribute.removeValue(r);
							}
						} else if (filters.getSelectionModel().getSelectedItem() instanceof AbstrNumericalAttribute) {
							AbstrNumericalAttribute<?> attribute = null;
							
							if (filters.getSelectionModel().getSelectedItem() instanceof ContinuousAttribute)
								attribute = (ContinuousAttribute) filters.getSelectionModel().getSelectedItem();
							else
								attribute = (DiscreteAttribute) filters.getSelectionModel().getSelectedItem();
							
							SimpleNumericFilterDialogController controller = new SimpleNumericFilterDialogController(
									attribute);
							Scene dialogScene = new Scene(controller, 500, 300);
							wizard.setScene(dialogScene);
							wizard.getIcons().add(new Image(getClass().getResourceAsStream("/images/cube_black.png")));
							wizard.setTitle("Filter Numerical Attribute");
							wizard.showAndWait();

							
							attribute.setSelectedMin(controller.getSelectedMin());
							attribute.setSelectedMax(controller.getSelectedMax());
						}
						contentController.requestTableUpdate();
					}
				}
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	@FXML
	protected void clickOnCubeViewSettings() {
		if (this.getLeft() == null)
			this.setLeft(cubeViewSettingsPanel);
		else
			this.setLeft(null);
		this.layout();

	}

	@FXML
	protected void clickOnMiniView() {
		if (this.getRight() == null)
			this.setRight(miniViewPanel);
		else
			this.setRight(null);
		this.layout();

	}

	@FXML
	protected void clickOnTableSettings() {
		contentController.clickOnTableSettings();
	}

	public List<Attribute<?>> getRows() {
		List<Attribute<?>> result = new ArrayList<Attribute<?>>();
		result.addAll(rows.getItems());
		return result;
	}

	public List<Attribute<?>> getColumns() {
		List<Attribute<?>> result = new ArrayList<Attribute<?>>();
		result.addAll(columns.getItems());
		return result;
	}

	public List<Attribute<?>> getFilters() {
		List<Attribute<?>> result = new ArrayList<Attribute<?>>();
		result.addAll(filters.getItems());
		return result;
	}

	public AbstrEventBase getEventBase() {
		return cube.getEventBase();
	}

	public List<Attribute<?>> getValidAttributeList() {
		List<Attribute<?>> attributes = new ArrayList<Attribute<?>>();

		for (DimensionImpl d : cube.getStructure().getDimensions())
			for (Attribute<?> a : d.getAttributes())
				if (!a.getType().equals(Attribute.IGNORE))
					attributes.add(a);
		return attributes;

	}

}
