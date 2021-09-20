package com.animeshagrawal.timerservice.connectors;

import com.animeshagrawal.timerservice.model.Payload;
import com.animeshagrawal.timerservice.model.PayloadLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.newrelic.api.agent.Trace;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
@Slf4j
public class ApiProducerConnector implements Connector {
    private static final Logger eventLogger = LoggerFactory.getLogger("eventLogger");
    @Trace(dispatcher=true)
    public boolean produceEvent(Payload event) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(event.getDestination());
            httpPost.setHeader("Content-type", "application/json");
            if (event.getMetaInfo() != null && event.getMetaInfo().keySet().size() > 0
                    && event.getMetaInfo().containsKey("headers") && event.getMetaInfo().get("headers").equalsIgnoreCase("yes")) {
                for(String key: event.getMetaInfo().keySet()) {
                    if(key.startsWith("header__")) {
                        String header = key.split("__")[1];
                        httpPost.setHeader(header, event.getMetaInfo().get(key));
                    }
                }
            }
            String finalPayload = new Gson().toJson(event);
            StringEntity params = new StringEntity(finalPayload);
            httpPost.setEntity(params);
            CloseableHttpResponse httpResponse = client.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != 200)
            {
                throw new RuntimeException("Failed with HTTP error code : " + statusCode);
            }
            PayloadLogger payloadLogger = new PayloadLogger(event, "Sent to API",
                    PayloadLogger.Status.PRODUCED.getValue());
            eventLogger.info("{}", payloadLogger);
            return true;
        } catch (Exception e) {
            log.error("Error while hitting API : ", e);
            PayloadLogger payloadLogger = new PayloadLogger(event, e.getMessage(),
                    PayloadLogger.Status.RETRY.getValue());
            eventLogger.info("{}", payloadLogger);
            throw new RuntimeException("Failed with error : " + e, e);
        }
    }
}
