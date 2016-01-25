package GLSLEditor.FileUI;

import GLSLEditor.Document;
import GLSLEditor.Editor;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;


public class FileBar {

    private HBox hbox;
    private List<FileTab> tabs;
    private FileTab selectedTab;
    private Editor editor;

    public FileBar(HBox box, Editor editor){
        hbox = box;
        tabs = new ArrayList<>();
        this.editor = editor;


    }





    public void selectTab(FileTab tab){
        if(tab == null || !tabs.contains(tab)){
            throw new IllegalArgumentException("Tried to select a tab that Isn't valid");

        }

        if(selectedTab != null && selectedTab.equals(tab)) return;

        tab.select();
        if(selectedTab != null)selectedTab.unselect();

        selectedTab = tab;



    }

    public FileTab getSelectedTab(){
        return selectedTab;
    }


    public void addTab(FileTab tab){
        if(tabs.contains(tab)){
            throw new IllegalArgumentException("ERROR: Tried to add tab to the filebar second time");
        }

        for(FileTab t : tabs){
            if(t.getDocument().getFilename().equals(tab.getDocument().getFilename()) && tab.getDocument().isFile()){
                throw new IllegalArgumentException("ERROR: Tried to add tab to the filebar second time");
            }

        }

        hbox.getChildren().add(tab.toNode());
        tabs.add(tab);
    }

    public int getNumTabs(){
        return tabs.size();
    }

    public int getTabIndex(FileTab tab){
        for(int i = 0; i < tabs.size(); ++i){
            if(tabs.get(i).equals(tab)) return i;

        }
        return -1;
    }

    public FileTab getByIndex(int i){
        return tabs.get(i);
    }


    public void removeTab(FileTab tab){
        if(tab == null || !tabs.contains(tab)){
            throw new IllegalArgumentException("ERROR: Tried to remove invalid FileTab from FileBar");
        }

        if(tab.equals(selectedTab)){
            if(tabs.size() > 1){
                if(getTabIndex(tab) != 0)editor.selectTab(getByIndex(getTabIndex(tab) - 1));
                else editor.selectTab(getByIndex(1));
            }
            else {
                editor.getCodeArea().disable();
            }
            }

        hbox.getChildren().remove(tab.toNode());
        tabs.remove(tab);
    }

    public void removeTab(String name){
        for(FileTab tab : tabs){
            if(tab.getName().equals(name)){
               removeTab(tab);
                return;
            }

        }
    }

    public void removeTab(Document document){
        removeTab(document.getName());
    }


    public FileTab getTab(String name){
        for(FileTab t : tabs){
            if(t.getName().equals(name)){
                return t;
            }

        }
        return null;
    }

    public FileTab getTab(Document document){return getTab(document.getName());}


    public boolean hasTab(FileTab tab){
        return tabs.contains(tab);
    }

    public boolean hasTab(String name){
        return getTab(name) != null;

    }

    public boolean hasTab(Document document){
        return hasTab(document.getName());
    }



    public HBox getBar(){
        return hbox;
    }



}
