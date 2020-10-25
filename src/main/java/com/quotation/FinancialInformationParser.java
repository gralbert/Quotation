package com.quotation;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class FinancialInformationParser {
	public static List<Quotation> parseQuotations(String response) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		List<Quotation> quotationList = new LinkedList<Quotation>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		StringReader responseReader = new StringReader(response);
		Document midD = db.parse(new InputSource(responseReader));
		responseReader.close();

		Document d;
		if(midD.getXmlEncoding().equalsIgnoreCase("UTF-8")) {
			d = midD;
		} else {
			try {
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				StringWriter finalDocumentWriter = new StringWriter();
				transformer.transform(new DOMSource(midD.getDocumentElement()), new StreamResult(finalDocumentWriter));
				StringReader finalDocumentReader = new StringReader(finalDocumentWriter.toString());
				try {
					d = db.parse(new InputSource(finalDocumentReader));
				} catch(IOException e) {
					throw new SAXException("Невозможно распарсить данные о котировках");
				}
				finalDocumentWriter.close();
			} catch(IOException e) {
				throw new TransformerException("Сбой преобразования данных в кодировку UTF-8");
			}
		}

		NodeList currencies = d.getElementsByTagName("Valute");
		for(int i = 0; i < currencies.getLength(); i++) {
			Quotation quotation = new Quotation();
			Element currency = (Element) currencies.item(i);
			quotation.setCharCode(currency.getElementsByTagName("CharCode").item(0).getTextContent());
			quotation.setName(currency.getElementsByTagName("Name").item(0).getTextContent());
			quotation.setValue(currency.getElementsByTagName("Value").item(0).getTextContent());
			quotation.setNominal(currency.getElementsByTagName("Nominal").item(0).getTextContent());
			quotationList.add(quotation);
		}
		return quotationList;
	}
}
