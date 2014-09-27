package org.pasmo.surveys

public interface Surveys {

    List<Map<String, String>> findAllBy(Map<String, String> survey, String outletType)

}