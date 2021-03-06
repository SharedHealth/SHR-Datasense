var generateCronExpression = function(unit, value, expressionTextBox) {
   var cronExpression;
   if (unit === 'HOURS') {
      cronExpression = '0 0 0/' + value + ' * * ?';
   } else if (unit === 'DAYS') {
      cronExpression = '0 0 0 0/' + value + ' * ?';
   }
   expressionTextBox.val(cronExpression);
}

var loadComponents = function() {
   $('.interval_unit').change(function() {
      var unit = $(this).val();
      var value = $(this).parent().find('.interval_value').val();
      var expressionTextBox = $(this).parent().find('.expression');
      generateCronExpression(unit, value, expressionTextBox);
   });

   $('.interval_value').change(function() {
      var value = $(this).val();
      var unit = $(this).parent().find('.interval_unit').val();
      var expressionTextBox = $(this).parent().find('.expression');
      generateCronExpression(unit, value, expressionTextBox);
   });
}

$(document).ready(loadComponents);

$(document).ready(function () {
    $(function(){
        var current_page_path = location.pathname;
        $( "a" ).each(function() {
            if ($(this).attr("href") !== "#") {
                var hrefPath = $(this).prop("pathname").split("/");
                var target_path = "/" + hrefPath[1];
                if (current_page_path.startsWith(target_path)) {
                    $('nav a').parents('li, ul').removeClass('active');
                    $(this).parent('li').addClass('active');
                    return false;
                }
            }
        });
    });
});