package io.jonasg.xjx.serdes.deserialize.config;

public class XjxConfiguration {

	/**
	 * Whether to fail when an enum value cannot be mapped to an enum constant
	 * Defaults to false and will default to null when a value cannot be mapped to a name.
	 */
	boolean failOnUnknownEnumValue = false;

	public boolean failOnUnknownEnumValue() {
		return this.failOnUnknownEnumValue;
	}
}
