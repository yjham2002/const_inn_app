package comm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.IOException;

public abstract class PrefModel {

    @JsonIgnore
    public abstract String toJson() throws IOException;

}
