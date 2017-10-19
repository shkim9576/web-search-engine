module.exports = function (app) {
    var query = require('../controllers/query.server.controller');
    app.post('/search', query.query);
};