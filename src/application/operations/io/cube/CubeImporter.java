package application.operations.io.cube;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;

import application.controllers.wizard.steps.DimensionsController;
import application.controllers.wizard.steps.ImportDataController;
import application.controllers.workers.DBWorker;
import application.controllers.workers.WorkerCatalog;
import application.models.attribute.abstr.Attribute;
import application.models.cube.Cube;
import application.models.cube.CubeStructure;
import application.models.cube.SerializableCubeStructure;
import application.models.cube.SerializableCubeStructure.SerializableDimensionImpl;
import application.models.dimension.DimensionImpl;
import application.models.eventbase.AbstrEventBase;
import application.models.eventbase.FileBasedEventBase;
import application.operations.dialogs.FileChooserDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CubeImporter {

	public static Cube importCube() {
		// retrieve the SerializedCubeStructure from file

		SerializableCubeStructure serializableCubeStructure;

		FileChooserDialog cubeChooser = new FileChooserDialog("Import Cube", "Select a cube file (.cub)", "");
		Optional<File> cubeFile = cubeChooser.showAndWait();

		if (!cubeFile.isPresent())
			return null;

		try {
			FileInputStream fileIn = new FileInputStream(cubeFile.get());
			ObjectInputStream in = new ObjectInputStream(fileIn);
			serializableCubeStructure = (SerializableCubeStructure) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
			return null;
		} catch (ClassNotFoundException c) {
			System.out.println("SerializableCubeStructure class not found");
			c.printStackTrace();
			return null;
		}

		// convert to CubeStructure
		ObservableList<DimensionImpl> newDimensions = FXCollections.observableArrayList();
		Set<Attribute> allAttributes = new HashSet<Attribute>();

		for (SerializableDimensionImpl d : serializableCubeStructure.getDimensions()) {
			DimensionImpl newDimension = new DimensionImpl(d.getDimensionName(), d.isTime());
			newDimension.setRoot(d.getRoot());
			if (d.isTime()) {
				allAttributes.add(newDimension.getRoot());
				newDimension.initializeTimeDimension();
			} else {
				for (Attribute a : d.getAttributes()) {
					newDimension.addAttribute(a);
					allAttributes.add(a);
				}
			}

			newDimensions.add(newDimension);
		}

		CubeStructure cubeStructure = new CubeStructure(newDimensions);

		// retrieve dataset (csv, xes, etc)

		FileChooserDialog dataChooser = new FileChooserDialog("Import Cube",
				"Please select the source of data for the imported cube", "");
		Optional<File> dataFile = dataChooser.showAndWait();
		if (!dataFile.isPresent())
			return null;

		// rebuild the AbstrEvent Base
		
		DBWorker dbWorker = WorkerCatalog.getDBWorker();		
		AbstrEventBase eventBase = dbWorker.createEventBase(dataFile.get().getAbsolutePath(),
				cubeFile.get().getName().replace(".cub", ""), Lists.newArrayList(allAttributes));
		// rebuild the cube

		Cube cube = new Cube(cubeStructure, eventBase);

		// set all static variables for correct functioning

		return cube;
	}

}
