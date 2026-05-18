package com.example.myroom.fake.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myroom.fake.model.FakeRoom;

public interface FakeRoomRepository extends JpaRepository<FakeRoom, Long> {
}
