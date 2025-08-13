# Website Tuyển Dụng - Hệ thống quản lý tuyển dụng & phân tích CV

## Giới thiệu
**Website Tuyển Dụng** là hệ thống quản lý tuyển dụng hiện đại, hỗ trợ doanh nghiệp và ứng viên trong việc:
- Đăng tuyển
- Ứng tuyển
- Quản lý hồ sơ
- Phân tích CV tự động
- Đánh giá mức độ phù hợp của ứng viên

Hệ thống cung cấp các công cụ quản trị mạnh mẽ cho **admin**, đồng thời mang lại trải nghiệm tối ưu cho người dùng cuối (**ứng viên, nhà tuyển dụng**).

---

## Đặc điểm nổi bật
- **Phân tích CV tự động**: Tích hợp AI (**Gemini API**) để trích xuất kỹ năng, kinh nghiệm, học vấn, chứng chỉ, chấm điểm ATS và lưu trữ chi tiết hồ sơ.
- **Quản lý người dùng, vai trò, phân quyền**: Hệ thống phân quyền linh hoạt, hỗ trợ nhiều vai trò (admin, user, ...).
- **Quản lý công việc, kỹ năng, quyền truy cập**: CRUD đầy đủ, hỗ trợ phân trang, lọc nâng cao.
- **Xác thực bảo mật**: Đăng ký, đăng nhập, refresh token, logout, lưu token bảo mật qua cookie HttpOnly.
- **API RESTful chuẩn hóa**: Tích hợp **Swagger/OpenAPI**, annotation rõ ràng, dễ mở rộng.
- **Quản lý hồ sơ ứng tuyển**: Tạo, cập nhật, xóa, truy vấn, phân tích tự động, lọc nâng cao theo nhiều tiêu chí.

---

## Công nghệ sử dụng
- **Spring Boot**: Framework Java xây dựng API RESTful, bảo mật, dễ mở rộng.
- **Spring Security**: Xác thực, phân quyền, quản lý token.
- **JPA/Hibernate**: ORM quản lý dữ liệu với MySQL.
- **MySQL**: Hệ quản trị cơ sở dữ liệu quan hệ.
- **Gemini API (Google)**: Phân tích nội dung CV bằng AI.
- **Swagger/OpenAPI**: Tự động sinh tài liệu API.

---

## Công cụ phát triển
- **IntelliJ IDEA**: IDE phát triển Java, Spring Boot.
- **Git**: Quản lý mã nguồn.
- **Maven**: Quản lý phụ thuộc và build project.

---

## Các tính năng chính

### Ứng viên / Người dùng
- Đăng ký, đăng nhập, xác thực, làm mới token, đăng xuất.
- Quản lý tài khoản cá nhân.
- Tạo, cập nhật, xóa hồ sơ ứng tuyển (CV).
- Phân tích CV tự động, chấm điểm ATS, lưu chi tiết hồ sơ.
- Xem, tìm kiếm, lọc công việc theo tên, vị trí, cấp độ.
- Ứng tuyển vào công việc, quản lý lịch sử ứng tuyển.

### Quản trị viên (Admin)
- **Quản lý người dùng**: Tạo, sửa, xóa, phân quyền, khóa/mở khóa.
- **Quản lý vai trò, quyền truy cập**: CRUD, phân quyền động.
- **Quản lý công việc**: Tạo, sửa, xóa, lọc, phân trang.
- **Quản lý kỹ năng**: Thêm, sửa, xóa, lọc.
- **Quản lý hồ sơ ứng tuyển**: Duyệt, xóa, phân tích lại, lọc nâng cao.

---

## Cấu trúc thư mục
```plaintext
websitetuyendung/
├── src/
│   └── main/
│       ├── java/                # Mã nguồn Java 
│       └── resources/           # Cấu hình, messages, properties
│           ├── application.properties
│           ├── messages.properties
│           └── messages_vi.properties
├── pom.xml                      # Cấu hình Maven
├── mvnw, mvnw.cmd, .mvn/        # Maven Wrapper
├── upload/                      # Thư mục lưu file upload (CV, avatar)
│   ├── avatar/
│   └── *.pdf

---
```

## Yêu cầu hệ thống
- **Java** 17+
- **MySQL** 8.0+
- **Maven**
- **Gemini API key** (Google AI)

---

## Cài đặt & Chạy thử
```plaintext
git clone https://github.com/dntduy21/job.git

# Cấu hình database, Gemini API key trong src/main/resources/application.properties
```

Ứng dụng backend chạy tại:  `http://localhost:8080`

---

## Liên hệ
📧 **Email:** dinhngoctranduy.2105@gmail.com  

---

## Lưu ý
- Hệ thống hỗ trợ **phân tích CV tự động**, cần cấu hình đúng **Gemini API key** để sử dụng tính năng này.
- Thư mục `upload` dùng để lưu **file PDF CV** và **avatar**, cần đảm bảo quyền ghi cho ứng dụng.
- Tài liệu API tự động tại:  `http://localhost:8080/swagger-ui/index.html`
