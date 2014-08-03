
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
 *       &lt;attribute name="apply" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *       &lt;attribute name="pattern" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xTemplate")
public class XTemplate {

	@XmlAttribute(required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "token")
	protected String	apply;
	@XmlAttribute(required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "token")
	protected String	pattern;

	/**
	 * Gets the value of the apply property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getApply() {
		return apply;
	}

	/**
	 * Sets the value of the apply property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setApply(String value) {
		this.apply = value;
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
