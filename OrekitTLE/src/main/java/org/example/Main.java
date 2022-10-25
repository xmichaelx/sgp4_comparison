package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {

        File orekitData = new File("G:\\orekit-data-master"); // change this
        DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
        manager.addProvider(new DirectoryCrawler(orekitData));
        var count = 0;
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("G:\\expected.json"));// change this
            JSONArray companyList = (JSONArray) obj;
            Iterator<JSONObject> iterator = companyList.iterator();
            while (iterator.hasNext()) {
                var item = iterator.next();
                var tle = (JSONArray) item.get("tle");
                var position = (JSONArray) item.get("position");
                var x = (Double) position.get(0);
                var y = (Double) position.get(1);
                var z = (Double) position.get(2);
                var time = item.get("time").toString();
                var s = new TLE(tle.get(0).toString(), tle.get(1).toString());
                var propagator = TLEPropagator.selectExtrapolator(s);
                var utc = TimeScalesFactory.getUTC();
                var coords = propagator.getPVCoordinates(new AbsoluteDate(time, utc));

                var orekit_x = coords.getPosition().getX()/1000;
                var orekit_y = coords.getPosition().getY()/1000;
                var orekit_z = coords.getPosition().getZ()/1000;

                var dist = 1000 * Math.sqrt(Math.pow(x-orekit_x, 2)+Math.pow(y-orekit_y, 2)+Math.pow(z-orekit_z, 2));
                Locale l = Locale.of("en_US");

                if (dist > 100) {
                    System.out.println(String.format(l, "%s %s", s.getSatelliteNumber(), dist));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
}