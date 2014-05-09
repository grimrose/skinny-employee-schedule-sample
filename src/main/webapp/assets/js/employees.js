$(function () {

    defineStartDatePicker();
    defineEndDatePicker();

    // submit hook
    $('form').on('submit', onSubmit);

    $.each(["started", "left"], function (index, prefix) {
        applyToDatePicker(prefix);
    });
});


function defineStartDatePicker(){
    $('#startedDatePicker').datetimepicker({
        pickTime: false
    });
    $('#startedDatePicker').on('dp.show', function (e) {
        var moment = e.date;
        $('#startedMoment').val(moment.valueOf());
    });
    $('#startedDatePicker').on("dp.change", function (e) {
        var moment = e.date;
        $('#startedMoment').val(moment.valueOf());
    });
    $('#startedDatePicker').on("change", function (e) {
        var input = $("#startedMoment").val();
        if ($.isNumeric(input)) {
            var startMoment = moment(Number(input));
            $('#startedDatePicker').data("DateTimePicker").setDate(startMoment);
        } else {
            $("#startedMoment").val("");
        }
    });
    $('#startedClearButton').on("click", function (e) {
        $("#startedDate").val("");
        $("#startedMoment").val("");
    });
}

function defineEndDatePicker() {
    $('#leftDatePicker').datetimepicker({
        pickTime: false
    });
    $('#leftDatePicker').on('dp.show', function (e) {
        var moment = e.date;
        $('#leftMoment').val(moment.valueOf());
    });
    $('#leftDatePicker').on("dp.change", function (e) {
        var moment = e.date;
        $('#leftMoment').val(moment.valueOf());
    });
    $('#leftDatePicker').on("change", function (e) {
        var input = $("#leftMoment").val();
        if ($.isNumeric(input)) {
            var endMoment = moment(Number(input));
            $('#leftDatePicker').data("DateTimePicker").setDate(endMoment);
        } else {
            $("#leftMoment").val("");
        }
    });
    $('#leftClearButton').on("click", function (e) {
        $("#leftDate").val("");
        $("#leftMoment").val("");
    });
}

function applyToDatePicker(prefix) {
    var targetMoment = inputToMoment(prefix);
    if (!targetMoment) {
        return;
    }
    $('#' + prefix + 'DatePicker').data("DateTimePicker").setDate(targetMoment);
    $('#' + prefix + 'Moment').val(targetMoment.valueOf());
}

function inputToMoment(prefix) {
    var year = $("input[name='" + prefix + "_employment_year']").val();
    var month = $("input[name='" + prefix + "_employment_month']").val();
    var day = $("input[name='" + prefix + "_employment_day']").val();
    var date = moment(year + "-" + month + "-" + day, "YYYY-MM-DD");
    return date.isValid() ? date : null;
}

function onSubmit() {
    var form = $("form");
    $.each(["started", "left"], function (index, prefix) {
        applyMomentToHidden(prefix);
    });

    // TODO validate

    form.off('submit', onSubmit);
    form.submit();
}


function applyMomentToHidden(prefix) {
    var target = $("#" + prefix + "Moment").val();
    var targetMoment = $.isNumeric(target) ? moment(Number(target)) : null;

    var year = "";
    var month = "";
    var day = "";
    if (targetMoment) {
        year = targetMoment.year();
        month = targetMoment.month() + 1;
        day = targetMoment.date();
    }
    $("input[name='" + prefix + "_employment_year']").val(year);
    $("input[name='" + prefix + "_employment_month']").val(month);
    $("input[name='" + prefix + "_employment_day']").val(day);
}
