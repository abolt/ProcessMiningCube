package application.controllers.mainview;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.controllers.MainController;
import application.models.cube.Cube;
import application.models.cube.CubeInfo;
import application.models.cube.CubeRepository;
import application.operations.io.cube.CubeExporter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

public class CubeRepositoryController extends BorderPane implements Initializable {

	private MainController mainController;
	private CubeRepository repository;
	private ObservableList<CubeInfo> cubeInfoList;

	@FXML
	private TableView<CubeInfo> table;

	@FXML
	private TableColumn<CubeInfo, String> nameCol;
	@FXML
	private TableColumn<CubeInfo, Integer> eventsCol, dimensionsCol, attributesCol;

	public CubeRepositoryController(MainController mainController) {
		FXMLLoader fxmlLoader = new FXMLLoader(
				this.getClass().getResource("/application/views/main/CubeRepository.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		this.mainController = mainController;
		cubeInfoList = FXCollections.observableArrayList();
		repository = new CubeRepository();

		updateRepositoryList();
		initializeTable();
	}

	private void initializeTable() {

		nameCol.setCellValueFactory(new PropertyValueFactory<CubeInfo, String>("name"));
		nameCol.setEditable(false);

		eventsCol.setCellValueFactory(new PropertyValueFactory<CubeInfo, Integer>("numEvents"));
		eventsCol.setEditable(false);

		dimensionsCol.setCellValueFactory(new PropertyValueFactory<CubeInfo, Integer>("numDimensions"));
		dimensionsCol.setEditable(false);

		attributesCol.setCellValueFactory(new PropertyValueFactory<CubeInfo, Integer>("numAttributes"));
		attributesCol.setEditable(false);

		// set the table
		table.setEditable(true);
		table.getSelectionModel().cellSelectionEnabledProperty().set(true);
		table.setItems(cubeInfoList);
	}

	public void updateRepositoryList() {
		// table.getItems().clear();
		cubeInfoList.clear();
		for (String cubeName : repository.getCubeNames())
			cubeInfoList.add(repository.getCube(cubeName).getCubeInfo());
		table.setItems(cubeInfoList);
		table.refresh();
	}

	public void addCube(Cube cube) {
		repository.addCube(cube);
		updateRepositoryList();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	/**
	 * Button handlers
	 */
	@FXML
	protected void openCube() {
		if (table.getSelectionModel().getSelectedItem() != null)
			mainController.exploreCube(repository.getCube(table.getSelectionModel().getSelectedItem().getName()));
	}

	@FXML
	protected void importCube() {

	}

	@FXML
	protected void removeCube() {

	}

	@FXML
	protected void exportCube() {
		if (table.getSelectionModel().getSelectedItem() != null)
			CubeExporter.exportCube(repository.getCube(table.getSelectionModel().getSelectedItem().getName()));
	}
	@FXML
	protected void createCube() {
		mainController.newCube();
	}
}
