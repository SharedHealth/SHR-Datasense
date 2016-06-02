package org.sharedhealth.datasense.dhis2.service;

import org.sharedhealth.datasense.dhis2.model.MetadataConfig;
import org.sharedhealth.datasense.dhis2.repository.DHISConfigDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacilityInfoService {
    @Autowired
    DHISConfigDao dhisConfigDao;

    @Transactional
    public Object getLastEncounter(MetadataConfig config) {
        return dhisConfigDao.getLastEncounter(config);
    }
}
