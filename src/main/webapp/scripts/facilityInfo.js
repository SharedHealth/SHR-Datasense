function FacilityInformations(){

    $('#facilityInfoForm').submit(function(e) {
        var targetUrl = "/metadata/encounter";
        var selectedFacility = $('input[name="selectedFacilities"]:checked').val();
        var postData ={"facilityId":selectedFacility};
        $.ajax({
            type: "POST",
            url: targetUrl,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(postData),
            success: function(response){
                var d = new Date(response);
                var formattedDate = d.getDate() + "-" + (d.getMonth() + 1) + "-" + d.getFullYear();
                var hours = (d.getHours() < 10) ? "0" + d.getHours() : d.getHours();
                var minutes = (d.getMinutes() < 10) ? "0" + d.getMinutes() : d.getMinutes();
                var formattedTime = hours + ":" + minutes;

                formattedDate = formattedDate + " " + formattedTime;
                alert("Response: createdAt: "+formattedDate);
            },
            dataType: "json",
            error: function(e){
                alert(e);
            }
        });
        e.preventDefault();
    });

}

var searchFacility = function(facilities,searchText){
    for(i=0;i<facilities.length;i++){
        $.each(facilities[i], function(key, value) {
            if(key=="facilityName" || key=="facilityId"){
                if(value == searchText){
                    var facilityId = facilities[i].facilityId;
                    var selectedFacilities = "selectedFacilities";
                    $("input[name=selectedFacilities][value=" + facilityId + "]").attr('checked', 'checked');
                    var selectedFacility = $('input[name="selectedFacilities"]:checked').val();
                    alert(selectedFacility);
                }
            }
        });
    }
}
