package org.codice.testify.testParsers;

import org.codice.testify.objects.ParsedData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

@RunWith(JUnit4.class)
public class XmlTestCaseParserTest {
    //Set objects
    private final String currentDir = System.getProperty("user.dir");
    private final XmlTestCaseParser xmlTestCaseParser = new XmlTestCaseParser();
    private ParsedData parsedData = null;

    @Test
    public void testXmlFileInput() {
        File file = new File (currentDir + "/src/test/resources/testcase1.xml");
        System.out.println(file.getAbsoluteFile());
        ParsedData parsedData = xmlTestCaseParser.parseTest(file);
        assert(parsedData.getAssertionBlock() != null);
        assert(parsedData.getRequest().getEndpoint().equals("https://google.com"));
        assert(parsedData.getRequest().getType().equals("JerseyTestProcessor"));
        assert(parsedData.getRequest().getTestBlock().entrySet().size() > 0);
    }

    @Test
    public void testNoParamsTest() {
        File file = new File(currentDir + "/src/test/resources/testcase2.xml");
        System.out.println(file.getAbsoluteFile());
        xmlTestCaseParser.parseTest(file);
    }
}
