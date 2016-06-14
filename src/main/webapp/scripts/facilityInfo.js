function FacilityInformations() {
    $("#searchTxt").keyup(function(event){
        if(event.keyCode == 13){
            $("#searchBtn").click();
        }
    });

    $("#searchBtn").click(function() {
        var searchText = $("#searchTxt").val();
        searchFacility(searchText);
        $('.collapse').collapse("hide");
    });

    $(document).on('click','input[name=selectedFacilities]',function(e){
        $('#lastEncounter').attr("hidden",true);
        $("#encounterDate").attr("hidden", false);
        $("#accordion").attr("hidden",false);
        $('#visitWithCount').attr("hidden", true);
        $("#visitDate").val("");
        clearErrors("#errorBlock1");
    });

    $("input[name=searchBy]").bind("click", function(e) {
       document.getElementById("searchTxt").value = "";
       clearErrors();
       $("#avalilableFacilities").attr("hidden",true);
       $("#accordion").attr("hidden",true);
       $('#encounterDate').attr("hidden",true);
       $('#lastEncounter').attr("hidden",true);
    });

    $('#getEncounterDate').bind("click", function(e) {
        document.getElementById("searchTxt").value = "";
        var selectedFacility = $('input[name="selectedFacilities"]:checked').val();
        var selectedFacilityName =$('input[name="selectedFacilities"]:checked').attr('id');
        var targetUrl = "/facility/" + selectedFacility + "/lastEncounterDate";
        $.ajax({
            type: "GET",
            url: targetUrl,
            success: function(response){
                clearErrors();
                var d = new Date(response);
                var formattedDate = d.getDate() + "-" + (d.getMonth() + 1) + "-" + d.getFullYear();
                var hours = (d.getHours() < 10) ? "0" + d.getHours() : d.getHours();
                var minutes = (d.getMinutes() < 10) ? "0" + d.getMinutes() : d.getMinutes();
                var formattedTime = hours + ":" + minutes;

               formattedDate = formattedDate + ", " + formattedTime;
               var result = {"facilityName":selectedFacilityName,"facilityId":selectedFacility,"encounterDate":formattedDate}
               $('#searchResultsLastEncounter').hide();
               var template = $('#template_search_lastencounter').html();
               Mustache.parse(template);
               var rendered = Mustache.render(template, result);
               $('#searchResultsLastEncounter').html(rendered);
               $('#lastEncounter').attr("hidden",false);
               $('#searchResultsLastEncounter').show();
               $("#encounterDate").attr("hidden", true);
            },
            error: function(e){
                showErrors(e);
            }
        });
        e.preventDefault();
    });

    $('#getVisitType').bind("click",function(e){
        clearErrors();
        $('#getVisitTypeButton').attr("hidden",true);
        $('#visitWithCount').attr("hidden", true);
        document.getElementById("searchTxt").value = "";
        var selectedFacility = $('input[name="selectedFacilities"]:checked').val();
        var date = $('#visitDate').val();
        var targetUrl =  "/facility/" + selectedFacility + "/visitTypes/forDate?date=" + date;
        $.ajax({
            type: "GET",
            url: targetUrl,
            success: function(response){
               if(response.length==0){
                   showErrors("No visit type for the date entered","#errorBlock1");
               }else{
                   var template = $('#template_scheduled_jobs_results').html();
                   var template = $("#template_visit_type_with_count").html();
                   Mustache.parse(template);
                   var rendered = Mustache.render(template,response);
                   $('#visitTypeWithCount tbody').html(rendered);
                   $('#visitWithCount').attr("hidden", false);
               }
            },
            error: function(e){
                showErrors(e)
            }
        });
    });

    $('#visitDate').datepicker({
             autoclose: true,
             onRender: function(dateTemp) {
                var nowTemp = new Date();
                var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
                var date = new Date(dateTemp.getFullYear(), dateTemp.getMonth(), dateTemp.getDate(), 0, 0, 0, 0);
                return now.valueOf() <= date.valueOf() ? 'disabled' : '';
             },
             format: 'dd/mm/yyyy'
    }).on('changeDate', function (e) {
       $('#getVisitTypeButton').attr("hidden",false);
       $('#visitWithCount').attr("hidden", true);
       clearErrors("#errorBlock1");
    });

    var validateFacilityId = function(searchTxt){
        if(searchTxt.match(/^\d+$/)) {
            return true;
        }
        else{
            return false;
        }
    }

    var getFacilities = function(targetUrl){
        $.ajax({
           type: "GET",
           url: targetUrl,
           success: function(result){
                clearErrors();
                if(result.length==0){
                    showErrors("No facility is available for given facility id/name");
                }
                else{
                   $('#searchResultsContainerForFacility').hide();
                   var template = $('#template_search_facilities').html();
                   Mustache.parse(template);
                   var rendered = Mustache.render(template, result);
                   $('#searchResultsContainerForFacility').html(rendered);
                   $('#searchResultsContainerForFacility').show();
                   $("#avalilableFacilities").attr("hidden",false);

                }
           }
        });
    }

    var searchFacility = function(searchTxt){
        $('#lastEncounter').attr("hidden",true);
        $("#avalilableFacilities").attr("hidden",true);
        $("#accordion").attr("hidden",true);
        var targetUrl;
        var selectedOption = $('input[name="searchBy"]:checked').val();
        if(selectedOption == "FacilityId"){
            if(validateFacilityId(searchTxt)){
                targetUrl = "/facility/search?id=" + searchTxt;
                getFacilities(targetUrl);
            }
            else{
                showErrors("Incorrect Facility Id. Facility ID contains only digits.");
            }
        }
        else if(selectedOption == "FacilityName"){
                targetUrl = "/facility/search?name=" + searchTxt;
                getFacilities(targetUrl);
        }

    }
}