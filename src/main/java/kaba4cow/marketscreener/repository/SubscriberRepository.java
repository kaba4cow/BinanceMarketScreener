package kaba4cow.marketscreener.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kaba4cow.marketscreener.entity.Subscriber;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

	@Query("SELECT s FROM Subscriber s WHERE s.subscribed = true AND :pump >= s.pumpThreshold")
	public List<Subscriber> findSubscribersByPump(//
			@Param("pump") Float pump);

	@Query("SELECT s FROM Subscriber s WHERE s.subscribed = true AND :dump >= s.dumpThreshold")
	public List<Subscriber> findSubscribersByDump(//
			@Param("dump") Float dump);

	@Query("SELECT s FROM Subscriber s WHERE s.subscribed = true AND :longLiquidation >= s.longLiquidationThreshold ")
	public List<Subscriber> findSubscribersByLongLiquidation(//
			@Param("longLiquidation") Float longLiquidation);

	@Query("SELECT s FROM Subscriber s WHERE s.subscribed = true AND :shortLiquidation >= s.shortLiquidationThreshold")
	public List<Subscriber> findSubscribersByShortLiquidation(//
			@Param("shortLiquidation") Float shortLiquidation);

}
