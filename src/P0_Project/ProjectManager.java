package P0_Project;

import PX_Data.JSONIOWrapper;
import org.json.simple.JSONObject;


public class ProjectManager {

    public boolean runInput;
    public ProjectInput input;
    public boolean runLemmatise;
    public ProjectLemmatise lemmatise;
    public boolean runModel;
    public ProjectModel model;
    public boolean runLabelIndex;
    public ProjectLabelIndex labelIndex;

    public ProjectManager(String projectFile){
        JSONObject projectSpec = JSONIOWrapper.LoadJSON(projectFile);
        getRuns((JSONObject) projectSpec.get("run"));
        getSpecs(projectSpec);
    }

    private void getRuns(JSONObject specs){
        runInput = (boolean) specs.get("input");
        runLemmatise = (boolean) specs.get("lemmatise");
        runModel = (boolean) specs.get("model");
        runLabelIndex = (boolean) specs.get("labelIndex");
    }

    private void getSpecs(JSONObject projectSpec){
        if(runInput){
            input = new ProjectInput();
            input.getSpecs((JSONObject) projectSpec.get("input"));
        }
        if(runLemmatise){
            lemmatise = new ProjectLemmatise();
            lemmatise.getSpecs((JSONObject) projectSpec.get("lemmatise"));
        }
        if(runModel){
            model = new ProjectModel();
            model.getSpecs((JSONObject) projectSpec.get("model"));
        }
        if(runLabelIndex){
            labelIndex = new ProjectLabelIndex();
            labelIndex.getSpecs((JSONObject) projectSpec.get("labelIndex"));
        }
    }

}