package gui.util;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Utils {
	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}

	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static <T> void formatTableColumnDate(TableColumn<T, LocalDate> tableColumn, String format) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, LocalDate> cell = new TableCell<T, LocalDate>() {				
				private DateTimeFormatter dtf =  DateTimeFormatter.ofPattern(format);

				@Override
				protected void updateItem(LocalDate item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						setText(dtf.format(item));
					}
				}
			};
			return cell;
		});
	}

	public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, Locale local) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Double> cell = new TableCell<T, Double>() {
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						Locale.setDefault(local);
						NumberFormat nFormat = NumberFormat.getCurrencyInstance(local);
						setText(String.format(nFormat.format(item)));
					}
				}
			};
			return cell;
		});
	}
	
	public static void formatTextFieldDouble(TextField field, Locale local) {
		field..setCellFactory(column -> {
			TableCell<T, Double> cell = new TableCell<T, Double>() {
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						Locale.setDefault(local);
						NumberFormat nFormat = NumberFormat.getCurrencyInstance(local);
						setText(String.format(nFormat.format(item)));
					}
				}
			};
			return cell;
		});
	}

}
