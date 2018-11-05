package com.mymap.mapserver;

import com.mymap.coremap.OSMUtil.OSMMathUtil;
import com.mymap.coremap.ServerBackend.SearchResultWrapper;
import com.mymap.coremap.ServerBackend.TempLocation;
import com.mymap.mapserver.responsetype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


@RestController
public class MyController {


    @RequestMapping(value = "/getArea", method = RequestMethod.GET)
    public Area getArea() {
        MapserverApplication.getLogger().info("Get area");
        return new Area(MapserverApplication.getMap().getMapBound());
    }

    @RequestMapping(value = "/getAreaArray", method = RequestMethod.GET)
    public AreaArray getAreaArray() {
        MapserverApplication.getLogger().info("Get area array");
        return new AreaArray(MapserverApplication.getMap().getMapBound());
    }

    @RequestMapping(value = "/getRoute")
    public MapRoute getRoute(@RequestParam(value = "st_lat") String st_lat,
                             @RequestParam(value = "st_lon") String st_lon,
                             @RequestParam(value = "ed_lat") String ed_lat,
                             @RequestParam(value = "ed_lon") String ed_lon,
                             @RequestParam(value = "searchEngine") String se,
                             @RequestParam(value = "cost") String costType) {
        TempLocation st_loc = new TempLocation(st_lat, st_lon);
        TempLocation ed_loc = new TempLocation(ed_lat, ed_lon);
        try {
            SearchResultWrapper result = MapserverApplication.getMap().findRoute(st_loc, ed_loc, se, costType);
            MapserverApplication.getLogger().info(String.format("Finish search with search engine: %s, find best: %s", se, costType));
            return new MapRoute(result);
        } catch (IllegalArgumentException e) {
            SearchResultWrapper error_result = new SearchResultWrapper();
            error_result.setTime(Double.NaN);
            error_result.setTime(Double.NaN);
            MapserverApplication.getLogger().error(String.format("Wrong search engine type:%s", se));
            return new MapRoute(error_result);
        }
    }

    @RequestMapping(value = "/getInitialView")
    public InitialView getInitialView() {
        MapserverApplication.getLogger().info("Get Initial View");
        return new InitialView(
                MapserverApplication.getMap().getCenter(),
                MapserverApplication.getMap().getZoomLevel()
        );
    }

    @RequestMapping(value = "/getCenterString")
    public LocationString getCenterString() {
        return new LocationString(MapserverApplication.getMap().getCenter());
    }

    @RequestMapping(value = "/locationSearchSuggestion", method = RequestMethod.POST)
    public RawJson GoogleRequestProxy(@RequestBody LocationQuery query) {
        String template = "location={location}&radius={radius}&keyword={keyword}&key={key}";
        Map<String, String> params = new HashMap<>();
        params.put("location", String.format("%f,%f", query.getLat(), query.getLng()));
        params.put("radius", String.valueOf(OSMMathUtil.getRadiusAtZoomLevel(query.getZoom())));
        params.put("keyword", query.getKeyword());
        params.put("key", MapserverApplication.getApiKey());
        UriComponents uriComponents = UriComponentsBuilder.newInstance().
                scheme("https").
                host("maps.googleapis.com").
                path("maps/api/place/nearbysearch/json").
                query(template).buildAndExpand(params);

        try {
            URL url = new URL(uriComponents.toUriString());
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                //System.out.println(inputLine);
                response.append(inputLine);
            }
            in.close();
            MapserverApplication.getLogger().info("Get input suggestion from google");
            return new RawJson(response.toString());
        } catch (IOException e) {
            MapserverApplication.getLogger().error("Unable to fetch info from google api");
            e.printStackTrace();
        }


        return null;
    }
}
