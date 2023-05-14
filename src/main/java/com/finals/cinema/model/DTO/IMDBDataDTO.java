package com.finals.cinema.model.DTO;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
    @Setter
    @NoArgsConstructor
    public class IMDBDataDTO {

        private JsonNode titles;
        private JsonNode names;
        private JsonNode companies;

        public IMDBDataDTO(JsonNode jsonNode) {
            this.titles = jsonNode.get("titles");
            this.names = jsonNode.get("names");
            this.companies = jsonNode.get("companies");
        }
}
