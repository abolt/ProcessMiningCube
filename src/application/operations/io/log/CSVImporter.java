package application.operations.io.log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.processmining.log.csv.CSVFileReferenceUnivocityImpl;
import org.processmining.log.csv.ICSVReader;
import org.processmining.log.csv.config.CSVConfig;
import org.processmining.log.csvimport.exception.CSVConversionException;

import application.controllers.wizard.steps.MappingController;
import application.models.attribute.abstr.Attribute;
import application.models.wizard.MappingRow;
import application.operations.io.Importer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CSVImporter extends Importer {

	private final int MAX_LINES = 5;

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

	// CSVFileReferenceUnivocityImpl csvFile = new
	// CSVFileReferenceUnivocityImpl(file.toPath());
	// CSVConfig config;
	// try {
	// config = new CSVConfig(csvFile);
	// CSVConversion conversion = new CSVConversion();
	// CSVConversionConfig conversionConfig = new
	// CSVConversionConfig(csvFile, config);
	//
	// // conversionConfig.autoDetect();
	//
	// conversionConfig.setCaseColumns(ImmutableList.of(case_id));
	// conversionConfig.setEventNameColumns(ImmutableList.of(activity_id));
	// conversionConfig.setCompletionTimeColumn(timestamp);
	// conversionConfig.setStartTimeColumn("");
	//
	// conversionConfig.setEmptyCellHandlingMode(CSVEmptyCellHandlingMode.SPARSE);
	//
	// conversionConfig.setErrorHandlingMode(CSVErrorHandlingMode.ABORT_ON_ERROR);
	// Map<String, CSVMapping> conversionMap =
	// conversionConfig.getConversionMap();
	// CSVMapping mapping = conversionMap.get(timestamp);
	// mapping.setDataType(Datatype.TIME);
	// mapping.setPattern(timestampFormat.toPattern());
	//
	// ConversionResult<XLog> result = conversion.doConvertCSVToXES(new
	// NoOpProgressListenerImpl(), csvFile,
	// config, conversionConfig);
	//
	// return result.getResult();
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return null;

	@Override
	public List<XEvent> getEventList(long size, List<Attribute> attributes) {

		List<XEvent> events = new ArrayList<XEvent>();
		// stores the ordered attributes
		List<String> attributeNames = new ArrayList<String>();
		// maps the attributes to their type
		Map<String, String> attributeNameAndType = new HashMap<String, String>();
		for (Attribute a : attributes)
			attributeNameAndType.put(a.getName(), a.getType());

		try {
			CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.DEFAULT);
			DateFormat dateFormat = null;
			boolean headerRead = false;
			for (CSVRecord csvRecord : parser) {
				XAttributeMap attributeMap = new XAttributeMapImpl();
				if (!headerRead) {
					for (int i = 0; i < csvRecord.size(); i++)
						attributeNames.add(csvRecord.get(i));
					headerRead = true;
				} else {
					for (int i = 0; i < csvRecord.size(); i++) {
						switch (attributeNameAndType.get(attributeNames.get(i))) {
						case Attribute.TEXT:
							attributeMap.put(attributeNames.get(i),
									new XAttributeLiteralImpl(attributeNames.get(i), csvRecord.get(i)));
							break;
						case Attribute.DISCRETE:
							attributeMap.put(attributeNames.get(i), new XAttributeDiscreteImpl(attributeNames.get(i),
									Integer.parseInt(csvRecord.get(i))));
							break;
						case Attribute.CONTINUOUS:
							attributeMap.put(attributeNames.get(i), new XAttributeContinuousImpl(attributeNames.get(i),
									Double.parseDouble(csvRecord.get(i))));
							break;
						case Attribute.DATE_TIME:
							if (dateFormat == null)
								dateFormat = MappingController.detectTimestampParser(csvRecord.get(i));
							attributeMap.put(attributeNames.get(i), new XAttributeTimestampImpl(attributeNames.get(i),
									dateFormat.parse(csvRecord.get(i)).getTime()));
							break;
						}
						events.add(new XEventImpl(attributeMap));
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return events;
	}
}
