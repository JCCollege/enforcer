package qmusa.jc.enforce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by qasim on 20/05/16.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Incidents {

        private String[] incidents;

        public Incidents() {
            // empty default constructor, necessary for Firebase to be able to deserialize blog posts
        }

        public String[] getIncidents() {
            return incidents;
        }
}
