/*
 *  *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2015 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 *  * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.ow2.proactive.addons.cloud_watch;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.ow2.proactive.addons.cloud_watch.model.RestResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

public class PCWUtility {
    private static final String PCW_REST_URL = "http://localhost:8080/proactive-cloud-watch/rules/";

    /**
     * method make Post call to REST API of Proactive Cloud Watch service for adding rule and starting polling
     * @param  jsonRule a rule specified directly in json format
     * @throws IOException
     */
    public static RestResponse addRule(String jsonRule) throws IOException {
        Request request = Request.Post(PCW_REST_URL);
        request.addHeader("Content-Type", "application/json");
        request.bodyString(jsonRule, ContentType.TEXT_PLAIN);
        return executeRequestAndCheck(request, HttpURLConnection.HTTP_CREATED);
    }

    /**
     * method make Post call to REST API of Proactive Cloud Watch service for adding rule and starting polling
     * @param  ruleFilePath local file path to rule
     * @throws IOException
     */
    public static RestResponse addRuleFromFile(String ruleFilePath) throws IOException {
        Request request = Request.Post(PCW_REST_URL);
        request.addHeader("Content-Type", "application/json");
        String contentRule = new String(Files.readAllBytes(Paths.get(ruleFilePath)));
        request.bodyString(contentRule, ContentType.TEXT_PLAIN);
        return executeRequestAndCheck(request, HttpURLConnection.HTTP_CREATED);
    }

    /**
     * method make Delete call to REST API of Proactive Cloud Watch service for deleting rule and stop polling
     * @param  ruleName a rule identifier
     * @throws IOException
     */
    public static RestResponse deleteRule(String ruleName) throws IOException {
        Request request = Request.Delete(PCW_REST_URL + "/" + ruleName);
        request.addHeader("Content-Type", "application/json");
        return executeRequestAndCheck(request, HttpURLConnection.HTTP_OK);
    }

    private static RestResponse executeRequestAndCheck(Request request, int httpSuccessfulStatus) throws IOException {
        RestResponse response = executeRequest(request);
        return checkResponseIsSucessful(response, httpSuccessfulStatus);
    }

    private static RestResponse checkResponseIsSucessful(RestResponse response, int successfulStatus) {
        if (response.getResponseCode() != successfulStatus) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getResponseCode()
                    + "\n Response: " + response.getResponse());
        }
        return response;
    }

    protected static RestResponse executeRequest(final Request request) throws IOException {
        Response requestResponse = request.execute();
        HttpResponse responseObject = requestResponse.returnResponse();
        return new RestResponse(
                responseObject.getStatusLine().getStatusCode(),
                EntityUtils.toString(responseObject.getEntity()));
    }
}
