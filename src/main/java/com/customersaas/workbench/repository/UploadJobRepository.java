package com.customersaas.workbench.repository;

import com.customersaas.workbench.entity.UploadJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadJobRepository extends JpaRepository<UploadJob, Long> {
}
