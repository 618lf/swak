package com.swak.actuator.config.metrics;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.convert.DurationStyle;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Meter.Type;

public class ServiceLevelAgreementBoundary {

	private final Object value;

	ServiceLevelAgreementBoundary(long value) {
		this.value = value;
	}

	ServiceLevelAgreementBoundary(Duration value) {
		this.value = value;
	}

	/**
	 * Return the underlying value of the SLA in form suitable to apply to the given meter
	 * type.
	 * @param meterType the meter type
	 * @return the value or {@code null} if the value cannot be applied
	 */
	public Long getValue(Meter.Type meterType) {
		if (meterType == Type.DISTRIBUTION_SUMMARY) {
			return getDistributionSummaryValue();
		}
		if (meterType == Type.TIMER) {
			return getTimerValue();
		}
		return null;
	}

	private Long getDistributionSummaryValue() {
		if (this.value instanceof Long) {
			return (Long) this.value;
		}
		return null;
	}

	private Long getTimerValue() {
		if (this.value instanceof Long) {
			return TimeUnit.MILLISECONDS.toNanos((long) this.value);
		}
		if (this.value instanceof Duration) {
			return ((Duration) this.value).toNanos();
		}
		return null;
	}

	public static ServiceLevelAgreementBoundary valueOf(String value) {
		if (isNumber(value)) {
			return new ServiceLevelAgreementBoundary(Long.parseLong(value));
		}
		return new ServiceLevelAgreementBoundary(DurationStyle.detectAndParse(value));
	}

	/**
	 * Return a new {@link ServiceLevelAgreementBoundary} instance for the given long
	 * value.
	 * @param value the source value
	 * @return a {@link ServiceLevelAgreementBoundary} instance
	 */
	public static ServiceLevelAgreementBoundary valueOf(long value) {
		return new ServiceLevelAgreementBoundary(value);
	}

	/**
	 * Return a new {@link ServiceLevelAgreementBoundary} instance for the given String
	 * value. The value may contain a simple number, or a {@link DurationStyle duration
	 * style string}.
	 * @param value the source value
	 * @return a {@link ServiceLevelAgreementBoundary} instance
	 */
	private static boolean isNumber(String value) {
		return value.chars().allMatch(Character::isDigit);
	}
}
