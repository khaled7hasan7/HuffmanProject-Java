
package huffman;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class used to handle and control with MainInterface.fxml
 */
public class MainInterfaceController implements Initializable {

    @FXML // fx:id="btBrowse"
    private Button btBrowse; // Value injected by FXMLLoader

    @FXML // fx:id="btTAnotherFile"
    private Button btAnotherFile; // Value injected by FXMLLoader

    @FXML // fx:id="cmASCII"
    private TableColumn<StatisticsTable, Byte> cmASCII; // Value injected by FXMLLoader

    @FXML // fx:id="cmFrequency"
    private TableColumn<StatisticsTable, Integer> cmFrequency; // Value injected by FXMLLoader

    @FXML // fx:id="cmHuffman"
    private TableColumn<StatisticsTable, String> cmHuffman; // Value injected by FXMLLoader

    @FXML // fx:id="cmLength"
    private TableColumn<StatisticsTable, Integer> cmLength; // Value injected by FXMLLoader

    @FXML // fx:id="comboBox"
    private ComboBox<String> comboBox; // Value injected by FXMLLoader

    @FXML // fx:id="tableView"
    private TableView<StatisticsTable> tableView; // Value injected by FXMLLoader

    @FXML // fx:id="txtHeader"
    private TextArea txtHeader; // Value injected by FXMLLoader

    @FXML // fx:id="lblHeader"
    private Label lblHeader; // Value injected by FXMLLoader

    @FXML // fx:id="lblStatistics"
    private Label lblStatistics; // Value injected by FXMLLoader

    private HuffmanCompress header;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.comboBox.getItems().add("Compress File");
        this.comboBox.getItems().add("Decompress File");
        this.comboBox.setDisable(false);
        this.btBrowse.setDisable(true);
        this.btAnotherFile.setDisable(true);
        this.lblHeader.setDisable(true);
        this.lblStatistics.setDisable(true);
        this.tableView.setDisable(true);
        this.txtHeader.setDisable(true);
        this.cmASCII.setCellValueFactory(new PropertyValueFactory<>("theByte"));
        this.cmFrequency.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        this.cmHuffman.setCellValueFactory(new PropertyValueFactory<>("huffmanCode"));
        this.cmLength.setCellValueFactory(new PropertyValueFactory<>("huffmanCodeLength"));
    }

    public void handleComboBox() {
        this.btBrowse.setDisable(false);
    }

    public void handleBrowse() {

        // File Chooser
        FileChooser fileChooser = new FileChooser();
        if (this.comboBox.getValue().equals("Decompress File")) {
            // Specifying the extension of the files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Files", "*.huf");
            fileChooser.getExtensionFilters().add(extFilter);
        }

        // Browsing one file as any extension
        File sourceFile = fileChooser.showOpenDialog(MainWindow.window.getScene().getWindow());

        if (sourceFile != null) { // To check if the user select the file or close the window without selecting
            if (sourceFile.length() == 0) { // size of the file zero(no data)
                Message.displayMessage("Warning", "There are no data in the " + sourceFile.getName());
            } else {


                if (this.comboBox.getValue().equals("Compress File")) {

                    // To check if the file selected was compressed or not
                    String filename = sourceFile.getName();
                    byte indexOfDot = (byte) filename.lastIndexOf('.');
                    String fileExtension = filename.substring(indexOfDot + 1);
                    if (fileExtension.equals("huf")) {
                        Message.displayMessage("Warning", "This file is already compressed");
                        return;
                    }

                    this.header = new HuffmanCompress();
                    this.header.compress(sourceFile); // encoding the file and print it encoded(compressed) to huf file
                    this.lblHeader.setDisable(false);
                    this.lblStatistics.setDisable(false);
                    this.tableView.setDisable(false);
                    this.txtHeader.setDisable(false);
                    this.txtHeader.setText(this.header.getFullHeaderAsString()); // display the header of the huf file after encoded
                    this.fillStatisticsTable(); // display the statistic table in the window
                } else {
                    HuffmanDecompress.decompress(sourceFile);
                }
                this.comboBox.setDisable(true); // hide
                this.btAnotherFile.setDisable(false); // display
            }

        }
    }


    // To handle button another file
    public void handleAnotherFile() {
        this.returnControlsDefault();
    }

    // To return all controller as it was from the beginning
    private void returnControlsDefault() {
        this.comboBox.setDisable(false);
        this.btBrowse.setDisable(false);
        this.btAnotherFile.setDisable(true);
        this.txtHeader.clear();
        this.tableView.getItems().clear();
        if (this.header != null) // I check because may he/she selected to decompress. and the header object will be null in this case
            this.header.returnDefault();
        this.tableView.setDisable(true);
        this.txtHeader.setDisable(true);
        this.lblHeader.setDisable(true);
        this.lblStatistics.setDisable(true);

    }

    // display the statistic table in the window
    private void fillStatisticsTable() {
        for (int i = 0; i < this.header.getHuffmanTable().length; i++) {
            if (this.header.getHuffmanTable()[i] != null)
                this.tableView.getItems().add(this.header.getHuffmanTable()[i]);
        }
    }

}
