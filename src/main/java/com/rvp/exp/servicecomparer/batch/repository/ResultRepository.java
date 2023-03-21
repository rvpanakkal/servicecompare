/**
 * 
 */
package com.rvp.exp.servicecomparer.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rvp.exp.servicecomparer.batch.models.output.Result;

/**
 * @author U12044
 *
 */
public interface ResultRepository extends JpaRepository<Result, Long> {

}
