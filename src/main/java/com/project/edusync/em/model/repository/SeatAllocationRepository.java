package com.project.edusync.em.model.repository;

import com.project.edusync.em.model.entity.Seat;
import com.project.edusync.em.model.entity.SeatAllocation;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface SeatAllocationRepository extends JpaRepository<SeatAllocation, Long> {

    // ── 1. Overlap detection: occupied seat IDs in a room (SINGLE query) ─────
    //    KEPT for backward compat
    @Query("""
        SELECT sa.seat.id FROM SeatAllocation sa
        WHERE sa.seat.room.id = :roomId
          AND sa.startTime < :endTime
          AND sa.endTime > :startTime
        """)
    Set<Long> findOccupiedSeatIdsInRoom(
        @Param("roomId") Long roomId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);

    // ── 2. Room availability: total allocation count per room — ALL rooms (SINGLE query)
    //    COUNT(sa.id) counts total allocations, not distinct seats, for capacity math
    @Query("""
        SELECT sa.seat.room.id, COUNT(sa.id)
        FROM SeatAllocation sa
        WHERE sa.startTime < :endTime
          AND sa.endTime > :startTime
        GROUP BY sa.seat.room.id
        """)
    List<Object[]> countOccupiedAllocationsPerRoom(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);

    // ── 3. Bulk student conflict check — ALL students at once, SINGLE query ──
    @Query("""
        SELECT sa.student.id FROM SeatAllocation sa
        WHERE sa.student.id IN :studentIds
          AND sa.startTime < :endTime
          AND sa.endTime > :startTime
        """)
    Set<Long> findAlreadyAllocatedStudentIds(
        @Param("studentIds") Collection<Long> studentIds,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);

    // ── 4. Pessimistic lock — lock ALL seats in room (unfiltered) ────────────
    //    Locks every seat row in the room to prevent concurrent allocation races.
    //    Filtering by capacity is done in-memory after obtaining the lock.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    @Query("""
        SELECT s FROM Seat s
        WHERE s.room.id = :roomId
        ORDER BY s.rowNumber ASC, s.columnNumber ASC
        """)
    List<Seat> lockAllSeatsInRoom(@Param("roomId") Long roomId);

    // ── 5. Per-seat occupancy via GROUP BY (no subquery, no N+1) ─────────────
    //    Returns [seatId, allocationCount] pairs for occupied seats in a time window.
    //    Seats with 0 allocations won't appear — absent = 0.
    @Query("""
        SELECT sa.seat.id, COUNT(sa.id)
        FROM SeatAllocation sa
        WHERE sa.seat.room.id = :roomId
          AND sa.startTime < :endTime
          AND sa.endTime > :startTime
        GROUP BY sa.seat.id
        """)
    List<Object[]> countAllocationsPerSeatInRoom(
        @Param("roomId") Long roomId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);

    // ── 6. Fetch allocations with all joins — zero lazy-loading issues ────────
    @Query("""
        SELECT sa FROM SeatAllocation sa
        JOIN FETCH sa.seat s
        JOIN FETCH s.room r
        JOIN FETCH sa.student st
        JOIN FETCH st.userProfile up
        WHERE sa.examSchedule.id = :examScheduleId
        ORDER BY s.rowNumber ASC, s.columnNumber ASC
        """)
    List<SeatAllocation> findByExamScheduleWithDetails(
        @Param("examScheduleId") Long examScheduleId);

    // ── 7. Single student conflict check ──────────────────────────────────────
    @Query("""
        SELECT COUNT(sa) > 0 FROM SeatAllocation sa
        WHERE sa.student.id = :studentId
          AND sa.startTime < :endTime
          AND sa.endTime > :startTime
        """)
    boolean isStudentAllocatedInTimeWindow(
        @Param("studentId") Long studentId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);

    // ── 8. Simple fetch by schedule (for bulk delete etc.) ────────────────────
    List<SeatAllocation> findByExamScheduleId(Long examScheduleId);

    // ── 9. Find seats blocked by single-seating exam allocations ─────────────
    //    Returns seat IDs that have ANY allocation from a schedule with
    //    maxStudentsPerSeat = 1, overlapping the given time window.
    //    These seats are FULLY BLOCKED regardless of the current exam's capacity.
    @Query("""
        SELECT DISTINCT sa.seat.id
        FROM SeatAllocation sa
        WHERE sa.seat.room.id = :roomId
          AND sa.startTime < :endTime
          AND sa.endTime > :startTime
          AND sa.examSchedule.maxStudentsPerSeat = 1
        """)
    Set<Long> findSeatIdsBlockedBySingleSeating(
        @Param("roomId") Long roomId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);

    // ── 10. Count of seats blocked by single-seating per room ────────────────
    //    Used in getAvailableRooms() to correctly compute room capacity
    //    when current exam is double-seating but some seats are locked by single exams.
    @Query("""
        SELECT sa.seat.room.id, COUNT(DISTINCT sa.seat.id)
        FROM SeatAllocation sa
        WHERE sa.startTime < :endTime
          AND sa.endTime > :startTime
          AND sa.examSchedule.maxStudentsPerSeat = 1
        GROUP BY sa.seat.room.id
        """)
    List<Object[]> countSingleSeatBlockedSeatsPerRoom(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);

    // ── 11. Count allocations on single-seating-blocked seats per room ────────
    //    Used to subtract these allocations from the "non-blocked" occupancy count,
    //    since blocked seats are removed entirely from the capacity calculation.
    @Query("""
        SELECT sa.seat.room.id, COUNT(sa.id)
        FROM SeatAllocation sa
        WHERE sa.startTime < :endTime
          AND sa.endTime > :startTime
          AND sa.seat.id IN (
            SELECT DISTINCT sa2.seat.id
            FROM SeatAllocation sa2
            WHERE sa2.startTime < :endTime
              AND sa2.endTime > :startTime
              AND sa2.examSchedule.maxStudentsPerSeat = 1
          )
        GROUP BY sa.seat.room.id
        """)
    List<Object[]> countAllocationsOnBlockedSeatsPerRoom(
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
}
