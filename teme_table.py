from sgp4.api import Satrec, WGS84
from sgp4.conveniences import sat_epoch_datetime,jday_datetime
from datetime import datetime, timedelta,timezone
import json

output_lines = []
with open("./2022_10_19.3le") as f:
    lines = [ x.strip() for x in f.readlines()]
    for i in range(0,len(lines),3):
        tle = [lines[i+1], lines[i+2]]
        satellite = Satrec.twoline2rv(*tle, WGS84)
        sat_epoch = sat_epoch_datetime(satellite)
        threshold = datetime(2022,10,18,tzinfo=timezone(timedelta()))
        t = datetime(2022,10,19,tzinfo=timezone(timedelta()))
        t_string = t.isoformat()

        if sat_epoch > threshold:
            jd,fr = jday_datetime(t)
            _, r, _ = satellite.sgp4(jd, fr)
            output_lines.append({"tle" : tle, "time" : t_string, "position" : [r[0], r[1], r[2]]})

with open("expected.json", "w") as g:
    json.dump(output_lines, g)



