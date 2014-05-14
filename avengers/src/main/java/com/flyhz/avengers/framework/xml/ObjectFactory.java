
package com.flyhz.avengers.framework.xml;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the com.flyhz.avengrs package.
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
	 * schema derived classes for package: com.flyhz.avengrs
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link Domain.BlackList }
	 * 
	 */
	public Domain.BlackList createDomainBlackList() {
		return new Domain.BlackList();
	}

	/**
	 * Create an instance of {@link Avengers }
	 * 
	 */
	public Avengers createAvengers() {
		return new Avengers();
	}

	/**
	 * Create an instance of {@link Domain }
	 * 
	 */
	public Domain createDomain() {
		return new Domain();
	}

	/**
	 * Create an instance of {@link Domain.Templates }
	 * 
	 */
	public Domain.Templates createDomainTemplates() {
		return new Domain.Templates();
	}

	/**
	 * Create an instance of {@link Domain.Templates.Template }
	 * 
	 */
	public Domain.Templates.Template createDomainTemplatesTemplate() {
		return new Domain.Templates.Template();
	}

}
