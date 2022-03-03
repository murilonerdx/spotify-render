package com.murilonerdx.spotifyrenderapi.repository;

import com.murilonerdx.spotifyrenderapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);
}
