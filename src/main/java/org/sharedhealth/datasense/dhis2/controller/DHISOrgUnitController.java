package org.sharedhealth.datasense.dhis2.controller;

import org.sharedhealth.datasense.client.DHIS2Client;
import org.sharedhealth.datasense.controller.DatasenseController;
import org.sharedhealth.datasense.dhis2.model.DHISOrgUnitConfig;
import org.sharedhealth.datasense.dhis2.model.DHISResponse;
import org.sharedhealth.datasense.dhis2.service.DHISMetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URISyntaxException;

@Controller
@RequestMapping(value = "/dhis2/orgUnits")
public class DHISOrgUnitController extends DatasenseController {

    public static final String DHIS_ORGUNIT_SEARCH_FORMAT = "/api/organisationUnits?filter=name:like:%s&fields=id,name,href&pageSize=100";
    @Autowired
    DHISMetaDataService metaDataService;

    @Autowired
    DHIS2Client dhis2Client;

    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public ModelAndView showReportsList() {
        ModelAndView modelAndView = new ModelAndView("dhis.orgUnits");
        modelAndView.addObject("allOrgUnits", metaDataService.getAvailableOrgUnits(true));
        return modelAndView;
    }

    @RequestMapping(value = "/configure", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public ModelAndView configure(@RequestParam(value = "facilityId") String facilityId) {
        ModelAndView modelAndView = new ModelAndView("dhis.orgunitConfig");
        modelAndView.addObject("facilityId", facilityId);
        return modelAndView;
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public ModelAndView reset(@RequestParam(value = "facilityId") String facilityId) {
        metaDataService.resetOrgUnitMap(facilityId);
        return new ModelAndView("redirect:/dhis2/orgUnits");
    }

    @RequestMapping(value = "/configure", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public
    @ResponseBody
    String configure(@RequestBody DHISOrgUnitConfig config) {
        metaDataService.save(config);
        return "{}";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public
    @ResponseBody
    DHISResponse searchDHISDataset(@RequestParam(value = "name") String name) throws IOException, URISyntaxException {
        String searchString = name.replaceAll("  ", " ").replaceAll(" ", "%20");
        String searchUri =
                String.format(DHIS_ORGUNIT_SEARCH_FORMAT, searchString);
        return dhis2Client.get(searchUri);
    }
}
