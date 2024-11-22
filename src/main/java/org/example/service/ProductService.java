package org.example.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.domain.Product;

public interface ProductService extends IService<Product> {

    List<Product> findAll();

}
