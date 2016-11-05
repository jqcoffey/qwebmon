import $ from 'jquery';
import 'mousetrap';
import vkbeautify from 'vkbeautify/index';

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
        return false;
    });

    Mousetrap(runningQueriesContextElem).bind(['enter', 'l', 'right'], function(e) {
        inspectQuery();
        return false;
    });

    Mousetrap(runningQueriesContextElem).bind(['h', 'left'], function(e) {
        $("#inspect-query").hide();
        $("#running-queries").show();
        return false;
    });

    Mousetrap.bind('escape', function(e) {
        refresh();
        return false;
    });
});

function refresh() {
    refreshSecondsElement().html(refreshSecondsValue);

    var target = window.location.search.substring(1);

    $.getJSON(`/refresh/${target}`, function(r) {
        $("#running-query-count").html(r.running_query_count);

        $("#running-queries table tbody").empty();
        $.each(r.running_queries, function(k, v) {
            var runSeconds = v.run_seconds;
            if (v.run_seconds >= longQueryThresholdSeconds) {
                runSeconds = `<span class="long-query">${v.run_seconds}</span>`;
            }
            $(`<tr><td class="user">${v.user}</td><td class="run-seconds">${runSeconds}</td><td class="hostname">${v.hostname}</td><td class="query">${v.query}</td></tr>`)
                .appendTo("#running-queries table tbody");
        })
    });
    resume();
}

function pause() {
    $("#paused").show()
}

function resume() {
    $("#paused").hide();
    $("#inspect-query").hide();
    $("#running-queries").show();
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

function inspectQuery() {
    $("#running-queries").hide();
    var query = $("#running-queries table tbody tr.highlighted td.query").eq(0).text();
    $('code#inspect-query-formatted').html(vkbeautify.sql(query));
    $("#inspect-query").show();
}