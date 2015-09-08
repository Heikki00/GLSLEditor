package GLSLEditor.Layouts.MainLayout;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;


import java.net.URL;
import java.util.ResourceBundle;


public class MainLayoutController implements Initializable{

    @FXML
    public org.fxmisc.richtext.CodeArea mainCodeArea;
    public HBox activeFileBar;
    public MenuItem openMenuItem, saveMenuItem, saveAsMenuItem, closeMenuItem, newMenuItem, optionsMenuItem;


    @Override
    public void initialize(URL location, ResourceBundle resources) {




    }
}
