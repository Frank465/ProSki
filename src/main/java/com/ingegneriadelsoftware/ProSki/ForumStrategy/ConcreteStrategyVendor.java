package com.ingegneriadelsoftware.ProSki.ForumStrategy;

import com.ingegneriadelsoftware.ProSki.Model.User;
import com.ingegneriadelsoftware.ProSki.Model.Vendor;
import com.ingegneriadelsoftware.ProSki.Model.VendorComment;
import com.ingegneriadelsoftware.ProSki.Model.VendorMessage;
import com.ingegneriadelsoftware.ProSki.Repository.VendorCommentRepository;
import com.ingegneriadelsoftware.ProSki.Repository.VendorMessageRepository;
import com.ingegneriadelsoftware.ProSki.Repository.VendorRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Classe concreta per la pubblicazione dei messaggi e dei commenti per il forum di un vendor
 */
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ConcreteStrategyVendor implements PublishingStrategy{

    private VendorRepository vendorRepository;
    private VendorMessageRepository vendorMessageRepository;
    private VendorCommentRepository vendorCommentRepository;

    public ConcreteStrategyVendor(VendorRepository vendorRepository, VendorMessageRepository vendorMessageRepository) {
        this.vendorRepository = vendorRepository;
        this.vendorMessageRepository = vendorMessageRepository;
    }

    @Override
    public String publishingMessage(String emailVendor, User user, String message) {
        Optional<Vendor> vendor = vendorRepository.findByEmail(emailVendor);
        VendorMessage vendorMessage = new VendorMessage(vendor.get(), user, message);
        vendorMessageRepository.save(vendorMessage);
        return vendorMessage.getMessage();
    }

    @Override
    public String publishingComment(Integer idVendorMessenge, User user, String message) {
        Optional<VendorMessage> vendorMessage = vendorMessageRepository.findById(idVendorMessenge);
        VendorComment vendorComment = new VendorComment(user, vendorMessage.get(), message);
        vendorCommentRepository.save(vendorComment);
        return message;
    }
}
