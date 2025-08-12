package com.alura.conv.model;

import java.util.Map;

public class LatestResp {
    private String result;
    private String base_code;
    private Map<String, Double> conversion_rates;

    public String getResult() { return result; }
    public String getBase_code() { return base_code; }
    public Map<String, Double> getConversion_rates() { return conversion_rates; }
}
