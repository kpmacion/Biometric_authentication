package com.example.authorization_api.controllers;

import com.example.authorization_api.models.User;
import com.example.authorization_api.models.dtos.UserDto;
import com.example.authorization_api.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@RestController
@RequestMapping(value = "/api/users")
public class UserController
{
    private static final UserService userService = new UserService();

    @GetMapping()
    public List<User> getUsers()
    {
        return userService.getUsers();
    }

    @GetMapping(value = "/{user_id}")
    public User getUser(@PathVariable long user_id)
    {
        if(!userService.userIsValid(user_id))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID: " + user_id + " does not exist");
        }
        return userService.getUser(user_id);
    }

    @PutMapping(value = "/{user_id}")
    public User modifyUser(@PathVariable long user_id, @RequestBody UserDto userDto)
    {
        if(!userService.userIsValid(user_id))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID: " + user_id + " does not exist");
        }
        return userService.modifyUser(user_id, userDto);
    }

    @DeleteMapping(value = "/{user_id}")
    public User deleteUser(@PathVariable long user_id)
    {
        if(!userService.userIsValid(user_id))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID: " + user_id + " does not exist");
        }
        return userService.deleteUser(user_id);
    }
}
