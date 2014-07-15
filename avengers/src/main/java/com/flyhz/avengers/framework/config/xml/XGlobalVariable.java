
package com.flyhz.avengers.framework.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for xGlobalVariable complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="xGlobalVariable">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="numCrawlThreads" type="{http://www.w3.org/2001/XMLSchema}unsignedShort"/>
 *         &lt;element name="numFetchContainers" type="{http://www.w3.org/2001/XMLSchema}unsignedShort"/>
 *         &lt;element name="numFetchThreads" type="{http://www.w3.org/2001/XMLSchema}unsignedShort"/>
 *         &lt;element name="numAnalyzeThreads" type="{http://www.w3.org/2001/XMLSchema}unsignedShort"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xGlobalVariable", propOrder = {

})
public class XGlobalVariable {

	@XmlElement(defaultValue = "3")
	@XmlSchemaType(name = "unsignedShort")
	protected int	numCrawlThreads;
	@XmlElement(defaultValue = "10")
	@XmlSchemaType(name = "unsignedShort")
	protected int	numFetchContainers;
	@XmlElement(defaultValue = "3")
	@XmlSchemaType(name = "unsignedShort")
	protected int	numFetchThreads;
	@XmlElement(defaultValue = "3")
	@XmlSchemaType(name = "unsignedShort")
	protected int	numAnalyzeThreads;

	/**
	 * Gets the value of the numCrawlThreads property.
	 * 
	 */
	public int getNumCrawlThreads() {
		return numCrawlThreads;
	}

	/**
	 * Sets the value of the numCrawlThreads property.
	 * 
	 */
	public void setNumCrawlThreads(int value) {
		this.numCrawlThreads = value;
	}

	/**
	 * Gets the value of the numFetchContainers property.
	 * 
	 */
	public int getNumFetchContainers() {
		return numFetchContainers;
	}

	/**
	 * Sets the value of the numFetchContainers property.
	 * 
	 */
	public void setNumFetchContainers(int value) {
		this.numFetchContainers = value;
	}

	/**
	 * Gets the value of the numFetchThreads property.
	 * 
	 */
	public int getNumFetchThreads() {
		return numFetchThreads;
	}

	/**
	 * Sets the value of the numFetchThreads property.
	 * 
	 */
	public void setNumFetchThreads(int value) {
		this.numFetchThreads = value;
	}

	/**
	 * Gets the value of the numAnalyzeThreads property.
	 * 
	 */
	public int getNumAnalyzeThreads() {
		return numAnalyzeThreads;
	}

	/**
	 * Sets the value of the numAnalyzeThreads property.
	 * 
	 */
	public void setNumAnalyzeThreads(int value) {
		this.numAnalyzeThreads = value;
	}

}
