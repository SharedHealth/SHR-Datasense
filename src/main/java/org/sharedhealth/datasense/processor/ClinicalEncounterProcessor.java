package org.sharedhealth.datasense.processor;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.sharedhealth.datasense.model.Encounter;
import org.sharedhealth.datasense.model.Facility;
import org.sharedhealth.datasense.model.Patient;
import org.sharedhealth.datasense.model.fhir.EncounterComposition;
import org.sharedhealth.datasense.model.fhir.ServiceProviderReference;
import org.sharedhealth.datasense.repository.EncounterDao;
import org.sharedhealth.datasense.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("clinicalEncounterProcessor")
public class ClinicalEncounterProcessor implements ResourceProcessor {
    private EncounterDao encounterDao;
    private ResourceProcessor nextProcessor;

    @Autowired
    public ClinicalEncounterProcessor(@Qualifier("subResourceProcessor") ResourceProcessor nextProcessor, EncounterDao encounterDao) {
        this.nextProcessor = nextProcessor;
        this.encounterDao = encounterDao;
    }

    @Override
    public void process(EncounterComposition composition) {
        org.hl7.fhir.instance.model.Encounter fhirEncounter = composition.getEncounterReference().getEncounterReferenceValue();
        Encounter encounter = mapEncounterFields(fhirEncounter, composition);
        encounterDao.save(encounter);
        composition.getEncounterReference().setValue(encounter);
        if (nextProcessor != null) {
            nextProcessor.process(composition);
        }
    }

    private Encounter mapEncounterFields(org.hl7.fhir.instance.model.Encounter fhirEncounter, EncounterComposition composition) {
        Encounter encounter = new Encounter();
        encounter.setEncounterType(fhirEncounter.getType().get(0).getTextSimple());
        encounter.setEncounterId(composition.getContext().getShrEncounterId());
        encounter.setEncounterVisitType(fhirEncounter.getClass_().getValue().toCode());
        Patient patient = composition.getPatientReference().getValue();
        encounter.setPatient(patient);
        ServiceProviderReference facilityReference = composition.getServiceProviderReference();
        if(facilityReference != null && facilityReference.getValue() != null) {
            Facility facility = facilityReference.getValue();
            encounter.setFacility(facility);
            encounter.setLocationCode(facility.getFacilityLocationCode());
        }
        String encounterDate = composition.getComposition().getDateSimple().toString();
        Date encounterDateTime = DateUtil.parseDate(encounterDate);
        encounter.setEncounterDateTime(encounterDateTime);
        setPatientAge(patient.getDateOfBirth(), encounterDateTime, encounter);
        return encounter;
    }

    @Override
    public void setNext(ResourceProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    public void setPatientAge(Date birthDate, Date encounterDate, Encounter encounter) {
        LocalDate localBirthDate = new LocalDate(birthDate.getTime());
        LocalDate localEncounterDate = new LocalDate(encounterDate.getTime());
        encounter.setPatientAgeInYears(new Period(localBirthDate, localEncounterDate, PeriodType.years()).getYears());
        encounter.setPatientAgeInMonths(new Period(localBirthDate, localEncounterDate, PeriodType.months()).getMonths());
        encounter.setPatientAgeInDays(new Period(localBirthDate, localEncounterDate, PeriodType.days()).getDays());
    }
}