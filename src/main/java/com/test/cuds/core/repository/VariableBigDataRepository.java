package com.test.cuds.core.repository;

import com.test.cuds.core.entity.VariableBigDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariableBigDataRepository extends JpaRepository<VariableBigDataEntity, Long> {
    List<VariableBigDataEntity> findByNameAndDataHashAndIsDeleted(String name, String dataHash, Byte isDeleted);

    List<VariableBigDataEntity> findByIdAndIsDeleted(Long id, Byte isDeleted);
}
