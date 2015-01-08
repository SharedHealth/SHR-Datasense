package org.sharedhealth.datasense.client;

import org.apache.log4j.Logger;
import org.sharedhealth.datasense.config.DatasenseProperties;
import org.sharedhealth.datasense.security.IdentityStore;
import org.sharedhealth.datasense.security.IdentityToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ShrWebClient {
    private Logger log = Logger.getLogger(ShrWebClient.class);
    private IdentityServiceClient identityServiceClient ;
    private DatasenseProperties properties;

    @Autowired
    public ShrWebClient(IdentityServiceClient identityServiceClient, DatasenseProperties properties) {
        this.identityServiceClient = identityServiceClient;
        this.properties = properties;
    }

    public String getEncounterFeedContent(URI uri) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/atom+xml");
        headers.put("facilityId", properties.getDatasenseFacilityId());
        headers.put("X-Auth-Token", identityServiceClient.getOrCreateToken().toString());
        try {
            return new WebClient().get(uri, headers);
        } catch (ConnectionException e) {
            log.error(String.format("Could not fetch feed for URI [%s]", uri.toString()), e);
            if (e.getErrorCode() == 401) {
                identityServiceClient.clearToken();
            }
            throw new IOException(e);
        }
    }
}