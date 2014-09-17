package pl.allegro.techblog.cassandra.domain.model;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.util.UUID;

@Table("cass_user")
public class User {

    @PrimaryKey
    private UUID id;
    private String login;
    private int age;

    public User(UUID id, String login, int age) {
        this.id = id;
        this.login = login;
        this.age = age;
    }

    public UUID getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public int getAge() {
        return age;
    }
}
