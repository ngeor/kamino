package com.github.ngeor.web2.services;

import com.github.ngeor.web2.db.User;
import com.github.ngeor.web2.db.UserRepository;
import com.github.ngeor.web2.mapping.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Deals with users.
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityMapper entityMapper;

    /**
     * Ensures the given user exists in the db.
     * Please note that a different UUID might be returned than the one provided.
     */
    public User ensure(com.github.ngeor.bitbucket.models.User model) {
        return userRepository.findById(model.getUuid())
            .orElseGet(
                ()
                    -> userRepository.findByDisplayName(model.getDisplayName())
                    .orElseGet(() -> persist(model)));
    }

    private User persist(com.github.ngeor.bitbucket.models.User model) {
        User result = entityMapper.toEntity(model);
        return userRepository.save(result);
    }
}
