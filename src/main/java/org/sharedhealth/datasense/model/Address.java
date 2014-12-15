package org.sharedhealth.datasense.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

public class Address {
    @JsonProperty("address_line")
    private String addressLine;

    @JsonProperty("division_id")
    private String divisionId;

    @JsonProperty("district_id")
    private String districtId;

    @JsonProperty("upazila_id")
    private String upazilaId;

    @JsonProperty("city_corporation_id")
    private String cityCorporationId;

    @JsonProperty("union_or_urban_ward_id")
    private String unionOrUrbanWardId;

    @JsonProperty("union_id")
    @JsonInclude(NON_EMPTY)
    private String unionId;

    @JsonProperty("thana_id")
    @JsonInclude(NON_EMPTY)
    private String thanaId;

    public Address() {
    }

    public Address(String addressLine, String divisionId, String districtId, String upazilaId, String cityCorporationId, String unionOrUrbanWardId, String unionId, String thanaId) {
        this.addressLine = addressLine;
        this.divisionId = divisionId;
        this.districtId = districtId;
        this.upazilaId = upazilaId;
        this.cityCorporationId = cityCorporationId;
        this.unionOrUrbanWardId = unionOrUrbanWardId;
        this.unionId = unionId;
        this.thanaId = thanaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;

        Address address = (Address) o;

        if (addressLine != null ? !addressLine.equals(address.addressLine) : address.addressLine != null) return false;
        if (divisionId != null ? !divisionId.equals(address.divisionId) : address.divisionId != null) return false;
        if (districtId != null ? !districtId.equals(address.districtId) : address.districtId != null) return false;
        if (upazilaId != null ? !upazilaId.equals(address.upazilaId) : address.upazilaId != null) return false;
        if (cityCorporationId != null ? !cityCorporationId.equals(address.cityCorporationId) : address.cityCorporationId != null)
            return false;
        if (unionOrUrbanWardId != null ? !unionOrUrbanWardId.equals(address.unionOrUrbanWardId) : address.unionOrUrbanWardId != null) return false;
        if (unionId != null ? !unionId.equals(address.unionId) : address.unionId != null) return false;
        if (thanaId != null ? !thanaId.equals(address.thanaId) : address.thanaId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = addressLine != null ? addressLine.hashCode() : 0;
        result = 31 * result + (divisionId != null ? divisionId.hashCode() : 0);
        result = 31 * result + (districtId != null ? districtId.hashCode() : 0);
        result = 31 * result + (upazilaId != null ? upazilaId.hashCode() : 0);
        result = 31 * result + (cityCorporationId != null ? cityCorporationId.hashCode() : 0);
        result = 31 * result + (unionOrUrbanWardId != null ? unionOrUrbanWardId.hashCode() : 0);
        result = 31 * result + (unionId != null ? unionId.hashCode() : 0);
        result = 31 * result + (thanaId != null ? thanaId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Address{");
        sb.append("addressLine='").append(addressLine).append('\'');
        sb.append(", divisionId='").append(divisionId).append('\'');
        sb.append(", districtId='").append(districtId).append('\'');
        sb.append(", upazilaId='").append(upazilaId).append('\'');
        sb.append(", cityCorporationId='").append(cityCorporationId).append('\'');
        sb.append(", unionOrUrbanWardId='").append(unionOrUrbanWardId).append('\'');
        sb.append(", unionId='").append(unionId).append('\'');
        sb.append(", thanaId='").append(thanaId).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getUpazilaId() {
        return upazilaId;
    }

    public void setUpazilaId(String upazilaId) {
        this.upazilaId = upazilaId;
    }

    public String getCityCorporationId() {
        return cityCorporationId;
    }

    public void setCityCorporationId(String cityCorporationId) {
        this.cityCorporationId = cityCorporationId;
    }

    public String getUnionOrUrbanWardId() {
        return unionOrUrbanWardId;
    }

    public void setUnionOrUrbanWardId(String unionOrUrbanWardId) {
        this.unionOrUrbanWardId = unionOrUrbanWardId;
    }

    public String createUserGeneratedDistrictId() {
        return divisionId + districtId;
    }

    public String createUserGeneratedUpazillaId() {
        return divisionId + districtId + upazilaId;
    }

    public String createUserGeneratedCityCorporationId() {
        return divisionId + districtId + upazilaId + cityCorporationId;
    }

    public String createUserGeneratedWardId() {
        return divisionId + districtId + upazilaId + cityCorporationId + unionOrUrbanWardId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getThanaId() {
        return thanaId;
    }

    public void setThanaId(String thanaId) {
        this.thanaId = thanaId;
    }


    public static String getAddressCodeForLevel(String code, int level) {
        if (code.length() < (level * 2)) {
            return ""; //fail instead?
        }

        int beginIndex = (level - 1) * 2;
        return code.substring(beginIndex, beginIndex+ 2);
    }

    public String getLocationCode() {
        StringBuilder locationBuilder = new StringBuilder();
        locationBuilder.append(divisionId.trim());
        locationBuilder.append(districtId.trim());
        locationBuilder.append(upazilaId.trim());
        if (StringUtils.isNotBlank(cityCorporationId)) locationBuilder.append(cityCorporationId.trim());
        if (StringUtils.isNotBlank(unionId)) locationBuilder.append(unionId.trim());
        if (StringUtils.isNotBlank(unionOrUrbanWardId)) locationBuilder.append(unionOrUrbanWardId.trim());
        return locationBuilder.toString();
    }
}
