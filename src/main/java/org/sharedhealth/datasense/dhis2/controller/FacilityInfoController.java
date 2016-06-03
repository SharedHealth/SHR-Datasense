package org.sharedhealth.datasense.dhis2.controller;

import org.sharedhealth.datasense.dhis2.model.DHISResponse;
import org.sharedhealth.datasense.dhis2.model.MetadataConfig;
import org.sharedhealth.datasense.dhis2.service.FacilityInfoService;
import org.sharedhealth.datasense.model.Facility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Controller
@RequestMapping(value = "/facilityInfo")
public class FacilityInfoController {
    @Autowired
    FacilityInfoService metaDataService;

    @Autowired
    FacilityInfoService facilityDataService;

    @RequestMapping(value = "/encounter", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public
    @ResponseBody
    Object showLastEncounter(@RequestBody MetadataConfig config) {
        return metaDataService.getLastEncounter(config);
    }

//    @RequestMapping(value = "/search", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
//    public @ResponseBody
//    List<Facility> searchFacility() throws IOException, URISyntaxException {
//        return facilityDataService.getAvailableFacilities();
//    }

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public @ResponseBody
    Object searchFacility(@RequestParam(value = "name") String name) throws IOException, URISyntaxException {
        return facilityDataService.getAvailableFacilities(name);
    }
}
