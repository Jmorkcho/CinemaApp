package com.finals.cinema.model.entity;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum UserStatus {

    CHILD,STUDENT,ADULT,PENSIONER;

    public static List<String> getValues()
    {
        List<String> values = Arrays.stream(values()).map(v -> v.toString().toLowerCase())
                                                     .map(StringUtils::capitalize)
                                                     .collect(Collectors.toList());
        return values;

    }
}
