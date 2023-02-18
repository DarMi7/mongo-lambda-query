package com.darmi.demo.request;

import com.darmi.plugin.core.PaginationDTO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class TaskCriteria {
    String name;
    String type;
    String fuzzyName;
    Integer points;
    Date begin;
    Date end;
    @NotNull
    @Valid
    PaginationDTO pagination;
}
