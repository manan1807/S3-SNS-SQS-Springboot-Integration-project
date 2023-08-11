package com.reactivespring.S3.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class S3Details {
    @JsonProperty("bucket")
    private Bucket bucket;
    @JsonProperty("object")
    private S3Object object;

    public Bucket getBucket() {
        return bucket;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    public S3Object getObject() {
        return object;
    }

    public void setObject(S3Object object) {
        this.object = object;
    }
}
