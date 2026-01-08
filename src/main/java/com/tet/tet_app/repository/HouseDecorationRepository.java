package com.tet.tet_app.repository;

import com.tet.tet_app.dto.response.HouseDecorationResponse;
import com.tet.tet_app.entity.HouseDecoration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HouseDecorationRepository
        extends JpaRepository<HouseDecoration, Long> {

    @Query("""
        SELECT new com.tet.tet_app.dto.response.HouseDecorationResponse(
            hd.id,
            s.id,
            s.name,
            s.imageUrl,
            hd.posX,
            hd.posY,
            hd.zIndex
        )
        FROM HouseDecoration hd
        JOIN ShopItem s ON s.id = hd.itemId
        WHERE hd.userId = :userId
        ORDER BY hd.zIndex ASC
    """)
    List<HouseDecorationResponse> findHouseByUser(@Param("userId") Long userId);
}
