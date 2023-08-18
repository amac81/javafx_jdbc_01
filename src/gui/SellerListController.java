package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {
	
	private SellerService service;
	
	@FXML
	private TableView<Seller> tableViewSeller;
	
	@FXML
	private TableColumn<Seller, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
	
	@FXML
	private Button btNew;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;
	
	private ObservableList<Seller> sellersObservableList;

	public void setSellerService(SellerService service) {
		this.service = service;
	}
		
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage stage = Utils.currentStage(event);
		Seller seller = new Seller();
		Department department = new Department();
		seller.setDepartment(department);
		createDialogForm(seller, "/gui/SellerForm.fxml", stage);
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/YYYY");
		
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, new Locale("PT", "pt"));
		
		TableColumn<Seller,String> sellerNameCol = new TableColumn<Seller,String>("Department");
		sellerNameCol.setCellValueFactory(new Callback<CellDataFeatures<Seller, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<Seller, String> p) {
		     	 return p.getValue().getDepartment().getDepartmentName();
		     }
		  });
		 
		 tableViewSeller.getColumns().add(sellerNameCol);

		
		
		//tableColumnSeller.setCellValueFactory(new PropertyValueFactory<>("Seller"));
		
	
		Stage stage  = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null!");
		}
		
		List <Seller> list = service.findAll();
		sellersObservableList = FXCollections.observableArrayList(list);
		
		tableViewSeller.setItems(sellersObservableList);
		
		initEditButtons();
		initDeleteButtons();
	}

	private void createDialogForm(Seller seller, String absoluteName, Stage parentStage) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			
			SellerFormController controller = loader.getController();
			controller.setSeller(seller);
			controller.setSellerService(new SellerService());
			// subscribe DataChangeListener events
			controller.subscribeDataChangeListener(this);
			
			controller.updateFormData(new Locale("PT", "pt"));
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller Data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		}
		catch(IOException e) {
			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
	
	@Override
	public void onDataChanged() {
		// we need to update TableView
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
							
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	
	private void initDeleteButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("delete");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				
				button.setOnAction(event -> removeEntity(obj));
			}

			
		});
	}
	
	private void removeEntity(Seller obj) {
		//show confirmation alert
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		if(result.get() == ButtonType.YES) 
		{
			if (service == null) {
				throw new IllegalStateException("Service was null!");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch(DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
}
