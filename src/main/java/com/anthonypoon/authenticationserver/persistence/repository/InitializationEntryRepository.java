package com.anthonypoon.authenticationserver.persistence.repository;

import com.anthonypoon.authenticationserver.persistence.entity.InitializationEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InitializationEntryRepository extends JpaRepository<InitializationEntry, Long> {
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM InitializationEntry e")
    boolean hasAny();
}
