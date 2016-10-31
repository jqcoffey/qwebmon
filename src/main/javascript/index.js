import $ from 'jquery';
import 'mousetrap';

var refreshSecondsValue = 10;
var longQueryThresholdSeconds = 60;

$(document).ready(function() {
    refresh();
    refreshSecondsElement().html(refreshSecondsValue);
    setInterval(decrementRefreshSeconds, 1000);
    setInterval(function() {
        if (shouldRefresh()) {
            refresh();
        }
    }, 100);

    var runningQueriesContext = $('#running-queries-context');
    runningQueriesContext.focus();
    var runningQueriesContextElem = runningQueriesContext.get(0);

    Mousetrap(runningQueriesContextElem).bind(['up', 'k'], function(e) {
        pause();
        highlightPrevQuery();
        return false;
    });

    Mousetrap(runningQueriesContextElem).bind(['down', 'j', 'space'], function(e) {
        pause();
        highlightNextQuery();
    });
    Mousetrap(runningQueriesContextElem).bind('escape', function(e) {
        refresh();
    });
});

function refresh() {
    refreshSecondsElement().html(refreshSecondsValue);

    $.getJSON("/refresh/fake-db", function(r) {
        var systemStatus = r.system_status;
        var runningQueries = r.running_queries;

        $("#running-query-count").html(systemStatus.running_query_count);
        $("#average-queries").html(systemStatus.average_queries);
        $("#average-queries-unit").html(systemStatus.average_queries_unit);

        $("#running-queries table tbody").empty();
        $.each(runningQueries, function(k, v) {
            var runSeconds = v.run_seconds;
            if (v.run_seconds >= longQueryThresholdSeconds) {
                runSeconds = `<span class="long-query">${v.run_seconds}</span>`;
            }
            $(`<tr><td class="user">${v.user}</td><td class="run-seconds">${runSeconds}</td><td class="query">${v.query}</td></tr>`)
                .appendTo("#running-queries table tbody");
        })
    });
    resume();
}

function pause() {
    $("#paused").show()
}

function resume() {
    $("#paused").hide()
}

function isPaused() {
    return !$("#paused").is(":hidden")
}

function refreshSecondsElement() {
    return $("#refresh-seconds")
}

function refreshInSeconds() {
    return refreshSecondsElement().text();
}

function shouldRefresh() {
    return (refreshInSeconds() == 0);
}

function decrementRefreshSeconds() {
    if (!isPaused()) {
        var refreshSeconds = refreshInSeconds();
        if (refreshSeconds > 0) {
            refreshSecondsElement().html(refreshSeconds - 1);
        }
    }
}

function highlightNextQuery() {
    var queryRows = $("#running-queries table tbody tr");
    if (queryRows.filter(".highlighted").length == 0) {
        highlightRow(queryRows, 0)
    } else {
        var highlightedIdx = $("#running-queries table tbody tr.highlighted").index();
        if (highlightedIdx == queryRows.length - 1) {
            swapHighlightedRows(queryRows, highlightedIdx, 0);
        } else {
            swapHighlightedRows(queryRows, highlightedIdx, highlightedIdx + 1);
        }
    }
}

function highlightPrevQuery() {
    var queryRows = $("#running-queries table tbody tr");
    if (queryRows.filter(".highlighted").length == 0) {
        highlightRow(queryRows, queryRows.length - 1)
    } else {
        var highlightedIdx = $("#running-queries table tbody tr.highlighted").index();
        if (highlightedIdx == 0) {
            swapHighlightedRows(queryRows, 0, queryRows.length - 1);
        } else {
            swapHighlightedRows(queryRows, highlightedIdx, highlightedIdx - 1);
        }
    }
}

function swapHighlightedRows(rows, from, to) {
    unhighlightRow(rows, from);
    highlightRow(rows, to);
}

function highlightRow(rows, idx) {
    rows.eq(idx).addClass("highlighted")
}

function unhighlightRow(rows, idx) {
    rows.eq(idx).removeClass("highlighted")
}