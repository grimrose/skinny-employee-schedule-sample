$(function () {
    defineStartDateTimePicker();

    defineEndDateTimePicker();

    // submit hook
    $("form").bind('submit', onSubmit);

    $.each(["start", "end"], function (index, prefix) {
        applyToDateTimePicker(prefix);
    });
});

function defineStartDateTimePicker() {
    $('#startDateTimePicker').datetimepicker();
    $('#starDateTimePicker').on('dp.show', function (e) {
        var moment = e.date;
        $('#startMoment').val(moment.valueOf());
    });
    $('#startDateTimePicker').on("dp.change", function (e) {
        var moment = e.date;
        $('#startMoment').val(moment.valueOf());
    });
    $('#startDateTimePicker').on("change", function (e) {
        var input = $("#startMoment").val();
        if ($.isNumeric(input)) {
            var startMoment = moment(Number(input));
            $('#startDateTimePicker').data("DateTimePicker").setDate(startMoment);
        } else {
            $("#startMoment").val("");
        }
    });
    $('#startClearButton').on("click", function (e) {
        $("#startDateTime").val("");
        $("#startMoment").val("");
    });
}

function defineEndDateTimePicker() {
    $('#endDateTimePicker').datetimepicker();
    $('#endDateTimePicker').on('dp.show', function (e) {
        var moment = e.date;
        $('#endMoment').val(moment.valueOf());
    });
    $('#endDateTimePicker').on("dp.change", function (e) {
        var moment = e.date;
        $('#endMoment').val(moment.valueOf());
    });
    $('#endDateTimePicker').on("change", function (e) {
        var input = $("#endMoment").val();
        if ($.isNumeric(input)) {
            var endMoment = moment(Number(input));
            $('#endDateTimePicker').data("DateTimePicker").setDate(endMoment);
        } else {
            $("#endMoment").val("");
        }
    });
    $('#endClearButton').on("click", function (e) {
        $("#endDateTime").val("");
        $("#endMoment").val("");
    });
}

function applyToDateTimePicker(prefix) {
    var targetMoment = inputToMoment(prefix);
    if (!targetMoment) {
        return;
    }
    $('#' + prefix + 'DateTimePicker').data("DateTimePicker").setDate(targetMoment);
    $('#' + prefix + 'Moment').val(targetMoment.valueOf());
}

function inputToMoment(prefix) {
    var year = $("input[name='" + prefix + "_year']").val();
    var month = $("input[name='" + prefix + "_month']").val();
    var day = $("input[name='" + prefix + "_day']").val();
    var hour = $("input[name='" + prefix + "_hour']").val();
    var min = $("input[name='" + prefix + "_minute']").val();
    var dateTime = moment(year + "-" + month + "-" + day + " " + hour + ":" + min, "YYYY-MM-DD HH:mm");
    return dateTime.isValid() ? dateTime : null;
}

function onSubmit() {
    var form = $("form");
    $.each(["start", "end"], function (index, prefix) {
        applyMomentToHidden(prefix);
    });

    // TODO validate

    form.unbind('submit', onSubmit);
    form.submit();
}

function applyMomentToHidden(prefix) {
    var target = $("#" + prefix + "Moment").val();
    var targetMoment = $.isNumeric(target) ? moment(Number(target)) : null;

    var year = "";
    var month = "";
    var day = "";
    var hour = "";
    var min = "";
    var sec = "";
    if (targetMoment) {
        year = targetMoment.year();
        month = targetMoment.month() + 1;
        day = targetMoment.date();
        hour = targetMoment.hour();
        min = targetMoment.minute();
        sec = 0;
    }
    $("input[name='" + prefix + "_year']").val(year);
    $("input[name='" + prefix + "_month']").val(month);
    $("input[name='" + prefix + "_day']").val(day);
    $("input[name='" + prefix + "_hour']").val(hour);
    $("input[name='" + prefix + "_minute']").val(min);
    $("input[name='" + prefix + "_second']").val(sec);
}
