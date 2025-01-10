package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()){
            throw new ResourceNotFoundException("Category", "CategoryId", categoryId);
        }
        Category existingCategory = category.get();
        boolean productNotPresent = true;
        List<Product> products = category.get().getProducts();
        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                productNotPresent = false;
                break;
            }
        }
        if(!productNotPresent) {
            throw new APIException("Product already exists!");
        }
        Product product = modelMapper.map(productDTO, Product.class);
        product.setProductImage("Default.png");
        product.setCategory(existingCategory);
        double specialPrice = productDTO.getPrice() - ((productDTO.getDiscount() / 100) * productDTO.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.saveAndFlush(product);
        return modelMapper.map(savedProduct, ProductDTO.class);

    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findAll(pageDetails);

        List<Product> retrievedProducts = pageProducts.getContent();
        if(retrievedProducts.isEmpty()){
            throw new APIException("There are no products to retrieve");
        }
        List<ProductDTO> retrievedProductsDTO = new ArrayList<>();
        for(Product var: retrievedProducts){
            retrievedProductsDTO.add(modelMapper.map(var, ProductDTO.class));
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(retrievedProductsDTO);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()){
            throw new ResourceNotFoundException("Category", "CategoryId", categoryId);
        }
        Category existingCategory = category.get();
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(existingCategory, pageDetails);
        List<Product> retrievedProducts = pageProducts.getContent();
        if(retrievedProducts.isEmpty()){
            throw new APIException("There are no products to retrieve");
        }
        List<ProductDTO> retrievedProductsDTO = new ArrayList<>();
        for(Product var: retrievedProducts){
            retrievedProductsDTO.add(modelMapper.map(var, ProductDTO.class));
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(retrievedProductsDTO);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);
        List<Product> retrievedProducts = pageProducts.getContent();
        if(retrievedProducts.isEmpty()){
            throw new APIException("There are no products to retrieve");
        }
        List<ProductDTO> retrievedProductsDTO = new ArrayList<>();
        for(Product var: retrievedProducts){
            retrievedProductsDTO.add(modelMapper.map(var, ProductDTO.class));
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(retrievedProductsDTO);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Optional<Product> checkedProduct = productRepository.findById(productId);
        if (checkedProduct.isEmpty()){
            throw new ResourceNotFoundException("Product","ProductId",productId);
        }
        Product product = modelMapper.map(productDTO, Product.class);
        Product existingProduct = checkedProduct.get();
        existingProduct.setProductName(product.getProductName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setSpecialPrice(product.getPrice() - ((product.getDiscount() / 100) * product.getPrice()));
        Product savedProduct = productRepository.saveAndFlush(existingProduct);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Optional<Product> checkedProduct = productRepository.findById(productId);
        if (checkedProduct.isEmpty()){
            throw new ResourceNotFoundException("Product","ProductId",productId);
        }
        Product existingProduct = checkedProduct.get();
        productRepository.deleteById(productId);

        return modelMapper.map(existingProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //Get the product from the DB
        Optional<Product> checkedProduct = productRepository.findById(productId);
        if (checkedProduct.isEmpty()){
            throw new ResourceNotFoundException("Product","ProductId",productId);
        }
        Product existingProduct = checkedProduct.get();
        //Upload image to server
        //Get the filename of the uploaded image
        String fileName = fileService.uploadImage(path, image);
        //Update the new filename to the product
        existingProduct.setProductImage(fileName);
        // Save Updated product
        Product savedProduct = productRepository.saveAndFlush(existingProduct);
        //Return the DTO after mapping product to DTO
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

}
