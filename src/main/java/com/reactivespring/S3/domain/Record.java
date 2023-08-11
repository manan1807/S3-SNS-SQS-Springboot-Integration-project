package com.reactivespring.S3.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Record {

    @JsonProperty("s3")
    private S3Details s3;

    public S3Details getS3() {
        return s3;
    }

    public void setS3(S3Details s3) {
        this.s3 = s3;
    }

}

