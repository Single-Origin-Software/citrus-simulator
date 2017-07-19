/*
 * Copyright 2006-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.simulator.annotation;

import com.consol.citrus.endpoint.adapter.mapping.MappingKeyExtractor;
import com.consol.citrus.endpoint.adapter.mapping.XPathPayloadMappingKeyExtractor;
import com.consol.citrus.simulator.endpoint.SimulatorEndpointAdapter;
import com.consol.citrus.ws.interceptor.LoggingEndpointInterceptor;
import com.consol.citrus.ws.server.WebServiceEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.EndpointMapping;
import org.springframework.ws.server.endpoint.MessageEndpoint;
import org.springframework.ws.server.endpoint.adapter.MessageEndpointAdapter;
import org.springframework.ws.server.endpoint.mapping.UriEndpointMapping;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Configuration
@Import(SimulatorWebServiceLoggingSupport.class)
@EnableConfigurationProperties(SimulatorWebServiceConfigurationProperties.class)
public class SimulatorWebServiceSupport {

    @Autowired(required = false)
    private SimulatorWebServiceConfigurer configurer;

    @Autowired
    private LoggingEndpointInterceptor loggingEndpointInterceptor;

    @Bean
    public MessageEndpointAdapter messageEndpointAdapter() {
        return new MessageEndpointAdapter();
    }

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext,
                                                            SimulatorWebServiceConfigurationProperties simulatorWebServiceConfiguration) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, getServletMapping(simulatorWebServiceConfiguration));
    }

    @Bean(name = "simulatorWsEndpointMapping")
    public EndpointMapping endpointMapping(ApplicationContext applicationContext) {
        UriEndpointMapping endpointMapping = new UriEndpointMapping();
        endpointMapping.setOrder(Ordered.HIGHEST_PRECEDENCE);

        endpointMapping.setDefaultEndpoint(webServiceEndpoint(applicationContext));
        endpointMapping.setInterceptors(interceptors());

        return endpointMapping;
    }

    @Bean(name = "simulatorWsEndpoint")
    public MessageEndpoint webServiceEndpoint(ApplicationContext applicationContext) {
        WebServiceEndpoint webServiceEndpoint = new WebServiceEndpoint();
        SimulatorEndpointAdapter endpointAdapter = simulatorEndpointAdapter();
        endpointAdapter.setApplicationContext(applicationContext);
        endpointAdapter.setMappingKeyExtractor(simulatorMappingKeyExtractor());
        webServiceEndpoint.setEndpointAdapter(endpointAdapter);

        return webServiceEndpoint;
    }

    @Bean(name = "simulatorWsEndpointAdapter")
    public SimulatorEndpointAdapter simulatorEndpointAdapter() {
        return new SimulatorEndpointAdapter();
    }

    @Bean(name = "simulatorWsMappingKeyExtractor")
    public MappingKeyExtractor simulatorMappingKeyExtractor() {
        if (configurer != null) {
            return configurer.mappingKeyExtractor();
        }

        return new XPathPayloadMappingKeyExtractor();
    }

    /**
     * Gets the web service message dispatcher servlet mapping. Clients must use this
     * context path in order to access the web service support on the simulator.
     *
     * @return
     */
    protected String getServletMapping(SimulatorWebServiceConfigurationProperties simulatorWebServiceConfiguration) {
        if (configurer != null) {
            return configurer.servletMapping(simulatorWebServiceConfiguration);
        }

        return simulatorWebServiceConfiguration.getServletMapping();
    }

    /**
     * Provides list of endpoint interceptors.
     *
     * @return
     */
    protected EndpointInterceptor[] interceptors() {
        List<EndpointInterceptor> interceptors = new ArrayList<>();
        if (configurer != null) {
            Collections.addAll(interceptors, configurer.interceptors());
        }
        interceptors.add(loggingEndpointInterceptor);
        return interceptors.toArray(new EndpointInterceptor[interceptors.size()]);
    }
}
