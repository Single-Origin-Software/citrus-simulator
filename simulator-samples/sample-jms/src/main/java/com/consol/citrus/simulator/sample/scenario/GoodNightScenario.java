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

package com.consol.citrus.simulator.sample.scenario;

import com.consol.citrus.simulator.scenario.*;

/**
 * @author Christoph Deppisch
 */
@Scenario("GoodNight")
public class GoodNightScenario extends AbstractSimulatorScenario {

    private static final String CORRELATION_ID = "correlationId";

    @Override
    public void run(ScenarioDesigner scenario) {
        scenario
            .receive(scenario.inboundEndpoint())
            .payload("<GoodNight xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Go to sleep!" +
                     "</GoodNight>")
            .extractFromHeader(CORRELATION_ID, "correlationId");

        scenario.correlation().start()
            .onHeader(CORRELATION_ID, "${correlationId}");

        scenario
            .send(scenario.replyEndpoint())
            .payload("<GoodNightResponse xmlns=\"http://citrusframework.org/schemas/hello\">" +
                        "Good Night!" +
                    "</GoodNightResponse>");

        scenario
            .receive(scenario.inboundEndpoint())
            .selector("correlationId = '${correlationId}'")
            .payload("<InterveningRequest>In between!</InterveningRequest>");

        scenario
            .send(scenario.replyEndpoint())
            .payload("<InterveningResponse>In between!</InterveningResponse>");
    }
}
