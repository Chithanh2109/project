# Skin Care Service Management System

**English**: Skin Care Service Management - Platform for spa management, appointment booking, and skincare services  
**Vietnamese**: Phần mềm quản lý dịch vụ chăm sóc da - Nền tảng quản lý spa, đặt lịch và dịch vụ chăm sóc da  
**Abbreviation**: SkinCare Service

## Giới thiệu

Hệ thống quản lý dịch vụ chăm sóc da là nền tảng giúp quản lý toàn diện các hoạt động của trung tâm spa, bao gồm đặt lịch, quản lý khách hàng, dịch vụ và chuyên viên trị liệu. Dự án sử dụng Spring Boot, Spring Security, Thymeleaf và H2 Database.

## Yêu cầu

- Java 17 hoặc cao hơn
- Maven 3.8 hoặc cao hơn

## Cài đặt

1. Clone repository:
```
git clone https://github.com/Chithanh2109/project.git
```

2. Di chuyển vào thư mục dự án:
```
cd project
```

3. Biên dịch và đóng gói ứng dụng:
```
mvn clean package
```

4. Chạy ứng dụng:
```
java -jar target/skincare-service-1.0-SNAPSHOT.jar
```

Hoặc sử dụng Maven để chạy:
```
mvn spring-boot:run
```

5. Truy cập ứng dụng tại: http://localhost:8080

## Tính năng chính

- **Trang chủ và thông tin**:
  - Giới thiệu thông tin trung tâm
  - Danh sách dịch vụ chăm sóc da
  - Thông tin chuyên viên trị liệu
  - Blog chia sẽ kinh nghiệm, tin tức

- **Trắc nghiệm đề xuất dịch vụ**:
  - Cho phép khách hàng làm trắc nghiệm
  - Hệ thống đề xuất dịch vụ phù hợp

- **Đặt lịch dịch vụ**:
  - Đặt dịch vụ chăm sóc da
  - Chọn chuyên viên trị liệu

- **Quản lý quy trình dịch vụ**:
  - Quản lý checkin, checkout khách hàng
  - Phân công chuyên viên
  - Ghi nhận kết quả thực hiện dịch vụ
  
- **Quản lý hệ thống**:
  - Quản lý chính sách thanh toán và hủy đơn
  - Quản lý thông tin dịch vụ, bảng giá
  - Quản lý lịch làm việc trung tâm
  - Quản lý thông tin chuyên viên
  - Quản lý đánh giá và phản hồi
  - Quản lý hồ sơ khách hàng
  - Báo cáo thống kê

## Vai trò người dùng

- **Guest**: Khách vãng lai, chỉ xem thông tin
- **Customer**: Khách hàng đã đăng ký
- **Skin Therapist**: Chuyên viên trị liệu da
- **Staff**: Nhân viên tại trung tâm
- **Manager**: Quản lý trung tâm

## Công nghệ

- Spring Boot
- Spring Security
- Spring Data JPA
- Thymeleaf
- H2 Database
- Bootstrap

## Kiến trúc

Dự án tuân theo kiến trúc MVC với các layer rõ ràng:
- **Model**: Các đối tượng như User, Service, Appointment
- **View**: Các template Thymeleaf
- **Controller**: Xử lý yêu cầu và phản hồi HTTP
- **Repository**: Truy cập dữ liệu
- **Service**: Xử lý logic nghiệp vụ

## Cập nhật mới

- Thêm tính năng quản lý dịch vụ chăm sóc da
- Cải thiện giao diện người dùng
- Tối ưu hóa hiệu suất hệ thống
- Bổ sung tính năng đánh giá dịch vụ
- Thêm giao diện quản lý cho admin

## Liên hệ

Để biết thêm thông tin, vui lòng liên hệ: nguyenchithanh2005.nvt@gmail.com 