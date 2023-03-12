package com.example.authorization_api.controllers;

import com.example.authorization_api.models.Application;
import com.example.authorization_api.models.dtos.ApplicationDto;
import com.example.authorization_api.services.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@RestController
@RequestMapping(value = "/api/applications")
public class ApplicationController
{
    private static final ApplicationService applicationService = new ApplicationService();

    @PostMapping()
    public Application addApplication(@RequestBody ApplicationDto applicationDto)
    {
        return applicationService.addApplication(applicationDto);
    }

    @GetMapping()
    public List<Application> getApplications()
    {
        return applicationService.getApplications();
    }

    @GetMapping(value = "/{app_id}")
    public Application getApplication(@PathVariable long app_id)
    {
        if(!applicationService.applicationIsValid(app_id))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application with ID: " + app_id + " does not exist");
        }
        return applicationService.getApplication(app_id);
    }

    @PutMapping(value = "/{app_id}")
    public Application modifyApplication(@PathVariable long app_id, @RequestBody ApplicationDto applicationDto)
    {
        if(!applicationService.applicationIsValid(app_id))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application with ID: " + app_id + " does not exist");
        }
        return applicationService.modifyApplication(app_id, applicationDto);
    }

    @DeleteMapping(value = "/{app_id}")
    public Application deleteApplication(@PathVariable long app_id)
    {
        if(!applicationService.applicationIsValid(app_id))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application with ID: " + app_id + " does not exist");
        }
        return applicationService.deleteApplication(app_id);
    }
}
