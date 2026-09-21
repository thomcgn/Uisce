package de.irishphonetics.analysis.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "uisce.lexicon.seed.enabled=true")
@AutoConfigureMockMvc
class AnalysisControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void returnsSourcedPronunciationForKnownWord() throws Exception {
        mvc.perform(post("/api/v1/analysis").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"word\":\"Dia\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value("Dia"))
                .andExpect(jsonPath("$.ipa").value("dʲiə"))
                .andExpect(jsonPath("$.pronunciations[0].reviewStatus").value("SOURCED"))
                .andExpect(jsonPath("$.segments").isEmpty());
    }

    @Test
    void preservesFadaAndDialectAlternatives() throws Exception {
        mvc.perform(post("/api/v1/analysis").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"word\":\"Sláinte\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.word").value("Sláinte"))
                .andExpect(jsonPath("$.pronunciations").isNotEmpty());
    }

    @Test
    void rejectsEmptyAndWhitespaceWords() throws Exception {
        for (String word : new String[] {"", "   "}) {
            mvc.perform(post("/api/v1/analysis").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"word\":\"" + word + "\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("INVALID_WORD"));
        }
    }

    @Test
    void rejectsMissingWordAndMalformedJson() throws Exception {
        mvc.perform(post("/api/v1/analysis").contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_WORD"));
        mvc.perform(post("/api/v1/analysis").contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void reportsUnsupportedWordWithoutInventingIpa() throws Exception {
        mvc.perform(post("/api/v1/analysis").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"word\":\"xyzzy\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("UNSUPPORTED_ANALYSIS"));
    }

    @Test
    void allowsTheLocalFrontendOrigin() throws Exception {
        mvc.perform(post("/api/v1/analysis").contentType(MediaType.APPLICATION_JSON)
                        .header("Origin", "http://localhost:3000")
                        .content("{\"word\":\"Dia\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }
}
