package com.springrestmvcproject.spring6restmvc.repositories;

import com.springrestmvcproject.spring6restmvc.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository  extends JpaRepository<Category, UUID> {
}
