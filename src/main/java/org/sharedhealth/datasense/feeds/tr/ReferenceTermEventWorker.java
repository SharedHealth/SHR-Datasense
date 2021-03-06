package org.sharedhealth.datasense.feeds.tr;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.sharedhealth.datasense.client.TrWebClient;
import org.sharedhealth.datasense.config.DatasenseProperties;
import org.sharedhealth.datasense.model.tr.TrReferenceTerm;
import org.sharedhealth.datasense.processor.tr.ReferenceTermProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReferenceTermEventWorker implements EventWorker {

    private final TrWebClient trWebClient;
    private ReferenceTermProcessor referenceTermProcessor;
    private final DatasenseProperties datasenseProperties;
    private Logger log = Logger.getLogger(ReferenceTermEventWorker.class);


    @Autowired
    public ReferenceTermEventWorker(TrWebClient trWebClient, ReferenceTermProcessor referenceTermProcessor, DatasenseProperties datasenseProperties) {
        this.trWebClient = trWebClient;
        this.referenceTermProcessor = referenceTermProcessor;
        this.datasenseProperties = datasenseProperties;
    }

    @Override
    public void process(Event event) {
        String referenceTermUri = datasenseProperties.getTrBasePath() + event.getContent();
        String errorMessage = String.format("Could not connect to [ %s ]", referenceTermUri);
        log.info("Getting TR reference term for " + referenceTermUri);
        try {
            TrReferenceTerm trReferenceTerm = trWebClient.getTrReferenceTerm(referenceTermUri);
            referenceTermProcessor.process(trReferenceTerm);
        } catch (Exception e) {
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }

    }

    @Override
    public void cleanUp(Event event) {

    }
}
