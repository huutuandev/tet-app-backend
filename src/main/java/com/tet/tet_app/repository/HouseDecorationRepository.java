package com.tet.tet_app.repository;

import com.tet.tet_app.dto.response.HouseDecorationResponse;
import com.tet.tet_app.entity.House;
import com.tet.tet_app.entity.HouseDecoration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HouseDecorationRepository
        extends JpaRepository<HouseDecoration, Long> {

    List<HouseDecoration> findByHouse(House house);
}
