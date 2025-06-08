package com.example.client.data.local.entities

import com.example.client.data.model.UserDTO
import com.example.client.domain.model.google.Location


fun LocationEntity.toLocation() = Location(
    id = id,
    name = name,
    address = address,
    lat = latitude,
    lng = longitude,
    userLogged = userLogged
)

fun Location.toEntity(userLogged: String, timestamp: Long = System.currentTimeMillis()) = LocationEntity(
    name = name,
    address = address,
    latitude = lat,
    longitude = lng,
    timestamp = timestamp,
    userLogged = userLogged
)


fun UserEntity.toUserDTO()= UserDTO(
    username = username,
)

fun UserDTO.toEntity(userLogged: String, timestamp: Long = System.currentTimeMillis()) = UserEntity(
    username = username,
    timestamp = timestamp,
    userLogged = userLogged
)
