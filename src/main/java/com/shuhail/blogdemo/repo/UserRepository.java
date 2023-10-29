package com.shuhail.blogdemo.repo;

import com.shuhail.blogdemo.domain.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;


public interface UserRepository extends ElasticsearchRepository<User, String> {
    User findByUserName(String username);
}
