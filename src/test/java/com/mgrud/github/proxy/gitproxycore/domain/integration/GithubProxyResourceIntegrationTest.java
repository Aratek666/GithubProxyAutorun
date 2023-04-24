package com.mgrud.github.proxy.gitproxycore.domain.integration;

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
    public void testExistingUserWithoutAnyRepository()
            throws Exception {

        mvc.perform(get("/user/repositories")
                        .param("userName", "sadad")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    public void testExistingUserRepositoryWithBranches()
            throws Exception {
        mvc.perform(get("/user/repositories")
                        .param("userName", "Aratek666")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("GithubProxyAutorun")))
                .andExpect(jsonPath("$[0].ownerLogin", is("Aratek666")))
                .andExpect(jsonPath("$[0].branches[0].name", is("master")))
                .andExpect(jsonPath("$[0].branches[0].sha", is("bad726a6863037f9ec6c6d67b8acb70cfb3dc58c")))
                .andExpect(jsonPath("$[1].name", is("GtihubProxyTestRepo")))
                .andExpect(jsonPath("$[1].ownerLogin", is("Aratek666")))
                .andExpect(jsonPath("$[1].branches[0].name", is("main")))
                .andExpect(jsonPath("$[1].branches[0].sha", is("38a6a3b16e5d1bf302f2b39c32bf7af194e05332")))
                .andExpect(jsonPath("$[2].branches", empty()));

    }
}
