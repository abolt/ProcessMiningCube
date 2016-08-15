package application.models.attribute;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import application.controllers.wizard.steps.MappingController;
import application.models.attribute.abstr.AbstrNumericalAttribute;
import application.models.attribute.abstr.Attribute;

public class DateTimeAttribute extends AbstrNumericalAttribute<Long> {

	private static final long serialVersionUID = 7733018547589239531L;

	private DateFormat dateFormat;

	public static final String YEAR = "_YEAR", QUARTER = "_QUARTER", MONTH = "_MONTH", WEEK_OF_MONTH = "_WEEK_OF_MONTH",
			DAY_OF_WEEK = "_DAY_OF_WEEK", DAY = "_DAY", HOUR = "_HOUR", MINUTE = "_MINUTE", SECOND = "_SECOND";

	public DateTimeAttribute(String name, String type, Attribute<?> parent) {
		super(name, type, parent);

		createDerivedAttributes();
	}

	private void createDerivedAttributes() {

		this.addChild(new DiscreteAttribute(this.name + YEAR, Attribute.DERIVED, this));
		this.addChild(new DiscreteAttribute(this.name + MONTH, Attribute.DERIVED, this));
		this.addChild(new DiscreteAttribute(this.name + WEEK_OF_MONTH, Attribute.DERIVED, this));
		this.addChild(new DiscreteAttribute(this.name + DAY_OF_WEEK, Attribute.DERIVED, this));
		this.addChild(new DiscreteAttribute(this.name + DAY, Attribute.DERIVED, this));
		this.addChild(new DiscreteAttribute(this.name + HOUR, Attribute.DERIVED, this));
		this.addChild(new DiscreteAttribute(this.name + MINUTE, Attribute.DERIVED, this));
		this.addChild(new DiscreteAttribute(this.name + SECOND, Attribute.DERIVED, this));
	}

	public boolean addValue(Date value) {

		if (addValue(value.getTime())) {
			// add to children
			Calendar cal = new GregorianCalendar();
			cal.setTime(value);

			((DiscreteAttribute) this.getChildren(this.name + YEAR)).addValue(cal.get(Calendar.YEAR));
			((DiscreteAttribute) this.getChildren(this.name + MONTH)).addValue(cal.get(Calendar.MONTH));
			((DiscreteAttribute) this.getChildren(this.name + WEEK_OF_MONTH)).addValue(cal.get(Calendar.WEEK_OF_MONTH));
			((DiscreteAttribute) this.getChildren(this.name + DAY_OF_WEEK)).addValue(cal.get(Calendar.DAY_OF_WEEK));
			((DiscreteAttribute) this.getChildren(this.name + DAY)).addValue(cal.get(Calendar.DAY_OF_MONTH));
			((DiscreteAttribute) this.getChildren(this.name + HOUR)).addValue(cal.get(Calendar.HOUR_OF_DAY));
			((DiscreteAttribute) this.getChildren(this.name + MINUTE)).addValue(cal.get(Calendar.MINUTE));
			((DiscreteAttribute) this.getChildren(this.name + SECOND)).addValue(cal.get(Calendar.SECOND));
			return true;
		}
		return false;
	}

	@Override
	public boolean addValue(String value) {
		if(dateFormat == null)
			dateFormat = MappingController.detectTimestampParser(value);
		if(value != null && !value.equalsIgnoreCase("null")){
			try {
				return addValue(dateFormat.parse(value));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}		
		return false;
	}
}
