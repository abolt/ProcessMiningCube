package application.controllers.wizard.abstr;

import application.controllers.wizard.NewCubeWizardController;

public abstract class AbstractWizardStepController {
	
	private NewCubeWizardController mainController;
	
	public AbstractWizardStepController(NewCubeWizardController controller){
		mainController = controller;
	}
	
	protected void goNext(){
		mainController.nextStep();
	}
	protected void goBack(){
		mainController.backStep();
	}

}
