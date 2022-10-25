const satellite = require('satellite.js');
const fs = require('fs');

let rawdata = fs.readFileSync('expected.json');
let student = JSON.parse(rawdata);
var t = new Date("2022-10-19T00:00:00+00:00");
console.log(t);

student.forEach(sat => {
    var satrec = satellite.twoline2satrec(sat.tle[0], sat.tle[1])
    var positionAndVelocity = satellite.propagate(satrec, new Date(sat.time));
    var dist = 1000 *Math.sqrt(Math.pow(sat.position[0] - positionAndVelocity.position.x,2) +
    Math.pow(sat.position[1] - positionAndVelocity.position.y,2) +
    Math.pow(sat.position[2] - positionAndVelocity.position.z,2) );
    if (dist > 1) {
        console.log(satrec.satnum,  dist);
    }
})
