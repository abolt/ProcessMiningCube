package application.operations.io.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.processmining.log.csv.CSVFileReferenceUnivocityImpl;
import org.processmining.log.csv.ICSVReader;
import org.processmining.log.csv.config.CSVConfig;

import application.models.eventbase.AbstrEventBase;
import application.models.wizard.MappingRow;
import application.operations.io.Importer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CSVImporter extends Importer {

	private final int MAX_LINES = 5;

	private SimpleDateFormat timestampFormat;

	public CSVImporter(File in) {
		super(in);
	}

	public void setTimestampFormat(SimpleDateFormat timestampFormat) {
		this.timestampFormat = timestampFormat;
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
				attributeObjects.add(new MappingRow(att, attributes.get(att), "", false));
			}
			return attributeObjects;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public AbstrEventBase importFromFile() {
		return null;
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
	}
}
