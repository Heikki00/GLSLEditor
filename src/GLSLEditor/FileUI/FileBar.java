package GLSLEditor.FileUI;

import GLSLEditor.Document;
import GLSLEditor.Editor;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

//Represents the bar between the CodeArea and the menubar. Handles the list of tabs, the selection of tabs, and pretty much all things tabs'
public class FileBar {

    //The UI element
    private HBox hbox;
    //A list of all the tabs in the bar
    private List<FileTab> tabs;
    //The selected tab
    private FileTab selectedTab;
    private Editor editor;

    public FileBar(HBox box, Editor editor){
        hbox = box;
        tabs = new ArrayList<>();
        this.editor = editor;


    }




    //A function that selects a tab. Called form Editor.select, so PLEASE do not call this to select tabs
    public void selectTab(FileTab tab){
        //If null or tha tab is not yet added to the bar(shouldn't happen), throw an exception
        if(tab == null || !tabs.contains(tab)){
            throw new IllegalArgumentException("Tried to select a tab that Isn't valid");

        }

        //If the tab is already selected, just return
        if(selectedTab != null && selectedTab.equals(tab)) return;

        //Select the new and unselect the old
        tab.select();
        if(selectedTab != null)selectedTab.unselect();

        selectedTab = tab;



    }

    public FileTab getSelectedTab(){
        return selectedTab;
    }

    //Adds a tab to the bar
    public void addTab(FileTab tab){
        //If the tab was already here
        if(tabs.contains(tab)){
            throw new IllegalArgumentException("ERROR: Tried to add tab to the filebar second time");
        }

        //If the document was already here("New Document", does not count!)
        for(FileTab t : tabs){
            if(t.getDocument().getFilename().equals(tab.getDocument().getFilename()) && tab.getDocument().isFile()){
                throw new IllegalArgumentException("ERROR: Tried to add a document to the filebar second time");
            }

        }

        hbox.getChildren().add(tab.toNode());
        tabs.add(tab);
    }

    //Amount of tabs
    public int getNumTabs(){
        return tabs.size();
    }

    //Index of the tab. Starts from 0, returns -1 if the tab was not found
    public int getTabIndex(FileTab tab){
        for(int i = 0; i < tabs.size(); ++i){
            if(tabs.get(i).equals(tab)) return i;

        }
        return -1;
    }

    public FileTab getByIndex(int i){
        return tabs.get(i);
    }

    //Removes a tab from the FileBar. Call Editor.removeTab to remove tab
    public void removeTab(FileTab tab){

        if(tab == null || !tabs.contains(tab)){
            throw new IllegalArgumentException("ERROR: Tried to remove invalid FileTab from FileBar");
        }

        //If removing a currenty selected tab(the most likely) and there are more than one tab
        if(tab.equals(selectedTab) && tabs.size() > 1){

                //Select another tab
                if(getTabIndex(tab) != 0)editor.selectTab(getByIndex(getTabIndex(tab) - 1));
                else editor.selectTab(getByIndex(1));

            }

        if(tabs.size() == 1) selectedTab = null;

        //Remove the tab from the list and UI
        hbox.getChildren().remove(tab.toNode());
        tabs.remove(tab);
    }

    //Remove a tab by name(calls the "by tab" version)
    public void removeTab(String name){
        for(FileTab tab : tabs){
            if(tab.getName().equals(name)){
               removeTab(tab);
                return;
            }

        }
    }

    //Remove a tab by document(calls the "by tab" version)
    public void removeTab(Document document){
        removeTab(document.getName());
    }

    //Returns a tab by name, or null if the tab was not found
    public FileTab getTab(String name){
        for(FileTab t : tabs){
            if(t.getName().equals(name)){
                return t;
            }

        }
        return null;
    }

    //Returns a tab by document
    public FileTab getTab(Document document){return getTab(document.getName());}

    //Does the bar contain this tab?
    public boolean hasTab(FileTab tab){
        return tabs.contains(tab);
    }

    public boolean hasTab(String name){
        for(FileTab tab : tabs){
            if(tab.getName().equals(name)) return true;
        }

        return false;
    }

    public boolean hasTab(Document document){
        return hasTab(document.getName());
    }



    public HBox getBar(){
        return hbox;
    }



}
