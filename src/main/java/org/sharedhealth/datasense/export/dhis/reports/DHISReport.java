package org.sharedhealth.datasense.export.dhis.reports;

import org.apache.log4j.Logger;
import org.sharedhealth.datasense.config.DatasenseProperties;
import org.sharedhealth.datasense.repository.FacilityDao;
import org.sharedhealth.datasense.util.HeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.Map;

public abstract class DHISReport {
    @Autowired
    protected FacilityDao facilityDao;
    @Autowired
    protected DatasenseProperties datasenseProperties;
    @Autowired
    protected DataSource dataSource;

    protected static final Logger logger = Logger.getLogger(DHISReport.class);

    public abstract void process(Map<String, Object> dataMap);
}