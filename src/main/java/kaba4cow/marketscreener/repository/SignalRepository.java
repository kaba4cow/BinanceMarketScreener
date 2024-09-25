package kaba4cow.marketscreener.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import kaba4cow.marketscreener.entity.Signal;
import kaba4cow.marketscreener.entity.SignalType;

@Repository
public interface SignalRepository extends JpaRepository<Signal, Long> {

	@Query("SELECT s FROM Signal s WHERE s.type = :type AND s.timestamp >= :timestamp AND s.baseAsset = :baseAsset AND s.quoteAsset = :quoteAsset AND s.value >= :valueThreshold")
	public List<Signal> findLastSignals(//
			@Param("type") SignalType type, //
			@Param("timestamp") Long timestamp, //
			@Param("baseAsset") String baseAsset, //
			@Param("quoteAsset") String quoteAsset, //
			@Param("valueThreshold") Float valueThreshold);

	@Modifying
	@Transactional
	@Query("DELETE FROM Signal s WHERE s.timestamp < :timestamp")
	public void deleteOldSignals(//
			@Param("timestamp") Long timestamp);

}
