package org.sharedhealth.datasense.model.fhir;

import org.hl7.fhir.instance.model.ResourceReference;
import org.sharedhealth.datasense.model.Patient;

import static org.apache.commons.lang3.StringUtils.substringAfterLast;

public class PatientReference extends ResourceReference {
    private ResourceReference subject;
    private Patient value;

    public PatientReference(ResourceReference subject) {
        this.subject = subject;
    }

    public String getHealthId() {
        return substringAfterLast(subject.getReferenceSimple(),"/");
    }

    public void setValue(Patient patient) {
        this.value = patient;
    }

    public Patient getValue() {
        return value;
    }
}