package com.quotation;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class Main {

	public static void main(String[] args) {
		Scanner inputScanner = new Scanner(System.in);
		System.out.println("Введите дату(dd/MM/yyyy) и код валюты, разделенные пробелом:");
		String request = inputScanner.nextLine();
		inputScanner.close();
		String[] arguments = request.split(" ");

		boolean argumentsIsCorrect;
		if (arguments.length == 2) {
			DateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			simpleDateFormat.setLenient(false);
			try {
				Date date = simpleDateFormat.parse(arguments[0]);
				String[] dateArray = arguments[0].split("/");
				argumentsIsCorrect = dateArray[0].matches("\\d{2}")
						&& dateArray[1].matches("\\d{2}")
						&& dateArray[2].matches("\\d{4}")
						&& date.compareTo(new Date()) <= 0
						&& arguments[1].matches("[A-Z]{3}");
			} catch (ParseException e) {
				argumentsIsCorrect = false;
			}
		} else {
			argumentsIsCorrect = false;
		}

		if (argumentsIsCorrect) {
			String dateString = arguments[0];
			String currencyCode = arguments[1];
			HttpResponse<String> quotationsResponse = Unirest.get("http://www.cbr.ru/scripts/XML_daily.asp")
					.queryString("date_req", dateString).asString();
			try {
				List<Quotation> result = FinancialInformationParser.parseQuotations(quotationsResponse.getBody());
				boolean currencyFound = false;
				for (Quotation quotation : result) {
					if (quotation.getCharCode().equals(currencyCode)) {
						currencyFound = true;
						System.out.println(quotation.getNominal() + " " + quotation.getName() + " = "
								+ quotation.getValue() + " Российских рубля");
						break;
					}
				}
				if (!currencyFound) {
					System.out.println("Котировка для данной валюты в заданный день не найдена");
				}
			} catch (ParserConfigurationException e) {
				System.err.println("Сбой парсинга информацию с сервера");
			} catch (SAXException e) {
				System.err.println("Ошибка при парсинге информации с сервера");
			} catch (IOException e) {
				System.err.println("Ошибка при парсинге информации с сервера");
			} catch (TransformerException e) {
				System.err.println("Сбой парсинга информацию с сервера");
			}
		} else {
			System.out.println("Неверно введены данные.");
			System.out.println("Проверьте корректность даты(она должна быть меньше или равна сегодняшней)");
			System.out.println("и кода валюты.");
			System.out.println("Правильный формат данных: (dd/MM/yyyy) (код валюты)");
			System.out.println("Например: 01/08/2020 USD");
		}
	}
}
