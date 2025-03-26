package UTH;

import java.util.Scanner;

/**
 * Main class cho API Hub
 * Mô tả: API Hub - Nền tảng cung cấp dịch vụ đăng tải, tìm kiếm, triển khai APIs
 * Chức năng: Quản lý, đăng tải, tìm kiếm, và triển khai API
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("===== API HUB =====");
        System.out.println("Nền tảng cung cấp dịch vụ đăng tải, tìm kiếm, triển khai APIs");
        System.out.println("Vui lòng cài đặt Maven và chạy 'mvn install' để khởi động ứng dụng Spring Boot");
        
        // Tạm thời cung cấp menu điều hướng đơn giản
        showMenu();
    }
    
    private static void showMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n==== MENU ====");
            System.out.println("1. Đăng ký tài khoản");
            System.out.println("2. Đăng nhập");
            System.out.println("3. Xem danh sách API");
            System.out.println("4. Tìm kiếm API");
            System.out.println("5. Thoát");
            System.out.print("Chọn chức năng (1-5): ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    System.out.println("Chức năng đăng ký tài khoản sẽ khả dụng sau khi cài đặt Spring Boot");
                    break;
                case "2":
                    System.out.println("Chức năng đăng nhập sẽ khả dụng sau khi cài đặt Spring Boot");
                    break;
                case "3":
                    System.out.println("Danh sách API sẽ hiển thị ở đây sau khi cài đặt Spring Boot");
                    break;
                case "4":
                    System.out.println("Chức năng tìm kiếm API sẽ khả dụng sau khi cài đặt Spring Boot");
                    break;
                case "5":
                    exit = true;
                    System.out.println("Cảm ơn bạn đã sử dụng API Hub!");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng chọn lại.");
            }
        }
        
        scanner.close();
    }
}