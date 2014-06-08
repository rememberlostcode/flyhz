
package com.flyhz.avengers.framework.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for xTemplate complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="xTemplate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="templateApplyEvents" type="{http://www.flyhz.com/avengrs}xEvents" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="url" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="pattern" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xTemplate", propOrder = {

})
public class XTemplate {

	protected XEvents	templateApplyEvents;
	@XmlAttribute(required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "token")
	protected String	url;
	@XmlAttribute(required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "token")
	protected String	pattern;

	/**
	 * Gets the value of the templateApplyEvents property.
	 * 
	 * @return possible object is {@link XEvents }
	 * 
	 */
	public XEvents getTemplateApplyEvents() {
		return templateApplyEvents;
	}

	/**
	 * Sets the value of the templateApplyEvents property.
	 * 
	 * @param value
	 *            allowed object is {@link XEvents }
	 * 
	 */
	public void setTemplateApplyEvents(XEvents value) {
		this.templateApplyEvents = value;
	}

	/**
	 * Gets the value of the url property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the value of the url property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUrl(String value) {
		this.url = value;
	}

	/**
	 * Gets the value of the pattern property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Sets the value of the pattern property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPattern(String value) {
		this.pattern = value;
	}

}
