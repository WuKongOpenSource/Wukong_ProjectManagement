package com.kakarote.work.common.project;

import com.kakarote.common.result.PageEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryTaskFileByWorkIdBO extends PageEntity {

    private Long workId;

    @Override
    public String toString() {
        return "QueryTaskFileByWorkIdBO{" +
                "workId=" + workId +
                '}';
    }
}
