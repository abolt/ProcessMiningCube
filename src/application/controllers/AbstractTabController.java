package application.controllers;

import javafx.scene.control.Tab;

public abstract class AbstractTabController {

	/**
	 * If a step is disabled, all future steps are disabled as well
	 * 
	 * @author abolt
	 *
	 */
	protected boolean enabled = false;
	protected boolean completed = false;
	protected String name;
	protected MainController mainController;

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
		if(completed)
			mainController.completeTriggered(name);
	}

	public String getName() {
		return name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Class<? extends AbstractTabController> getControllerClass() {
		return this.getClass();
	}

	public void setEnabled(boolean value) {
		enabled = value;
		enableTab(value);
	}

	public void init(MainController mainControllerInput) {
		mainController = mainControllerInput;
	}

	public abstract void initializeTab(Tab input);

	protected abstract void enableTab(boolean value);

	public abstract void updateImage();
}
