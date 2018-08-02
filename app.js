var fs = require("fs");
var path = require('path'); 
var $ = require("jquery");
var request = require('ajax-request');

//var $ = require('jquery'), XMLHttpRequest = require('xmlhttprequest').XMLHttpRequest;
//var $ = function (selector) {
//    return document.querySelector(selector);
//}

var array = [];
var array2 = [];
var array3 = [];
var logElement;
var inputElement;
var dataMode = 'worte';

var modes = ['worte', 'deu_eng', 'deepl'];

var setMode = function (m) {
    dataMode = m;
    for (var i = 0, len = modes.length; i < len; i++) {
        $('#'+modes[i]).css({"color": "white"});
    }
    $('#'+m).css({"color": "blue"});
};

var startSearch = function (q) {
    if (dataMode == 'deu_eng') {
        startSearchTei(q, array2);
    } else if (dataMode == 'deepl') {
        startSearchDeepl(q);
    } else {
        startSearchWorte(q);
    }
};

var startSearchDeepl = function (q, arr) {
    q = q.value.trim();
    if (q.length < 3) return;

    request({
        url: 'https://www2.deepl.com/jsonrpc',
        method: 'GET',
        data: {
            ajax: "1",
            delay: "800",
            eventkind: "change",
            forleftside: "true",
            jsStatus: "0",
            kind: "full",
            onlyDictEntries: "1",
            text: q,
            source: "german",
            translator: "dnsof7h3k2lgh3gda"
            }
        }, 
        function(err, res, body) {
            console.log(err);
            console.log(res);
            console.log(body);
            logElement.html(body);
            logElement.scrollTop = logElement.scrollHeight;
        }
    );
};

var startSearchTei = function (q, arr) {
    q = q.value.trim();
    if (q.length < 3) return;
    subarray = [];
    hit = '';
    for (var i = 0, len = arr.length; i < len; i++) {
        if (arr[i].indexOf('<orth>') > -1) {
            hit = arr[i];
            continue;
        }
        
        if (arr[i].indexOf('<quote>') > -1) {
            entry = hit+';'+array2[i];
            entry = entry.replace('<orth>', '');
            entry = entry.replace('</orth>', '');
            entry = entry.replace('<quote>', '');
            entry = entry.replace('</quote>', '');
            if (entry.toLowerCase().indexOf(q.toLowerCase()) > -1) {
                subarray.push(entry);
            }
        }
    }
    subarray.sort();
    j=0;
    dummy='';
    var argRegEx = new RegExp(q, 'gi');
    for (var i = 0, len = subarray.length; i < len; i++) {
        var block = subarray[i].match(argRegEx);
        tmp = subarray[i].replace(block[0], '<span class="mark">'+block[0]+'</span>');
        tmp = tmp.split(';');
        if (j%2==0) {
            dummy += '<div class="st"><b>' + tmp[0] + "</b><br />";
        } else {
            dummy += '<div class="nd"><b>' + tmp[0] + "</b><br />";
        }
        dummy += tmp.slice(1).join(' &middot; ') + "</div>";
        j++;
    }
    logElement.html(dummy);
    logElement.scrollTop = logElement.scrollHeight;    
};

var startSearchWorte = function (q) {
    q = q.value.trim();
    if (q.length < 3) return;
    subarray = [];
    for (var i = 0, len = array.length; i < len; i++) {
        if (array[i].startsWith('#')) continue;
        if (array[i].toLowerCase().indexOf(q.toLowerCase()) > -1) {
            subarray.push(array[i]);
        }
    }
    subarray.sort();
    j=0;
    dummy='';
    var argRegEx = new RegExp(q, 'gi');
    for (var i = 0, len = subarray.length; i < len; i++) {
        var block = subarray[i].match(argRegEx);
        tmp = subarray[i].replace(block[0], '<span class="mark">'+block[0]+'</span>');
        tmp = tmp.split(';');
        if (j%2==0) {
            dummy += '<div class="st"><b>' + tmp[0] + "</b><br />";
        } else {
            dummy += '<div class="nd"><b>' + tmp[0] + "</b><br />";
        }
        dummy += tmp.slice(1).join(' &middot; ') + "</div>";
        j++;
    }
    logElement.html(dummy);
    logElement.scrollTop = logElement.scrollHeight;
};

onload = function() {
    logElement = $("#output");
    inputElement = $("#query");
    
    fs.readFile(path.join(__dirname, 'raw', 'openthesaurus.txt'), 'utf8', function (err,data) {
        array = data.split("\n");
    });
    
    fs.readFile(path.join(__dirname, 'raw', 'deu_eng.tei'), 'utf8', function (err,data) {
        array2 = data.split("\n");
    });
    
    setMode('worte');
};