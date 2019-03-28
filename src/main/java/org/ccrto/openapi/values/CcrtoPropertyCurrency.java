package org.ccrto.openapi.values;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.ccrto.openapi.context.Context;
import org.ccrto.openapi.context.ContextHelper;
import org.ccrto.openapi.context.DecodeMethod;
import org.ccrto.openapi.system.SystemProperties;
import org.ccrto.openapi.values.api.IValueCurrency;

/**
 * 
 * CcrtoPropertyCurrency obiekt reprezentujący 'Currency'
 *
 * @author Sławomir Cichy &lt;slawomir.cichy@ibpm.pro&gt;
 * @version $Revision: 1.1 $
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "ccrtoCurrency")
public class CcrtoPropertyCurrency extends CcrtoProperty implements IValueCurrency {

	private static final long serialVersionUID = -1186306668877303365L;

	@XmlAttribute(required = false)
	private Boolean isEncoded;

	private String stringValue;

	/**
	 * @return the {@link #isEncoded}
	 */
	public Boolean getIsEncoded() {
		return isEncoded;
	}

	/**
	 * @param isEncoded
	 *            the {@link #isEncoded} to set
	 */
	public void setIsEncoded(Boolean isEncoded) {
		this.isEncoded = isEncoded;
	}

	public void setType(String type) {
		CcrtoPropertyType currencyType = CcrtoPropertyType.getType(type);
		if (currencyType == null || !CcrtoPropertyType.CURRENCY.equals(currencyType)) {
			throw new IllegalArgumentException("Type should be one of values: \"Currency\"");
		}
		this.type = type;
	}

	/* Overridden (non-Javadoc) */
	@Override
	public String toString() {
		return stringValue;
	}

	/* Overridden (non-Javadoc) */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
		return result;
	}

	/* Overridden (non-Javadoc) */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CcrtoPropertyCurrency)) {
			return false;
		}
		CcrtoPropertyCurrency other = (CcrtoPropertyCurrency) obj;
		if (stringValue == null) {
			if (other.stringValue != null) {
				return false;
			}
		} else if (!stringValue.equals(other.stringValue)) {
			return false;
		}
		return true;
	}

	/* Overridden (non-Javadoc) */
	@Override
	public boolean isNull() {
		return StringUtils.isBlank(stringValue);
	}

	/* Overridden (non-Javadoc) */
	@Override
	public String getObjectValue() {
		return this.stringValue;
	}

	/* Overridden (non-Javadoc) */
	@Override
	public void setObjectValue(String value) {
		this.stringValue = (String) value;
	}

	public static CcrtoPropertyCurrency getInstance(String systemName, Double value, boolean forRequest) {
		SystemProperties systemProperties = SystemProperties.getSystemProperties(systemName);
		Context context = systemProperties.createDefaultContext();
		if (StringUtils.isNotBlank(systemName)) {
			ContextHelper.setSystemNameInContext(context, systemName);
		}
		return getInstance(context, value, forRequest);
	}

	public static CcrtoPropertyCurrency getInstance(Context context, Double value, boolean forRequest) {
		String valueCode = ContextHelper.getCurrencyCode(context);
		DecodeMethod decodeMethod = ContextHelper.getDecodeMethod(context, forRequest);
		return getInstance(context, value, valueCode, decodeMethod);
	}

	public static CcrtoPropertyCurrency getInstance(Context context, Double value, String valueCode,
			DecodeMethod decodeMethod) {
		CcrtoPropertyCurrency instance = new CcrtoPropertyCurrency();
		if (value == null) {
			/* zwracam pustą instancję obiektu reprezentującego cenę */
			return instance;
		}
		boolean isNotEncoded = (DecodeMethod.ALL_WITHOUT_LOB.equals(decodeMethod)
				|| DecodeMethod.ALL.equals(decodeMethod));
		StringBuilder sb = new StringBuilder();
		if (isNotEncoded) {
			instance.isEncoded = isNotEncoded;
			Locale lLoc = ContextHelper.getUserLocale(context);
			String format = ContextHelper.getCurrencyFormat(context);
			DecimalFormat df = new DecimalFormat(format, new DecimalFormatSymbols(lLoc));
			sb.append(df.format(value));
			sb.append(valueCode);
		} else {
			sb.append(Double.toString(value.doubleValue()));
			sb.append(valueCode);
		}
		instance.stringValue = sb.toString();
		return instance;
	}

}
