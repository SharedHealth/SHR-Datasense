package org.sharedhealth.datasense.dhis2.controller;

import org.quartz.SchedulerException;
import org.sharedhealth.datasense.client.DHIS2Client;
import org.sharedhealth.datasense.dhis2.model.DHISReportConfig;
import org.sharedhealth.datasense.dhis2.model.DHISResponse;
import org.sharedhealth.datasense.dhis2.model.DatasetJobSchedule;
import org.sharedhealth.datasense.dhis2.service.DHISMetaDataService;
import org.sharedhealth.datasense.dhis2.service.JobSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping(value = "/dhis2/reports")
public class DHISReportController {


    public static final String DHIS_DATASET_SEARCH_FORMAT = "/api/dataSets?filter=name:like:%s&fields=id,name,href,periodType";
    private static final String DHIS_DATASET_ORGUNIT_FORMAT = "/api/dataSets/%s?fields=id,name,organisationUnits";
    @Autowired
    DHISMetaDataService metaDataService;

    @Autowired
    DHIS2Client dhis2Client;

    @Autowired
    private JobSchedulerService jobScheduler;

    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public ModelAndView showReportsList() {
        ModelAndView modelAndView = new ModelAndView("dhis.reports");
        modelAndView.addObject("availableReports", metaDataService.getConfiguredReports());
        return modelAndView;
    }

    @RequestMapping(value = "/configure", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public ModelAndView configure(@RequestParam(value = "reportName") String reportName) {
        ModelAndView modelAndView = new ModelAndView("dhis.datasetConfig");
        modelAndView.addObject("reportName", reportName);
        //TODO
        modelAndView.addObject("configFile", reportName + ".json") ;
        return modelAndView;
    }

    @RequestMapping(value = "/configure", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public @ResponseBody
    String configure(@RequestBody DHISReportConfig config) {
        metaDataService.save(config);
        return "{}";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public @ResponseBody
    DHISResponse searchDHISDataset(@RequestParam(value = "name") String name) {
        String searchUri =
                String.format(DHIS_DATASET_SEARCH_FORMAT, name);
        return dhis2Client.get(searchUri);
    }

    @RequestMapping(value = "/{datasetId}/applicableOrgUnits", method = RequestMethod.GET, produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public @ResponseBody
    DHISResponse getOrgUnits(@PathVariable String datasetId) {
        String searchUri = String.format(DHIS_DATASET_ORGUNIT_FORMAT, datasetId);
        return dhis2Client.get(searchUri);
    }



    @RequestMapping(value = "/schedule/{datasetId}", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public ModelAndView showScheduleOptions(@PathVariable String datasetId) {
        return viewModelForDataset(datasetId);
    }

    @RequestMapping(value = "/schedule/{datasetId}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public ModelAndView scheduleReportSubmission(ReportScheduleRequest scheduleRequest) {
        String[] formErrors = new String[] {"Error Occurred."};
        try {
            if (!scheduleRequest.getSelectedFacilities().isEmpty()) {
                jobScheduler.scheduleJob(scheduleRequest);
                return new ModelAndView("redirect:/dhis2/reports");
            } else {
                formErrors[0] = "Please select a facility/Organization Unit";
            }
        } catch (Exception e) {
            e.printStackTrace();
            formErrors[0] = e.getMessage();
        }
        ModelAndView viewModel = viewModelForDataset(scheduleRequest.getDatasetId());
        viewModel.addObject("formErrors", formErrors);
        return viewModel;
    }

    @RequestMapping(value = "/schedule/{datasetId}/jobs", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ROLE_SHR System Admin')")
    public @ResponseBody
    List<DatasetJobSchedule> showScheduleForDataset(@PathVariable String datasetId) {
        try {
            return jobScheduler.findAllJobsForDataset(datasetId);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ModelAndView viewModelForDataset(String datasetId) {
        ModelAndView modelAndView = new ModelAndView("dhis.scheduleReports");
        modelAndView.addObject("orgUnits", metaDataService.getAvailableOrgUnits(false));
        modelAndView.addObject("supportedPeriodTypes", Arrays.asList(ReportScheduleRequest.SUPPORTED_PERIOD_TYPES));
        modelAndView.addObject("reportConfig", metaDataService.getReportConfigForDataset(datasetId));
        return modelAndView;
    }


}
