package com.hassan.ems.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "ems.leave")
public class LeaveProperties {

    @Min(1)
    @Max(60)
    private int annualAllowanceDays = 20;
}
