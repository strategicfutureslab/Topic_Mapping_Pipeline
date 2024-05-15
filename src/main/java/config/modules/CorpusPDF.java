package config.modules;

import IO.ProjectConfig;
import config.Module;

import java.util.HashMap;

/**
 * Class for parsing and storing PDF corpus input parameters
 */
public class CorpusPDF extends Module {

    private static final String[] MANDATORY_PARAMS = new String[]{"source","output"};

    /** Filename of the source CSV file */
    public final String source;
    /** Filename of the output corpus JSON file */
    public final String output;

    /**
     * Constructor, parses and stores module parameters
     * @param name Module name as described in the YAML config file
     * @param moduleParams Map of unparsed YAML parameters
     * @throws ProjectConfig.ParseException If the configuration does not include all mandatory parameters or if a parameter is not found
     */
    public CorpusPDF(String name, HashMap<String, Object> moduleParams) throws ProjectConfig.ParseException{
        super(name, "corpusPDF");
        for(String p: MANDATORY_PARAMS){
            if(!moduleParams.containsKey(p)) throw new ProjectConfig.ParseException("Module of type \""+moduleType+"\" must have a \""+p+"\" parameter");
        }
        source = getStringParam("source", moduleParams);
        output = getStringParam("output", moduleParams);
    }
}