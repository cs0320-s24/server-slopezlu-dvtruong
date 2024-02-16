package edu.brown.cs.student.main.server.CensusAPI.StateAndCountyCodes;

/**
 * a record that holds a state name and county name
 * @param state a state in the US
 * @param county a county in the state that was specified
 */
public record stateCounty(String state, String county) {}
