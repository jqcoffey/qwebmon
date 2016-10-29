import $ from 'jquery';

$(document).ready(function() {
    systemStatus("vertica");
    runningQueries("vertica");
})

function systemStatus(target) {
    $.getJSON("/system-status/" + target, function(r) {
        console.log(r);
        $("#running-query-count").html(r.running_query_count);
        $("#average-queries").html(r.average_queries);
        $("#average-queries-unit").html(r.average_queries_unit);
    })
}

function runningQueries(target) {
    $.getJSON("/running-queries/" + target, function(r) {
        console.log(r);
        $.each(r, function(k, v) {
            $("<tr><td>" + v.user + "</td><td>" + v.run_seconds + "</td><td>" + v.query + "</td></tr>")
                .appendTo("#running-queries table");
        })
    })
}