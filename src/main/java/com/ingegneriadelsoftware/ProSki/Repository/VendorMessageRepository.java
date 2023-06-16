package com.ingegneriadelsoftware.ProSki.Repository;

import com.ingegneriadelsoftware.ProSki.Model.Vendor;
import com.ingegneriadelsoftware.ProSki.Model.VendorMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorMessageRepository extends CrudRepository<VendorMessage, Integer> {

    List<VendorMessage> findAllByVendor(Vendor vendor);
}
