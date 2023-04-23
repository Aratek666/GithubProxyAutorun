package com.mgrud.github.proxy.gitproxycore.domain.boundary;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GithubProxyResourceIntegrationTest {
    @Autowired
    private MockMvc mvc;


    @Test
    public void testMissingUserNameParamResponse()
            throws Exception {

        mvc.perform(get("/user/repositories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("MissingParameter")));
    }

    @Test
    public void testNotExistingUserNameParam()
            throws Exception {

        mvc.perform(get("/user/repositories")
                        .param("userName", "asdadadadsa")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("UserNameNotFound")));
    }


    @Test
    public void testExistingUserWithOneRepositoryWithoutBranches()
            throws Exception {

        mvc.perform(get("/user/repositories")
                        .param("userName", "Aratek666")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].branches", empty()));
    }

    @Test
    public void testExistingUserWithoutAnyRepository()
            throws Exception {

        mvc.perform(get("/user/repositories")
                        .param("userName", "sadad")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    //TODO Ok Test ( create data at github)
}
