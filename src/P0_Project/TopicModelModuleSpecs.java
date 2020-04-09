package P0_Project;

import org.json.simple.JSONObject;

/**
 * Class for Topic Model module project specification
 */
public class TopicModelModuleSpecs {

    /** Filename to lemma data (from Lemmatise module) */
    public String lemmas;
    /** Which module to run: "simple" or "hierarchical" */
    public String module;
    /** Directory name in which to write the topic model data
     * (can be same as corpus or lemma, or a level lower to contain all files in one place) */
    public String outputDir;
    /** Model specifications for the topic model (main topic model if "hierarchical" module used) */
    public ModelSpecs mainModel;
    /** Model specifications for the sub topic model (if "hierarchical" module used) */
    public ModelSpecs subModel;
    /** Filename for the JSON document file generated, not including directory */
    public String documentOutput;
    /** Filename for the CSV similarity file, not including directory:
     * similarity between main and sub topic model if "hierarchical" module used,
     * optional, defaults to "" */
    public String similarityOutput;
    /** Flag for writing similarity between main and sub topic model if "hierarchical" module used,
     * defaults to false if similarityOuput = "" */
    public boolean outputSimilarity = false;
    /** Filename for the CSV assignment file, not including directory:
     * assignment between main and sub topics if "hierarchical" module used,
     * optional, defaults to "" */
    public String assignmentOutput;
    /** Flag for writing assignment between main and sub topics if "hierarchical" module used,
     * defaults to false if assignmentOutput = "" */
    public boolean outputAssignment = false;
    /** Maximum number of main topics assigned to a sub topic, if "hierarchical" module used,
     * optional, defaults to 1 */
    public int maxAssign;

    /**
     * Constructor: reads the specification from the "model" entry in the project file
     * @param specs JSON object attached to "model"
     */
    public TopicModelModuleSpecs(JSONObject specs){
        lemmas = (String) specs.get("lemmas");
        module = (String) specs.get("module");
        outputDir = (String) specs.get("outputDir");
        documentOutput = outputDir + specs.get("documentOutput");
        mainModel = new ModelSpecs((JSONObject) specs.get("mainModel"), outputDir);
        if(module.equals("hierarchical")){
            subModel = new ModelSpecs((JSONObject) specs.get("subModel"), outputDir);
            JSONObject hierarchySpecs = (JSONObject) specs.get("hierarchy");
            maxAssign = Math.toIntExact((long) hierarchySpecs.getOrDefault("maxAssign", (long) 1));
            similarityOutput = (String) hierarchySpecs.getOrDefault("modelSimOutput", "");
            if(!similarityOutput.equals("")){
                outputSimilarity = true;
                similarityOutput = outputDir+similarityOutput;
            }
            assignmentOutput = (String) hierarchySpecs.getOrDefault("assignmentOutput", "");
            if(!assignmentOutput.equals("")){
                outputAssignment = true;
                assignmentOutput = outputDir+assignmentOutput;
            }
        }
    }
}