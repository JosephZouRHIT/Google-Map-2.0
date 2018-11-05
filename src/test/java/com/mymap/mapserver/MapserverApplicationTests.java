package com.mymap.mapserver;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MapserverApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRoute() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/getRoute").
                param("st_lat", "39.468131").
                param("st_lon", "-87.360245").
                param("ed_lat", "39.468894").
                param("ed_lon", "-87.398102").
                param("searchEngine", "AStar").
                param("cost", "Distance")).
                andDo(print()).
                andExpect(status().isOk()).
                andReturn();
        String content = result.getResponse().getContentAsString();
        JSONObject obj = new JSONObject(content);
        JSONArray arr = obj.getJSONArray("route");
        for (int i = 0, l = arr.length(); i < l; i++) {
            JSONObject str = (JSONObject) arr.get(i);
            System.out.println(str);
        }
    }

}
