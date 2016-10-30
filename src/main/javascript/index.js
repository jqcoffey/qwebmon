import $ from 'jquery';

var refreshSecondsValue = 10;

$(document).ready(function() {
    refresh();
    refreshSecondsElement().html(refreshSecondsValue);
    setInterval(decrementRefreshSeconds, 1000);
    setInterval(function() {
        if (shouldRefresh()) {
            refresh();
        }
    }, 100);

    // bind keydown events to the whole document
    $(document).keydown(function(e) {
        var keyCode = e.which;
        console.log("have key code: " + keyCode);
        switch (keyCode) {
            // down arrow
            case 40:
                pause();
                highlightNextQuery();
                break;
            // j key
            case 74:
                pause();
                highlightNextQuery();
                break;
            // space bar
            case 32:
                pause();
                highlightNextQuery();
                break;
            // up arrow
            case 38:
                pause();
                highlightPrevQuery();
                break;
            // k key
            case 75:
                pause();
                highlightPrevQuery();
                break;
            // escape
            case 27:
                refresh();
        }
    })
});

function refresh() {
    systemStatus("vertica");
    runningQueries("vertica");
    refreshSecondsElement().html(refreshSecondsValue);
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

function systemStatus(target) {
    $.getJSON("/system-status/" + target, function(r) {
        $("#running-query-count").html(r.running_query_count);
        $("#average-queries").html(r.average_queries);
        $("#average-queries-unit").html(r.average_queries_unit);
    })
}

function runningQueries(target) {
    $.getJSON("/running-queries/" + target, function(r) {
        $("#running-queries table tbody").empty();
        $.each(r, function(k, v) {
            $(`<tr><td class="user ansi-bright-yellow-fg">${v.user}</td><td class="run-seconds">${v.run_seconds}</td><td class="query">${v.query}</td></tr>`)
                .appendTo("#running-queries table tbody");
        })
    })
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