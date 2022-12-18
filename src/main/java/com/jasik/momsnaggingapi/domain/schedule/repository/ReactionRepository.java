package com.jasik.momsnaggingapi.domain.schedule.repository;

import com.jasik.momsnaggingapi.domain.schedule.Category;
import com.jasik.momsnaggingapi.domain.schedule.Interface.ReactionUserInterface;
import com.jasik.momsnaggingapi.domain.schedule.Interface.ScheduleNaggingInterface;
import com.jasik.momsnaggingapi.domain.schedule.Reaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Optional<Reaction> findByFromUserAndToUserAndTypeIdAndScheduleDate(Long fromUser, Long toUser,
        int typeId, LocalDate scheduleDate);

    void deleteByFromUserAndToUserAndTypeIdAndScheduleDate(Long fromUser, Long toUser, int typeId,
        LocalDate scheduleDate);

    List<Reaction> findByToUserAndScheduleDate(Long toUser, LocalDate scheduleDate);

    @Transactional
    @Query("select r as reaction, u as user from Reaction r inner join User u " +
        "on r.fromUser = u.id where r.toUser = :toUser and r.scheduleDate = :scheduleDate")
    List<ReactionUserInterface> findWithUserByToUserAndScheduleDate(@Param("toUser") Long toUser,
        @Param("scheduleDate") LocalDate scheduleDate);


    List<Reaction> findByFromUserAndToUserAndScheduleDate(Long fromUser, Long toUser, LocalDate scheduleDate);

}
