package gui.util;

import javafx.scene.control.TextField;

public class Constraints {

	public static void setTextFieldInteger(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
				if ( (newValue != null) && !(newValue.matches("\\d*")) ) {
					txt.setText(oldValue);
				}
			});
	}

	public static void setTextFieldMaxLength(TextField txt, int max) {
			txt.textProperty().addListener((obs, oldValue, newValue) -> {
				if ( (newValue != null) && (newValue.length() > max) ) {
					txt.setText(oldValue);
				}
			});
	}

	public static void setTextFieldDouble(TextField txt) {
			///Este código comentado provoca erro quando tenta carregar (load) a janela SellerForm.fxml ( PORQUE??? )
			txt.textProperty().addListener( (obs, oldValue, newValue) -> {
				if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
					txt.setText(oldValue);
				}
			});
	}
}
