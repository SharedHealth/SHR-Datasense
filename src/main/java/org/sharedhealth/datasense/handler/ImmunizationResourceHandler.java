package org.sharedhealth.datasense.handler;

import org.hl7.fhir.instance.model.Coding;
import org.hl7.fhir.instance.model.DateAndTime;
import org.hl7.fhir.instance.model.Immunization;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceType;
import org.sharedhealth.datasense.model.Encounter;
import org.sharedhealth.datasense.model.Medication;
import org.sharedhealth.datasense.model.MedicationStatus;
import org.sharedhealth.datasense.model.fhir.EncounterComposition;
import org.sharedhealth.datasense.repository.MedicationDao;
import org.sharedhealth.datasense.util.DateUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static org.sharedhealth.datasense.util.TrUrlMatcher.isTrMedicationUrl;

@Component
public class ImmunizationResourceHandler implements FhirResourceHandler {

    @Autowired
    private MedicationDao medicationDao;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImmunizationResourceHandler.class);

    @Override
    public boolean canHandle(Resource resource) {
        return resource.getResourceType().equals(ResourceType.Immunization);
    }

    @Override
    public void process(Resource resource, EncounterComposition composition) {
        Immunization immunization = (Immunization) resource;
        Medication medication = new Medication();
        medication.setStatus(MedicationStatus.Administered);
        Encounter encounter = composition.getEncounterReference().getValue();
        medication.setEncounter(encounter);
        medication.setPatient(composition.getPatientReference().getValue());
        medication.setDateTime(getDateTime(immunization, encounter));
        String codeSimple = getDrugId(immunization);
        if (codeSimple == null) {
            logger.warn("Cannot save non-coded immnunizations.");
            return;
        }
        medication.setDrugId(codeSimple);
        medicationDao.save(medication);
    }

    private String getDrugId(Immunization immunization) {
        List<Coding> codings = immunization.getVaccineType().getCoding();
        for (Coding coding : codings) {
            if (coding.getSystemSimple() != null && isTrMedicationUrl(coding.getSystemSimple())) {
                return coding.getCodeSimple();
            }
        }
        return null;
    }

    private Date getDateTime(Immunization immunization, Encounter encounter) {
        DateAndTime dateSimple = immunization.getDateSimple();
        return dateSimple != null ? DateUtil.parseDate(dateSimple.toString()) : encounter.getEncounterDateTime();
    }
}