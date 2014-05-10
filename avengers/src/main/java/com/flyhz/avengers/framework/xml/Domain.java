
package com.flyhz.avengers.framework.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for domain complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="domain">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="blackList" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="black" type="{http://www.w3.org/2001/XMLSchema}token" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="templates" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="template" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="url" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *                           &lt;attribute name="parser" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
@XmlType(name = "domain", propOrder = {

})
public class Domain {

	protected Domain.BlackList	blackList;
	protected Domain.Templates	templates;
	@XmlAttribute(required = true)
	@XmlSchemaType(name = "anySimpleType")
	protected String			root;

	/**
	 * Gets the value of the blackList property.
	 * 
	 * @return possible object is {@link Domain.BlackList }
	 * 
	 */
	public Domain.BlackList getBlackList() {
		return blackList;
	}

	/**
	 * Sets the value of the blackList property.
	 * 
	 * @param value
	 *            allowed object is {@link Domain.BlackList }
	 * 
	 */
	public void setBlackList(Domain.BlackList value) {
		this.blackList = value;
	}

	/**
	 * Gets the value of the templates property.
	 * 
	 * @return possible object is {@link Domain.Templates }
	 * 
	 */
	public Domain.Templates getTemplates() {
		return templates;
	}

	/**
	 * Sets the value of the templates property.
	 * 
	 * @param value
	 *            allowed object is {@link Domain.Templates }
	 * 
	 */
	public void setTemplates(Domain.Templates value) {
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

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="black" type="{http://www.w3.org/2001/XMLSchema}token" maxOccurs="unbounded" minOccurs="0"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "black" })
	public static class BlackList {

		@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
		@XmlSchemaType(name = "token")
		protected List<String>	black;

		/**
		 * Gets the value of the black property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the black property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getBlack().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link String }
		 * 
		 * 
		 */
		public List<String> getBlack() {
			if (black == null) {
				black = new ArrayList<String>();
			}
			return this.black;
		}

	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="template" maxOccurs="unbounded">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;attribute name="url" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
	 *                 &lt;attribute name="parser" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
	 *               &lt;/restriction>
	 *             &lt;/complexContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "template" })
	public static class Templates {

		@XmlElement(required = true)
		protected List<Domain.Templates.Template>	template;

		/**
		 * Gets the value of the template property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the template property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getTemplate().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link Domain.Templates.Template }
		 * 
		 * 
		 */
		public List<Domain.Templates.Template> getTemplate() {
			if (template == null) {
				template = new ArrayList<Domain.Templates.Template>();
			}
			return this.template;
		}

		/**
		 * <p>
		 * Java class for anonymous complex type.
		 * 
		 * <p>
		 * The following schema fragment specifies the expected content
		 * contained within this class.
		 * 
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;attribute name="url" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
		 *       &lt;attribute name="parser" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 * 
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "")
		public static class Template {

			@XmlAttribute(required = true)
			@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
			@XmlSchemaType(name = "token")
			protected String	url;
			@XmlAttribute(required = true)
			@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
			@XmlSchemaType(name = "token")
			protected String	parser;

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
			 * Gets the value of the parser property.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getParser() {
				return parser;
			}

			/**
			 * Sets the value of the parser property.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setParser(String value) {
				this.parser = value;
			}

		}

	}

}
