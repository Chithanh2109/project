package com.simplebeauty.service;

import com.simplebeauty.model.Service;
import com.simplebeauty.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;

    @Autowired
    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với id: " + id));
    }

    public Service getServiceBySlug(String slug) {
        return serviceRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với slug: " + slug));
    }

    public Service createService(Service service) {
        if (serviceRepository.existsBySlug(service.getSlug())) {
            throw new RuntimeException("Slug đã tồn tại");
        }
        return serviceRepository.save(service);
    }

    public Service updateService(Long id, Service serviceDetails) {
        Service service = getServiceById(id);
        
        if (!service.getSlug().equals(serviceDetails.getSlug()) && serviceRepository.existsBySlug(serviceDetails.getSlug())) {
            throw new RuntimeException("Slug đã tồn tại");
        }
        
        service.setName(serviceDetails.getName());
        service.setShortDescription(serviceDetails.getShortDescription());
        service.setDescription(serviceDetails.getDescription());
        service.setPrice(serviceDetails.getPrice());
        service.setDurationMinutes(serviceDetails.getDurationMinutes());
        service.setImageUrl(serviceDetails.getImageUrl());
        service.setSlug(serviceDetails.getSlug());
        
        return serviceRepository.save(service);
    }

    public void deleteService(Long id) {
        Service service = getServiceById(id);
        serviceRepository.delete(service);
    }
    
    public void createSampleServices() {
        if (serviceRepository.count() == 0) {
            // Dịch vụ 1: Điều trị mụn
            Service acneTreatment = new Service();
            acneTreatment.setName("Điều trị mụn chuyên sâu");
            acneTreatment.setShortDescription("Liệu trình điều trị mụn toàn diện, giúp loại bỏ mụn tận gốc và ngăn ngừa tái phát.");
            acneTreatment.setDescription("Liệu trình điều trị mụn chuyên sâu tại SimpleBeauty được thiết kế đặc biệt để giải quyết tận gốc các vấn đề về mụn. Quy trình bao gồm làm sạch sâu, tẩy tế bào chết, chiết xuất mụn chuyên nghiệp, đắp mặt nạ kháng khuẩn và sử dụng các sản phẩm chuyên biệt. Liệu trình này không chỉ giúp loại bỏ mụn hiện tại mà còn ngăn ngừa sự hình thành mụn mới, giúp da sạch mụn, khỏe mạnh và rạng rỡ.");
            acneTreatment.setPrice(new BigDecimal("1200000"));
            acneTreatment.setDurationMinutes(90);
            acneTreatment.setImageUrl("https://via.placeholder.com/350x200");
            acneTreatment.setSlug("acne-treatment");
            serviceRepository.save(acneTreatment);
            
            // Dịch vụ 2: Trẻ hóa da
            Service antiAging = new Service();
            antiAging.setName("Trẻ hóa da");
            antiAging.setShortDescription("Công nghệ tiên tiến giúp làm mờ nếp nhăn, cải thiện độ đàn hồi và kết cấu da.");
            antiAging.setDescription("Liệu trình trẻ hóa da tại SimpleBeauty sử dụng công nghệ tiên tiến kết hợp với các sản phẩm chứa thành phần chống lão hóa mạnh mẽ. Quy trình bao gồm làm sạch, tẩy tế bào chết, massage nâng cơ, đắp mặt nạ collagen và sử dụng serum chống lão hóa cao cấp. Liệu trình này giúp làm mờ nếp nhăn, cải thiện độ đàn hồi, làm săn chắc da và mang lại vẻ tươi trẻ, rạng rỡ cho làn da.");
            antiAging.setPrice(new BigDecimal("1800000"));
            antiAging.setDurationMinutes(120);
            antiAging.setImageUrl("https://via.placeholder.com/350x200");
            antiAging.setSlug("anti-aging");
            serviceRepository.save(antiAging);
            
            // Dịch vụ 3: Chăm sóc da cơ bản
            Service hydrating = new Service();
            hydrating.setName("Chăm sóc da cơ bản");
            hydrating.setShortDescription("Liệu trình làm sạch sâu, cung cấp dưỡng chất và độ ẩm cho làn da tươi sáng.");
            hydrating.setDescription("Liệu trình chăm sóc da cơ bản tại SimpleBeauty là sự kết hợp hoàn hảo giữa làm sạch sâu và cung cấp dưỡng chất. Quy trình bao gồm tẩy trang, rửa mặt, tẩy tế bào chết, xông hơi, làm sạch sâu, massage thư giãn, đắp mặt nạ dưỡng ẩm và dưỡng da. Liệu trình này giúp loại bỏ bụi bẩn, tế bào chết, cung cấp độ ẩm và dưỡng chất, mang lại làn da sạch sẽ, mềm mại và tươi sáng.");
            hydrating.setPrice(new BigDecimal("800000"));
            hydrating.setDurationMinutes(60);
            hydrating.setImageUrl("https://via.placeholder.com/350x200");
            hydrating.setSlug("hydrating-facial");
            serviceRepository.save(hydrating);
            
            // Dịch vụ 4: Điều trị nám
            Service pigmentation = new Service();
            pigmentation.setName("Điều trị nám");
            pigmentation.setShortDescription("Phương pháp điều trị nám hiệu quả, giúp làm mờ và ngăn ngừa sự hình thành nám.");
            pigmentation.setDescription("Liệu trình điều trị nám tại SimpleBeauty sử dụng công nghệ tiên tiến kết hợp với các sản phẩm chuyên biệt để giải quyết tận gốc vấn đề nám, tàn nhang. Quy trình bao gồm làm sạch, tẩy tế bào chết, sử dụng các sản phẩm làm mờ nám, đắp mặt nạ dưỡng trắng và bảo vệ da khỏi tác hại của ánh nắng mặt trời. Liệu trình này giúp làm mờ các vết nám, tàn nhang, cải thiện tông màu da và ngăn ngừa sự hình thành nám mới.");
            pigmentation.setPrice(new BigDecimal("1500000"));
            pigmentation.setDurationMinutes(90);
            pigmentation.setImageUrl("https://via.placeholder.com/350x200");
            pigmentation.setSlug("pigmentation");
            serviceRepository.save(pigmentation);
        }
    }
}
