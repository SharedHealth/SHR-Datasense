package org.sharedhealth.datasense.handler.mappers;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.primitive.*;
import org.sharedhealth.datasense.util.DateUtil;

import java.util.List;

import static org.sharedhealth.datasense.util.FhirCodeLookup.getConceptId;
import static org.sharedhealth.datasense.util.FhirCodeLookup.getReferenceCode;

public class ObservationValueMapper {

    public static final String HL7_FHIR_VS_URL_FOR_BOOLEAN = "http://hl7.org/fhir/v2/0136";
    public static final String HL7_FHIR_VS_SYSTEM_FOR_BOOLEAN = "http://hl7.org/fhir/v2/vs/0136";

    /**
     * Use this only for observation values
     *
     * @param value
     * @return
     */
    public String getObservationValue(IDatatype value) {
        if (value instanceof StringDt) {
            return ((StringDt) value).getValue();
        } else if (value instanceof DecimalDt) {
            return ((DecimalDt) value).getValue().toString();
        } else if (value instanceof DateDt) {
            return DateUtil.parseToString(((DateDt) value).getValue());
        } else if (value instanceof DateTimeDt) {
            return DateUtil.parseToString(((DateTimeDt) value).getValue());
        } else if (value instanceof BooleanDt) {
            return ((BooleanDt) value).getValue().toString();
        } else if (value instanceof QuantityDt) {
            return ((QuantityDt) value).getValue().toString();
        }
        //TODO : Codeable concept should point to concept synced from TR (Can be done after we sync all concepts from
        // TR).
        else if (value instanceof CodeableConceptDt) {
            List<CodingDt> codings = ((CodeableConceptDt) value).getCoding();
            if (isValueBoolean(codings)) {
                return getValueBoolean(codings);
            }
            String referenceCode = getReferenceCode(codings);
            if (referenceCode != null) {
                return referenceCode;
            } else {
                return getConceptId(codings);
            }
        }
        return null;
    }

    private boolean isValueBoolean(List<CodingDt> codings) {
        if (!codings.isEmpty()) {
            String system = codings.get(0).getSystem();
            return system.equalsIgnoreCase(HL7_FHIR_VS_URL_FOR_BOOLEAN) || system.equalsIgnoreCase(HL7_FHIR_VS_SYSTEM_FOR_BOOLEAN);
        }
        return false;
    }

    private String getValueBoolean(List<CodingDt> codings) {
        if (!codings.isEmpty()) {
            CodingDt coding = codings.get(0);
            String system = coding.getSystem();
            if (system.equalsIgnoreCase(HL7_FHIR_VS_URL_FOR_BOOLEAN) || system.equalsIgnoreCase(HL7_FHIR_VS_SYSTEM_FOR_BOOLEAN)) {
                return String.valueOf(coding.getCode().equalsIgnoreCase("Y"));
            }
        }
        return "false";
    }

}
