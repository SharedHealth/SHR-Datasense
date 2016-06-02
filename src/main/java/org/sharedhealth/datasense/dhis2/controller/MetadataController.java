package org.sharedhealth.datasense.dhis2.controller;

import org.sharedhealth.datasense.dhis2.model.MetadataConfig;
import org.sharedhealth.datasense.dhis2.service.FacilityInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/metadata")
public class MetadataController {
    @Autowired
    FacilityInfoService metaDataService;

    @RequestMapping(value = "/encounter", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public
    @ResponseBody
    Object showLastEncounter(@RequestBody MetadataConfig config) {
        return metaDataService.getLastEncounter(config);
    }
}
