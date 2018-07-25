var fs = require("fs");

var $ = function (selector) {
    return document.querySelector(selector);
}

var array = [];
var array2 = [];
var array3 = [];
var array4 = [];
var logElement;
var inputElement;
var dataMode = 'worte';

var modes = ['worte', 'deu_eng', 'ara_eng', 'kur_deu'];

var setMode = function (m) {
    dataMode = m;
    for (var i = 0, len = modes.length; i < len; i++) {
        $('#'+modes[i]).style.color = "white";
    }
    $('#'+m).style.color = "blue";
};

var startSearch = function (q) {
    if (dataMode == 'deu_eng') {
        startSearchTei(q, array2);
    } else if (dataMode == 'ara_eng') {
        startSearchTei(q, array3);
    } else if (dataMode == 'kur_deu') {
        startSearchTei(q, array4);
    } else {
        startSearchWorte(q);
    }
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
    $("#output").innerHTML = dummy;
    $("#output").scrollTop = logElement.scrollHeight;
    
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
    $("#output").innerHTML = dummy;
    $("#output").scrollTop = logElement.scrollHeight;
    
};

onload = function() {
    logElement = $("#output");
    inputElement = $("#query");
    
    fs.readFile('raw/openthesaurus.txt', 'utf8', function (err,data) {
        array = data.split("\n");
    });
    
    fs.readFile('raw/deu_eng.tei', 'utf8', function (err,data) {
        array2 = data.split("\n");
    });
    
    fs.readFile('raw/ara_eng.tei', 'utf8', function (err,data) {
        array3 = data.split("\n");
    });
    
    fs.readFile('raw/kur_deu.tei', 'utf8', function (err,data) {
        array4 = data.split("\n");
    });
    
    setMode('worte');
};