package application.operations.dialogs;

import java.util.List;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.factory.XFactoryNaiveImpl;

import application.models.attribute.abstr.Attribute;
import application.models.xlog.XLogStructure;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class XLogStructureDialog extends Dialog<XLogStructure> {

	public XLogStructureDialog(List<Attribute> attributes, Attribute caseID, Attribute eventID, Attribute timestamp,
			XFactory factory) {

		setTitle("XLog Structure Definition");
		setHeaderText("The XLog(s) to be created need structure. Please select the following:");

		ChoiceBox<Attribute> caseIDchoice = new ChoiceBox<Attribute>(FXCollections.observableArrayList(attributes));
		if (caseID != null)
			caseIDchoice.getSelectionModel().select(caseID);

		ChoiceBox<Attribute> eventIDchoice = new ChoiceBox<Attribute>(FXCollections.observableArrayList(attributes));
		if (eventID != null)
			eventIDchoice.getSelectionModel().select(eventID);
		
		ChoiceBox<Attribute> timestampChoice = new ChoiceBox<Attribute>(FXCollections.observableArrayList(attributes));
		if (timestamp != null)
			timestampChoice.getSelectionModel().select(timestamp);
		
		ChoiceBox<String> factoryChoice = new ChoiceBox<String>(
				FXCollections.observableArrayList("Naive (stored in memory)", "MapDB (stored in disk)"));
		if(factory!= null){
			if (factory instanceof XFactoryNaiveImpl)
				factoryChoice.getSelectionModel().select(0);
			else
				factoryChoice.getSelectionModel().select(1);
		}

		VBox mainBox = new VBox();
		mainBox.setPadding(new Insets(10));
		mainBox.setSpacing(8);

		HBox box1 = new HBox();
		box1.setSpacing(8);
		box1.getChildren().add(new Label("Case ID:"));
		box1.getChildren().add(caseIDchoice);
		mainBox.getChildren().add(box1);

		HBox box2 = new HBox();
		box2.setSpacing(8);
		box2.getChildren().add(new Label("Event ID:"));
		box2.getChildren().add(eventIDchoice);
		mainBox.getChildren().add(box2);

		HBox box3 = new HBox();
		box3.setSpacing(8);
		box3.getChildren().add(new Label("Event Timestamp:"));
		box3.getChildren().add(timestampChoice);
		mainBox.getChildren().add(box3);

		HBox box4 = new HBox();
		box4.setSpacing(8);
		box4.getChildren().add(new Label("XLog Factory:"));
		box4.getChildren().add(factoryChoice);
		mainBox.getChildren().add(box4);

		getDialogPane().setContent(mainBox);
		ButtonType ok = new ButtonType("Ok", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

		setResultConverter(dialogButton -> {
			if (dialogButton == ok) {
				XFactory selectedFactory;
				if (factoryChoice.getSelectionModel().getSelectedItem().contains("Naive")) // naive
					selectedFactory = new XFactoryNaiveImpl();
				else
					selectedFactory = new XFactoryBufferedImpl();

				return new XLogStructure(caseIDchoice.getSelectionModel().getSelectedItem(),
						eventIDchoice.getSelectionModel().getSelectedItem(),
						timestampChoice.getSelectionModel().getSelectedItem(), selectedFactory);
			}
			return null;
		});

	}

}
