package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;



import models.Transaction;
import models.UserSession;
import Services.TransactionService;
import application.Utilities.AlertHelper;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.time.LocalDate;


public class TransactionController implements Initializable {

    private TransactionService transactionService = new TransactionService();

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    @FXML
    private TableView<Transaction> tableView;

    @FXML
    private Label lblUser;

    @FXML
    private Label lblTotal;

    @FXML
    private ComboBox<Transaction.Category> categoryOptions;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField amountField;

    @FXML
    private Button addButton;

    @FXML
    private Button searchButton;
    
    @FXML
    private DatePicker datePicker;

    @FXML
    private DatePicker editDatePicker;
    
    @FXML
    private TableColumn<Transaction, String> editColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
        	//tableView = new TableView<>();
        	
            populateTableItems();
            populateTransactionTypeOptions();
            setTotalLabelText();
            setLoggedInLabelText();
            editColumn.setCellFactory(editButtonTableCell(this));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addTransaction(ActionEvent event) throws IOException {
        Window owner = addButton.getScene().getWindow();

        if (descriptionField.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Please enter a description");
            return;
        }

        if (amountField.getText().isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Please enter an amount");
            return;
        }

        if (categoryOptions.getValue() == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Select your transaction category");
            return;
        }
        if (datePicker.getValue() == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Please select a date");
            return;
        }

        String description = descriptionField.getText();
      
        try {
            Float amount = Float.valueOf(amountField.getText());
            if (amount < 0) {
                throw new NumberFormatException("Negative amount");
            }

            Transaction.Category category = categoryOptions.getValue();
            LocalDate date = datePicker.getValue();

            Transaction transaction = transactionService.create(description, amount, category, UserSession.getInstance().getUser().getId(), date);
            transactions.add(transaction);

            setTotalLabelText();
            clearInputs();
        } catch (NumberFormatException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Please enter a valid, non-negative amount");
        }
    }
    
    @FXML
    public void editTransaction(ActionEvent event) {
        // Get the selected transaction from the TableView
        Transaction transaction = tableView.getSelectionModel().getSelectedItem();
        if (transaction == null) {
            // No transaction is selected
            return;
        }

        // Open a new window or dialog for editing the transaction
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edit_transaction.fxml"));
            Parent root = loader.load();
            EditTransactionController editTransactionController = loader.getController();
            editTransactionController.setTransaction(transaction);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Transaction");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Refresh the TableView after editing
            refresh(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Callback<TableColumn<Transaction, String>, TableCell<Transaction, String>> editButtonTableCell(TransactionController transactionController) {
        return param -> new TableCell<Transaction, String>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    transactionController.editTransaction(new ActionEvent());
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        };
    }

    
    @FXML
    public void deleteTransaction(ActionEvent event) throws IOException {
        Transaction transaction = tableView.getSelectionModel().getSelectedItem();

        transactions.remove(transaction);
        transactionService.delete(transaction.getId());

        setTotalLabelText();
        clearInputs();
    }

    @FXML
    public void searchTransaction(ActionEvent event) throws IOException {
        String description = descriptionField.getText();

        transactions = FXCollections.observableArrayList(transactionService.search(description));
        tableView.setItems(transactions);

        setTotalLabelText();
    }

    @FXML
    public void refresh(ActionEvent event) throws IOException {
        transactions = FXCollections.observableArrayList();

        populateTableItems();
        populateTransactionTypeOptions();
        setTotalLabelText();
        setLoggedInLabelText();
        setTotalLabelText();
        clearInputs();
    }

    public void goToPieChart(ActionEvent event) throws IOException {
        
    	Parent root = FXMLLoader.load(getClass().getResource("/pie_chart.fxml"));
		Scene scene = new Scene(root,900,600);
		Stage stage = new Stage();
		stage.setTitle("Income vs. Expenses");
		stage.setScene(scene);
		stage.show();
    }
    
    private void populateTableItems() throws IOException {
        transactions.addAll(transactionService.getByUserId(UserSession.getInstance().getUser().getId()));
        
        tableView.setRowFactory(new Callback<TableView<Transaction>, TableRow<Transaction>>() {
            @Override
            public TableRow<Transaction> call(TableView<Transaction> tableView) {
                final TableRow<Transaction> row = new TableRow<Transaction>() {
                    @Override
                    protected void updateItem(Transaction data, boolean empty) {
                        super.updateItem(data, empty);
                        if (data != null && data.getCategory() != null) {
                            switch (data.getCategory()) {
                                case SALARY:
                                    setStyle("-fx-text-background-color: green;");
                                    break;
                                case OTHER_INCOME:
                                    setStyle("-fx-text-background-color: green;");
                                    break;
                                case HOUSING:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case UTILITIES:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case GROCERIES:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case TRANSPORTATION:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case INSURANCE:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case HEALTH_WELLNESS:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                case ENTERTAINMENT:
                                    setStyle("-fx-text-background-color: red;");
                                    break;
                                // Add more cases if you have more categories
                                default:
                                    setStyle("-fx-text-background-color: black;");
                                    break;
                            }
                        } else {
                            setStyle("-fx-text-background-color: black;");
                        }
                    }
                };

                return row;
            }
        });


        tableView.setItems(transactions);
    }

    private void populateTransactionTypeOptions() {
    	categoryOptions.getItems().setAll(Transaction.Category.values());
    }

    private void setLoggedInLabelText() {
        lblUser.setText(String.format("Logged in as: %s", UserSession.getInstance().getUser().getUsername()));
    }

    private void setTotalLabelText() {
        float sum = transactions.stream().collect(Collectors.summingDouble(o -> o.getAmount())).floatValue();
        lblTotal.setText(String.format("Total: %.2f", sum));

        if (sum == 0) {
            lblTotal.setTextFill(Paint.valueOf("black"));
        } else if (sum > 0) {
            lblTotal.setTextFill(Paint.valueOf("green"));
        } else {
            lblTotal.setTextFill(Paint.valueOf("red"));
        }
    }

    private void clearInputs() {
        descriptionField.clear();
        amountField.clear();
        categoryOptions.getSelectionModel().clearSelection();
    }
}