package alexparunov.lookaround.events;

public class User {
  private String email;
  private String name;
  private String userId;

  public User() {
  }

  public User(String email, String name, String userId) {
    this.setEmail(email);
    this.setName(name);
    this.setUserId(userId);
  }


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @Override
  public String toString() {
    return "  Name: " + this.name + "\n" + "  Email: " + this.email + "\n" + "  UserId: " + this.userId;
  }
}
