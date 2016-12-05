package application.operations.io.log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.log.csv.CSVFile;
import org.processmining.log.csv.CSVFileReferenceUnivocityImpl;
import org.processmining.log.csv.ICSVReader;
import org.processmining.log.csv.config.CSVConfig;
import org.processmining.log.csvimport.CSVConversion;
import org.processmining.log.csvimport.CSVConversion.ConversionResult;
import org.processmining.log.csvimport.CSVConversion.NoOpProgressListenerImpl;
import org.processmining.log.csvimport.CSVConversion.ProgressListener;
import org.processmining.log.csvimport.config.CSVConversionConfig;
import org.processmining.log.csvimport.config.CSVConversionConfig.CSVEmptyCellHandlingMode;
import org.processmining.log.csvimport.config.CSVConversionConfig.CSVErrorHandlingMode;
import org.processmining.log.csvimport.config.CSVConversionConfig.Datatype;
import org.processmining.log.csvimport.exception.CSVConversionException;

import com.google.common.collect.Lists;

import application.models.attribute.abstr.Attribute;
import application.models.wizard.MappingRow;
import application.operations.io.Importer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CSVImporter extends Importer {

	private final int MAX_LINES = 10;

	public CSVImporter(File in) {
		super(in);
	}

	@Override
	public ObservableList<MappingRow> getSampleList() {

		CSVFileReferenceUnivocityImpl csvFile = new CSVFileReferenceUnivocityImpl(file.toPath());
		CSVConfig config;

		try {
			config = new CSVConfig(csvFile);
			ICSVReader reader = csvFile.createReader(config);
			Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();

			// attribute names are on the first row
			String[] names = reader.readNext();
			for (String s : names) {
				attributes.put(s, new HashSet<String>());
			}

			for (int j = 0; j < MAX_LINES; j++) {
				String[] line = reader.readNext();
				for (int i = 0; i < names.length; i++) {
					if (line[i] != null && !line[i].equals("null"))
						attributes.get(names[i]).add(line[i]);
				}
			}

			ObservableList<MappingRow> attributeObjects = FXCollections.observableArrayList();
			for (String att : attributes.keySet()) {
				attributeObjects.add(new MappingRow(att, attributes.get(att), Attribute.IGNORE));
			}
			return attributeObjects;

		} catch (IOException | CSVConversionException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<XEvent> getEventList(long size, List<Attribute> attributes) {

		List<XEvent> events = new ArrayList<XEvent>();

		try {
			CSVConversion conversion = new CSVConversion();
			CSVFile csvFile = new CSVFileReferenceUnivocityImpl(file.toPath());
			CSVConfig importConfig = new CSVConfig(csvFile);
			CSVConversionConfig conversionConfig = new CSVConversionConfig(csvFile, importConfig);
			conversionConfig.autoDetectDataTypes();

			conversionConfig.setEmptyCellHandlingMode(CSVEmptyCellHandlingMode.SPARSE);
			conversionConfig.setErrorHandlingMode(CSVErrorHandlingMode.OMIT_EVENT_ON_ERROR);
			conversionConfig.setShouldAddStartEventAttributes(false);

			ProgressListener cmdLineProgressListener = new NoOpProgressListenerImpl();
			ConversionResult<XLog> result = conversion.doConvertCSVToXES(cmdLineProgressListener, csvFile, importConfig,
					conversionConfig);
			XLog log = result.getResult();
			for (XTrace t : log)
				for (XEvent e : t)
					events.add(e);

		} catch (Exception e) {
			System.out.println("Error"); 
			e.printStackTrace();
			return null;
		}
		return events;

	}

	private boolean isNameInAttributeList(String name, List<Attribute> attributes) {
		for (Attribute a : attributes)
			if (name.equals(a.getName()))
				return true;
		return false;
	}
}
