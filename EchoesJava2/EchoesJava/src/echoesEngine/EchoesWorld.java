package echoesEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import FAtiMA.deliberativeLayer.plan.Step;
import FAtiMA.util.parsers.StripsOperatorsLoaderHandler;
import utils.Logger;
import utils.EchoesObject;

/**
 * EchoesWorld: represents the world for the purposes of FaTiMa.
 * 
 * Based on WorldTest.java from the FaTiMa distribution.
 * 
 * @author Mary Ellen Foster
 */
public class EchoesWorld {

    private ArrayList<EchoesAgent> agents;
    private ArrayList<EchoesObject> objects;
    private ArrayList<Step> actions;

    private void logError(String error, Exception ex) {
        Logger.LogError(error + ": " + ex.getMessage());
    }

    public EchoesWorld(String actionsFile, ArrayList<EchoesObject> objects) {
        this.agents = new ArrayList<EchoesAgent>();
        this.objects = objects;

        // Load the actions
        Logger.Log("info", "LOAD: " + actionsFile);
        StripsOperatorsLoaderHandler op = new StripsOperatorsLoaderHandler("[SELF]");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser;
        try {
            parser = factory.newSAXParser();
            parser.parse(new File(actionsFile), op);
        } catch (ParserConfigurationException e) {
            logError("Unable to load action file", e);
        } catch (SAXException e) {
            logError("Unable to load action file", e);
        } catch (IOException e) {
            logError("Unable to load action file", e);
        }
    }

    public void addAgent(EchoesAgent agent) {
        agents.add(agent);
    }
    
    public ArrayList<EchoesAgent> getAgents() {
        return agents;
    }
    
    public ArrayList<EchoesObject> getObjects() {
        return objects;
    }
    
    public ArrayList<Step> getActions() {
        return actions;
    }
}
