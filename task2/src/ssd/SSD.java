package ssd;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class SSD {

    private static DocumentBuilderFactory documentBuilderFactory;
    private static DocumentBuilder documentBuilder;
    private static final String pathAuctionsXSD = "resources/auctions.xsd";
    private static final String pathBidXSD = "resources/auction-bid.xsd";

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
            System.err.println("Usage: java SSD <input.xml> <bid.xml> <output.xml>");
            System.exit(1);
        }
	
		String inputPath = args[0];
		String bidPath = args[1];
		String outputPath = args[2];
		
    
       initialize();
       transform(inputPath, bidPath, outputPath);
    }
	
	private static void initialize() throws Exception {    
       documentBuilderFactory = DocumentBuilderFactory.newInstance();
       documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }
	
	/**
     * Use this method to encapsulate the main logic for this example. 
     * 
     * First read in the auction house document. 
     * Second create a BiddingHandler and an XMLReader (SAX parser)
     * Third parse the bidDocument
     * Last get the Document from the BiddingHandler and use a
     *    Transformer to print the document to the output path.
     * 
     * @param inputPath Path to the xml file to get read in.
     * @param bidPath Path to the bid file
     * @param outputPath Path to the output xml file.
     */
    private static void transform(String inputPath, String bidPath, String outputPath) throws Exception {

        documentBuilderFactory.setValidating(true);
        documentBuilderFactory.setNamespaceAware(true);

        // Read in the data from the auction house xml document, created in example 1

        Document db = documentBuilder.newDocument();
        try {
            db = documentBuilder.parse(new File(inputPath));
            db.getDocumentElement().normalize();
        } catch (FileNotFoundException ex) {
            exit(ex.getMessage());
        }

        // Create an input source for the bid document

		Document bid = documentBuilder.newDocument();
        try {
            bid = documentBuilder.parse(new File(bidPath));
            bid.getDocumentElement().normalize();
        } catch (FileNotFoundException ex) {
            exit(ex.getMessage());
        }

        XMLReader parser = XMLReaderFactory.createXMLReader();
        BiddingHandler handler = new BiddingHandler(db);
        parser.setContentHandler(handler);

        // start the actual parsing

        handler.parse(bid);

        // Validate file before storing

        if (!isValid(bid.getDocumentURI(), pathBidXSD) || !handler.hasValidConstraints()) {
            exit("This auction ended or is expired.");
        }

        // get the document from the biddingHandler
        
		Document modifiedDb = handler.getDocument();
        
        // validate
        
		if (!isValid(modifiedDb.getDocumentURI(), pathAuctionsXSD)) {
            exit("Invalid XML \"auctions\" document.");
        }
        
        //store the document

        DOMSource source = new DOMSource(modifiedDb);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // dont mess up everything
        StreamResult result = new StreamResult(outputPath);
        transformer.transform(source, result);
		
    }

    /**
     * Prints an error message and exits with return code 1
     * 
     * @param message The error message to be printed
     */
    public static void exit(String message) {
    	System.err.println(message);
    	System.exit(1);
    }

    /**
     * True if given xml validates against given xsd
     * Dirty, but working fine.
     * @param xml: String
     * @param xsd: String
     * @return boolean
     */
    private static boolean isValid(String xml, String xsd) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    

}
