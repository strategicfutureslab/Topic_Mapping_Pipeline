package input;

import IO.CSVHelper;
import IO.Console;
import IO.Timer;
import config.Project;
import config.modules.CorpusCSV;
import data.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Module generating a corpus from a CSV file
 *
 * @author P. Le Bras
 * @version 2
 */
public class CSVInput extends InputModule {

    private String sourceFile;
    private String outputFile;
    private HashMap<String, String> docFields;

    /**
     * Main module method - processes parameters, reads CSV file and write JSON corpus
     * @param moduleParameters
     * @param projectParameters
     * @throws IOException
     */
    public static void run(CorpusCSV moduleParameters, Project projectParameters) throws IOException {
        String MODULE_NAME = moduleParameters.moduleName+" ("+moduleParameters.moduleType+")";
        Console.moduleStart(MODULE_NAME);
        Timer.start(MODULE_NAME);
        CSVInput instance = new CSVInput();
        instance.processParameters(moduleParameters, projectParameters);
        try {
            instance.loadCSV();
            instance.writeJSON(instance.outputFile);
        } catch (Exception e) {
            Console.moduleFail(MODULE_NAME);
            throw e;
        }
        Console.moduleComplete(MODULE_NAME);
        Timer.stop(MODULE_NAME);
    }

    // processes project and module parameters
    private void processParameters(CorpusCSV moduleParameters, Project projectParameters){
        Console.log("Processing parameters");
        sourceFile = projectParameters.sourceDirectory+moduleParameters.source;
        outputFile = projectParameters.dataDirectory+moduleParameters.output;
        docFields = moduleParameters.fields;
        Console.tick();
        Console.info("Reading CSV input from "+sourceFile+" and saving to "+outputFile, 1);
    }

    // loads document data from CSV
    private void loadCSV() throws IOException {
        CSVHelper.ProcessCSVRow rowProcessor = (row, rowNum) -> {
            Document doc = new Document(Integer.toString(rowNum),rowNum);
            for(Map.Entry<String, String> entry: docFields.entrySet()){
                doc.addField(entry.getKey(), row.getField(entry.getValue()));
            }
            documents.put(doc.getId(), doc);
        };
        try {
            CSVHelper.loadCSVFile(sourceFile, rowProcessor);
        } catch (IOException e) {
            Console.error("Error while reading the CSV input");
            throw e;
        } finally {
            Console.note("Number of documents loaded from file: "+documents.size());
        }
    }
}
