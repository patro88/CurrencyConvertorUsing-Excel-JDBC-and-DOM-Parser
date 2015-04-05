package appLayer;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import service.Services;

public class CurrencyConversion {

	public static void main(String[] args) {

		//Front end Display
		/*Select one of the following:
		e - exchange rate
		y - list years for country
		c - list countries
		x - export options
		q - quit*/
		String choice = "";
		String fileName = "/Users/Shakti/Desktop/QlikProject/InputFiles/Qlik-DataMgmt-ExchangeRates.xls";
		String csvFileName = "/Users/Shakti/Desktop/QlikProject/InputFiles/Qlik-DataMgmt-CountryCurrencyCodes.csv";
		Services serv = new Services();
		do{
			System.out.println("Select one of the following: ");
			System.out.println("e - exchange rate");
			System.out.println("y - list years for country");
			System.out.println("c - list countries");
			System.out.println("x - export options");
			System.out.println("q - quit");
			Scanner sc = new Scanner(System.in);
			choice = sc.nextLine();
			switch(choice){
			
			case "e" :  System.out.print("Enter the year:");
			sc = new Scanner(System.in);
			String yr = sc.nextLine();
			System.out.print("Enter the country name:");
			sc = new Scanner(System.in);
			String cn = sc.nextLine();
			if(yr.matches("^\\d+$")){
				Double rate = serv.exchangeRate(cn, fileName, Integer.parseInt(yr));
				if(null != rate){
					System.out.println("The exchange rate for "+cn+" in "+ yr +" was "+rate);
					String code = serv.getCode(cn, csvFileName);
					if(code == null){
						code = "the country�s currency code";
					}

					System.out.print("Convert from USD to "+code+" ? (y/n)");
					sc = new Scanner(System.in);
					String ch = sc.nextLine();
					if( ch.equalsIgnoreCase("N")){
						break;
					}else if (ch.equalsIgnoreCase("Y")){
						System.out.print("Enter amount in USD:");
						sc = new Scanner(System.in);
						Double amount = Double.parseDouble(sc.nextLine());
						DecimalFormat df = new DecimalFormat("#.##");
						System.out.println("The converted currency amount in " + code+ " is "+ df.format(amount * rate));
					}else{
						System.err.println("Your input is not valid. Try again.");
					}

				}else{
					System.err.println("Please enter proper input values(Country).");
				}

			}else{
				System.err.println("Please enter proper input values(Year).");
			}
			break;
			
			case "y" :  System.out.print("Enter the country name:");
			sc = new Scanner(System.in);
			String country = sc.nextLine();
			Set<Integer> years = serv.getYears(country, fileName);
			if(null!= years){
				for (Iterator iterator = years.iterator(); iterator
						.hasNext();) {
					System.out.println( iterator.next());

				}
			}else{
				System.err.println("Country Name Doesn't exist. Please try again.");
			}
			break;
			
			case "c" :  Set<String> countries = serv.getCountries(fileName);
			for (Iterator iterator = countries.iterator(); iterator
					.hasNext();) {
				System.out.println( iterator.next());

			}
			break;
			case "x" :  System.out.print("Group by year (y) or country (c)?");
			sc = new Scanner(System.in);
			String group = sc.nextLine();

			switch(group){
			case "c" : 	System.out.print("Enter the output file name:");
			sc = new Scanner(System.in);
			String exportFile = sc.nextLine();
			serv.exportCountryWise(exportFile, fileName, csvFileName);
			break;
			case "y" : 	System.out.print("Enter the output file name:");
			sc = new Scanner(System.in);
			String expFile = sc.nextLine();
			serv.exportYearWise(expFile, fileName, csvFileName);
			break;
			default : 	System.out.println("Your input is not valid."); 
			break;
			}
			break;
			case "q" :  System.exit(0);

			default	 :	System.out.println("Please enter a valid input.");
			break;
			}
		}while(!(choice.equals("q")));
	}
}
