package com.geodevai.data.dto;

import java.util.List;

public record SearchResponse<T>(
    List<T> items,
    int count,
    String query
) {}
