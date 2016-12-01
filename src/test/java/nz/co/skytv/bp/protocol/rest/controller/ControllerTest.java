package nz.co.skytv.bp.protocol.rest.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import nz.co.skytv.bp.protocol.spring.BaseConfiguration;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


/**
 * This is for integration testing the controller.<br>
 * Leave the tests @Ignore.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BaseConfiguration.class })
@WebAppConfiguration
public class ControllerTest {

  @Autowired
  private WebApplicationContext wac;


  private MockMvc mockMvc;

  @Before
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Ignore
  @Test
  public void shouldReturnOK() throws Exception {


    mockMvc.perform(post("/greenbuttonreset")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"Channel\":\"BOLLOCKS\"}"))
        .andExpect(status().isOk());

  }


}
