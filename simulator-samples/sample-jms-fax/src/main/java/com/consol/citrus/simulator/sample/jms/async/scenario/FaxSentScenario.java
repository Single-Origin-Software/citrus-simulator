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

package com.consol.citrus.simulator.sample.jms.async.scenario;

import com.consol.citrus.simulator.sample.jms.async.model.FaxStatusEnumType;
import com.consol.citrus.simulator.scenario.Scenario;
import com.consol.citrus.simulator.scenario.ScenarioDesigner;

import static com.consol.citrus.simulator.sample.jms.async.variables.Variables.*;

/**
 * @author Martin Maher
 */
@Scenario("FaxSent")
public class FaxSentScenario extends AbstractFaxScenario {

    @Override
    public void run(ScenarioDesigner scenario) {
        scenario
            .receive(scenario.inboundEndpoint())
            .xpath(ROOT_ELEMENT_XPATH, "SendFaxMessage")
            .extractFromPayload(REFERENCE_ID_XPATH, REFERENCE_ID_VAR);

        scenario
            .send(getStatusEndpoint())
            .payload(
                    getPayloadHelper().generateFaxStatusMessage(REFERENCE_ID_PH,
                            FaxStatusEnumType.QUEUED,
                            "The fax message has been queued and will be send shortly"),
                    getPayloadHelper().getMarshaller()
            );

        scenario.sleep(2000L);

        scenario
            .send(getStatusEndpoint())
            .payload(
                    getPayloadHelper().generateFaxStatusMessage(REFERENCE_ID_PH,
                            FaxStatusEnumType.SUCCESS,
                            "The fax message has been successfully sent"),
                    getPayloadHelper().getMarshaller()
            );
    }
}
