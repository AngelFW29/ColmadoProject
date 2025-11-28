package Controller;

import DAO.CategoryProductDAO;
import Model.ProductCategory;

import java.util.ArrayList;
import java.util.List;

public class CategoryController {
    private CategoryProductDAO categoryProductDAO;
    public CategoryController(CategoryProductDAO categoryProductDAO) {
        this.categoryProductDAO = categoryProductDAO;
    }

    public ProductCategory getCategory(int id){
        return categoryProductDAO.read(id);
    }

    public List<ProductCategory> getCategories() {
        return categoryProductDAO.findAll();
    }

    public ArrayList<String> getCategoriesName() {
        return categoryProductDAO.findAllNames();
    }

    public ProductCategory getProductCategoryByName(String name) {
        return categoryProductDAO.searchByName(name);
    }



}
