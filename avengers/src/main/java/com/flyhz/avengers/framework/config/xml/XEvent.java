
package com.flyhz.avengers.framework.config.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for xEvent complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="xEvent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="constructor" type="{http://www.flyhz.com/avengrs}xConstructor" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="class" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xEvent", propOrder = {

})
public class XEvent {

	protected XConstructor	constructor;
	@XmlAttribute(name = "class", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String		clazz;

	/**
	 * Gets the value of the constructor property.
	 * 
	 * @return possible object is {@link XConstructor }
	 * 
	 */
	public XConstructor getConstructor() {
		return constructor;
	}

	/**
	 * Sets the value of the constructor property.
	 * 
	 * @param value
	 *            allowed object is {@link XConstructor }
	 * 
	 */
	public void setConstructor(XConstructor value) {
		this.constructor = value;
	}

	/**
	 * Gets the value of the clazz property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getClazz() {
		return clazz;
	}

	/**
	 * Sets the value of the clazz property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setClazz(String value) {
		this.clazz = value;
	}

}
