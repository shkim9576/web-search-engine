function buildResultList(array) {

    $("#result table").remove();

    var table = $('<table>');
    $.each(array,
        function (i, v) {
            var tr = $('<tr>');
            var index = 1 + parseInt(i);
            tr.append('<td>' + index + '</td>');

            var ul = $('<ul>');
            ul.append('<li>' + '<b>TITLE:</b>  ' + '<a href=\"' + v.url + '\">' + v.title + '</a>' + '</li>');
            ul.append('<li>' + '<b>URL:</b>  ' + '<a href=\"' + v.url + '\">' + v.url + '</a>' + '</li>');
            ul.append('<li>' + '<b>DOCID:</b>  ' + v.docID + '</li>');
            ul.append('<li>' + '<b>DESCRIPTION:</b>  ' + v.description + '</li>');
            ul.append('<li>' + '<b>SNIPPETS:</b>  ' + v.snippets + '</li>');

            tr.append(ul);
            table.append(tr);
        }
    );

    $("#result").append(table);
}

function buildSuggestionList(array) {

    $("#suggestionList").empty();
    filteredArray = removeDuplicates(array);

    var suggestionRsult = [];
    $.each(filteredArray,
        function (i, v) {
            var splits = v.trim().split(" ");
            var lastWord = splits[splits.length - 1];
            if (isAlphanumeric(lastWord) && !isSameAsQuery(v)) {
                suggestionRsult.push(v);
                $("#suggestionList").append('<option data-value=' + lastWord + '>' + v + '</option>');
            }
        }
    );

    console.log("suggestion result = " + suggestionRsult);
}

function removeDuplicates(array) {
    var seen = {};
    return array.filter(function(item) {
        return seen.hasOwnProperty(item) ? false : (seen[item] = true);
    });
}

function isSameAsQuery(str) {
    return str == $("#searchTextBox").val();
}

function isAlphanumeric(str) {
    return /^[a-zA-Z0-9- ]*$/.test(str);
}

$(document).ready(function () {

    $("#spellCorrectionLabel").hide();

    $("#submitQueryBtn").click(function (e) {
        e.preventDefault();
        $.ajax({
            type: "POST",
            url: "http://localhost:3000/search",
            data: {
                query: $("#searchTextBox").val(),
                algorithm: $('input[name=algorithm]:checked', '#queryForm').val()
            },
            success: function (response) {
                // Spelling correction
                if (response.spellCorrection != '' && response.spellCorrection.toLowerCase() != $("#searchTextBox").val().toLowerCase()) {
                    console.log("spellcheck: suggestion = " + response.spellCorrection);
                    $("#spellCorrection").text(response.spellCorrection);
                    $("#spellCorrectionLabel").show();
                    $("#spellCorrection").show();
                } else {
                    $("#spellCorrection").hide();
                    $("#spellCorrectionLabel").hide();
                }

                if (response.searchCount > 0) {
                    // Result label
                    $("#resultLabel").text("Results 1 - 10 of " + response.searchCount);
                    $("#resultLabel").show();

                    // Top 10 results
                    buildResultList(response.results);
                    $("#result").show();
                } else {
                    $("#resultLabel").text("found 0");
                    $("#resultLabel").show();
                    $("#result").hide();
                }
            },
            error: function (result) {
                console.error("error: " + result);
            }
        });
    });

    $("#searchTextBox").keyup(function () {
        $.ajax({
            type: "POST",
            url: "http://localhost:3000/suggestions",
            data: {
                query: $("#searchTextBox").val().toLowerCase(),
                algorithm: $('input[name=algorithm]:checked', '#queryForm').val()
            },
            success: function (response) {
                var resultStr = JSON.stringify(response);
                console.log("success: " + resultStr);
                buildSuggestionList(response.results);
            },
            error: function (result) {
                console.error("error: " + result);
            }
        });
    })

});
