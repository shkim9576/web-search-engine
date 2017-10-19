module.exports = function (app) {
    var suggestion = require('../controllers/suggestion.server.controller');
    app.post('/suggestions', suggestion.suggestions);
};