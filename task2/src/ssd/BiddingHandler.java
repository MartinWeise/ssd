package ssd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BiddingHandler extends DefaultHandler {
	/**
	 * Use this xPath variable to create xPath expression that can be
	 * evaluated over the XML document.
	 */
	private static XPath xPath = XPathFactory.newInstance().newXPath();

	private Document auctionsDoc;
	private Document bid;
	private boolean created = false;
	
    public BiddingHandler(Document doc) {
    	auctionsDoc = doc;
    }

    /**
     * Return the current stored auctions document
     * @return XML Document
     */
	public Document getDocument() {
		return auctionsDoc;
	}

	/**
	 * Parses the bid document into the auctions document without actually saving it
	 * Side effects: auctionsDoc changes most likely
	 * @param bid: Document
	 * @throws XPathExpressionException
     */
	public void parse(Document bid) throws XPathExpressionException {
		this.bid = bid;
		Node root = auctionsDoc.getDocumentElement().getElementsByTagName("bids").item(0);

		XPathExpression xName = xPath.compile("//auctions/bids/product[@id=" + getProductId() + "]");
		Element productNode = (Element) xName.evaluate(this.auctionsDoc, XPathConstants.NODE);
		// not found => create one
		if (productNode == null) {
			this.created = true;
			productNode = auctionsDoc.createElement("product");
			productNode.setAttribute("id",""+getProductId());
		}

		Element bidNode = auctionsDoc.createElement("bid");
		bidNode.setAttribute("user",getNameValue());
		bidNode.setTextContent(""+getBidValue());

		productNode.appendChild(bidNode);
		productNode.normalize();
		if (this.created) {
			root.appendChild(productNode);
		} else {
		}
	}

	private String getNameValue() throws XPathExpressionException {
		XPathExpression xName = xPath.compile("//bid[@product]/user/text()");
		return (String) xName.evaluate(this.bid, XPathConstants.STRING);
	}

	private int getProductId() throws XPathExpressionException {
		XPathExpression xProduct = xPath.compile("//bid[@product]/@product");
		return Integer.parseInt((String) xProduct.evaluate(this.bid, XPathConstants.STRING));
	}

	private int getBidValue() throws XPathExpressionException {
		XPathExpression xProduct = xPath.compile("//bid[@product]/bid/text()");
		return Integer.parseInt((String) xProduct.evaluate(this.bid, XPathConstants.STRING));
	}

	public boolean hasValidConstraints() throws XPathExpressionException {

		// auction has not ended

		XPathExpression xProduct = xPath.compile("//auctions/products/product[@id=" + getProductId() + "]/@auctionEnd");
		String date = (String) xProduct.evaluate(this.auctionsDoc, XPathConstants.STRING);
		XPathExpression xProductExpired = xPath.compile("//auctions/products/product[@id=" + getProductId() + "]/expired");
		boolean expired = (boolean) xProductExpired.evaluate(this.auctionsDoc, XPathConstants.BOOLEAN);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date end = null;

		try {
			end = dateFormat.parse(date);
		} catch (ParseException e) {
			System.err.println("Product with ID " + getProductId() + " not found in auctions document.");
			System.exit(1);
			return false;
		}

		if (new Date().after(end) || expired) {
			return false;
		}

		// bid > current bids

		XPathExpression xHighestBid = xPath.compile("//bids/product[@id=" + getProductId() + "]/bid[last()-1]/text()");
		String hb = (String) xHighestBid.evaluate(this.auctionsDoc, XPathConstants.STRING);

		int highestBid = 0;
		if(hb.trim().length() > 0) {
			highestBid = Integer.parseInt(hb);
		}

		if (highestBid >= getBidValue()) {
			System.err.println("Please place a higher bid.");
			System.exit(1);
			return false;
		}

		// bid < balance of user

		XPathExpression xBalance = xPath.compile("//auctions/users/user[@username='" + getNameValue() + "']/balance/text()");
		String b = (String) xBalance.evaluate(this.auctionsDoc, XPathConstants.STRING);

		if (b.trim().length() < 1) {
			System.err.println("User \"" + getNameValue() + "\" not found in actions document.");
			System.exit(1);
			return false;
		}
		int balance = Integer.parseInt(b);

		if (balance < getBidValue()) {
			System.err.println("The balance of this user is not enough for placing this bid.");
			System.exit(1);
			return false;
		}

		return true;
	}
	
}

