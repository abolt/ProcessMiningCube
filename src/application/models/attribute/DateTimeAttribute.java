package application.models.attribute;

import java.text.DateFormat;
import java.util.Date;

import application.models.attribute.abstr.AbstrAttribute;
import application.models.attribute.abstr.Attribute;

public class DateTimeAttribute extends AbstrAttribute<Date> {

	private static final long serialVersionUID = 7733018547589239531L;

	private DateFormat dateFormat;

	public static final String YEAR = "YEAR", QUARTER = "QUARTER", MONTH = "MONTH", WEEK_OF_MONTH = "WEEK OF MONTH",
			DAY_OF_WEEK = "DAY OF WEEK", DAY = "DAY", HOUR = "HOUR", MINUTE = "MINUTE", SECOND = "SECOND";

	public DateTimeAttribute(String name, String type, Attribute parent) {
		super(name, type, parent);

	}
	
	public void setFormat(DateFormat format){
		this.dateFormat = format;
	}

	// public boolean addValue(String value) {
	// if (dateFormat == null)
	// dateFormat = MappingController.detectTimestampParser(value);
	// if (value != null && !value.equalsIgnoreCase("null")) {
	// try {
	// return addValue(dateFormat.parse(value));
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// }
	// return false;
	// }
	//
	// private boolean addValue(Date value) {
	//
	// if (addValue(value.getTime())) {
	// // add to children
	// Calendar cal = new GregorianCalendar();
	// cal.setTime(value);
	//
	// ((DiscreteAttribute)
	// this.getChildren(YEAR)).addValue(cal.get(Calendar.YEAR));
	// ((DiscreteAttribute)
	// this.getChildren(MONTH)).addValue(cal.get(Calendar.MONTH));
	// ((DiscreteAttribute)
	// this.getChildren(WEEK_OF_MONTH)).addValue(cal.get(Calendar.WEEK_OF_MONTH));
	// ((DiscreteAttribute)
	// this.getChildren(DAY_OF_WEEK)).addValue(cal.get(Calendar.DAY_OF_WEEK));
	// ((DiscreteAttribute)
	// this.getChildren(DAY)).addValue(cal.get(Calendar.DAY_OF_MONTH));
	// ((DiscreteAttribute)
	// this.getChildren(HOUR)).addValue(cal.get(Calendar.HOUR_OF_DAY));
	// ((DiscreteAttribute)
	// this.getChildren(MINUTE)).addValue(cal.get(Calendar.MINUTE));
	// ((DiscreteAttribute)
	// this.getChildren(SECOND)).addValue(cal.get(Calendar.SECOND));
	// return true;
	// }
	// return false;
	// }

}
