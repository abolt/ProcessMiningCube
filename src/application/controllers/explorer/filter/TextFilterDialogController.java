package application.controllers.explorer.filter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckListView;

import application.models.attribute.TextAttribute;
import application.models.attribute.abstr.Attribute;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class TextFilterDialogController extends BorderPane implements Initializable {

	@FXML
	CheckListView<String> listView;

	@FXML
	TextField textField;

	public TextFilterDialogController(Attribute attribute) {

		FXMLLoader fxmlLoader = new FXMLLoader(
				this.getClass().getResource("/application/views/filter/TextFilterDialog.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		initializeList((TextAttribute) attribute);

	}

	private void initializeList(TextAttribute attribute) {

		// initialize attributeName
		textField.setText(attribute.getLabel());
		textField.setEditable(false);

		// initialize selections
		ObservableList<String> list = FXCollections.observableArrayList();
		for (String value : attribute.getValueSet())
			list.add((String) value);
		listView.setItems(list);
		listView.getCheckModel().clearChecks();

		for (String value : attribute.getSelectedValueSet())
			listView.getCheckModel().check(value);
		this.layout();

	}

	public ObservableList<String> getSelectedValues() {
		return listView.getCheckModel().getCheckedItems();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	@FXML
	protected void clickSelectAll() {
		listView.getCheckModel().checkAll();
	}

	@FXML
	protected void clickInvertSelection() {
		for (int i = 0; i < listView.getCheckModel().getItemCount(); i++) {
			if (listView.getCheckModel().isChecked(i))
				listView.getCheckModel().clearCheck(i);
			else
				listView.getCheckModel().check(i);
		}
	}

}
