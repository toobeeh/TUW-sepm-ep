package at.ac.tuwien.sepm.assignment.individual.entity;

public class Owner {
  private long id;
  private String firstName;
  private String lastName;
  private String email;

  public long getId() {
    return id;
  }

  public Owner setId(long id) {
    this.id = id;
    return this;
  }

  public String getFirstName() {
    return firstName;
  }

  public Owner setFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public String getLastName() {
    return lastName;
  }

  public Owner setLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public Owner setEmail(String email) {
    this.email = email;
    return this;
  }

  @Override
  public String toString() {
    return "Owner{"
        + "id=" + id
        + ", firstName='" + firstName + '\''
        + ", lastName='" + lastName + '\''
        + ", email='" + email + '\''
        + '}';
  }
}
