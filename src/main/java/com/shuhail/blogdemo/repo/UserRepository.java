package com.shuhail.blogdemo.repo;

import com.shuhail.blogdemo.domain.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserRepository extends ElasticsearchRepository<User, String> {
    User findByUserName(String username);
}
