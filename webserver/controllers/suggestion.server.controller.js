var unirest = require('unirest');

var coreName = 'pageRankCore';
var solrBaseUrl = 'http://localhost:8983/solr/';

// when the query is "President Tr",
// it should obtain suggestions for "Tr" and then provide "President Trump" as the suggestion
exports.suggestions = function (req, res) {
    var search = req.body.query.trim();
    var splits = search.split(" ");
    var lastWord = splits[splits.length - 1];
    var previous = '';
    if (splits.length > 1) {
        previous = search.substring(0, search.trim().length - lastWord.length).trim();
    }
    console.log("suggestions: search term = " + search);
    console.log("suggestions: last word = " + lastWord);
    console.log("suggestions: previous = " + previous);

    var algorithm = req.body.algorithm;
    var query;

    if (algorithm == 'lucene') {
        query = 'q=' + encodeURI(lastWord) + '&wt=json';
    } else {
        query = 'q=' + encodeURI(lastWord) + '&sort=' + encodeURI('pageRankFile desc') + '&wt=json';
    }

    unirest.get(solrBaseUrl + coreName + '/suggest?' + query)
        .end(function (response) {
            var solrResponse = JSON.parse(response.body);
            var suggest = solrResponse.suggest.suggest[lastWord];
            if(suggest != undefined) {
                var suggestions = suggest.suggestions;
                var returnArray = [];
                for (var i=0; i<suggestions.length; i++) {
                    if (previous.length > 0) {
                        returnArray.push(previous + ' ' + suggestions[i].term);
                    } else {
                        returnArray.push(suggestions[i].term);
                    }
                }

                console.log("suggestion: results = " + returnArray);
                res.send({"results": returnArray});
            } else {
                console.log("suggestion: results is empty");
                res.send({"results": []});
            }
        });

}