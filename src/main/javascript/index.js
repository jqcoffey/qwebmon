import $ from 'jquery';

function systemStatus(target) {
    $.getJson("/system-status/" + target, function(r) {
        console.log(r)
    })
}

function runningQueries(target) {
    $.getJson("/running-queries/" + target, function(r) {
        console.log(r)
    })
}