/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lqv.hotelapp;

import com.lqv.pojo.Employee;
import com.lqv.service.EmployeeService;
import com.lqv.service.JdbcUtils;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class ManagementController implements Initializable {

    @FXML
    private void switchToManagement() throws IOException {
        App.setRoot("management");
    }

    @FXML
    private void switchToRooms() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void switchToRule() throws IOException {
        App.setRoot("rule");
    }

    @FXML
    private void switchToOrderView() throws IOException {
        App.setRoot("orderView");
    }

    @FXML
    private void logOut() throws IOException {
        App.setRoot("login");
    }

    @FXML
    private Text textRole;
    @FXML
    private Text textNameEmp;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtMail;
    @FXML
    private TableView tbEmployee;
    @FXML
    private TextField txtKeywords;

    private int empId;
    private String error;
    int i;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

//        Hi???n th??? vai tr?? c???a ng?????i d??ng hi???n th???i
        this.textRole.setText(App.getEmp().getRole());
        this.textNameEmp.setText(App.getEmp().getName());

//        L???y d??? li???u khi t???i scene
        loadColumns();
        loadData("");

//        C???p nh???p TableView khi g?? t??? kh??a t??n nh??n vi??n
        this.txtKeywords.textProperty().addListener((obj) -> {
            loadData(this.txtKeywords.getText());
        });

//         L???y gi?? tr??? Object room khi click ch???n
        this.tbEmployee.setRowFactory(obj -> {
            TableRow row = new TableRow();

            row.setOnMouseClicked(evt -> {
                Employee e = (Employee) this.tbEmployee.getSelectionModel().getSelectedItem();

                if (e.getPhone() == null) {
                    txtPhone.setText("");
                } else {
                    txtPhone.setText(e.getPhone().toString());
                }
                if (e.getEmail() == null) {
                    txtMail.setText("");
                } else {
                    txtMail.setText(e.getEmail().toString());

                }
                txtName.setText(e.getName());
                empId = e.getId();
            });
            return row;
        });
//        System.out.println("ket qua: " + this.tbEmployee.getItems());
//        i = 1;
//        System.out.println("phan tu:" + i++);
    }

    private void loadColumns() {
        TableColumn colId = new TableColumn("M?? nh??n vi??n");
        colId.setCellValueFactory(new PropertyValueFactory("id"));

        TableColumn colName = new TableColumn("T??n nh??n vi??n");
        colName.setPrefWidth(90);
        colName.setCellValueFactory(new PropertyValueFactory("name"));

        TableColumn colPhone = new TableColumn("S??? ??i???n tho???i");
        colPhone.setPrefWidth(90);
        colPhone.setCellValueFactory(new PropertyValueFactory("phone"));

        TableColumn colEmail = new TableColumn("Email");
        colEmail.setPrefWidth(120);
        colEmail.setCellValueFactory(new PropertyValueFactory("email"));

        TableColumn colRole = new TableColumn("Ch???c v???");
        colRole.setPrefWidth(100);
        colRole.setCellValueFactory(new PropertyValueFactory("role"));

        TableColumn colUser = new TableColumn("T??i kho???n");
        colUser.setPrefWidth(100);
        colUser.setCellValueFactory(new PropertyValueFactory("username"));

//        --------------------------------
        TableColumn colAcc = new TableColumn();
        colAcc.setCellFactory((obj) -> {
            Button btn = new Button("T??i kho???n");

            btn.setOnAction(evt -> {
                this.handleChoose(evt);
            });
            TableCell cell = new TableCell();
            cell.setGraphic(btn);
            return cell;
        });

//        //      -------------------------------
        TableColumn colAction = new TableColumn();
        colAction.setCellFactory((obj) -> {
            Button btn = new Button("X??a");

            btn.setOnAction(evt -> {
                Utils.getAlertBox("B???n ch???c ch???n x??a kh??ng?", Alert.AlertType.CONFIRMATION)
                        .showAndWait().ifPresent(bt -> {
                            if (bt == ButtonType.OK) {
                                try {
                                    TableCell cell = (TableCell) ((Button) evt.getSource()).getParent();
                                    Employee e = (Employee) cell.getTableRow().getItem();

                                    try ( Connection conn = JdbcUtils.getConn()) {
                                        EmployeeService s = new EmployeeService(conn);
                                        if (s.deleteEmp(e.getId()) == true) {
                                            Utils.getAlertBox("SUCCESSFUL", Alert.AlertType.INFORMATION).show();
                                            this.loadData("");
                                        } else {
                                            Utils.getAlertBox("FAILED", Alert.AlertType.ERROR).show();
                                        }
                                    }

                                } catch (SQLException ex) {
                                    Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
            });

            TableCell cell = new TableCell();
            cell.setGraphic(btn);
            return cell;
        });

        this.tbEmployee.getColumns().addAll(colId, colName, colPhone, colEmail, colRole, colAcc, colUser, colAction);
    }

    private void loadData(String kw) {
        try {
            Connection conn = JdbcUtils.getConn();
            EmployeeService e = new EmployeeService(conn);
            List<Employee> emps = e.getEmployees(kw);
            this.tbEmployee.setItems(FXCollections.observableList(emps));
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addEmp() {
        Employee e = new Employee();
        e.setName(txtName.getText());
        e.setPhone(txtPhone.getText());
        e.setEmail(txtMail.getText());

        if (e.getPhone().length() < 1) {
            error = "B???n ph???i nh???p s??? ??i???n tho???i";
        }
        if (e.getName().length() < 1) {
            error = "B???n ph???i nh???p t??n nh??n vi??n";
        }

        Connection conn;
        try {
            conn = JdbcUtils.getConn();
            EmployeeService s = new EmployeeService(conn);
            if (s.addEmp(e) == true) {
                Utils.getAlertBox("SUCCESSFUL", Alert.AlertType.INFORMATION).show();
                loadData("");
            } else {
                Utils.getAlertBox(error, Alert.AlertType.WARNING).show();
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void updateEmp() {
        Employee e = new Employee();
        e.setName(txtName.getText());
        e.setPhone(txtPhone.getText());
        e.setEmail(txtMail.getText());

//        ??i???u ki???n t???o h??? s?? cho nh??n vi??n
        if (e.getPhone().length() < 1) {
            error = "B???n ph???i nh???p s??? ??i???n tho???i";
        }
        if (e.getName().length() < 1) {
            error = "B???n ph???i nh???p t??n nh??n vi??n";
        }

//        ??i???u ki???n ch???nh s???a Account cho nh??n vi??n (ch??? nh??n vi??n ???? c?? h??? s?? tr?????c ???? m???i ???????c t???o t??i kho???n)
        Connection conn;
        try {
            conn = JdbcUtils.getConn();

            EmployeeService s = new EmployeeService(conn);
            if (s.updateEmp(e, empId) == true) {
                Utils.getAlertBox("SUCCESSFUL", Alert.AlertType.INFORMATION).show();
                loadData("");
            } else {
                Utils.getAlertBox(error, Alert.AlertType.WARNING).show();
            }

            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("bao loi: " + error);
        error = "";
    }

    @FXML
    private void handleChoose(ActionEvent evt) {
        Employee e = new Employee();
        TableCell cell = (TableCell) ((Button) evt.getSource()).getParent();
        Employee emp = (Employee) cell.getTableRow().getItem();

        FXMLLoader Loader = new FXMLLoader();
        Loader.setLocation(getClass().getResource("createAcc.fxml"));
        try {
            Loader.load();
        } catch (IOException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        CreateAccController display = Loader.getController();

//      L???y t??n ph??ng truy???n v??o txtRoom
        display.setEmp(emp);

//      L???y th??ng tin object room
        display.getEmpId(emp);

        Parent p = Loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(p));

//        thao t??c xong s??? load l???i trang
        stage.showAndWait();
        loadData("");
    }

}
