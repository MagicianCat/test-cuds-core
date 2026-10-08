package com.test.cuds.core.repository;

import com.test.cuds.core.entity.VariableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariableRepository extends JpaRepository<VariableEntity, Long> {
    List<VariableEntity> findByIdAndIsDeleted(Long id, Byte isDeleted);
}
