
package com.flyhz.avengers.framework.config.xml;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the com.flyhz.avengers.framework.config.xml
 * package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	/**
	 * Create a new ObjectFactory that can be used to create new instances of
	 * schema derived classes for package:
	 * com.flyhz.avengers.framework.config.xml
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link XTemplates }
	 * 
	 */
	public XTemplates createXTemplates() {
		return new XTemplates();
	}

	/**
	 * Create an instance of {@link XEvents }
	 * 
	 */
	public XEvents createXEvents() {
		return new XEvents();
	}

	/**
	 * Create an instance of {@link XDomains }
	 * 
	 */
	public XDomains createXDomains() {
		return new XDomains();
	}

	/**
	 * Create an instance of {@link XEvent }
	 * 
	 */
	public XEvent createXEvent() {
		return new XEvent();
	}

	/**
	 * Create an instance of {@link XFilter }
	 * 
	 */
	public XFilter createXFilter() {
		return new XFilter();
	}

	/**
	 * Create an instance of {@link XConstructor }
	 * 
	 */
	public XConstructor createXConstructor() {
		return new XConstructor();
	}

	/**
	 * Create an instance of {@link XDomain }
	 * 
	 */
	public XDomain createXDomain() {
		return new XDomain();
	}

	/**
	 * Create an instance of {@link XTemplate }
	 * 
	 */
	public XTemplate createXTemplate() {
		return new XTemplate();
	}

}
