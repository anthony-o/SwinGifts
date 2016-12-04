'use strict';

var express = require('express'),
    path = require('path'),
    morgan = require('morgan'),
    bodyParser = require('body-parser');
var app = express();

app.use(morgan('dev'));

app.use(express.static(path.join(__dirname, '../webapp')));
app.use(bodyParser.json());


app.get('/api/wishLists', function (req, res) {
    var wishList = [
        {
            id: 1,
            url: 'https://shop.lego.com/fr-FR/L-Etoile-de-la-Mort-75159',
            name: "L'Étoile de la Mort™"
        },
        {
            id: 2,
            url: 'https://www.trictrac.net/jeu-de-societe/roll-for-the-galaxy-1',
            name: 'Roll for the Galaxy'
        },
        {
            id: 3,
            name: "L'avion de barbie"
        }
    ];
    res.send([
        {
            id: 1,
            person: {
                id: 1,
                name: 'Anthony'
            },
            wishItems: wishList
        },
        {
            id: 2,
            person: {
                id: 2,
                name: 'Mickaël'
            },
            wishItems: wishList
        }
    ]);
});

app.get('/api/events/:eventId', function (req, res) {
    res.send({
        name: 'Noël entre cousins'
    });
});

app.post('/api/wishItems', function (req, res) {
    console.log(req.body);
    setTimeout(function() {
        res.send('1'); // send the new ID of new wishItem
    }, 1000);
});

app.post('/api/persons', function (req, res) {
    console.log(req.body);
    console.log(JSON.stringify(req.query));
    setTimeout(function() {
        res.send({personId: 4, wishListId: 4}); // send the new ID of new wishItem
    }, 1000);
});


app.listen(3000, function () {
    console.log('Listening on port 3000');
});