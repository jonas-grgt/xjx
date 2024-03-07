package io.jonasg.xjx.serdes.deserialize.config;

import io.jonasg.xjx.serdes.XjxSerdes;

@SuppressWarnings("UnusedReturnValue")
public class ConfigurationBuilder {

	private final XjxConfiguration xjxConfiguration;

	public ConfigurationBuilder(XjxConfiguration xjxConfiguration) {
		this.xjxConfiguration = xjxConfiguration;
	}

	/**
	 * Configures the {@link XjxSerdes} to fail when an enum value cannot be mapped to an enum constant.
	 * When not set, defaults to false and will default to null when a value cannot be mapped to a name.
	 * @param failOnUnmappableEnumValue Whether to fail when an enum value cannot be mapped to an enum constant
	 * @return The ConfigurationBuilder
	 */
	public ConfigurationBuilder failOnUnknownEnumValue(boolean failOnUnmappableEnumValue) {
		this.xjxConfiguration.failOnUnknownEnumValue = failOnUnmappableEnumValue;
		return this;
	}
}
