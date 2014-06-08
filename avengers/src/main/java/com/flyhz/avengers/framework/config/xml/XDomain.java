
package com.flyhz.avengers.framework.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for xDomain complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="xDomain">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="urlFilterBeforeCrawl" type="{http://www.flyhz.com/avengrs}xFilter" minOccurs="0"/>
 *         &lt;element name="crawlEvents" type="{http://www.flyhz.com/avengrs}xEvents" minOccurs="0"/>
 *         &lt;element name="urlFilterAfterCrawl" type="{http://www.flyhz.com/avengrs}xFilter" minOccurs="0"/>
 *         &lt;element name="fetchEvents" type="{http://www.flyhz.com/avengrs}xEvents" minOccurs="0"/>
 *         &lt;element name="templates" type="{http://www.flyhz.com/avengrs}xTemplates" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="root" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xDomain", propOrder = {

})
public class XDomain {

	protected XFilter		urlFilterBeforeCrawl;
	protected XEvents		crawlEvents;
	protected XFilter		urlFilterAfterCrawl;
	protected XEvents		fetchEvents;
	protected XTemplates	templates;
	@XmlAttribute(required = true)
	@XmlSchemaType(name = "anySimpleType")
	protected String		root;

	/**
	 * Gets the value of the urlFilterBeforeCrawl property.
	 * 
	 * @return possible object is {@link XFilter }
	 * 
	 */
	public XFilter getUrlFilterBeforeCrawl() {
		return urlFilterBeforeCrawl;
	}

	/**
	 * Sets the value of the urlFilterBeforeCrawl property.
	 * 
	 * @param value
	 *            allowed object is {@link XFilter }
	 * 
	 */
	public void setUrlFilterBeforeCrawl(XFilter value) {
		this.urlFilterBeforeCrawl = value;
	}

	/**
	 * Gets the value of the crawlEvents property.
	 * 
	 * @return possible object is {@link XEvents }
	 * 
	 */
	public XEvents getCrawlEvents() {
		return crawlEvents;
	}

	/**
	 * Sets the value of the crawlEvents property.
	 * 
	 * @param value
	 *            allowed object is {@link XEvents }
	 * 
	 */
	public void setCrawlEvents(XEvents value) {
		this.crawlEvents = value;
	}

	/**
	 * Gets the value of the urlFilterAfterCrawl property.
	 * 
	 * @return possible object is {@link XFilter }
	 * 
	 */
	public XFilter getUrlFilterAfterCrawl() {
		return urlFilterAfterCrawl;
	}

	/**
	 * Sets the value of the urlFilterAfterCrawl property.
	 * 
	 * @param value
	 *            allowed object is {@link XFilter }
	 * 
	 */
	public void setUrlFilterAfterCrawl(XFilter value) {
		this.urlFilterAfterCrawl = value;
	}

	/**
	 * Gets the value of the fetchEvents property.
	 * 
	 * @return possible object is {@link XEvents }
	 * 
	 */
	public XEvents getFetchEvents() {
		return fetchEvents;
	}

	/**
	 * Sets the value of the fetchEvents property.
	 * 
	 * @param value
	 *            allowed object is {@link XEvents }
	 * 
	 */
	public void setFetchEvents(XEvents value) {
		this.fetchEvents = value;
	}

	/**
	 * Gets the value of the templates property.
	 * 
	 * @return possible object is {@link XTemplates }
	 * 
	 */
	public XTemplates getTemplates() {
		return templates;
	}

	/**
	 * Sets the value of the templates property.
	 * 
	 * @param value
	 *            allowed object is {@link XTemplates }
	 * 
	 */
	public void setTemplates(XTemplates value) {
		this.templates = value;
	}

	/**
	 * Gets the value of the root property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * Sets the value of the root property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRoot(String value) {
		this.root = value;
	}

}
