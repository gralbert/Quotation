package com.quotation;

public class Quotation {
	private String mNominal;
	private String mCharCode;
	private String mName;
	private String mValue;
	
	public String getNominal() {
		return mNominal;
	}
	public void setNominal(String nominal) {
		mNominal = nominal;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		mName = name;
	}
	public String getValue() {
		return mValue;
	}
	public void setValue(String value) {
		mValue = value;
	}
	public String getCharCode() {
		return mCharCode;
	}
	public void setCharCode(String charCode) {
		mCharCode = charCode;
	}
}
