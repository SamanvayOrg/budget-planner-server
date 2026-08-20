package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.SampleBudgetLine;
import org.mbs.budgetplannerserver.domain.State;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SampleBudgetLineRepository  extends CrudRepository<SampleBudgetLine, Long> {

    // Seeding a new budget loads every sample line for the state. function/detailedHead —
    // and everything transitively reachable from them — are eager @ManyToOne with no
    // batching, so the derived-query form of this method issued a separate SELECT per
    // association per row (an N+1 that dominated budget-creation time).
    //
    // Fetch-joining the whole eager to-one graph collapses it into one query. Safe to go
    // this deep because every join below is to-one; the reverse collections
    // (MinorHead#detailedHeads, MajorHead#minorHeads) are LAZY, so nothing multiplies rows
    // and no `distinct` is needed.
    @Query("select sbl from SampleBudgetLine sbl "
            + "left join fetch sbl.function f "
            + "left join fetch f.functionGroup "
            + "left join fetch sbl.detailedHead dh "
            + "left join fetch dh.minorHead mh "
            + "left join fetch mh.majorHead mah "
            + "left join fetch mah.majorHeadGroup "
            + "where sbl.state = :state")
    List<SampleBudgetLine> findAllByState(@Param("state") State state);
}
