package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable {
	
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
	
	private ObservableList<Seller> sellersObservableList;

	public void setSellerService(SellerService service) {
		this.service = service;
	}
		
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage stage = Utils.currentStage(event);
		createDialogForm("/gui/SellerForm.fxml", stage);
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
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		
		TableColumn<Seller,String> departmentNameCol = new TableColumn<Seller,String>("Department Name");
		departmentNameCol.setCellValueFactory(new Callback<CellDataFeatures<Seller, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<Seller, String> p) {
		     	 return p.getValue().getDepartment().getDepartmentName();
		     }
		  });
		 
		 tableViewSeller.getColumns().add(departmentNameCol);

		
		
		//tableColumnDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
		
	
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
	}

	private void createDialogForm(String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
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
	
}
