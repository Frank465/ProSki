package com.ingegneriadelsoftware.ProSki.ForumStrategy;

import com.ingegneriadelsoftware.ProSki.Model.Location;
import com.ingegneriadelsoftware.ProSki.Model.LocationMessage;
import com.ingegneriadelsoftware.ProSki.Model.User;
import com.ingegneriadelsoftware.ProSki.Repository.LocationMessageRepository;
import com.ingegneriadelsoftware.ProSki.Repository.LocationRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class ConcreteStrategyLocation implements PublishingStrategy{

    private LocationRepository locationRepository;
    private LocationMessageRepository locationMessageRepository;

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
}
