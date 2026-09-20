package com.caesar.add2num.web.config;

import com.caesar.add2num.core.MyBigNumber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Publishes the TASK 1 calculator as a Spring bean.
 *
 * <p>{@link MyBigNumber} belongs to the core library and deliberately carries no Spring annotation,
 * so that the library stays usable outside Spring. Wiring it up is the web module's job, and doing
 * it here keeps that decision in one place. The calculator is stateless, so a single shared
 * instance serves every request.
 */
@Configuration(proxyBeanMethods = false)
public class CoreConfiguration {

    @Bean
    public MyBigNumber myBigNumber() {
        return new MyBigNumber();
    }
}
