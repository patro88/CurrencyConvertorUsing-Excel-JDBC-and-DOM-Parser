package service;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dao.DataAccess;

public class Services {

	//for option c
	public Set<String> getCountries(String fileName){
		//Create dao object
		DataAccess dao = new DataAccess();

		//Retireve the list of countries from the Map countryMap
		return dao.excelRead(fileName).keySet();
	}

	//for option y
	public Set<Integer> getYears(String country, String fileName){
		//Create dao object
		DataAccess dao = new DataAccess();

		//Retireve the list of countries from the Map countryMap
		Map<Integer, Double> temp =  dao.excelRead(fileName).get(country);
		if(temp != null){
			return temp.keySet();
		}else{
			return null;
		}
	}

	//for option e
	public Double exchangeRate(String country, String fileName, Integer year){
		//Create dao object
		DataAccess dao = new DataAccess();
		Double rate = null;
		//Retireve the list of countries from the Map countryMap
		Map<Integer, Double> temp =  dao.excelRead(fileName).get(country);
		if(temp != null){
			rate = temp.get(year);
		}
		return rate;
	}				

	//get currency code
	public String getCode(String country, String fileName){
		//Create dao object
		DataAccess dao = new DataAccess();

		//Retireve the list of countries from the Map countryMap
		return dao.parseCSV(fileName).get(country);
	}


	//countrywise xml data
	public void exportCountryWise(String xmlFile, String xlsFile, String csvFile){
		try
		{
			DataAccess dao = new DataAccess();
			Map<String, Map<Integer, Double>> countryMap = dao.excelRead(xlsFile);
			Map<String, String> currencyMap = dao.parseCSV(csvFile);

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);
			Element rootElement = doc.createElement("CurrencyConversions");
			doc.appendChild(rootElement);

			for (Map.Entry<String, Map<Integer, Double>> e : countryMap.entrySet()) {
				String country = e.getKey();
				String currencyCode = currencyMap.get(country);

				//country element
				Element c = doc.createElement("Country");
				rootElement.appendChild(c);

				// set attribute to country element
				Attr attr1 = doc.createAttribute("Name");
				attr1.setValue(country);
				c.setAttributeNode(attr1);

				Attr attr2 = doc.createAttribute("CurrencyCode");
				attr2.setValue(currencyCode);				
				c.setAttributeNode(attr2);

				//set child elements
				Map<Integer, Double> yearMap = e.getValue();
				for (Map.Entry<Integer, Double> y : yearMap.entrySet()) {
					Integer year = y.getKey();
					Double rate = y.getValue();

					//set year elements
					Element yr = doc.createElement("ConversionRate");
					yr.appendChild(doc.createTextNode(rate.toString()));
					c.appendChild(yr);

					//attribute of child element
					Attr attr = doc.createAttribute("Year");
					attr.setValue(year.toString());
					yr.setAttributeNode(attr);
				}
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("standalone","yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(xmlFile));
			transformer.transform(source, result);
			System.out.println("File saved!");
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}


	//yearwise xml data
	public void exportYearWise(String xmlFile, String xlsFile, String csvFile){
		try
		{
			DataAccess dao = new DataAccess();
			Map<String, Map<Integer, Double>> countryMap = dao.excelRead(xlsFile);
			Map<String, String> currencyMap = dao.parseCSV(csvFile);

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);
			Element rootElement = doc.createElement("CurrencyConversions");
			doc.appendChild(rootElement);


			Map<Integer, Double> yearMap = countryMap.get(countryMap.keySet().toArray()[0]);
			for (Map.Entry<Integer, Double> ymap : yearMap.entrySet()) {

				String currYear = ymap.getKey().toString();
				//element year
				Element yr = doc.createElement("Year");

				Attr attr = doc.createAttribute("Year");
				attr.setValue(currYear);
				yr.setAttributeNode(attr);
				rootElement.appendChild(yr);

				for (Map.Entry<String, Map<Integer, Double>> e : countryMap.entrySet()) {
					String country = e.getKey();
					String currencyCode = currencyMap.get(country);

					//element conversion					
					Element c = doc.createElement("ConversionRate");
					c.appendChild(doc.createTextNode(e.getValue().get(ymap.getKey()).toString()));					

					Attr a1 = doc.createAttribute("Name");
					a1.setValue(country);
					c.setAttributeNode(a1);

					Attr a2 = doc.createAttribute("CurrencyCode");
					a2.setValue(currencyCode);
					c.setAttributeNode(a2);

					yr.appendChild(c);
				}			
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("standalone","yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(xmlFile));
			transformer.transform(source, result);
			System.out.println("File saved!");
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
}
