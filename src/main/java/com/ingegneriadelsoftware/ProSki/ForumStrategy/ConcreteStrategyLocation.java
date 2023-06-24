package com.ingegneriadelsoftware.ProSki.ForumStrategy;

import com.ingegneriadelsoftware.ProSki.Model.*;
import com.ingegneriadelsoftware.ProSki.Repository.LocationCommentRepository;
import com.ingegneriadelsoftware.ProSki.Repository.LocationMessageRepository;
import com.ingegneriadelsoftware.ProSki.Repository.LocationRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Component
public class ConcreteStrategyLocation implements PublishingStrategy{

    private LocationRepository locationRepository;
    private LocationMessageRepository locationMessageRepository;
    private LocationCommentRepository locationCommentRepository;

    public ConcreteStrategyLocation(LocationRepository locationRepository, LocationMessageRepository locationMessageRepository) {
        this.locationRepository = locationRepository;
        this.locationMessageRepository = locationMessageRepository;
    }

    @Override
    public String publishingMessage(String nameLocation, User user, String message) {
        Optional<Location> location = locationRepository.findByName(nameLocation);
        LocationMessage locationMessage = new LocationMessage(location.get(), user, message);
        locationMessageRepository.save(locationMessage);
        return locationMessage.getMessage();
    }

    @Override
    public String publishingComment(Integer idLocationMessenger, User user, String message) {
        Optional<LocationMessage> vendorMessage = locationMessageRepository.findById(idLocationMessenger);
        LocationComment locationComment = new LocationComment(user, vendorMessage.get(), message);
        locationCommentRepository.save(locationComment);
        return message;
    }
}
