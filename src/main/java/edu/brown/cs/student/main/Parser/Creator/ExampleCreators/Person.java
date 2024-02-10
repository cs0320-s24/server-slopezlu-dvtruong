package edu.brown.cs.student.main.Parser.Creator.ExampleCreators;

/** This class is an example used in PersonCreator. */
public class Person {
  private String name;
  private int age;
  private String hometown;

  /**
   * The person constructor
   *
   * @param name the name of the person
   * @param age the age of the person
   * @param hometown the hometown of the person
   */
  public Person(String name, int age, String hometown) {
    this.name = name;
    this.age = age;
    this.hometown = hometown;
  }

  /**
   * This method converts the person object into a string.
   *
   * @return a string representation of this person object
   */
  @Override
  public String toString() {
    return this.name + "," + Integer.toString(this.age) + "," + this.hometown;
  }
}
