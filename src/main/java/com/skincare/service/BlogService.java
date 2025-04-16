package com.skincare.service;

import com.skincare.model.Blog;
import com.skincare.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogService {
    
    private final BlogRepository blogRepository;
    
    @Autowired
    public BlogService(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }
    
    private List<Blog> blogs = new ArrayList<>();
    
    // Phương thức lấy tất cả bài viết đang hoạt động
    public List<Blog> getAllActiveBlogs() {
        return blogs.stream()
                .filter(blog -> "ACTIVE".equals(blog.getStatus()))
                .collect(Collectors.toList());
    }
    
    // Phương thức lấy chi tiết một bài viết
    public Optional<Blog> getBlogById(Long id) {
        return blogs.stream()
                .filter(blog -> blog.getId().equals(id))
                .findFirst();
    }
    
    // Phương thức lấy các bài viết gần đây nhất
    public List<Blog> getRecentBlogs(int limit) {
        return blogs.stream()
                .filter(blog -> "ACTIVE".equals(blog.getStatus()))
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    // Phương thức lưu bài viết
    public Blog saveBlog(Blog blog) {
        if (blog.getId() == null) {
            Long newId = blogs.stream()
                    .mapToLong(Blog::getId)
                    .max()
                    .orElse(0L) + 1;
            blog.setId(newId);
            blogs.add(blog);
        } else {
            for (int i = 0; i < blogs.size(); i++) {
                if (blogs.get(i).getId().equals(blog.getId())) {
                    blogs.set(i, blog);
                    break;
                }
            }
        }
        return blog;
    }
    
    // Phương thức xóa bài viết
    public void deleteBlog(Long id) {
        blogs.removeIf(blog -> blog.getId().equals(id));
    }
} 