package pl.allegro.techblog.cassandra.domain.repository;

import com.datastax.driver.core.utils.UUIDs;
import pl.allegro.techblog.cassandra.CassandraIntegrationTest;
import pl.allegro.techblog.cassandra.domain.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@CassandraIntegrationTest
public class UserRepositoryIntegrationTest {

    @Inject
    private UserRepository userRepository;

    @Test
    public void shouldInsertUser() throws Exception {
        //given
        UUID uuid = UUIDs.timeBased();
        String login = "cassandra_kata";
        int age = 34;
        User user = new User(uuid, login, age);

        //when
        userRepository.save(user);

        //then
        User foundUser = userRepository.findOne(uuid);
        assertThat(foundUser.getLogin()).isEqualTo(login);
        assertThat(foundUser.getAge()).isEqualTo(age);
    }
}
