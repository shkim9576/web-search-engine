var cheerio = require('cheerio')
var preq = require('preq'); // Promisified request library
var parseDublinCore = require('html-metadata').parseDublinCore;
var fs = require('fs');
var spell = require('spell');
var solr = require('solr-client');
var solrClient = solr.createClient({
    host: 'localhost',
    port: 8983,
    core: 'pageRankCore'
});


// Build local file to URL map
var readMap = require('readline').createInterface({
    input: fs.createReadStream('mapLATimesDataFile.csv')
});

var fileUrlMap = {};
readMap.on('line', function (line) {
    var arr = line.split(",");
    fileUrlMap[arr[0]] = arr[1];
});


// Load spell correction data
var dict = spell();
fs.readFile('big.txt', 'utf8', function (err, data) {
    console.log("spellcheck: loading big.txt");
    if (err) {
        return console.log(err);
    }
    dict.load(data);
    console.log("spellcheck: big.txt is loaded");
});


function getSpellCorrection(search) {
    var splits = search.split(" ");
    var result = [];

    // correct every single word in query sentence
    for (var i = 0; i < splits.length; i++) {
        var array = dict.suggest(splits[i]);
        array.sort(function (a, b) {
            return parseInt(b.score) - parseInt(a.score);  // desc order
        });

        if (array.length > 0) {
            result.push(array[0].word); // the highest score
        } else {
            result.push(splits[i]);
        }
    }

    return result.join(' ');
}

// "President Trump": it is ok to return both or any one.
function getSnippets(search, fileID, description, title, url) {

    var file = fs.readFileSync('../crawl_data/LATimesDownloadData/' + fileID, "utf8").toString();

    var $ = cheerio.load(file)
    var document = $.text();
    var sentences = document.split(".");

    var splits = search.split(" "); // queries with several terms

    for (var i = 0; i < sentences.length; i++) {
        for (var k = 0; k < splits.length; k++) {
            if (sentences[i].toLowerCase().includes(splits[k].toLowerCase()) && !sentences[i].includes('-webkit') && !sentences[i].includes('font-family') && !sentences[i].includes('; }') && !sentences[i].includes('com/') && !sentences[i].includes('"}') && !sentences[i].includes('html{') && !sentences[i].includes('©') && !sentences[i].includes(');') && !sentences[i].includes('};') && !sentences[i].includes('\';') && !sentences[i].includes('")') && !sentences[i].includes(':{') && !sentences[i].includes('{"') && !sentences[i].includes('\'/') && !sentences[i].includes('":"')) {
                console.log("snippets: found = " + sentences[i].trim());
                return sentences[i].trim();
            }
        }
    }

    // if body does not have one, look for description
    for (var y = 0; y < description.length; y++) {
        for (var k = 0; k < splits.length; k++) {
            if (description[y].toLowerCase().includes(splits[k].toLowerCase())) {
                console.log("snippets: found in description = " + description[y].trim());
                return description[y].trim();
            }
        }
    }

    // look for links
    var links = [];
    $('a').each(function (i, elem) {
        links[i] = $(this).text();
    });

    // look for title
    for (var k = 0; k < splits.length; k++) {
        if (title.toLowerCase().includes(splits[k].toLowerCase())) {
            console.log("snippets: found in title = " + title.trim());
            return title.trim();
        }
    }

    // from link
    for (i = 0; i < links.length; i++) {
        for (var k = 0; k < splits.length; k++) {
            if (links[i].toLowerCase().includes(splits[k].toLowerCase())) {
                console.log("snippets: found in link = " + links[i].trim());
                return links[i].trim();
            }
        }
    }

    // from url
    /*
    var scrape = require('html-metadata');
    var fromOpenGraph;
    scrape(url, function (error, metadata) {
        for (var k = 0; k < splits.length; k++) {
            if (metadata.openGraph.title.toLowerCase().includes(splits[k].toLowerCase())) {
                console.log("snippets: found in metadata = " + metadata.openGraph.title.trim());
                fromOpenGraph = metadata.openGraph.title.trim();
            }
        }
    });

    if (fromOpenGraph != undefined) {
        return fromOpenGraph;
    }
    */

    console.log("snippet: no snippet");
    return search;
}

exports.query = function (req, res) {
    var search = req.body.query;
    var algorithm = req.body.algorithm;
    var query;

    if (algorithm == 'lucene') {
        query = 'q=' + encodeURI(search) + '&wt=json';
    } else {
        query = 'q=' + encodeURI(search) + '&sort=' + encodeURI('pageRankFile desc') + '&wt=json';
    }

    solrClient.search(query, function (err, obj) {

        var results = [];
        obj.response.docs.forEach(function (doc) {
            var title = (doc.title == undefined) ? "" : doc.title[0];
            var fileID = doc.id.split(/[/ ]+/).pop();
            var url = fileUrlMap[fileID];
            var docID = (doc.id == undefined) ? "" : doc.id;
            var description = (doc.description == undefined) ? "" : doc.description;
            var snippets = getSnippets(search, fileID, description, title, url);

            var result = {
                title: title,
                url: url,
                docID: docID,
                description: description,
                snippets: snippets
            };

            results.push(result);
        });

        var spellCorrection = getSpellCorrection(search);

        res.send({
            "searchCount": obj.response.numFound,
            "spellCorrection": spellCorrection,
            "results": results
        });
    })
}