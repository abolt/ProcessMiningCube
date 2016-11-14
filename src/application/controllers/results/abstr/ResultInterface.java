package application.controllers.results.abstr;

import javax.swing.JComponent;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

public interface ResultInterface {
	
	public void render();
	
	public XLog transformToLog(XEvent... events);
	
	public JComponent getResult();

}
