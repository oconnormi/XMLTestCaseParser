package org.codice.testify.testParsers;

import com.thoughtworks.xstream.mapper.Mapper;
import org.codice.testify.objects.ActionData;
import org.codice.testify.objects.ParsedData;
import org.codice.testify.objects.Request;
import org.codice.testify.objects.TestifyLogger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class XmlTestCaseParser implements BundleActivator, TestParser {

    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder = null;
    private Document document = null;

    @Override
    public ParsedData parseTest(File file) {

        TestifyLogger.debug("Running " + this.getClass().getSimpleName(), this.getClass().getSimpleName());

        // Set up document builder to extract xml elements
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) { TestifyLogger.error("Error creating document builder " + e, this.getClass().getSimpleName()); }

        try {
            document = builder.parse(file);
        } catch (SAXException e) { TestifyLogger.error("Error parsing xml input " + e, this.getClass().getSimpleName());
        } catch (IOException e) { TestifyLogger.error("Error reading xml input " + e, this.getClass().getSimpleName()); }

        // Get Endpoint and Type
        String endpoint = document.getElementsByTagName("endpoint").item(0).getTextContent().trim();
        String type = document.getElementsByTagName("type").item(0).getTextContent().trim();

        TestifyLogger.debug("Endpoint contains: " + endpoint, this.getClass().getSimpleName());
        TestifyLogger.debug("Type contains: " + type, this.getClass().getSimpleName());

        // Get Action Blocks
        String preTestSetterAction = null;
        String preTestProcessorAction = null;
        String postTestProcessorAction = null;
        try {
            preTestSetterAction = document.getElementsByTagName("preTestSetterAction").item(0).getTextContent().trim();
        } catch (NullPointerException e) {
            TestifyLogger.debug("No PreTestSetterActions Found", this.getClass().getSimpleName());
        }
        try {
            preTestProcessorAction = document.getElementsByTagName("preTestProcessorAction").item(0).getTextContent().trim();
        } catch (NullPointerException e) {
            TestifyLogger.debug("No PreTestProcessorActions Found", this.getClass().getSimpleName());
        }
        try {
            postTestProcessorAction = document.getElementsByTagName("postTestProcessorAction").item(0).getTextContent().trim();
        } catch (NullPointerException e) {
            TestifyLogger.debug("No PostTestProcessorActions Found", this.getClass().getSimpleName());
        }

        TestifyLogger.debug("PreTestSetterActions: " + preTestSetterAction, this.getClass().getSimpleName());
        TestifyLogger.debug("PreTestProcessorActions: " + preTestProcessorAction, this.getClass().getSimpleName());
        TestifyLogger.debug("PostTestProcessorActions: " + postTestProcessorAction, this.getClass().getSimpleName());

        // Get Assertions
        String assertions = null;
        try {
            assertions = document.getElementsByTagName("assertions").item(0).getTextContent().trim();
        } catch (NullPointerException e) {
            TestifyLogger.debug("No Assertions Found", this.getClass().getSimpleName());
        }

        TestifyLogger.debug("Assertions: " + assertions, this.getClass().getSimpleName());

        // Get test block
        NodeList testNodeList = document.getElementsByTagName("test");
        Node testNode = testNodeList.item(0);
        HashMap<String, String> testMap = new HashMap<>();

        if (testNode.getNodeType() == Node.ELEMENT_NODE) {
            testMap.put(testNode.getNodeName(), testNode.getTextContent());
            for (int i = 0; i < testNode.getChildNodes().getLength(); i++) {
                Node node = testNode.getChildNodes().item(i);
                if (node.getNodeName() != "#text") {
                    testMap.put(node.getNodeName(), node.getTextContent().trim());
                }
            }
        }

        Request request = new Request(type, endpoint, testMap);

        ActionData actionData = new ActionData(preTestSetterAction, preTestProcessorAction, postTestProcessorAction);

        return new ParsedData(request, assertions, actionData);
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception {

    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {

    }
}
