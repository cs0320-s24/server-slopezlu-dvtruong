package edu.brown.cs.student.main.server.CensusAPI;

import java.util.List;

/**
 * A record that holds the data of a desired county
 *
 * @param a list of string that holds a response from the Census API of the desired county. It
 *     should however hold the broadbandData in the 2nd item in the list
 */
public record broadbandData(List<String> data) {}
