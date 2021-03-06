
package com.flyhz.avengers.framework.config.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="globalVariable" type="{http://www.flyhz.com/avengrs}xGlobalVariable"/>
 *         &lt;element name="proxy" type="{http://www.flyhz.com/avengrs}xProxy" minOccurs="0"/>
 *         &lt;element name="domain" type="{http://www.flyhz.com/avengrs}xDomain" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "globalVariable", "proxy", "domain" })
@XmlRootElement(name = "xDomains")
public class XDomains {

	@XmlElement(required = true)
	protected XGlobalVariable	globalVariable;
	protected XProxy			proxy;
	@XmlElement(required = true)
	protected List<XDomain>		domain;

	/**
	 * Gets the value of the globalVariable property.
	 * 
	 * @return possible object is {@link XGlobalVariable }
	 * 
	 */
	public XGlobalVariable getGlobalVariable() {
		return globalVariable;
	}

	/**
	 * Sets the value of the globalVariable property.
	 * 
	 * @param value
	 *            allowed object is {@link XGlobalVariable }
	 * 
	 */
	public void setGlobalVariable(XGlobalVariable value) {
		this.globalVariable = value;
	}

	/**
	 * Gets the value of the proxy property.
	 * 
	 * @return possible object is {@link XProxy }
	 * 
	 */
	public XProxy getProxy() {
		return proxy;
	}

	/**
	 * Sets the value of the proxy property.
	 * 
	 * @param value
	 *            allowed object is {@link XProxy }
	 * 
	 */
	public void setProxy(XProxy value) {
		this.proxy = value;
	}

	/**
	 * Gets the value of the domain property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the domain property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDomain().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link XDomain }
	 * 
	 * 
	 */
	public List<XDomain> getDomain() {
		if (domain == null) {
			domain = new ArrayList<XDomain>();
		}
		return this.domain;
	}

}
