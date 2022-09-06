package com.jasik.momsnaggingapi.domain.push.repository;

import com.jasik.momsnaggingapi.domain.push.Interface.PushNaggingInterface;
import com.jasik.momsnaggingapi.domain.push.Push;
import com.jasik.momsnaggingapi.domain.push.Push.PushType;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PushRepository extends JpaRepository<Push, Long> {

    @Query("select p as push, n as nagging from Push p left outer join Nagging n " +
            "on p.naggingId = n.id")
    Page<PushNaggingInterface> findAllByPushTypeOrderByIdDesc(PushType pushType, Pageable pageable);
}
