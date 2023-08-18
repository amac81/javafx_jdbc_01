package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	private Seller entity;
	private SellerService service;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private Label labelId;

	@FXML
	private TextField textFieldId;

	@FXML
	private TextField textFieldName;

	@FXML
	private TextField textFieldEmail;
	
	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField textFieldBaseSalary;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btClose;

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setSellerService(SellerService service) {
		this.service = service;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		// other objects, as long as they implement the DataChangeListener interface,
		// can "subscribe" to receive events from my class
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {

		// defensive programming, because we are not using framework for dependency
		// injection
		if (entity == null) {
			throw new IllegalStateException("Entity was null!");
		}

		if (service == null) {
			throw new IllegalStateException("Service was null!");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();

			Utils.currentStage(event).close();
		} 
		catch (ValidationException e) 
		{
			setErrorMessages(e.getErrors());
		} 
		catch (DbException e) 
		{
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}

	}

	private void notifyDataChangeListeners() {
		// notify all "subscribers"
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Seller getFormData() {
		Department dep = entity.getDepartment();
		
		Seller seller = new Seller();

		// textFieldName can't be empty

		ValidationException exception = new ValidationException("Validation Error");

		seller.setId(Utils.tryParseToInt(textFieldId.getText()));

		if (textFieldName.getText() == null || textFieldName.getText().trim().isEmpty()) {
			exception.addError("name", "Field can't be empty");
		}

		seller.setName(textFieldName.getText());
		seller.setEmail(textFieldEmail.getText());
		//seller.setBirthDate(LocalDate.parse(dpBirthDate.getText(), dtf1));
		
		seller.setBaseSalary(Double.parseDouble(textFieldBaseSalary.getText()));
		
		seller.setDepartment(dep);

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return seller;
	}

	@FXML
	public void onBtCloseAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(textFieldId);
		Constraints.setTextFieldDouble(textFieldBaseSalary);
		Constraints.setTextFieldMaxLength(textFieldName, 80);
		Constraints.setTextFieldMaxLength(textFieldEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
	}

	public void updateFormData(Locale local) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null!");
		}

		textFieldId.setText(String.valueOf(entity.getId()));
		textFieldName.setText(entity.getName());
		
		textFieldEmail.setText(entity.getEmail());
		
		Locale.setDefault(local);
		
		textFieldBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));	
		
		dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
		
		if (fields.contains("email")) {
			labelErrorName.setText(errors.get("email"));
		}

		if (fields.contains("birthDate")) {
			labelErrorName.setText(errors.get("birthDate"));
		}
		
		if (fields.contains("baseSalary")) {
			labelErrorName.setText(errors.get("baseSalary"));
		}
	}
}
