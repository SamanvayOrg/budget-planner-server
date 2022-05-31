package org.mbs.budgetplannerserver.contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BudgetContract {


	public static BudgetContract dummy() {
		BudgetContract budgetContract = new BudgetContract();
		budgetContract.setBudgetYear("2021-22");
		budgetContract.setHeadings(Arrays.asList("Sr.No.", "Account Head", "Account Code", "For the year 2016-17", "For the year 2017-18", "For the year 2018-19", "For 8 Months from April 2019 to Nov 2019(Actual)", "For 4 months from Dec 19 To March 2020(Estimated)", "Total 2019/20(Estimated)", "Estimate of Next year 2020-21"));
		budgetContract.setOpeningBalance(Arrays.asList("OPENING BALANCE (आरंभीची शिल्लक)", " ", " ", "5,61,04,828", "43,28,77,975", "34,86,63,631", "37,69,82,428", "24,05,80,263", "37,69,82,428", "40,34,77,428"));
		budgetContract.setNpTax(Arrays.asList("A ", "नगरपरीषद कर", " ", " ", " ", " ", " ", " ", " ", " "));
		budgetContract.setPropTax(Arrays.asList("1", "मालमत्ता कर", "910-1110", "3,24,33,177", "3,18,41,926", "2,13,26,825", "1,31,99,889", "2,68,00,111", "4,00,00,000", "5,00,00,000"));
		budgetContract.setFireTax(Arrays.asList("2", "अग्निशामक कर", "916-1110", "33,22,015", "31,31,056", "18,90,893", "11,69,830", "18,30,170", "30,00,000", "45,00,000"));
		budgetContract.setCleanTax(Arrays.asList("3", "विशेष स्वच्छता कर", "943-1190", "", "", "", "", "", "", ""));
		budgetContract.setTheaterTax(Arrays.asList("4", "सिनेमा कर", "920-1120", "12,810.00", "", "100.00", "", "10,000", "10000", "15000"));
		budgetContract.setPenalty(Arrays.asList("5", "पेनल्टी", "049-1790", "14,91,065", "11,00,704", "3,88,770", "96,054", "9,03,946", "10,00,000", "1000000"));
		budgetContract.setWaterTax(Arrays.asList("6", " विशेष पाणीपट्टी  ", "912-1119", "1,03,59,918", "1,24,83,518", "58,86,988", "54,36,752", "95,63,248", "1,50,00,000", "2,75,00,000"));
		budgetContract.setAdvTax(Arrays.asList("7", " जाहीरात कर ", "920-1120", "4,31,031", "88,520", "64,470", "58,270", "4,41,730", "5,00,000", "5,00,000"));
		budgetContract.setWasteTax(Arrays.asList("8", " विशेष घनकचरा व्यवस्थापन कर  ", "944-1190", "15,27,454", "15,99,305", "13,68,978", "9,75,916", "15,24,084", "25,00,000", "30,00,000"));
		budgetContract.setMailaTax(Arrays.asList("9", "  मैला व्यवस्थापन कर  ", "944-1190", "24,99,314", "33,49,667", "32,08,799", "17,82,148", "27,17,852", "45,00,000", "45,00,000"));
		budgetContract.setTreeTax(Arrays.asList("10", " वृक्ष कर ", "948-1140", "4,14,910", "3,91,243", "2,36,996", "1,48,200", "3,51,800", "5,00,000", "7,50,000"));
		budgetContract.setTotalA(Arrays.asList(" ", "  एकूण (A) ", "", "5,24,91,694", "5,39,85,939", "3,43,72,819", "2,28,67,059", "4,41,42,941", "6,70,10,000", "9,17,65,000"));
		return budgetContract;

	}


	private String budgetYear;
	private List<String> headings = new ArrayList<String>();
	private List<String> openingBalance = new ArrayList<>();
	private List<String> npTax = new ArrayList<>();
	private List<String> propTax = new ArrayList<>();
	private List<String> fireTax = new ArrayList<>();
	private List<String> cleanTax = new ArrayList<>();
	private List<String> theaterTax = new ArrayList<>();
	private List<String> penalty = new ArrayList<>();
	private List<String> waterTax = new ArrayList<>();
	private List<String> advTax = new ArrayList<>();
	private List<String> wasteTax = new ArrayList<>();
	private List<String> mailaTax = new ArrayList<>();
	private List<String> treeTax = new ArrayList<>();
	private List<String> totalA = new ArrayList<>();

	public List<String> getTotalA() {
		return totalA;
	}

	public void setTotalA(List<String> totalA) {
		this.totalA = totalA;
	}

	public List<String> getPropTax() {
		return propTax;
	}

	public void setPropTax(List<String> propTax) {
		this.propTax = propTax;
	}

	public List<String> getFireTax() {
		return fireTax;
	}

	public void setFireTax(List<String> fireTax) {
		this.fireTax = fireTax;
	}

	public List<String> getCleanTax() {
		return cleanTax;
	}

	public void setCleanTax(List<String> cleanTax) {
		this.cleanTax = cleanTax;
	}

	public List<String> getTheaterTax() {
		return theaterTax;
	}

	public void setTheaterTax(List<String> theaterTax) {
		this.theaterTax = theaterTax;
	}

	public List<String> getPenalty() {
		return penalty;
	}

	public void setPenalty(List<String> penalty) {
		this.penalty = penalty;
	}

	public List<String> getWaterTax() {
		return waterTax;
	}

	public void setWaterTax(List<String> waterTax) {
		this.waterTax = waterTax;
	}

	public List<String> getAdvTax() {
		return advTax;
	}

	public void setAdvTax(List<String> advTax) {
		this.advTax = advTax;
	}

	public List<String> getWasteTax() {
		return wasteTax;
	}

	public void setWasteTax(List<String> wasteTax) {
		this.wasteTax = wasteTax;
	}

	public List<String> getMailaTax() {
		return mailaTax;
	}

	public void setMailaTax(List<String> mailaTax) {
		this.mailaTax = mailaTax;
	}

	public List<String> getTreeTax() {
		return treeTax;
	}

	public void setTreeTax(List<String> treeTax) {
		this.treeTax = treeTax;
	}


	public List<String> getNpTax() {
		return npTax;
	}

	public void setNpTax(List<String> npTax) {
		this.npTax = npTax;
	}

	public List<String> getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(List<String> openingBalance) {
		this.openingBalance = openingBalance;
	}

	public List<String> getHeadings() {
		return headings;
	}

	public void setHeadings(List<String> headings) {
		this.headings = headings;
	}

	public String getBudgetYear() {
		return budgetYear;
	}

	public void setBudgetYear(String budgetYear) {
		this.budgetYear = budgetYear;
	}
}
