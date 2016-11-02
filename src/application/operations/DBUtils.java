package application.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections15.keyvalue.MultiKey;
import org.apache.commons.collections15.map.MultiKeyMap;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;

import application.models.attribute.abstr.AbstrNumericalAttribute;
import application.models.attribute.abstr.Attribute;
import application.models.attribute.impl.ContinuousAttribute;
import application.models.attribute.impl.DiscreteAttribute;
import application.models.attribute.impl.TextAttribute;
import application.models.condition.ConditionUtils;
import application.models.condition.abstr.Condition;
import application.models.condition.factory.ConditionFactory;
import application.models.eventbase.AbstrEventBase;
import application.models.explorer.HeaderTree;
import application.models.explorer.HeaderTree.Node;
import application.models.metric.Metric;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class with static methods used to interact with the DB and extract useful(?)
 * things
 * 
 * @author abolt
 *
 */

public class DBUtils {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Grid getGrid(List<Attribute> rows, List<Attribute> columns, List<Attribute> filters,
			AbstrEventBase eb, Metric currentMetric) {

		int rowCount, colCount;

		HeaderTree rowHeaders = new HeaderTree(rows);
		HeaderTree colHeaders = new HeaderTree(columns);

		rowCount = rowHeaders.getLeafs(-1).size();
		colCount = colHeaders.getLeafs(-1).size();

		GridBase grid = new GridBase(rowCount + columns.size(), colCount + rows.size());
		ObservableList<ObservableList<SpreadsheetCell>> cells = FXCollections.observableArrayList();

		// initialize empty table
		for (int i = 0; i < grid.getRowCount(); i++) {
			ObservableList<SpreadsheetCell> row = FXCollections.observableArrayList();
			for (int j = 0; j < grid.getColumnCount(); j++) {
				SpreadsheetCell cell = SpreadsheetCellType.STRING.createCell(i, j, 1, 1, "");
				row.add(cell);
			}
			cells.add(row);
		}
		grid.setRows(cells);

		if (rows.isEmpty() && columns.isEmpty()) {
			grid.setCellValue(0, 0, "Empty");
			return grid;
		}

		// fill row headers
		List<Node> rowleafs = rowHeaders.getAccumulatedLeafs(-1);
		for (int i = 0; i < grid.getRowCount(); i++)
			for (int j = 0; j < rows.size(); j++)
				if (i >= columns.size())
					grid.setCellValue(i, j, rowleafs.get(i - columns.size()).values.get(j).getAsString());

		// fill column headers
		List<Node> colleafs = colHeaders.getAccumulatedLeafs(-1);
		for (int j = 0; j < grid.getColumnCount(); j++)
			for (int i = 0; i < columns.size(); i++) {
				if (j >= rows.size())
					grid.setCellValue(i, j, colleafs.get(j - rows.size()).values.get(i).getAsString());
			}

		// span row headers
		for (int j = 0; j < rows.size(); j++) {
			int spanCounter = 0;
			String oldValue = null;
			int oldIndex = 0;
			for (int i = 0; i < grid.getRowCount(); i++) {
				if (oldValue == null) {
					oldValue = cells.get(i).get(j).getText();
					oldIndex = i;
					spanCounter = 0;
				} else if (cells.get(i).get(j).getText() != null && cells.get(i).get(j).getText().equals(oldValue))
					spanCounter++;
				else if (spanCounter > 0) {
					for (int z = oldIndex + 1; z <= spanCounter; z++)
						cells.get(z).set(j, null);
					grid.spanRow(spanCounter + 1, oldIndex, j);

					oldValue = cells.get(i).get(j).getText();
					oldIndex = i;
					spanCounter = 0;
				} else {
					oldValue = cells.get(i).get(j).getText();
					oldIndex = i;
					spanCounter = 0;
				}
			}
			if (spanCounter > 0) {
				for (int z = oldIndex + 1; z <= spanCounter; z++)
					cells.get(z).set(j, null);
				grid.spanRow(spanCounter + 1, oldIndex, j);
			}
		}
		// span columns
		for (int i = 0; i < columns.size(); i++) {
			int spanCounter = 0;
			String oldValue = null;
			int oldIndex = 0;
			for (int j = 0; j < grid.getColumnCount(); j++) {
				if (oldValue == null) {
					oldValue = cells.get(i).get(j).getText();
					oldIndex = j;
					spanCounter = 0;
				} else if (cells.get(i).get(j).getText() != null && cells.get(i).get(j).getText().equals(oldValue))
					spanCounter++;
				else if (spanCounter > 0) {
					for (int z = oldIndex + 1; z <= spanCounter; z++)
						cells.get(i).set(z, null);
					grid.spanColumn(spanCounter + 1, i, oldIndex);

					oldValue = cells.get(i).get(j).getText();
					oldIndex = j;
					spanCounter = 0;
				} else {
					oldValue = cells.get(i).get(j).getText();
					oldIndex = j;
					spanCounter = 0;
				}
			}
			if (spanCounter > 0) {
				for (int z = oldIndex + 1; z <= spanCounter; z++)
					cells.get(i).set(z, null);
				grid.spanColumn(spanCounter + 1, i, oldIndex);
			}
		}

		/**
		 * add the values to the cells. First we get all the conditions of the
		 * queries. Second, we ask for all the queries at the same time though a
		 * connection manager and the use od batches Third, we get the results
		 * back and put them into the cells.
		 */
		MultiKeyMap map = new MultiKeyMap();
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++) {
				List<Condition> conditions = new ArrayList<Condition>();
				// row conditions
				for (Condition c : rowleafs.get(i).values)
					if (c.getAttribute() != null && !c.getTail().getTailConditions().isEmpty())
						conditions.add(c);
				// column conditions
				for (Condition c : colleafs.get(j).values)
					if (c.getAttribute() != null && !c.getTail().getTailConditions().isEmpty())
						conditions.add(c);

				if (!conditions.isEmpty()) {
					MultiKey<Integer> key = new MultiKey<Integer>(i, j);
					map.put(key, conditions);
				}
			}
		}

		// filter conditions
		List<Condition> conditions = new ArrayList<Condition>();
		for (Attribute att : filters)
			for (Condition c : createConditionsFromFilterAttribute(att))
				if (c.getAttribute() != null && !c.getTail().getTailConditions().isEmpty())
					conditions.add(c);

		MultiKeyMap result = eb.query(map, conditions, rowCount * colCount, rows.size() + columns.size(),
				currentMetric);

		for (int i = 0; i < rowCount; i++)
			for (int j = 0; j < colCount; j++)
				grid.setCellValue(i + columns.size(), j + rows.size(), result.get(i, j));

		return grid;
	}

	@SuppressWarnings("rawtypes")
	private static Collection<Condition> createConditionsFromFilterAttribute(Attribute attribute) {

		List<Condition> conditions = new ArrayList<Condition>();
		if (attribute instanceof TextAttribute) {
			if (((TextAttribute) attribute).getSelectedValueSet().size() != attribute.getValueSet().size()) {
				Condition newCondition = ConditionFactory.createCondition((TextAttribute) attribute);
				String value = "(";
				Iterator<String> iterator = ((TextAttribute) attribute).getSelectedValueSet().iterator();
				while (iterator.hasNext()) {
					value = value + "'" + iterator.next() + "'";
					if (iterator.hasNext())
						value = value + ", ";
				}
				value = value + ")";
				if (attribute.getValueSet().size() / ((TextAttribute) attribute).getSelectedValueSet().size() >= 2)
					ConditionUtils.addConditionToTail(newCondition, Condition.IN, value);
				else
					ConditionUtils.addConditionToTail(newCondition, Condition.NOT_IN, value);
				conditions.add(newCondition);
			}
		} else if (attribute instanceof DiscreteAttribute) {
			if (((DiscreteAttribute) attribute).getSelectedMin() > ((DiscreteAttribute) attribute).getMin()
					|| ((DiscreteAttribute) attribute).getSelectedMax() < ((DiscreteAttribute) attribute).getMax()) {
				Condition newCondition = ConditionFactory.createCondition((DiscreteAttribute) attribute);
				if (((DiscreteAttribute) attribute).getSelectedMin() > ((DiscreteAttribute) attribute).getMin())
					ConditionUtils.addConditionToTail(newCondition, Condition.BIGGER_THAN_EQUALS,
							"'" + ((AbstrNumericalAttribute) attribute).getSelectedMin().toString() + "'");
				if (((DiscreteAttribute) attribute).getSelectedMax() < ((DiscreteAttribute) attribute).getMax())
					ConditionUtils.addConditionToTail(newCondition, Condition.SMALLER_THAN_EQUALS,
							"'" + ((DiscreteAttribute) attribute).getSelectedMax().toString() + "'");
				conditions.add(newCondition);
			}
		} else if (attribute instanceof ContinuousAttribute) {
			if (((ContinuousAttribute) attribute).getSelectedMin() > ((ContinuousAttribute) attribute).getMin()
					|| ((ContinuousAttribute) attribute).getSelectedMax() < ((ContinuousAttribute) attribute)
							.getMax()) {
				Condition newCondition = ConditionFactory.createCondition((ContinuousAttribute) attribute);
				if (((ContinuousAttribute) attribute).getSelectedMin() > ((ContinuousAttribute) attribute).getMin())
					ConditionUtils.addConditionToTail(newCondition, Condition.BIGGER_THAN_EQUALS,
							"'" + ((ContinuousAttribute) attribute).getSelectedMin().toString() + "'");
				if (((ContinuousAttribute) attribute).getSelectedMax() < ((ContinuousAttribute) attribute).getMax())
					ConditionUtils.addConditionToTail(newCondition, Condition.SMALLER_THAN_EQUALS,
							"'" + ((ContinuousAttribute) attribute).getSelectedMax().toString() + "'");
				conditions.add(newCondition);
			}
		}
		return conditions;
	}

}
